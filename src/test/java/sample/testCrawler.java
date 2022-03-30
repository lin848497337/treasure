package sample;

import org.junit.Test;
import sample.model.DailyIndex;
import sample.service.StockCrawlerService;
import sample.util.Console;
import sample.util.StockUtil;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class testCrawler {

    static StockCrawlerService service = new StockCrawlerService();


    public static void main(String args[]) throws ParseException {
        Console console = new Console(System.out);
        while (true){
            List<DailyIndex> dailyIndices =  service.getHistoryDailyIndexs(1, "300468" , "sz", "20220216");
            DailyIndex dailyIndex = dailyIndices.get(1);

            String line = dailyIndex.getClosingPrice() + "(" +StockUtil.getRise(dailyIndex) * 100+")";
            console.write(line);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
