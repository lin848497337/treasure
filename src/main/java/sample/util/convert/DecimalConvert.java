package sample.util.convert;

import sample.util.TypeConvert;

import java.math.BigDecimal;

public class DecimalConvert implements TypeConvert<BigDecimal> {

    @Override
    public boolean isSupport(Class o) {
        return o.equals(BigDecimal.class);
    }

    @Override
    public BigDecimal convert(Object o) {
        return new BigDecimal(o.toString());
    }
}
