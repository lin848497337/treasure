package sample.view.emotion.collect;

import sample.model.DailyIndex;
import sample.view.emotion.CollectorContext;
import sample.view.tools.Dot;

import java.util.List;
import java.util.Map;

public interface ICollector {
    void collect(String date, List<DailyIndex> dailyIndexList, CollectorContext context);
    List<Dot> fillMap(Map<String, List<Dot>> dotListMap);
}
