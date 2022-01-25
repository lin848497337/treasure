package sample.model;

import lombok.Data;
import sample.db.ColumnDefine;
import sample.db.TableDefine;

import java.math.BigDecimal;
import java.util.Date;

@TableDefine("daily_index")
@Data
public class DailyIndex extends BaseModel {

    @ColumnDefine(define = "int")
    private int stockInfoId;
    @ColumnDefine(define = "varchar(16)")
    private String date;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal preClosingPrice;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal openingPrice;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal highestPrice;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal lowestPrice;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal closingPrice;
    @ColumnDefine(define = "bigint")
    private long tradingVolume;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal tradingValue;
    @ColumnDefine(define = "decimal(31,2)")
    private BigDecimal changeRate;



    @Override
    public String toString() {
        return "DailyIndex [stockInfoId=" + stockInfoId + ", date=" + date + ", openingPrice=" + openingPrice
                + ", preClosingPrice=" + preClosingPrice + ", highestPrice=" + highestPrice + ", closingPrice="
                + closingPrice + ", lowestPrice=" + lowestPrice + ", tradingVolume=" + tradingVolume + ", tradingValue="
                + tradingValue + ", BaseModel=" + super.toString() + "]";
    }

}
