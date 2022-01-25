package sample.service.strategy.impl;

import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.strategy.IStrategy;
import sample.util.StockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContinuesRedStrategy implements IStrategy {

    private static final double limit = 0.03;

    @Override
    public List<StockInfo> run(Map<Integer, List<DailyIndex>> marketsMap) throws Exception {
        final List<Integer> selectedIdList = new ArrayList<>();
        marketsMap.entrySet().stream().forEach(e->{
            List<DailyIndex> dailyIndices = e.getValue();
            final int last = dailyIndices.size() - 1;
            if (StockUtil.getRise(dailyIndices.get(last)) > limit && StockUtil.getRise(dailyIndices.get(last - 1)) > limit){
                selectedIdList.add(e.getKey());
            }
        });

        return DatabaseManager.getInstance().selectByIds(selectedIdList);
    }
}
