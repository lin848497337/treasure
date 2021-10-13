package sample.model;

import sample.db.ColumnDefine;
import sample.db.TableDefine;
import sample.util.StockConsts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TableDefine("stock_info")
public class StockInfo extends BaseModel {
    @ColumnDefine(define = "varchar(20)")
    private String code;
    @ColumnDefine(define = "varchar(20)")
    private String name;
    @ColumnDefine(define = "varchar(20)")
    private String exchange;
    @ColumnDefine(define = "varchar(20)")
    private String abbreviation;
    @ColumnDefine(define = "int")
    private int state;
    @ColumnDefine(define = "int")
    private int type;

    private List<Integer> poolType = new ArrayList<>();

    private String belong;
    private String riseInfo;
    private BigDecimal price;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isValid() {
        return state != StockConsts.StockState.Terminated.value();
    }

    public boolean isA() {
        return type == StockConsts.StockType.A.value();
    }

    public boolean isIndex() {
        return type == StockConsts.StockType.Index.value();
    }

    public List<Integer> getPoolType() {
        return poolType;
    }

    public void setPoolType(List<Integer> poolType) {
        this.poolType = poolType;
    }

    public void addPoolType(Integer poolType){
        this.poolType.add(poolType);
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public String getRiseInfo() {
        return riseInfo;
    }

    public void setRiseInfo(String riseInfo) {
        this.riseInfo = riseInfo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StockInfo [code=" + code + ", name=" + name + ", exchange=" + exchange + ", abbreviation="
                + abbreviation + ", state=" + state + ", type=" + type + ", BaseModel=" + super.toString() + "]";
    }

}
