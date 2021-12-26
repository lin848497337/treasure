package sample.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.AppController;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        lanchApp(primaryStage);
//        showKLine(primaryStage);
    }

    private void lanchApp(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        primaryStage.setTitle("Treasure");
        primaryStage.setScene(new Scene(loader.load(), 800, 600));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.init();
    }


    private static void showKLine(Stage stage) throws Exception {
        AppController.getInstasnce().init();
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
