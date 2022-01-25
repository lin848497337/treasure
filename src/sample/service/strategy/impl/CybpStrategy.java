package sample.service.strategy.impl;

import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.strategy.IStrategy;
import sample.util.StockUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * chang yang bu po
 */
public class CybpStrategy implements IStrategy {

    private static final int LESS_DAY = 3;

    @Override
    public List<StockInfo> run(Map<Integer, List<DailyIndex>> marketsMap) throws Exception {
        final List<Integer> selectedIdList = new ArrayList<>();
        marketsMap.entrySet().stream().forEach((e)->{
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
    }
}
