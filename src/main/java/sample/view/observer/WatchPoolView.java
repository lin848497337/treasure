package sample.view.observer;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import sample.db.DatabaseManager;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.service.AppService;
import sample.util.ImageProducerUtil;
import sample.view.AlertUtil;
import sample.view.ApplicationContextHolder;
import sample.view.tools.ButtonCell;
import sample.view.tools.KLine;
import sample.view.tools.TableCellClick;

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

public class WatchPoolView implements FxmlView<WatchPoolModel>, Initializable {



    private void showKline(){
        StockInfo stockInfo = (StockInfo) this.poolView.getSelectionModel().selectedItemProperty().getValue();
        if (stockInfo != null){
            try{
                List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(stockInfo.getId());
                KLine.showKLine(kline, dailyIndices);
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.poolView.setItems(viewModel.getStockList());

        this.poolView.setOnMouseClicked(event -> {
            showKline();
        });

        this.poolView.setOnKeyReleased(event -> {
            showKline();
        });


        actionButton.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new ButtonCell("Delete", poolView, new TableCellClick() {
                    @Override
                    public void onTableCellClick(StockInfo stockInfo) {
                        try{
                            AppService.getInstasnce().deletePool(stockInfo);
                            viewModel.onRefresh("aa");
                        }catch (Exception e){
                            AlertUtil.exception(e);
                        }
                    }
                });
            }
        });
    }

    @InjectViewModel
    private WatchPoolModel viewModel;

    @FXML
    private TableView<?> poolView;

    @FXML
    private TableColumn actionButton;

    @FXML
    private BorderPane kline;

    @FXML
    void onRefreshStockList(ActionEvent event) {
        try {
            this.viewModel.onRefresh("aaa");
        } catch (Exception e) {
            AlertUtil.exception(e);
        }
    }


    @FXML
    void onExport(ActionEvent event) {
        if(!viewModel.getStockList().isEmpty()){
            Iterator<StockInfo> it = viewModel.getStockList().getValue().iterator();
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
                ImageProducerUtil.createImage(outputList, new Font("宋体", Font.PLAIN, 100), file);
            }catch (Exception e){
                AlertUtil.exception(e);
            }finally {
            }
        }

    }
}
