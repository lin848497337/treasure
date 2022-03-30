package sample.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class AlertUtil {
    public static void warn(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.CLOSE);
        alert.showAndWait();
    }

    public static boolean confirm(String msg){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        Optional result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }


    public static void exception(Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        e.printStackTrace(pw);
        pw.close();
        Alert alert = new Alert(Alert.AlertType.ERROR, stringWriter.toString(), ButtonType.CLOSE);
        alert.showAndWait();
    }
}
