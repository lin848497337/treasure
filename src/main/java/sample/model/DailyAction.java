package sample.model;

import sample.db.ColumnDefine;
import sample.db.TableDefine;

@TableDefine("daily_action")
public class DailyAction extends BaseModel{
    @ColumnDefine(define = "int")
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
