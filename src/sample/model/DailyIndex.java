package sample.model;

import sample.db.ColumnDefine;
import sample.db.TableDefine;

import java.math.BigDecimal;
import java.util.Date;

@TableDefine("daily_index")
public class DailyIndex extends BaseModel {

    @ColumnDefine(define = "int")
    private int stockInfoId;
    @ColumnDefine(define = "timestamp")
    private Date date;
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

    public int getStockInfoId() {
        return stockInfoId;
    }

    public void setStockInfoId(int stockInfoId) {
        this.stockInfoId = stockInfoId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getPreClosingPrice() {
        return preClosingPrice;
    }

    public void setPreClosingPrice(BigDecimal preClosingPrice) {
        this.preClosingPrice = preClosingPrice;
    }

    public BigDecimal getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(BigDecimal openingPrice) {
        this.openingPrice = openingPrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public BigDecimal getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(BigDecimal closingPrice) {
        this.closingPrice = closingPrice;
    }

    public long getTradingVolume() {
        return tradingVolume;
    }

    public void setTradingVolume(long tradingVolume) {
        this.tradingVolume = tradingVolume;
    }

    public BigDecimal getTradingValue() {
        return tradingValue;
    }

    public void setTradingValue(BigDecimal tradingValue) {
        this.tradingValue = tradingValue;
    }

    @Override
    public String toString() {
        return "DailyIndex [stockInfoId=" + stockInfoId + ", date=" + date + ", openingPrice=" + openingPrice
                + ", preClosingPrice=" + preClosingPrice + ", highestPrice=" + highestPrice + ", closingPrice="
                + closingPrice + ", lowestPrice=" + lowestPrice + ", tradingVolume=" + tradingVolume + ", tradingValue="
                + tradingValue + ", BaseModel=" + super.toString() + "]";
    }

}
