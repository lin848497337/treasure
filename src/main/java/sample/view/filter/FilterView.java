package sample.view.filter;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import sample.db.DatabaseManager;
import sample.model.Algorithm;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.AppService;
import sample.util.ImageProducerUtil;
import sample.util.StockPoolTypeEnum;
import sample.view.AlertUtil;
import sample.view.ApplicationContextHolder;
import sample.view.tools.ButtonCell;
import sample.view.tools.KLine;
import sample.view.tools.TableCellClick;

import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
        Button export = new Button("export");
        export.setOnAction(event -> {
              if(!viewModel.getStockList().isEmpty()){
                  Iterator<StockInfo>  it = viewModel.getStockList().getValue().iterator();
                  FileChooser fileChooser = new FileChooser();
                  fileChooser.setTitle("导出文件");
                  File file = fileChooser.showSaveDialog(ApplicationContextHolder.primaryStage);
                  if (file == null){
                      return;
                  }
                  List<String> outputList = new ArrayList<>();
                  try{
                      while (it.hasNext()){
                          StockInfo si = it.next();
                          outputList.add(si.getCode());
                      }
                      ImageProducerUtil.createImage(outputList, new Font("宋体", Font.PLAIN, 200), file);
                  }catch (Exception e){
                      AlertUtil.exception(e);
                  }finally {
                  }
              }
        });
        strategyFunc.getChildren().add(export);
        try {
            List<Algorithm> algorithms = DatabaseManager.getInstance().listAlgorithm();
            algorithms.addAll(AppService.getInstasnce().define());

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
