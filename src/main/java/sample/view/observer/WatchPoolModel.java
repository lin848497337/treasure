package sample.view.observer;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import sample.db.DatabaseManager;
import sample.model.StockInfo;

import java.util.List;

@Data
public class WatchPoolModel implements ViewModel {

    private ListProperty stockList = new SimpleListProperty(FXCollections.observableArrayList() );

    void onRefresh(String searchCode) throws Exception {
        List<StockInfo> stockInfoList = DatabaseManager.getInstance().selectPool();
        ObservableList<StockInfo> observableList = FXCollections.observableArrayList();
        observableList.addAll(stockInfoList);
        stockList.setValue(observableList);
    }
}
