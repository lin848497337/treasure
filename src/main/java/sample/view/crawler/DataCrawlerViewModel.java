package sample.view.crawler;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import sample.model.DailyAction;
import sample.service.AppService;
import sample.util.DateFormatUtil;

@Data
public class DataCrawlerViewModel implements ViewModel {
    private StringProperty lastCrawlerMarketDate = new SimpleStringProperty("2021-01-20");
    private StringProperty lastCrawlerStockDate = new SimpleStringProperty("2021-01-20");
    private StringProperty crawlerDayInterval = new SimpleStringProperty("60");
    private DoubleProperty crawlerMarketProgress = new SimpleDoubleProperty(0);
    private DoubleProperty crawlerStockProgress = new SimpleDoubleProperty(0);

    public DataCrawlerViewModel() {
        DailyAction dailyAction = AppService.getInstasnce().selectLastCrawlerStockAction();
        if (dailyAction != null && dailyAction.getCreateTime() != null) {
            lastCrawlerStockDate.setValue(DateFormatUtils.format(dailyAction.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }else {
            lastCrawlerStockDate.setValue("no crawler data");
        }

        DailyAction MarketAction = AppService.getInstasnce().selectLastCrawlerMarketAction();
        if (MarketAction != null && MarketAction.getCreateTime() != null) {
            lastCrawlerMarketDate.setValue(DateFormatUtils.format(MarketAction.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }else {
            lastCrawlerMarketDate.setValue("no crawler data");
        }
    }


    void onStartCrawlerStockListData() {
        AppService.getInstasnce().asyncLoadData((p)->{
            System.out.println("load p "+p );
            crawlerStockProgress.setValue(p);
        });
        lastCrawlerStockDate.setValue(DateFormatUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
    }

    void onStartCrawlerMarketData() {
        int dayInterval = 5;
        String dayIntervalStr = crawlerDayInterval.getValue();
        if (NumberUtils.isCreatable(dayIntervalStr)){
            dayInterval = NumberUtils.createInteger(dayIntervalStr);
        }

        AppService.getInstasnce().asyncLoadMarketData(dayInterval, (p)->{
            crawlerMarketProgress.setValue(p);
        });
        lastCrawlerMarketDate.setValue(DateFormatUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));


    }
}
