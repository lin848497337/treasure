package sample.service.strategy.impl;

import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.strategy.IStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一个月内，单日成交量是最低量2倍以上
 */
public class TrendStrategy implements IStrategy {

    private static final int DAILY_SIZE = 30;

    @Override
    public List<StockInfo> run(Map<Integer, List<DailyIndex>> marketsMap) throws Exception {
        List<Integer> selectIdList = new ArrayList<>();
        marketsMap.entrySet().stream().forEach(e->{
            int stockId = e.getKey();
            List<DailyIndex> dailyIndices = e.getValue();
            // 趋势主升、缩量回调，
            if (dailyIndices.size() > DAILY_SIZE){
                dailyIndices = dailyIndices.subList(dailyIndices.size() - DAILY_SIZE, dailyIndices.size());
            }

            DailyIndex minRate = null;
            DailyIndex maxRate = null;
            // 查找最低换手和最高换手
            for (DailyIndex di : dailyIndices){
                if (minRate == null){
                    minRate = di;
                }
                if (maxRate == null) {
                    maxRate = di;
                }
                if (maxRate.getChangeRate().compareTo(di.getChangeRate()) < 0){
                    maxRate = di;
                }
                if (minRate.getChangeRate().compareTo(di.getChangeRate()) > 0){
                    minRate = di;
                }

            }
            if (maxRate.getChangeRate().divide(minRate.getChangeRate()).compareTo(new BigDecimal(2)) > 2){
                selectIdList.add(stockId);
            }

        });
        return DatabaseManager.getInstance().selectByIds(selectIdList);
    }
}
