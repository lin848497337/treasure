package sample.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.parser.DailyIndexParser;
import sample.parser.EastmoneyStockInfoParser;
import sample.util.HttpUtil;
import sample.util.StockConsts;
import sample.util.StockUtil;

public class StockCrawlerService {

    private CloseableHttpClient httpClient;

    private EastmoneyStockInfoParser stockInfoParser;

    private DailyIndexParser dailyIndexParser;

    public StockCrawlerService() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(30);
        cm.setMaxTotal(100);

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(1000 * 5)
            .setSocketTimeout(1000 * 5).build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(cm).build();
        stockInfoParser = new EastmoneyStockInfoParser();
        dailyIndexParser = new DailyIndexParser();
    }

    public List<StockInfo> getStockList() {
        ArrayList<StockInfo> list = new ArrayList<>();
        list.addAll(getStockList("m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23,b:MK0021,b:MK0022,b:MK0023,b:MK0024"));
        return list;
    }

    public static void main(String[] args) {
        StockCrawlerService service = new StockCrawlerService();
        System.out.println(service.getStockList().size());
    }

    private List<StockInfo> getStockList(String fs) {
        String content = HttpUtil.sendGet(httpClient, "http://20.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=10000000&np=1&fid=f3&fields=f12,f13,f14&fs=" + fs);
        if (content != null) {
            List<StockInfo> list = stockInfoParser.parseStockInfoList(content);
            list = list.stream().filter(v -> v.getExchange() != null).collect(Collectors.toList());
            list.forEach(stockInfo -> stockInfo.setAbbreviation(StockUtil.getPinyin(stockInfo.getName())));
            return list;
        }
        return Collections.emptyList();
    }

    
    public StockConsts.StockState getStockState(String code) {
        String content = HttpUtil.sendGet(httpClient,
            "http://quote.eastmoney.com/" + StockUtil.getFullCode(code) + ".html", "gb2312");
        if (content != null) {
            return stockInfoParser.parseStockState(content);
        }

        return null;
    }

    
    public DailyIndex getDailyIndex(String code) {
        String content = HttpUtil.sendGet(httpClient, "http://hq.sinajs.cn/list=" + StockUtil.getFullCode(code), "gbk");
        if (content != null) {
            return dailyIndexParser.parseDailyIndex(content);
        }
        return null;
    }

    
    public List<DailyIndex> getHistoryDailyIndexs(int stockId, String code,String exchange, String beginDate)
        throws ParseException {
        String content = getHistoryDailyIndexsString(code, exchange, beginDate);
        if (content != null) {
            return dailyIndexParser.parseHistoryDailyIndexList(stockId, content);
        }
        return Collections.emptyList();
    }

    /**
     * http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6&fields2=f45,f46,f47,f48,f49,f50,f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65,f66,f67&klt=101&fqt=1&secid=0.002162&beg=20210104&end=20500000&_=1619326432129
     * @param code
     * @return
     */
    public String getHistoryDailyIndexsString(String code,String exchange, String begDate) {
        String url = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6&fields2=f45,f46,f47,f48,f49,f50,f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65,f66,f67&klt=101&fqt=1&"
            + "secid="+normalizeSecId(code, exchange)
            + "&beg=" +begDate
            + "&end=20500000"
            + "&_="+System.currentTimeMillis();
        return HttpUtil.sendGet(httpClient, url, "gbk");
    }

    private String normalizeSecId(String code,String exchange){
        StockConsts.Exchange exchangeEnum = StockConsts.Exchange.valueOfName(exchange);
        if (exchangeEnum.isSh()){
            return "1."+code;
        }else {
            return "0."+code;
        }
    }

}
