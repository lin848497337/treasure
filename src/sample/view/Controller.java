package sample.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import sample.AppController;

import java.io.IOException;

public class Controller {

    @FXML
    private Text actionTarget;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public void init(){
        AppController.getInstasnce().init();
    }


    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        actionTarget.setText("login with "+username.getText() + " @ " + password.getText());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("MainFrame.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        BorderPane other = fxmlLoader.load();
        ((Button)event.getSource()).getScene().setRoot(other);
        MainFrameController mainFrameController = fxmlLoader.getController();
        mainFrameController.onStart();
    }
}
