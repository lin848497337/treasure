package sample.model;

import sample.db.ColumnDefine;
import sample.db.TableDefine;

@TableDefine("stock_pool_info")
public class StockPoolInfo extends BaseModel{
    @ColumnDefine(define = "varchar(20)")
    private String code;
    @ColumnDefine(define = "varchar(20)")
    private String name;
    @ColumnDefine(define = "int")
    private Integer poolType;

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

    public Integer getPoolType() {
        return poolType;
    }

    public void setPoolType(Integer poolType) {
        this.poolType = poolType;
    }
}
