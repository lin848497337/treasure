package sample.view.strategy;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import sample.model.Algorithm;
import sample.view.AlertUtil;
import sample.view.tools.XCell;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class StrategyView implements FxmlView<StrategyViewModel>, Initializable {

    private Algorithm algorithm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.strategyOutputResult.setItems(viewModel.getStockList());
        ruleEditor.textProperty().bindBidirectional(viewModel.getRule());
        this.strategyList.setItems(viewModel.getStrategyList());
        initListView();
        try {
            viewModel.refreshStrategyList();
        } catch (Exception e) {
            AlertUtil.exception(e);
        }
    }

    private void initListView(){
        strategyList.setCellFactory(new Callback<ListView<Algorithm>, ListCell<Algorithm>>() {
            @Override
            public ListCell<Algorithm> call(ListView<Algorithm> param) {
                XCell xCell = new XCell();
                xCell.buttonHandler = new CellClickHandler() {
                    @Override
                    public void onClick(Algorithm algorithm) {
                        try {
                            if(!AlertUtil.confirm("确认删除["+algorithm.getName()+"]")){
                                return;
                            }
                            viewModel.onDelete(algorithm);
                        } catch (Exception e) {
                            AlertUtil.exception(e);
                        }
                    }
                };
                return xCell;
            }
        });
        strategyList.getSelectionModel().selectedItemProperty().addListener(
            (ChangeListener) (observable, oldValue, newValue) -> {
                algorithm = (Algorithm) newValue;
                viewModel.getRule().setValue(algorithm.getRule());
            });
    }

    @InjectViewModel
    private StrategyViewModel viewModel;

    @FXML
    private TextArea strategyOutputConsole;

    @FXML
    private TextArea ruleEditor;

    @FXML
    private Button saveRuleBtn;

    @FXML
    private TableView<?> strategyOutputResult;

    @FXML
    private ListView<Algorithm> strategyList;

    private int line = 0;

    private StringBuilder consoleOutputBuffer = new StringBuilder();

    @FXML
    void onPreviewRuleAction(ActionEvent event) {
        try {
            log("begin preview rule ");
            viewModel.onPreviewRuleAction();
            log("end preview rule ");
        } catch (Exception e) {
            error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    @FXML
    void onNewRuleAction(ActionEvent event){
        this.algorithm = null;
        this.viewModel.getRule().setValue("");
    }

    private void error(Exception e){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
        PrintWriter pw = new PrintWriter(bos);
        e.printStackTrace(pw);
        pw.flush();
        consoleOutputBuffer.append(bos.toString()).append("\n");
        line ++;
        flush();
    }

    private void log(String log){
        consoleOutputBuffer.append(log).append("\n");
        line++;
        flush();
    }

    private void flush(){
        strategyOutputConsole.setText(consoleOutputBuffer.toString());
    }

    @FXML
    void onSaveRuleAction(ActionEvent event) {
        try {
            if (algorithm == null) {
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("策略名称编辑");
                textInputDialog.setHeaderText("请输入策略名称:");
                textInputDialog.showAndWait();
                String name = textInputDialog.getEditor().getText();
                if (StringUtils.isBlank(name)){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "名称不能为空", ButtonType.CLOSE);
                    alert.showAndWait();
                    return;
                }
                viewModel.getName().setValue(name);
                log("save rule ["+name+"]");
                viewModel.onSaveAction();
            }else {
                viewModel.onUpdate(algorithm);
            }

        } catch (Exception e) {
            error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

}
