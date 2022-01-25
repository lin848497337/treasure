package sample.service.strategy.impl;

import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.strategy.IStrategy;
import sample.util.StockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * xian ren zhi lu
 */
public class XrzlStrategy implements IStrategy {

    private static final float limit = 0.05f;

    @Override
    public List<StockInfo> run(Map<Integer, List<DailyIndex>> marketsMap) throws Exception {
        final List<Integer> selectedIdList = new ArrayList<>();
        marketsMap.entrySet().stream().forEach(e->{
            final int last = e.getValue().size() - 1;
            DailyIndex dailyIndex = e.getValue().get(last);
            if(StockUtil.getHtWave(dailyIndex) > limit && StockUtil.getRise(dailyIndex) > 0.0){
                selectedIdList.add(e.getKey());
            }
        });
        return DatabaseManager.getInstance().selectByIds(selectedIdList);
    }
}
