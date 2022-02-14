package sample.view.filter;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.util.Callback;
import sample.db.DatabaseManager;
import sample.model.Algorithm;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.AppService;
import sample.util.StockPoolTypeEnum;
import sample.view.AlertUtil;
import sample.view.tools.ButtonCell;
import sample.view.tools.KLine;
import sample.view.tools.TableCellClick;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FilterView implements FxmlView<FilterModel>, Initializable {


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterCreatationCheckBox.selectedProperty().bindBidirectional(viewModel.getFilterCreating());
        filterNewCheckBox.selectedProperty().bindBidirectional(viewModel.getFilterNew());
        filterSTCheckBox.selectedProperty().bindBidirectional(viewModel.getFilterST());
        filterScenCheckBox.selectedProperty().bindBidirectional(viewModel.getFilterScen());
        filterStockTableView.setItems(viewModel.getStockList());
        rowActionCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new ButtonCell("Watch", filterStockTableView, new TableCellClick() {
                    @Override
                    public void onTableCellClick(StockInfo stockInfo) {
                        try{
                            AppService.getInstasnce().watchPool(stockInfo, StockPoolTypeEnum.SINGLE_RED_POOL);
                        }catch (Exception e){
                            AlertUtil.exception(e);
                        }
                    }
                });
            }
        });

        filterStockTableView.setOnKeyReleased(event -> {
            StockInfo stockInfo =
                (StockInfo) filterStockTableView.getSelectionModel().selectedItemProperty().getValue();

            if (stockInfo != null){
                try{
                    List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(stockInfo.getId());
                    KLine.showKLine(klineContainer, dailyIndices);;
                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        filterStockTableView.setOnMouseClicked(event -> {
            StockInfo stockInfo =
                (StockInfo) filterStockTableView.getSelectionModel().selectedItemProperty().getValue();

            if (stockInfo != null){
                try{
                    List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(stockInfo.getId());
                    KLine.showKLine(klineContainer, dailyIndices);;
                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        refreshStrategy();

        AppService.getInstasnce().strategyChangeProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                refreshStrategy();
            }
        });
    }

    private void refreshStrategy(){
        strategyFunc.getChildren().clear();
        try {
            List<Algorithm> algorithms = DatabaseManager.getInstance().listAlgorithm();
            for (Algorithm al : algorithms){
                Button button = new Button(al.getName());
                button.setOnAction(e->{
                    try {
                        viewModel.onPreviewRuleAction(al.getRule());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
                strategyFunc.getChildren().add(button);
            }
        } catch (Exception e) {
            AlertUtil.exception(e);
        }

    }

    @InjectViewModel
    private FilterModel viewModel;


    @FXML
    private CheckBox filterCreatationCheckBox;

    @FXML
    private CheckBox filterNewCheckBox;

    @FXML
    private CheckBox filterScenCheckBox;

    @FXML
    private TableView filterStockTableView;

    @FXML
    private HBox strategyFunc;

    @FXML
    private TableColumn rowActionCol;

    @FXML
    private CheckBox filterSTCheckBox;

    @FXML
    private BorderPane klineContainer;

}
