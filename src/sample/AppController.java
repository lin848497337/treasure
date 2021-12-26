package sample;

import org.apache.commons.lang3.time.DateFormatUtils;
import sample.db.DatabaseManager;
import sample.model.DailyAction;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.StockCrawlerService;
import sample.util.ActionTypeEnum;
import sample.util.StockPoolTypeEnum;
import sample.util.StockUtil;
import sample.view.ProgressCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AppController {

    private static AppController instasnce = new AppController();

    private static ExecutorService executorService = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private static ExecutorService grapMarketExecutorService = Executors.newFixedThreadPool(10, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private StockCrawlerService stockCrawlerService = new StockCrawlerService();

    private AppController(){}

    public static AppController getInstasnce() {
        return instasnce;
    }

    public void init(){
        try {
            DatabaseManager.getInstance().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asyncLoadData(ProgressCallback callback){
        executorService.execute(()->{
            loadData(callback);
        });
    }

    public void loadData(ProgressCallback callback){
        try {
            System.out.println("begin load data");
            callback.onProgressChange(0.1);
            List<StockInfo> stockInfoList = stockCrawlerService.getStockList();
            callback.onProgressChange(0.5);
            DatabaseManager.getInstance().saveStock(stockInfoList);
            callback.onProgressChange(1);
            System.out.println("load data finish");
            DailyAction action = new DailyAction();
            action.setType(ActionTypeEnum.CRAWLER_STOCK.ordinal());
            DatabaseManager.getInstance().saveAction(action);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void asyncLoadMarketData(int interval, ProgressCallback callback){
        executorService.execute(()->{
            loadMarketData(interval, callback);
        });
    }


    public void loadMarketData(int interval, ProgressCallback callback){
        try {
            System.out.println("begin load market data");
            DatabaseManager.getInstance().truncateDailyIndex();
            callback.onProgressChange(0.1);
            List<StockInfo> stockInfoList = DatabaseManager.getInstance().selectStockList();
            if (stockInfoList.isEmpty()){
                callback.onProgressChange(1);
                System.out.println("stock info is empty!");
                return;
            }
            double totalSize = stockInfoList.size();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -interval-7);
            String begin = DateFormatUtils.format(calendar.getTimeInMillis(), "yyyyMMdd");
            AtomicInteger crawlerCount = new AtomicInteger(0);
            CountDownLatch countDownLatch = new CountDownLatch(stockInfoList.size());
            for (StockInfo stockInfo : stockInfoList){
                grapMarketExecutorService.execute(() -> {
                    List<DailyIndex> dailyIndices = new ArrayList<>();
                    int count = crawlerCount.incrementAndGet();
                    double progress = 0.1 + (count * 0.6/totalSize);
                    callback.onProgressChange(progress);
                    try{
                        List<DailyIndex> dailyIndexList = stockCrawlerService.getHistoryDailyIndexs(stockInfo.getId(), stockInfo.getCode(), stockInfo.getExchange(), begin);
                        dailyIndexList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                        int min = Math.min(interval, dailyIndexList.size());
                        for (int i=0 ; i<min ; i++){
                            dailyIndices.add(dailyIndexList.get(i));
                        }
                        DatabaseManager.getInstance().saveDailyIndex(dailyIndices);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        countDownLatch.countDown();
                    }
                    dailyIndices.clear();
                });

            }
            countDownLatch.await();
            callback.onProgressChange(1);
            System.out.println("load market data finish");
            DailyAction action = new DailyAction();
            action.setType(ActionTypeEnum.CRAWLER_MARKET_DATA.ordinal());
            DatabaseManager.getInstance().saveAction(action);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public List<StockInfo> selectStockList(){
        try {
            return DatabaseManager.getInstance().selectStockList();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public DailyIndex selectLastDailyIndex(int stockId){
        try {
            return DatabaseManager.getInstance().selectLastDailyIndex(stockId);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public DailyAction selectLastCrawlerStockAction(){
        try {
            return DatabaseManager.getInstance().seletLastCrawlerStockAction(ActionTypeEnum.CRAWLER_STOCK.ordinal());
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public DailyAction selectLastCrawlerMarketAction(){
        try {
            return DatabaseManager.getInstance().seletLastCrawlerStockAction(ActionTypeEnum.CRAWLER_MARKET_DATA.ordinal());
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Map<Integer, List<DailyIndex>> prepareMarketData() throws Exception {
        List<DailyIndex> dailyIndexList = DatabaseManager.getInstance().listAllDailyIndex();
        Map<Integer, List<DailyIndex>> map = new HashMap<>();
        dailyIndexList.forEach((d)->{
            List<DailyIndex> dailyIndices = map.get(d.getStockInfoId());
            if (dailyIndices == null){
                dailyIndices = new ArrayList<>();
                map.put(d.getStockInfoId(), dailyIndices);
            }
            dailyIndices.add(d);
        });
        for (Map.Entry<Integer,List<DailyIndex>> entry : map.entrySet()){
            List<DailyIndex> dailyIndices = entry.getValue();
            dailyIndices.sort(Comparator.comparing(DailyIndex::getDate));
            DailyIndex first = dailyIndices.get(0);
            BigDecimal closePrice = first.getClosingPrice();
            for (int i=1 ; i<dailyIndices.size() ; i++){
                dailyIndices.get(i).setPreClosingPrice(closePrice);
                closePrice = dailyIndices.get(i).getClosingPrice();
            }
        }
        return map;
    }

    /**
     * 双阴反包
     * @return
     */
    public List<StockInfo> filterDoubleGreenKey(){
        try {
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectStockIdList = new ArrayList<>();
            for (Map.Entry<Integer,List<DailyIndex>> entry : map.entrySet()){
                List<DailyIndex> dailyIndices = entry.getValue();
                if (dailyIndices.size() < 4){
                    continue;
                }
                DailyIndex third = dailyIndices.get(dailyIndices.size() - 3);
                if (!StockUtil.isZhangting(third)){
                    continue;
                }
                DailyIndex second = dailyIndices.get(dailyIndices.size() - 2);
                if (!StockUtil.isZhangting(second)){
                    continue;
                }
                DailyIndex first = dailyIndices.get(dailyIndices.size() - 1);
                if (StockUtil.isGreenKey(first) &&  StockUtil.shakeClosePrice(first) > 0.05){
                    selectStockIdList.add(entry.getKey());
                }
            }
            return DatabaseManager.getInstance().selectByIds(selectStockIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 极限低吸
     * @return
     */
    public List<StockInfo> extreme30p(){
        return Collections.EMPTY_LIST;
    }

    public List<StockInfo> forceTrend(){
        try{
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectStockIdList = new ArrayList<>();
            for (Map.Entry<Integer,List<DailyIndex>> entry : map.entrySet()) {
                List<DailyIndex> dailyIndices = entry.getValue();
                int count = dailyIndices.size();
                if (count < 5){
                    continue;
                }
                if (StockUtil.getRise(dailyIndices.get(count-2))> 0.05 &&
                    StockUtil.isZhangting(dailyIndices.get(count-2)) &&
                    StockUtil.getRise(dailyIndices.get(count-1))> 0.05){
                    selectStockIdList.add(entry.getKey());
                }
            }
            return DatabaseManager.getInstance().selectByIds(selectStockIdList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public List<StockInfo> skyAddOil(){
        try {
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                if (dailyIndices.size() < 4){
                    return;
                }
                DailyIndex third = dailyIndices.get(dailyIndices.size() - 3);
                if (!StockUtil.isZhangting(third)){
                    return;
                }
                DailyIndex second = dailyIndices.get(dailyIndices.size() - 2);
                if (!StockUtil.isZhangting(second)){
                    return;
                }
                DailyIndex first = dailyIndices.get(dailyIndices.size() - 1);
                if (!StockUtil.isZhangting(second) && StockUtil.isRedK(first) && StockUtil.getRise(first) < 4){
                    selectedIdList.add(e.getKey());
                }
            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 板三阳
     * 放量涨停， 缩量三根阳线
     * @return
     */
    public List<StockInfo> banThreeReadKey(){
        try {
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                if (dailyIndices.size() < 6){
                    return;
                }
                if (StockUtil.isZhangting(dailyIndices.get(dailyIndices.size() - 5))){
                    return;
                }
                DailyIndex forth = dailyIndices.get(dailyIndices.size() - 4);
                if (!StockUtil.isZhangting(forth)){
                    return;
                }
                DailyIndex third = dailyIndices.get(dailyIndices.size() - 3);
                double riseThird = StockUtil.getRise(third);
                if (StockUtil.isZhangting(third) || riseThird < 0 || riseThird > 3){
                    return;
                }
                DailyIndex second = dailyIndices.get(dailyIndices.size() - 2);
                double riseSecond = StockUtil.getRise(second);
                if (StockUtil.isZhangting(second) || riseSecond < 0 || riseSecond > 3){
                    return;
                }
                DailyIndex first = dailyIndices.get(dailyIndices.size() - 1);
                double riseFirst = StockUtil.getRise(first);
                if (StockUtil.isZhangting(first) || riseFirst < 0 || riseFirst > 3){
                    return;
                }
                selectedIdList.add(e.getKey());
            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 板三阳
     * 放量涨停， 缩量三根阳线
     * @return
     */
    public List<StockInfo> fourReadKey(){
        try {
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                if (dailyIndices.size() < 5){
                    return;
                }
                DailyIndex forth = dailyIndices.get(dailyIndices.size() - 4);
                if (!StockUtil.isRedK(forth)){
                    return;
                }
                DailyIndex third = dailyIndices.get(dailyIndices.size() - 3);
                if (!StockUtil.isRedK(third)){
                    return;
                }
                DailyIndex second = dailyIndices.get(dailyIndices.size() - 2);
                if (!StockUtil.isRedK(second)){
                    return;
                }
                DailyIndex first = dailyIndices.get(dailyIndices.size() - 1);
                if (!StockUtil.isRedK(first)){
                    return;
                }
                selectedIdList.add(e.getKey());
            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 长阳不破,标记进入观察池
     * @return
     */
    public List<StockInfo> organCollectAndStart(){
        try{
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            final int LESS_DAY = 3;
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                int count = dailyIndices.size();
                count = Math.min(count, 30);
                BigDecimal lowerPrice = null;
                int dayCount = 0;
                for (int i=count - 1 ; i >= 0 ; i--){
                    DailyIndex di = dailyIndices.get(i);
                    if (lowerPrice == null){
                        lowerPrice = di.getLowestPrice();
                        continue;
                    }
                    if (StockUtil.getRise(di) > 0.08){
                        if (dayCount < LESS_DAY){
                            return;
                        }
                        if (di.getLowestPrice().compareTo(lowerPrice) < 0){
                            selectedIdList.add(e.getKey());
                            return;
                        }
                    }else{
                        if (di.getLowestPrice().compareTo(lowerPrice) < 0){
                            lowerPrice = di.getLowestPrice();
                        }
                    }
                    dayCount ++;
                }
            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        }catch (Exception e){

        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 大于13点不破最后一阳,标记进入观察池
     * @return
     */
    public List<StockInfo> thirteenRule(){
        try{
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                int count = dailyIndices.size();
                if (count < 4){
                    return;
                }
                int dayCount = count - 4;
                DailyIndex d4 = dailyIndices.get(dayCount++);
                DailyIndex d3 = dailyIndices.get(dayCount++);
                DailyIndex d2 = dailyIndices.get(dayCount++);
                DailyIndex d1 = dailyIndices.get(dayCount++);
                if (StockUtil.getRise(d4) + StockUtil.getRise(d3) < 0.13){
                    return;
                }
                if (d2.getClosingPrice().compareTo(d3.getLowestPrice()) < 0 || Math.abs(StockUtil.getRise(d2) )> 0.02){
                    return;
                }
                if (d1.getClosingPrice().compareTo(d3.getLowestPrice()) < 0 || Math.abs(StockUtil.getRise(d1) )> 0.02){
                    return;
                }
                selectedIdList.add(e.getKey());
            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        }catch (Exception e){

        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 高振幅个股
     * @return
     */
    public List<StockInfo> highWaveRule(){
        try{
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                int count = dailyIndices.size();
                if (count < 4){
                    return;
                }
                int dayCount = count - 4;
                DailyIndex d4 = dailyIndices.get(dayCount++);
                DailyIndex d3 = dailyIndices.get(dayCount++);
                DailyIndex d2 = dailyIndices.get(dayCount++);
                DailyIndex d1 = dailyIndices.get(dayCount++);
                double wave = 0.03;
                if (StockUtil.getWave(d1) > wave &&
                    StockUtil.getWave(d2) > wave &&
                    StockUtil.getWave(d3) > wave &&
                    StockUtil.getWave(d4) > wave){
                    selectedIdList.add(e.getKey());
                }

            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        }catch (Exception e){

        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 高振幅个股
     * @return
     */
    public List<StockInfo> baCongRule(){
        try{
            Map<Integer, List<DailyIndex>> map = prepareMarketData();
            List<Integer> selectedIdList = new ArrayList<>();
            map.entrySet().stream().forEach((e)->{
                List<DailyIndex> dailyIndices = e.getValue();
                int count = dailyIndices.size();
                if (count < 10){
                    return;
                }
                int dayCount = count - 4;
                DailyIndex d4 = dailyIndices.get(dayCount++);
                DailyIndex d3 = dailyIndices.get(dayCount++);
                DailyIndex d2 = dailyIndices.get(dayCount++);
                DailyIndex d1 = dailyIndices.get(dayCount++);
                BigDecimal protectedPrice = d4.getClosingPrice();
                for (int i=0 ; i<dayCount ; i++){
                    if (dailyIndices.get(i).getClosingPrice().compareTo(protectedPrice) > 0){
                        return;
                    }
                }
                double r4 = StockUtil.getRise(d4);
                double r3 = StockUtil.getRise(d3);
                if (r4 + r3> 0.12 && r4 > 0.05 &&
                     d2.getClosingPrice().compareTo(protectedPrice) > 0 &&
                    d1.getClosingPrice().compareTo(protectedPrice) > 0){
                    selectedIdList.add(e.getKey());
                }

            });
            return DatabaseManager.getInstance().selectByIds(selectedIdList);
        }catch (Exception e){

        }

        return Collections.EMPTY_LIST;
    }

    public void watchPool(StockInfo stockInfo , StockPoolTypeEnum poolTypeEnum) throws Exception {
        DatabaseManager.getInstance().addToPool(stockInfo.getId(), poolTypeEnum);
    }

    public List<StockInfo> refreshWatchPool() throws Exception {
        return DatabaseManager.getInstance().selectPool();
    }
}
