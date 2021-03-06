package sample.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static  sample.util.StockConsts.StockType;
import static  sample.util.StockConsts.Exchange;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import sample.model.DailyIndex;

public class StockUtil {

    private static final List<String> CODES_SH_A = Arrays.asList("600", "601", "603", "605", "688", "689");
    private static final List<String> CODES_SH_INDEX = Arrays.asList("000001");
    private static final List<String> CODES_SH_ETF = Arrays.asList("51", "58" , "56");

    private static final List<String> CODES_SZ_A = Arrays.asList("000", "001", "002", "003", "004", "300", "301");
    private static final List<String> CODES_SZ_INDEX = Arrays.asList("399001", "399006");
    private static final List<String> CODES_SZ_ETF = Arrays.asList("15");

    private StockUtil() {
    }

    private static String getExchange(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        if (isCodeStart(code, CODES_SH_A, CODES_SH_ETF)) {
            return Exchange.SH.getName();
        }
        if (isCodeStart(code, CODES_SZ_A, CODES_SZ_ETF)) {
            return Exchange.SZ.getName();
        }
        return null;
    }

    public static String getFullCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        String exchange = StockUtil.getExchange(code);
        if (exchange == null) {
            return code;
        }
        return exchange + code;
    }

    public static int getStockType(String exchange, String code) {
        if (exchange == null) {
            exchange = StockUtil.getExchange(code);
        }
        if (StockConsts.Exchange.valueOfName(exchange).isSh()) {
            if (CODES_SH_INDEX.contains(code)) {
                return StockConsts.StockType.Index.value();
            }
        } else {
            if (CODES_SZ_INDEX.contains(code)) {
                return StockType.Index.value();
            }
        }
        if (isCodeStart(code, CODES_SH_A, CODES_SZ_A)) {
            return StockType.A.value();
        }
        if (isCodeStart(code, CODES_SH_ETF, CODES_SZ_ETF)) {
            return StockType.ETF.value();
        }
        throw new NoSuchElementException("no stock type exchange " + exchange + ", code " + code);
    }

    public static String getPinyin(String name) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder sb = new StringBuilder();
        for (char ch : name.toLowerCase().toCharArray()) {
            if (ch == '*') {
                continue;
            }
            if (ch > 31 && ch < 127) {
                sb.append(ch);
            } else if (ch == '???') {
                sb.append('h');
            } else {
                try {
                    String[] arr = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
                    if (arr == null) {
                        throw new RuntimeException("not support character " + name);
                    }
                    sb.append(arr[0].charAt(0));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    throw new RuntimeException("get pinyin error", e);
                }
            }
        }
        return sb.toString();
    }


    private static boolean isCodeStart(String code, List<String> list) {
        return list.stream().anyMatch(code::startsWith);
    }

    private static boolean isCodeStart(String code, List<String> list01, List<String> list02) {
        return isCodeStart(code, list01) || isCodeStart(code, list02);
    }

    public static boolean isZhangting(DailyIndex dailyIndex){
        return dailyIndex.getPreClosingPrice().multiply(new BigDecimal("1.1")).setScale(2, RoundingMode.HALF_UP).compareTo(dailyIndex.getClosingPrice())==0;
    }

    public static boolean isTouchZhangting(DailyIndex dailyIndex){
        return dailyIndex.getPreClosingPrice().multiply(new BigDecimal("1.1")).setScale(2, RoundingMode.HALF_UP).compareTo(dailyIndex.getHighestPrice())==0;
    }

    public static boolean isDiegting(DailyIndex dailyIndex){
        return dailyIndex.getPreClosingPrice().divide(new BigDecimal("1.1"), 2, RoundingMode.HALF_UP).compareTo(dailyIndex.getClosingPrice()) == 0;
    }

    public static double getOpenRise(DailyIndex dailyIndex){
        return dailyIndex.getOpeningPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static double getCloseRise(DailyIndex dailyIndex){
        return dailyIndex.getClosingPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static double getHighestRise(DailyIndex dailyIndex){
        return dailyIndex.getHighestPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 2, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static boolean isGreenKey(DailyIndex dailyIndex){
        int cmp = dailyIndex.getOpeningPrice().compareTo(dailyIndex.getClosingPrice());
        return (cmp == 0 && dailyIndex.getPreClosingPrice().compareTo(dailyIndex.getClosingPrice()) > 0 ) || cmp > 0;
    }

    public static boolean isChuangYeBan(String code){
        return code.startsWith("300");
    }

    public static boolean isHuShi(String code){
        return code.startsWith("600") || code.startsWith("601") || code.startsWith("603") || code.startsWith("605");
    }

    public static boolean isShenShi(String code){
        return code.startsWith("000");
    }

    public static boolean isKeChuang(String code){
        return code.startsWith("688");
    }

    public static boolean isZhongXiao(String code){
        return code.startsWith("002");
    }

    public static  boolean isBeiJiaoSuo(String code){
        return code.startsWith("83") || code.startsWith("87") || code.startsWith("88") || code.startsWith("82");
    }

    public static boolean isRedK(DailyIndex dailyIndex){
        int cmp = dailyIndex.getOpeningPrice().compareTo(dailyIndex.getClosingPrice());
        return (cmp == 0 && dailyIndex.getPreClosingPrice().compareTo(dailyIndex.getClosingPrice()) < 0 ) || cmp < 0;
    }

    public static double getRise(DailyIndex dailyIndex){
        return dailyIndex.getClosingPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 4, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static double shakeClosePrice(DailyIndex dailyIndex){
        return dailyIndex.getHighestPrice().subtract(dailyIndex.getClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 4, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static double getWave(DailyIndex dailyIndex){
        return dailyIndex.getHighestPrice().subtract(dailyIndex.getLowestPrice()).divide(dailyIndex.getPreClosingPrice(), 4, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    public static double getHtWave(DailyIndex dailyIndex){
        return dailyIndex.getHighestPrice().subtract(dailyIndex.getPreClosingPrice()).divide(dailyIndex.getPreClosingPrice(), 4, RoundingMode.HALF_UP).doubleValue() * 100;
    }
}
