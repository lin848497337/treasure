package sample;

import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.stage.Stage;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.AppService;
import sample.view.tools.KLine;

import java.util.List;

public class TestKline  extends Application {
    public static void doMain() throws Exception {
        AppService.getInstasnce().init();
        StockInfo stockInfo = DatabaseManager.getInstance().selectByIds(Lists.newArrayList(123)).get(0);
        List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(123);
        KLine.showKLine(stockInfo.getName(), dailyIndices);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        doMain();
    }
}
