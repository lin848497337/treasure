package sample.math.script

import sample.math.BaseScript
import sample.util.StockUtil

class TwiceRiseTest extends BaseScript{
    @Override
    boolean doFilter() {
        boolean  find = false;
        while (CURRENT_DATE > 20){
            if(test()){
                find = true;
            }
            CURRENT_DATE --;
        }
        return find;
    }

    boolean test(){
        int d = CURRENT_DATE;
        // 第一天红盘
        if(!StockUtil.isRedK(dailyIndices.get(d--))){
            return false;
        }
        boolean green = false;
        // 两天红盘
        for (int i=0 ; i < 2; i++){
            if (!StockUtil.isRedK(dailyIndices.get(d--))){
                green = true;
                break;
            }
        }
        if (!green) {
            return false;
        }
        d++;
        while (green) {
            if(StockUtil.isRedK(dailyIndices.get(d--))){
                break
            }
        }
        d++;
        for (int i=0 ; i<3 ; i++){
            if (!StockUtil.isZhangting(dailyIndices.get(d--))){
                return false;
            }
        }
        println getName()+"@" + dailyIndices.get(CURRENT_DATE).getDate();
        this.CURRENT_DATE = d;
        return true;
    }

}
