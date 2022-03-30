package sample.math.cmd;

/**
 * * MA5(CD)
 *  * MA10(CD)
 *  * MA20(CD)
 *  * CLOSE(CD)
 *  * OPEN(CD)
 *  * HIGHT(CD)
 *  * LOW(CD)
 *  * VOL(CD)
 *  * CURRENT_DATE:d
 *  *
 *  * MAX(BEGIN, END, EXP)
 *  * MIN(BEGIN, END, EXP)
 */
public enum CmdType {
    MA5(1),
    MA10(1),
    MA20(1),
    CLOSE(1),
    OPEN(1),
    HIGHT(1),
    LOW(1),
    VOL(1),
    CURRENT_DATE(0),
    MAX(3),
    MIN(3)
    ;

    CmdType(int paramSize) {
        this.paramSize = paramSize;
    }

    private int paramSize;

    public int getParamSize() {
        return paramSize;
    }
}
