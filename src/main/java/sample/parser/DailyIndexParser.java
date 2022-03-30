package sample.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import sample.model.DailyIndex;
import sample.util.DecimalUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyIndexParser {

    /*
     * 0：新晨科技, 股票名字; 1：27.55″, 今日开盘价; 2：27.25″, 昨日收盘价; 3：26.91″, 当前价格; 4：27.55″,
     * 今日最高价; 5：26.20″, 今日最低价; 6：26.91″, 竞买价, 即“买一报价; 7：26.92″, 竞卖价, 即“卖一报价;
     * 8：22114263″ 成交金额
     */
    public DailyIndex parseDailyIndex(String content) {
        String[] strs = content.split(",");
        if (strs.length <= 1) {
            return null;
        }
        BigDecimal openingPrice = new BigDecimal(strs[1]);
        BigDecimal preClosingPrice = new BigDecimal(strs[2]);
        BigDecimal closingPrice = new BigDecimal(strs[3]);
        BigDecimal highestPrice = new BigDecimal(strs[4]);
        BigDecimal lowestPrice = new BigDecimal(strs[5]);
        long tradingVolume = Long.parseLong(strs[8]);
        BigDecimal tradingValue = new BigDecimal(strs[9]);
        String date = strs[30];
//        Date date = DateUtils.parseDate(strs[30], new String[] { "yyyy-MM-dd" });

        DailyIndex dailyIndex = new DailyIndex();
        dailyIndex.setOpeningPrice(openingPrice);
        dailyIndex.setPreClosingPrice(preClosingPrice);
        dailyIndex.setClosingPrice(closingPrice);
        dailyIndex.setHighestPrice(highestPrice);
        dailyIndex.setLowestPrice(lowestPrice);
        dailyIndex.setTradingVolume(tradingVolume);
        dailyIndex.setTradingValue(tradingValue);
        dailyIndex.setDate(date);

        return dailyIndex;
    }

    public List<DailyIndex> parseHistoryDailyIndexList(int stockInfoID, String content) throws ParseException {
        JSONObject jsonObject = JSON.parseObject(content);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        if (dataObject == null){
            return Collections.EMPTY_LIST;
        }
        JSONArray kline = dataObject.getJSONArray("klines");
        List<DailyIndex> dailyIndices = new ArrayList<>();
        BigDecimal preClosePrice = null;
        for (int i=0 ; i<kline.size() ; i++){
            String item = kline.getString(i);
            DailyIndex dailyIndex = new DailyIndex();
            String items[] = item.split(",");
            dailyIndex.setDate(items[0]);
            if (preClosePrice == null){
                preClosePrice = new BigDecimal(items[1]);
            }
            dailyIndex.setOpeningPrice(new BigDecimal(items[1]));
            dailyIndex.setClosingPrice(new BigDecimal(items[2]));
            dailyIndex.setHighestPrice(new BigDecimal(items[3]));
            dailyIndex.setLowestPrice(new BigDecimal(items[4]));
            dailyIndex.setTradingVolume(Long.valueOf(items[5]));
            dailyIndex.setTradingValue(new BigDecimal(items[6]));
            dailyIndex.setPreClosingPrice(preClosePrice);
            dailyIndex.setChangeRate(new BigDecimal(items[10]));
            dailyIndex.setStockInfoId(stockInfoID);
            dailyIndices.add(dailyIndex);
            preClosePrice = dailyIndex.getClosingPrice();
        }
        return dailyIndices;
    }

    private static DailyIndex parseHistoryDailyIndex(String content) {
        // 日期   开盘  最高  最低  收盘  成交量 成交金额
        // <tr><td><a name="06/06/2018">06/06/2018</a></td><td>23.10</td><td>25.45</td><td>22.72</td><td>24.28</td>
        // <td>17,867,500</td><td>433,192,000</td><td>0.68</td><td><span class='changeup'>2.88 %</span></td><td></td>
        // <td><span class='changeup'>12.02 %</span></td><td>10365.125</td><td><span class='changedn'>-0.20 %</class></td><
        content = content.replace(" class=altertd", "");
        Pattern pattern = Pattern.compile("<td>([\\s\\S]{3,60}?)</td>");
        Matcher matcher = pattern.matcher(content);

        String[] values = new String[7];
        int index = 0;
        while (matcher.find()) {
            values[index++] = matcher.group(1);
            if (index == 7) {
                break;
            }
        }

        String dateStr = values[0].substring(9, 19);
//        Date date = DateUtils.parseDate(dateStr, new String[] { "MM/dd/yyyy" });
        BigDecimal openingPrice = DecimalUtil.fromStr(values[1]);
        BigDecimal highestPrice = DecimalUtil.fromStr(values[2]);
        BigDecimal lowestPrice = DecimalUtil.fromStr(values[3]);
        BigDecimal closingPrice = DecimalUtil.fromStr(values[4]);
        long tradingVolume = Long.parseLong(values[5].replace(",", ""));
        BigDecimal tradingValue = DecimalUtil.fromStr(values[6]);

        DailyIndex dailyIndex = new DailyIndex();
        dailyIndex.setOpeningPrice(openingPrice);
        dailyIndex.setClosingPrice(closingPrice);
        dailyIndex.setHighestPrice(highestPrice);
        dailyIndex.setLowestPrice(lowestPrice);
        dailyIndex.setTradingVolume(tradingVolume);
        dailyIndex.setTradingValue(tradingValue);
        dailyIndex.setDate(dateStr);

        return dailyIndex;
    }

}
