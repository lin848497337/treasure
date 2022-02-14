package sample;

import com.google.common.collect.Lists;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.StockInfo;
import sample.service.AppService;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.view.tools.KLine;
import sample.view.MainFrameView;
import sample.view.MainFrameViewModel;
import sun.jvm.hotspot.oops.Klass;

import java.io.IOException;
import java.util.List;

public class Bootstrap extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        lanchApp(primaryStage);
//        showKLine(primaryStage);
    }

    private void lanchApp(Stage primaryStage) throws IOException {
        AppService.getInstasnce().init();
        ViewTuple<MainFrameView, MainFrameViewModel> viewTuple = FluentViewLoader.fxmlView(MainFrameView.class).load();

        Parent root = viewTuple.getView();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    private static void showKLine(Stage stage) throws Exception {
        AppService.getInstasnce().init();
        StockInfo stockInfo = DatabaseManager.getInstance().selectByIds(Lists.newArrayList(123)).get(0);
        List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(123);
        KLine.showKLine(stockInfo.getName(), dailyIndices);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
