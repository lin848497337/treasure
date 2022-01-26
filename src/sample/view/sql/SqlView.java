package sample.view.sql;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;
import sample.db.DatabaseManager;
import sample.view.AlertUtil;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

public class SqlView implements FxmlView<SqlViewModel>, Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.getResultList().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int size = resultView.getColumns().size();
                resultView.getColumns().remove(0 , size);
                List list = (List) newValue;
                Map map = (Map) list.get(0);
                Set<String> set = map.keySet();
                for (String name : set){
                    TableColumn tableColumn = new TableColumn<>(name);
                    tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures param) {
                            return new SimpleStringProperty(((Map<String, String>)param.getValue()).get(name));
                        }
                    });
                    resultView.getColumns().add(tableColumn);
                }
                resultView.setItems(FXCollections.observableArrayList(list));
            }
        });

    }

    @InjectViewModel
    private SqlViewModel viewModel;

    @FXML
    private Button ExecuteSQLBtn;

    @FXML
    private TableView<?> resultView;

    @FXML
    private TextArea sqlEditor;

    @FXML
    void executeSQLBtn(ActionEvent event) {
        try {
            viewModel.executeSQLBtn(sqlEditor.getText());
        } catch (Exception e) {
            AlertUtil.exception(e);
        }
    }

}
