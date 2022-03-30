package sample.view.emotion.collect;

import sample.model.DailyIndex;
import sample.view.emotion.CollectorContext;
import sample.view.tools.Dot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaisingLimitCounterCollector implements ICollector{

    private String name;
    private List<Dot> explosiveRateList = new ArrayList<>();

    public RaisingLimitCounterCollector(String name) {
        this.name = name;
    }

    @Override
    public void collect(String date, List<DailyIndex> dailyIndexList, CollectorContext context) {
        int max = 0;
        for (DailyIndex d : dailyIndexList){
            int counter = context.getLianBanTimes(d.getStockInfoId());
            max = Math.max(max, counter);
        }
        Dot dot = new Dot();
        dot.x = explosiveRateList.size();
        dot.y = max;

        explosiveRateList.add(dot);

    }

    @Override
    public List<Dot> fillMap(Map<String, List<Dot>> dotListMap) {
        dotListMap.put(name, explosiveRateList);
        return explosiveRateList;
    }
}
