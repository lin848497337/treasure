package sample.view.emotion.collect;

import sample.model.DailyIndex;
import sample.util.StockUtil;
import sample.view.emotion.CollectorContext;
import sample.view.tools.Dot;

import java.util.List;
import java.util.Map;

public class AfterCollector implements ICollector{
    @Override
    public void collect(String date, List<DailyIndex> dailyIndexList, CollectorContext context) {
        for (DailyIndex d : dailyIndexList){
            if (StockUtil.isZhangting(d)){
                context.addZhangTingTimes(d.getStockInfoId());
            }
        }
    }

    @Override
    public List<Dot> fillMap(Map<String, List<Dot>> dotListMap) {
        return null;
    }


}
