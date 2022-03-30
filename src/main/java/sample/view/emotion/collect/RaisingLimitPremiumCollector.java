package sample.view.emotion.collect;

import sample.model.DailyIndex;
import sample.util.StockUtil;
import sample.view.emotion.CollectorContext;
import sample.view.tools.Dot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaisingLimitPremiumCollector implements ICollector{
    /**
     * 几板
     */
    private int nTimes;
    private int preNDays = 0;
    private List<Dot> result = new ArrayList<>();
    private PreCondition condition;
    private String name;

    public RaisingLimitPremiumCollector(int nTimes, String name, int preNDays) {
        this.nTimes = nTimes;
        this.name = name;
        this.preNDays = preNDays;
    }

    @Override
    public void collect(String date, List<DailyIndex> dailyIndexList, CollectorContext context) {
        double total = 0;
        int num = 0;
        for (DailyIndex d : dailyIndexList){
            int counter = context.getLianBanTimes(d.getStockInfoId());
            if (counter == nTimes && context.recent(d.getStockInfoId()) > preNDays){
                total += StockUtil.getOpenRise(d);
                num ++;
            }
        }
        Dot dot = new Dot();
        dot.x = result.size();
        if (num == 0){
            dot.y = 0;
        }else {
            dot.y = total / num;
        }
        result.add(dot);
    }

    @Override
    public List<Dot> fillMap(Map<String, List<Dot>> dotListMap) {
        return dotListMap.put(name, result);
    }

}
