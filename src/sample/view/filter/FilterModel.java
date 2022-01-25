package sample.view.filter;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import sample.db.DatabaseManager;
import sample.model.StockInfo;
import sample.service.AppService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FilterModel implements ViewModel {

    private ListProperty stockList = new SimpleListProperty(FXCollections.observableArrayList() );
    private BooleanProperty filterCreating = new SimpleBooleanProperty(false);
    private BooleanProperty filterNew = new SimpleBooleanProperty(false);
    private BooleanProperty filterST = new SimpleBooleanProperty(false);
    private BooleanProperty filterScen = new SimpleBooleanProperty(false);

    void onRefresh(String searchCode) throws Exception {
        List<StockInfo> stockInfoList = DatabaseManager.getInstance().selectStockListByNameOrCode(searchCode);
        ObservableList<StockInfo> observableList = FXCollections.observableArrayList();
        observableList.addAll(stockInfoList);
        stockList.setValue(observableList);
    }

    void onPreviewRuleAction(String rule) throws Exception {
        List<StockInfo> stockInfoList = AppService.getInstasnce().executeRule(rule);
        boolean fc = filterCreating.getValue();
        boolean n = filterNew.getValue();
        boolean st = filterST.getValue();
        boolean scen = filterScen.getValue();
        stockInfoList = stockInfoList.stream().filter(s->{
            if (fc && s.getCode().startsWith("30")) {
                return false;
            }
            if (st && ( s.getName().startsWith("ST") || s.getName().startsWith("*ST") )) {
                return false;
            }
            if (scen && s.getCode().startsWith("688")){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        ObservableList<StockInfo> observableList = FXCollections.observableArrayList();
        observableList.addAll(stockInfoList);
        stockList.setValue(observableList);
    }

    public static void main(String[] args) {
        List<Integer> idList = new ArrayList<>();
        for (int i=0 ; i<10 ; i++){
            idList.add(i);
        }
        idList = idList.stream().filter(id -> id==2).collect(Collectors.toList());
        for (int i=0 ; i<idList.size() ; i++){
            System.out.println(idList.get(i));
        }
    }
}
