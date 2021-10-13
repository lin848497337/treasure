package sample.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        primaryStage.setTitle("Treasure");
        primaryStage.setScene(new Scene(loader.load(), 800, 600));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.init();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
