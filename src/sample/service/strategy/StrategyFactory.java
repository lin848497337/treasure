package sample.service.strategy;

import sample.service.strategy.impl.ContinuesRedStrategy;
import sample.service.strategy.impl.CybpStrategy;
import sample.service.strategy.impl.TrendStrategy;
import sample.service.strategy.impl.XrzlStrategy;

public class StrategyFactory {
    public static IStrategy createStrategy(StrategyEnum storageEnum){
        switch (storageEnum){
        case CYBP:
            return new CybpStrategy();
        case CR:
            return new ContinuesRedStrategy();
        case XRZL:
            return new XrzlStrategy();
        case TD:
            return new TrendStrategy();
        }
        return null;
    }
}
