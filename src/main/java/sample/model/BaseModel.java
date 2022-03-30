package sample.model;

import lombok.Data;
import sample.db.ColumnDefine;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class BaseModel implements Serializable {

    @ColumnDefine(define = "int", autoIncrementPk = true)
    private int id;
    @ColumnDefine(define = "TIMESTAMP  DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime = new Timestamp(System.currentTimeMillis());
    @ColumnDefine(define = "TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ")
    private Timestamp updateTime = new Timestamp(System.currentTimeMillis());




    @Override
    public String toString() {
        return "BaseModel [id=" + id + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }

}
