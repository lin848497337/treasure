package sample;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.service.AppService;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.view.tools.KLine;
import sample.view.MainFrameView;
import sample.view.MainFrameViewModel;

import java.io.IOException;
import java.util.List;

public class Bootstrap extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        lanchApp(primaryStage);
//        showKLine(primaryStage);
    }

    private void lanchApp(Stage primaryStage) throws IOException {
        ViewTuple<MainFrameView, MainFrameViewModel> viewTuple = FluentViewLoader.fxmlView(MainFrameView.class).load();

        Parent root = viewTuple.getView();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    private static void showKLine(Stage stage) throws Exception {
        AppService.getInstasnce().init();
        int width = 360;
        int height = 100;
        List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(5838);
        KLine kLine = new KLine(dailyIndices);
        kLine.drawKLine(height, width);
        Scene scene = new Scene(kLine, width, height);
        stage.setScene(scene);
        stage.setTitle("");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
