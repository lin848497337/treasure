package sample.util;

public enum  StockPoolTypeEnum {
    SINGLE_RED_POOL(1);
    private int value;

    StockPoolTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
