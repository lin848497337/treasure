package sample.math.script

import sample.math.BaseScript
import sample.model.DailyIndex
import sample.util.StockUtil

class TwiceRise extends BaseScript{
    @Override
    boolean doFilter() {
        int d = CURRENT_DATE;
        DailyIndex current = dailyIndices.get(d--);
        if (current.getStockInfoId() == 3321){
            println getName();
        }
        if (!(StockUtil.getRise(current) >= -1 && StockUtil.getHighestRise(current) < 6 )){
            return false;
        }

        double rise = 0;
        while (true) {
            current = dailyIndices.get(d--);
            if(StockUtil.isRedK(current)){
                break
            }
            rise += StockUtil.getCloseRise(current);
        }
        return  rise < -4;
    }

}
