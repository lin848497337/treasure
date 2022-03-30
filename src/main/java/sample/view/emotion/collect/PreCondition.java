package sample.view.emotion.collect;

import sample.model.DailyIndex;
import sample.view.emotion.CollectorContext;

public interface PreCondition {
    boolean expect(DailyIndex d, CollectorContext cc);
}
