package sample.view.emotion.collect;

import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.util.StockUtil;
import sample.view.emotion.CollectorContext;
import sample.view.tools.Dot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExplosiveRateCollector implements ICollector{

    private String name;
    private int nTimes;
    private int preNDays;
    private List<Dot> explosiveRateList = new ArrayList<>();

    public ExplosiveRateCollector(String name, int nTimes, int preNDays) {
        this.name = name;
        this.nTimes = nTimes;
        this.preNDays = preNDays;
    }

    @Override
    public void collect(String date, List<DailyIndex> dailyIndexList, CollectorContext context) {
        int total = 0;
        int explosive = 0;
        for (DailyIndex d : dailyIndexList){
            int counter = context.getLianBanTimes(d.getStockInfoId());
            if (counter == nTimes && context.recent(d.getStockInfoId()) > preNDays && d.getClosingPrice().doubleValue() < 10){
                if(StockUtil.isTouchZhangting(d) && !StockUtil.isZhangting(d)){
                    explosive ++;
                    total++;
                    try {
                        StockInfo si = DatabaseManager.getInstance().selectByIds(Arrays.asList(d.getStockInfoId())).get(0);
                        System.out.println(date + " with "+si.getName()+"["+si.getCode()+"]");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (StockUtil.isZhangting(d)){
                    total++;

                }
            }
        }
        Dot dot = new Dot();
        dot.x = explosiveRateList.size();
        if (total == 0){
            dot.y = 0;
        }else{
            dot.y = explosive * 100 / total;
        }

        explosiveRateList.add(dot);
    }

    @Override
    public List<Dot> fillMap(Map<String, List<Dot>> dotListMap) {
        return dotListMap.put(name, explosiveRateList);
    }
}
