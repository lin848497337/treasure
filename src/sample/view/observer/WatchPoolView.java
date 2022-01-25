package sample.view.observer;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.util.Callback;
import sample.db.DatabaseManager;
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

public class WatchPoolView implements FxmlView<WatchPoolModel>, Initializable {

    private Dialog klinDialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.poolView.setItems(viewModel.getStockList());

        this.poolView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                StockInfo stockInfo = (StockInfo) newValue;
                System.out.println(stockInfo);
                if (klinDialog != null){
                    klinDialog.hide();
                    klinDialog.close();
                    klinDialog = null;
                }
                try{
                    int width = 360;
                    int height = 300;
                    List<DailyIndex> dailyIndices = DatabaseManager.getInstance().selectDailyIndexByStockId(stockInfo.getId());
                    KLine kLine = new KLine(dailyIndices);
                    Dialog dialog = new Dialog();
                    dialog.setWidth(width);
                    dialog.setHeight(height);
                    dialog.initModality(Modality.NONE);
                    dialog.setDialogPane(kLine);
                    kLine.drawKLine(height, width);
                    dialog.getDialogPane().getButtonTypes().add(new ButtonType(stockInfo.getName(), ButtonBar.ButtonData.OK_DONE));
                    dialog.show();
                    klinDialog = dialog;
                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                }
            }
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
    void onRefreshStockList(ActionEvent event) {
        try {
            this.viewModel.onRefresh("aaa");
        } catch (Exception e) {
            AlertUtil.exception(e);
        }
    }
}