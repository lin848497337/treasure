package sample.view.strategy;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import sample.db.DatabaseManager;
import sample.model.Algorithm;
import sample.model.StockInfo;
import sample.service.AppService;

import java.util.List;

@Data
public class StrategyViewModel implements ViewModel {

    private ListProperty stockList = new SimpleListProperty(FXCollections.observableArrayList() );

    private ListProperty strategyList = new SimpleListProperty(FXCollections.observableArrayList());

    private StringProperty rule = new SimpleStringProperty("");
    private StringProperty name = new SimpleStringProperty("");

    void onPreviewRuleAction() throws Exception {
        List<StockInfo> stockInfoList = AppService.getInstasnce().executeRule(rule.getValue());
        ObservableList<StockInfo> observableList = FXCollections.observableArrayList();
        observableList.addAll(stockInfoList);
        stockList.setValue(observableList);
    }

    void onSaveAction() throws Exception {
        Algorithm algorithm = new Algorithm();
        algorithm.setName(name.getValue());
        algorithm.setRule(rule.getValue());
        DatabaseManager.getInstance().saveAlgorithm(algorithm);
        AppService.getInstasnce().strategyChangeProperty.setValue(System.currentTimeMillis());
        refreshStrategyList();
    }

    void onUpdate(Algorithm algorithm) throws Exception {
        algorithm.setRule(rule.getValue());
        DatabaseManager.getInstance().updateAlgorithm(algorithm);
        AppService.getInstasnce().strategyChangeProperty.setValue(System.currentTimeMillis());
    }

    void onDelete(Algorithm algorithm) throws Exception {
        DatabaseManager.getInstance().deleteAlgorithmById(algorithm.getId());
        refreshStrategyList();
        AppService.getInstasnce().strategyChangeProperty.setValue(System.currentTimeMillis());
    }

    void refreshStrategyList() throws Exception {
        List<Algorithm> algorithmList = DatabaseManager.getInstance().listAlgorithm();
        strategyList.setValue(FXCollections.observableArrayList(algorithmList));
    }
}
