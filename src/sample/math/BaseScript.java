package sample.math;

import sample.model.DailyIndex;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public abstract class BaseScript {
    public int CURRENT_DATE = 0;
    public int CD = 0;
    public List<DailyIndex> dailyIndices;

    public final boolean filter(List<DailyIndex> dailyIndices){
        this.dailyIndices = dailyIndices;
        CURRENT_DATE = dailyIndices.size() - 1;
        CD = CURRENT_DATE;
        return doFilter();
    }

    public abstract boolean doFilter();

    public final double ROSE(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getClosingPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).multiply(
            BigDecimal.valueOf(100)).doubleValue();
    }

    public final double MA5(int offset){
        return _MA(offset, 5);
    }

    public final double MA10(int offset){
        return _MA(offset, 10);
    }

    public final double MA20(int offset){
        return _MA(offset, 20);
    }

    private double _MA(int offset , int days){
        int realOffset = CURRENT_DATE + offset;
        int size = 0;
        BigDecimal total = new BigDecimal(0);
        for (int i = realOffset ; i>=0 ; i-- ){
            size++;
            DailyIndex dailyIndex = dailyIndices.get(i);
            total = total.add(dailyIndex.getClosingPrice());
            if (size == days){
                break;
            }
        }
        return total.divide(new BigDecimal(size), 2, RoundingMode.HALF_UP).doubleValue();
    }

    public final double CLOSE(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getClosingPrice().doubleValue();
    }

    public final double OPEN(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getOpeningPrice().doubleValue();
    }

    public final double HIGH(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getHighestPrice().doubleValue();
    }

    public final double LOW(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getLowestPrice().doubleValue();
    }

    public final long VOL(int offset){
        int realOffset = CURRENT_DATE + offset;
        DailyIndex dailyIndex = dailyIndices.get(realOffset);
        return dailyIndex.getTradingVolume();
    }

    public final long AVOL(int boffset, int eoffset){
        long vol = 0;
        int days = eoffset - boffset;
        if (days <= 0){
            throw new IllegalArgumentException();
        }
        for (int i=boffset  ; i<eoffset; i++){
            vol += VOL(i);
        }
        return vol / days;
    }

    public final long AVOL(int days){
        int b = 0 - CURRENT_DATE;
        int e = days - CURRENT_DATE;
        return AVOL(b, e);
    }

}
