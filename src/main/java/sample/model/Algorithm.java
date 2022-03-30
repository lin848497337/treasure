package sample.model;

import lombok.Data;
import sample.db.ColumnDefine;
import sample.db.TableDefine;

@TableDefine("algorithm")
@Data
public class Algorithm extends BaseModel{
    @ColumnDefine(define = "varchar(20)")
    private String name;
    @ColumnDefine(define = "TEXT")
    private String rule;
}
