package sample.model;

import sample.db.ColumnDefine;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {

    @ColumnDefine(define = "int", autoIncrementPk = true)
    private int id;
    @ColumnDefine(define = "timestamp")
    private Date createTime = new Date();
    @ColumnDefine(define = "timestamp")
    private Date updateTime = new Date();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setBaiscModel(boolean insert) {
        updateTime = new Date();
        if (insert) {
            createTime = updateTime;
        }
    }

    @Override
    public String toString() {
        return "BaseModel [id=" + id + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }

}
