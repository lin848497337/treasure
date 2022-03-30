package sample;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.service.AppService;
import sample.view.ApplicationContextHolder;
import sample.view.MainFrameView;
import sample.view.MainFrameViewModel;

import java.io.IOException;

public class Bootstrap extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        lanchApp(primaryStage);
    }

    private void lanchApp(Stage primaryStage) throws IOException {
        ApplicationContextHolder.primaryStage = primaryStage;
        AppService.getInstasnce().init();
        ViewTuple<MainFrameView, MainFrameViewModel> viewTuple = FluentViewLoader.fxmlView(MainFrameView.class).load();

        Parent root = viewTuple.getView();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
