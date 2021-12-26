package sample.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import sample.AppController;
import sample.db.DatabaseManager;
import sample.model.DailyAction;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.util.DateFormatUtil;
import sample.util.StockPoolTypeEnum;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainFrameController {

    @FXML
    private Label lastCrawlerDate;

    @FXML
    private Label lastCrawlerMarketDate;

    @FXML
    private ProgressBar crawlerDataProgressBar;

    @FXML
    private ProgressBar crawlerMarketDataProgressBar;

    @FXML
    private TableView stockListTableView;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colCode;

    @FXML
    private TableColumn colEx;

    @FXML
    private TableColumn colAbb;

    @FXML
    private TableColumn colSta;

    @FXML
    private TableColumn colType;

    @FXML
    private Label tableStatusLabel;

    @FXML
    private TextField crawlerDayIntervalField;

    @FXML
    private TableView filterStockTableView;

    @FXML
    private TableColumn filterColName;

    @FXML
    private TableColumn filterColCode;

    @FXML
    private TableColumn filterColPrice;

    @FXML
    private TableColumn filterColBelong;

    @FXML
    private TableColumn filterColRase;

    @FXML
    private TableColumn filterColAction;

    @FXML
    private TextField stockSearchFilterTextField;

    @FXML
    private CheckBox filterSTCheckBox;

    @FXML
    private CheckBox filterCreatationCheckBox;

    @FXML
    private CheckBox filterNewCheckBox;

    @FXML
    private TextArea sqlEditor;

    @FXML
    private TableView resultView;

    @FXML
    private TableColumn idColumnOfPool;

    @FXML
    private TableColumn nameColumnOfPool;

    @FXML
    private TableColumn codeColumnOfPool;

    @FXML
    private TableColumn labelColumnOfPool;

    @FXML
    private TableColumn actionColumnOfPool;

    private Dialog klinDialog;

    @FXML
    private TableView poolView;

    public void onStart(){
        DailyAction dailyAction = AppController.getInstasnce().selectLastCrawlerStockAction();
        if (dailyAction != null) {
            lastCrawlerDate.setText(DateFormatUtils.format(dailyAction.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }else {
            lastCrawlerDate.setText("no crawler data");
        }

        DailyAction MarketAction = AppController.getInstasnce().selectLastCrawlerMarketAction();
        if (MarketAction != null) {
            lastCrawlerMarketDate.setText(DateFormatUtils.format(MarketAction.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }else {
            lastCrawlerMarketDate.setText("no crawler data");
        }

        filterStockTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
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
    }

    @FXML
    public void onStartCrawlerData(ActionEvent event){
        AppController.getInstasnce().asyncLoadData((p)->{
            System.out.println("load p "+p );
            crawlerDataProgressBar.setProgress(p);
        });
        lastCrawlerDate.setText(DateFormatUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
    }

    @FXML
    public void onStartCrawlerMarketData(ActionEvent event){
        int dayInterval = 5;
        String dayIntervalStr = crawlerDayIntervalField.getText();
        if (NumberUtils.isCreatable(dayIntervalStr)){
            dayInterval = NumberUtils.createInteger(dayIntervalStr);
        }

        AppController.getInstasnce().asyncLoadMarketData(dayInterval, (p)->{
            crawlerMarketDataProgressBar.setProgress(p);
        });
        lastCrawlerMarketDate.setText(DateFormatUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
    }

    @FXML
    public void onRefreshStockList(ActionEvent event){
        ObservableList<StockInfo> list = FXCollections.observableArrayList();
        List<StockInfo> stockInfoList = AppController.getInstasnce().selectStockList();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colEx.setCellValueFactory(new PropertyValueFactory<>("exchange"));
        colAbb.setCellValueFactory(new PropertyValueFactory<>("abbreviation"));
        colSta.setCellValueFactory(new PropertyValueFactory<>("state"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        String text = stockSearchFilterTextField.getText();
        if (StringUtils.isNotBlank(text)){
            text = text.trim();
        }else {
            text = null;
        }

        Iterator<StockInfo> it = stockInfoList.iterator();

        while (it.hasNext()) {
            StockInfo ss = it.next();
            if (text != null && !ss.getCode().contains(text) && !ss.getName().contains(text)) {
                it.remove();
            }
        }
        list.addAll(stockInfoList);
        stockListTableView.setItems(list);
        tableStatusLabel.setText("size : "+list.size());
    }

    private void refreshFilterStockList(List<StockInfo> stockInfos){
        ObservableList<StockInfo> list = FXCollections.observableArrayList();

        Iterator<StockInfo> it = stockInfos.iterator();
        boolean filterSt = filterSTCheckBox.isSelected();
        boolean filter300 = filterCreatationCheckBox.isSelected();
        boolean filterNew = filterNewCheckBox.isSelected();

        while (it.hasNext()){
            StockInfo ss = it.next();
            if (filter300 && ss.getCode().startsWith("30")){
                it.remove();
                continue;
            }
            if (filterSt && ss.getName().contains("st")){
                it.remove();
                continue;
            }
            DailyIndex dailyIndex = AppController.getInstasnce().selectLastDailyIndex(ss.getId());
            if (dailyIndex != null){
                ss.setPrice(dailyIndex.getClosingPrice());
            }else {
                ss.setPrice(new BigDecimal(0));
            }
        }
        list.addAll(stockInfos);


        filterColBelong.setCellValueFactory(new PropertyValueFactory<>("belong"));
        filterColCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        filterColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        filterColPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        filterColRase.setCellValueFactory(new PropertyValueFactory<>("riseInfo"));
        filterColAction.setCellFactory(
            (Callback<TableColumn<StockInfo, Boolean>, TableCell<StockInfo, Boolean>>) personBooleanTableColumn -> new AddPersonCell( filterStockTableView));

        filterStockTableView.setItems(list);
    }

    /** A table cell containing a button for adding a new person. */
    private class AddPersonCell extends TableCell<StockInfo, Boolean> {
        // a button for adding a new person.
        final Button addButton       = new Button("Add");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();
        // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
        final DoubleProperty buttonY = new SimpleDoubleProperty();

        /**
         * AddPersonCell constructor
         * @param table the table to which a new person can be added.
         */
        AddPersonCell( final TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(addButton);
            addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    table.getSelectionModel().select(getTableRow().getIndex());
                    //Person person = new Person();
                    //table.getSelectionModel().select(getIndex());
                    //person = table.getSelectionModel().getSelectedItem();
                    StockInfo selectStock = (StockInfo) getTableRow().getItem();
                    try{
                        AppController.getInstasnce().watchPool(selectStock, StockPoolTypeEnum.SINGLE_RED_POOL);
                    }catch (Exception e){
                        throw new RuntimeException("watch pool failed!");
                    }
                    System.out.println("select "+getTableRow().getItem());
                }
            });
        }

        /** places an add button in the row only if the row is not empty. */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }


    @FXML
    public void doubleShadow(ActionEvent event){
        // 双阴算法为两个涨停板以上，第一个断板出现阴线，有可能出现双阴反包情况
        List<StockInfo> stockInfoList = AppController.getInstasnce().filterDoubleGreenKey();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void extreme30p(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().extreme30p();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void forceTrend(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().forceTrend();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void skyAddOil(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().skyAddOil();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void banThreeReadKey(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().banThreeReadKey();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void fourReadKey(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().fourReadKey();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void organCollectAndStart(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().organCollectAndStart();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void thirteenRule(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().thirteenRule();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void highWaveRule(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().highWaveRule();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void baCongRule(ActionEvent event){
        List<StockInfo> stockInfoList = AppController.getInstasnce().baCongRule();
        refreshFilterStockList(stockInfoList);
    }

    @FXML
    public void executeSQLBtn(ActionEvent event){
        String sql = sqlEditor.getText();
        try{
            Connection conn = DatabaseManager.getInstance().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            int size = resultView.getColumns().size();
            resultView.getColumns().remove(0 , size);
            for (int i=0 ; i<count ; i++){
                final String name = metaData.getColumnName(i+1);
                TableColumn tableColumn = new TableColumn<>(name);
                tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures param) {
                        return new SimpleStringProperty(((Map<String, String>)param.getValue()).get(name));
                    }
                });
                resultView.getColumns().add(tableColumn);
            }
            ObservableList<Map> list = FXCollections.observableArrayList();
            while (rs.next()){
                Map<String, String> map = new HashMap<>();
                for(int i=0 ; i<count ; i++){
                    Object v = rs.getObject(i+1);
                    map.put(metaData.getColumnName(i+1), String.valueOf(v));
                };
                list.add(map);
            }
            resultView.setItems(list);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @FXML
    public void onSelectStock(ActionEvent event){

    }

    @FXML
    public void refreshWatchPool(ActionEvent event){
        try {
            List<StockInfo> stockInfoList = AppController.getInstasnce().refreshWatchPool();
            ObservableList<StockInfo> list = FXCollections.observableArrayList();
            list.addAll(stockInfoList);


            idColumnOfPool.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumnOfPool.setCellValueFactory(new PropertyValueFactory<>("name"));
            codeColumnOfPool.setCellValueFactory(new PropertyValueFactory<>("code"));
            labelColumnOfPool.setCellValueFactory(new PropertyValueFactory<>("label"));

            poolView.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
