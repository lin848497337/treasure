package sample.view.emotion;

import de.saxsys.mvvmfx.ViewModel;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.util.StockUtil;
import sample.view.emotion.collect.AfterCollector;
import sample.view.emotion.collect.ExplosiveRateCollector;
import sample.view.emotion.collect.ICollector;
import sample.view.emotion.collect.RaisingLimitCounterCollector;
import sample.view.emotion.collect.RaisingLimitPremiumCollector;
import sample.view.tools.Dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EmotionViewModel  implements ViewModel {

    public Map<String, List<Dot>> pointMap = new HashMap<>();

    public void refreshLianBan() throws Exception {
        List<DailyIndex> dailyIndexList = DatabaseManager.getInstance().listAllDailyIndex();
        TreeMap<String, List<DailyIndex>> dailyListMap = new TreeMap<>();
        Map<Integer, StockInfo> stockInfoMap = DatabaseManager.getInstance().selectStockList().stream().collect(Collectors.toMap(StockInfo::getId, s->s));
        for (DailyIndex d : dailyIndexList){
            StockInfo stockInfo = stockInfoMap.get(d.getStockInfoId());
            if (!StockUtil.isHuShi(stockInfo.getCode()) && !StockUtil.isShenShi(stockInfo.getCode())){
                continue;
            }
            List<DailyIndex> indexList = dailyListMap.get(d.getDate());
            if (indexList == null){
                indexList = new ArrayList<>();
                dailyListMap.put(d.getDate(), indexList);
            }
            indexList.add(d);
        }


        List<ICollector> collectors = new ArrayList<>();
        collectors.add(new RaisingLimitPremiumCollector(1, "首板溢价", 20));
        collectors.add(new RaisingLimitCounterCollector("连板高度"));
//        collectors.add(new ExplosiveRateCollector("首板炸板率", 0, 40));
        collectors.add(new AfterCollector());
        CollectorContext cc = new CollectorContext();
        int index = 0;
        for (Map.Entry<String, List<DailyIndex>> entry : dailyListMap.entrySet()){
            String date = entry.getKey();
            List<DailyIndex> dailyIndices = entry.getValue();
            cc.setIndex(index++);
            collectors.stream().forEach(c->{
                c.collect(date, dailyIndices, cc);
            });

            cc.cleanUp();
        }

        collectors.stream().forEach(c->{
            c.fillMap(pointMap);
        });
    }
}
