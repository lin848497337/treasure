package sample.view.stock;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.StockInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class StockListView  implements FxmlView<StockListViewModel>, Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stockListTableView.setItems(viewModel.getStockList());
    }

    @InjectViewModel
    private StockListViewModel viewModel;

    @FXML
    private Label tableStatusLabel;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colEx;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TableColumn<?, ?> colCode;

    @FXML
    private TextField stockSearchFilterTextField;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colSta;

    @FXML
    private TableView<?> stockListTableView;

    @FXML
    private TableColumn<?, ?> colAbb;

    @FXML
    void onRefreshStockList(ActionEvent event) {
        String searchValue = stockSearchFilterTextField.textProperty().getValue();
        try {
            this.viewModel.onRefresh(searchValue);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
}
