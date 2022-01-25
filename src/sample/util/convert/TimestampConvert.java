package sample.util.convert;

import sample.util.TypeConvert;

import java.sql.Timestamp;

public class TimestampConvert implements TypeConvert<Timestamp> {

    @Override
    public boolean isSupport(Class o) {
        return o.equals(Timestamp.class);
    }

    @Override
    public Timestamp convert(Object o) {
        return new Timestamp((Long)o);
    }
}
