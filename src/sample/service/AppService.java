package sample.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import org.apache.commons.lang3.time.DateFormatUtils;
import sample.db.DatabaseManager;
import sample.math.AlgorithmExecutor;
import sample.model.DailyAction;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.strategy.IStrategy;
import sample.service.strategy.StrategyEnum;
import sample.service.strategy.StrategyFactory;
import sample.util.ActionTypeEnum;
import sample.util.StockPoolTypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AppService {

    private static AppService instasnce = new AppService();

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

    public LongProperty strategyChangeProperty = new SimpleLongProperty(0);

    private StockCrawlerService stockCrawlerService = new StockCrawlerService();

    private AppService(){}

    public static AppService getInstasnce() {
        return instasnce;
    }
    private AlgorithmExecutor executor = new AlgorithmExecutor();

    public void init(){
        try {
            DatabaseManager.getInstance().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StockInfo> executeRule(String rule) throws Exception {
        Map<Integer, List<DailyIndex>> map = prepareMarketData();
        List<Integer> stockIdList =  executor.execute(rule, map);
        return DatabaseManager.getInstance().selectByIds(stockIdList);
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
                        DatabaseManager.getInstance().saveDailyIndexAsync(dailyIndices);
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
            while (!DatabaseManager.getInstance().isQueueEmpty()){
                Thread.sleep(1000L);
            }
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


    private List<StockInfo> runStrategy(StrategyEnum storageEnum){
        IStrategy strategy = StrategyFactory.createStrategy(storageEnum);
        try {
            return strategy.run(prepareMarketData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public List<StockInfo> cybp(){
        return runStrategy(StrategyEnum.CYBP);
    }

    public List<StockInfo> lianyang(){
        return runStrategy(StrategyEnum.CR);
    }

    public List<StockInfo> xrzl(){
        return runStrategy(StrategyEnum.XRZL);
    }
    public List<StockInfo> trend(){
        return runStrategy(StrategyEnum.TD);
    }

    public void watchPool(StockInfo stockInfo , StockPoolTypeEnum poolTypeEnum) throws Exception {
        DatabaseManager.getInstance().addToPool(stockInfo.getId(), poolTypeEnum);
    }

    public void deletePool(StockInfo stockInfo) throws Exception {
        DatabaseManager.getInstance().delFromPool(stockInfo.getId());
    }

    public List<StockInfo> refreshWatchPool() throws Exception {
        return DatabaseManager.getInstance().selectPool();
    }
}
