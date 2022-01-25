package sample.util;

public interface TypeConvert<R> {
    boolean isSupport(Class o);
    R convert(Object o);
}
