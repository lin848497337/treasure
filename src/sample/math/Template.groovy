package sample.math

import sample.model.DailyIndex

import java.math.RoundingMode

/**
 * 定义
 * MA5(CD)
 * MA10(CD)
 * MA20(CD)
 * CLOSE(CD)
 * OPEN(CD)
 * HIGHT(CD)
 * LOW(CD)
 * VOL(CD)
 * CURRENT_DATE:d
 *
 * MAX(BEGIN, END, EXP)
 * MIN(BEGIN, END, EXP)
 */
class Template {

    private int CURRENT_DATE = 0;
    private int CD = 0;
    private List<DailyIndex> dailyIndices;

    boolean filter(List<DailyIndex> dailyIndices){
        this.dailyIndices = dailyIndices;
        CURRENT_DATE = dailyIndices.size() - 1;
        CD = CURRENT_DATE;
        return doFilter();
    }

    boolean doFilter(){
        // gen code
        return
    }

    double ROSE(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getClosingPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).multiply(100).doubleValue();
    }

    double MA5(int offset){
        return _MA(offset, 5);
    }

    double MA10(int offset){
        return _MA(offset, 10);
    }

    double MA20(int offset){
        return _MA(offset, 20);
    }

    double _MA(int offset , int days){
        int realOffset = CURRENT_DATE + offset;
        int size = 0;
        BigDecimal total = new BigDecimal(0);
        for (int i = realOffset ; i-- ; i>=0){
            size++;
            DailyIndex dailyIndex = dailyIndices.get(i);
            total = total.add(dailyIndex.getClosingPrice());
            if (size == days){
                break;
            }
        }
        return total.divide(new BigDecimal(size), 2, RoundingMode.HALF_UP).doubleValue();
    }

    double CLOSE(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getClosingPrice().doubleValue();
    }

    double OPEN(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getOpeningPrice().doubleValue();
    }

    double HIGH(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getHighestPrice().doubleValue();
    }

    double LOW(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getLowestPrice().doubleValue();
    }

    long VOL(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getTradingVolume();
    }

}
