package sample.service.strategy;

import sample.model.DailyIndex;
import sample.model.StockInfo;

import java.util.List;
import java.util.Map;

public interface IStrategy {
    List<StockInfo> run(Map<Integer, List<DailyIndex>> marketsMap) throws Exception;
}
