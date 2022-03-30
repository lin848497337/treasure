package sample.view.sql;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import sample.db.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

@Data
public class SqlViewModel implements ViewModel {

    private ListProperty resultList = new SimpleListProperty(FXCollections.observableArrayList() );

    public void executeSQLBtn(String sql) throws Exception{
        Connection conn = DatabaseManager.getInstance().getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        ObservableList<Map> list = FXCollections.observableArrayList();
        while (rs.next()){
            Map<String, String> map = new TreeMap<>();
            for(int i=0 ; i<count ; i++){
                Object v = rs.getObject(i+1);
                map.put(metaData.getColumnName(i+1), String.valueOf(v));
            };
            list.add(map);
        }
        resultList.setValue(list);
    }
}
