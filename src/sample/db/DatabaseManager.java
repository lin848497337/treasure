package sample.db;

import org.apache.commons.collections.CollectionUtils;
import sample.model.Algorithm;
import sample.model.DailyAction;
import sample.model.DailyIndex;
import sample.model.StockInfo;
import sample.model.StockPoolInfo;
import sample.util.SQLBuilder;
import sample.util.StockPoolTypeEnum;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private static DatabaseManager instance = new DatabaseManager();
    private static final String JDBC_URL = "jdbc:sqlite:sqliteDB.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private LinkedBlockingQueue<List<DailyIndex>> asyncQueue = new LinkedBlockingQueue<>(2048);

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void init() throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = getConnection();
        try{
            prepareTable(conn);
        }finally {
            conn.close();
        }

        Thread t = new Thread(() -> {
            while (true){
                try{
                    List<DailyIndex> dailyIndices = asyncQueue.poll(2, TimeUnit.SECONDS);
                    if (dailyIndices == null){
                        continue;
                    }
                    saveDailyIndex(dailyIndices);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void saveStock(List<StockInfo> stockInfoList) throws Exception {
        Connection conn = getConnection();
        try{
            SQLBuilder<StockInfo> builder = new SQLBuilder<>(StockInfo.class);
            builder.truncate(conn);
            for (StockInfo stockInfo : stockInfoList){
                builder.insert(stockInfo, conn);
            }
        }finally {
            conn.close();
        }
    }

    public void truncateDailyIndex()throws Exception {
        Connection conn = getConnection();
        try{
            SQLBuilder<DailyIndex> builder = new SQLBuilder<>(DailyIndex.class);
            builder.truncate(conn);
        }finally {
            conn.close();
        }
    }

    private void  saveDailyIndex(List<DailyIndex> dailyIndices) throws Exception {
        Connection conn = getConnection();
        try{
            SQLBuilder<DailyIndex> builder = new SQLBuilder<>(DailyIndex.class);
            for (DailyIndex stockInfo : dailyIndices){
                builder.insert(stockInfo, conn);
            }
        }finally {
            conn.close();
        }
    }

    public void saveDailyIndexAsync(List<DailyIndex> dailyIndices) throws InterruptedException {
        asyncQueue.put(new ArrayList<>(dailyIndices));
    }

    public boolean isQueueEmpty(){
        return asyncQueue.isEmpty();
    }

    public void saveAction(DailyAction action) throws Exception {
        Connection conn = getConnection();
        try{
            SQLBuilder<DailyAction> builder = new SQLBuilder<>(DailyAction.class);
            builder.insert(action, conn);
        }finally {
            conn.close();
        }
    }

    public List<StockInfo> selectStockList() throws Exception {
        Connection conn = getConnection();
        try {
            List<StockInfo> stockInfoList = new SQLBuilder<>(StockInfo.class).selectAll(conn);
            fillStockPoolInfo(stockInfoList, conn);
            return stockInfoList;
        }finally {
            conn.close();
        }
    }

    public List<StockInfo> selectStockListByNameOrCode(String key) throws Exception {
        Connection conn = getConnection();
        try {
            List<StockInfo> stockInfoList = new SQLBuilder<>(StockInfo.class).select(conn, "code like '%"+key+"%' or name like '%"+key+"%'", null);
            fillStockPoolInfo(stockInfoList, conn);
            return stockInfoList;
        }finally {
            conn.close();
        }
    }

    private void fillStockPoolInfo(List<StockInfo> stockInfoList, Connection conn) throws Exception {
        SQLBuilder<StockPoolInfo> poolInfoSQLBuilder = new SQLBuilder<>(StockPoolInfo.class);
        for (StockInfo si : stockInfoList){
            List<StockPoolInfo> stockPoolInfoList = poolInfoSQLBuilder.select(conn, "code='"+si.getCode()+"'", null);
            if (!CollectionUtils.isEmpty(stockInfoList)){
                for (StockPoolInfo spi : stockPoolInfoList){
                    si.addPoolType(spi.getPoolType());
                }
            }
        }
    }

    public DailyIndex selectLastDailyIndex(int stockId) throws Exception {
        Connection conn = getConnection();
        try {
            return new SQLBuilder<>(DailyIndex.class).select(conn, "stock_info_id = "+stockId, "order by date desc limit 1").get(0);
        }finally {
            conn.close();
        }
    }


    public DailyAction seletLastCrawlerStockAction(int type)throws Exception{
        Connection conn = getConnection();
        try {
            List<DailyAction> lst = new SQLBuilder<>(DailyAction.class).select(conn, "type="+ type, "order by id desc limit 1");
            if (lst.size() > 0){
                return lst.get(0);
            }
        }finally {
            conn.close();
        }
        return null;
    }





    private void prepareTable(Connection conn) throws SQLException {
        List<SQLBuilder> tableList = new ArrayList<>();
        tableList.add(new SQLBuilder<>(StockInfo.class));
        tableList.add(new SQLBuilder<>(DailyIndex.class));
        tableList.add(new SQLBuilder<>(DailyAction.class));
        tableList.add(new SQLBuilder<>(StockPoolInfo.class));
        tableList.add(new SQLBuilder<>(Algorithm.class));

        Set<String> haveTableSet = allTables(conn);
        Statement ps = conn.createStatement();
        try{
            for (SQLBuilder builder : tableList){
                if (!haveTableSet.contains(builder.getTableName())){
                    String ddl = builder.generateDDL();
                    System.out.println("prepare table for : "+ddl);
                    ps.execute(ddl);
                }
            }
        }finally {
            ps.close();
        }
    }

    private Set<String> allTables(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet resultSet = dbmd.getTables(null, null, null, null);
        Set<String> allTablesSet = new HashSet<>();
        while (resultSet.next()) {
            String strTableName = resultSet.getString("TABLE_NAME");
            allTablesSet.add(strTableName.toLowerCase());
        }
        return allTablesSet;
    }


    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        return connection;
    }

    public List<DailyIndex> listAllDailyIndex() throws Exception{
        Connection con = getConnection();
        try{
            SQLBuilder<DailyIndex> sqlBuilder = new SQLBuilder<>(DailyIndex.class);
            return sqlBuilder.selectAll(con);
        }finally {
            con.close();
        }
    }

    public List<StockInfo> selectByIds(List<Integer> idList) throws Exception{
        if (CollectionUtils.isEmpty(idList)){
            return Collections.EMPTY_LIST;
        }
        Connection con = getConnection();
        try{
            SQLBuilder<StockInfo> sqlBuilder = new SQLBuilder<>(StockInfo.class);
            StringBuilder sb = new StringBuilder();
            sb.append("id in (");
            for (Integer id : idList){
                sb.append(id).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            List<StockInfo> stockInfoList = sqlBuilder.select(con, sb.toString(), null);
            fillStockPoolInfo(stockInfoList, con);
            return stockInfoList;
        }finally {
            con.close();
        }
    }

    public void addToPool(Integer stockId, StockPoolTypeEnum poolTypeEnum) throws Exception {
        StockInfo stockInfo = selectByIds(Arrays.asList(stockId)).get(0);
        StockPoolInfo poolInfo = new StockPoolInfo();
        poolInfo.setCode(stockInfo.getCode());
        poolInfo.setStockInfoId(stockId);
        poolInfo.setName(stockInfo.getName());
        poolInfo.setPoolType(poolTypeEnum.getValue());
        SQLBuilder<StockPoolInfo> poolInfoSQLBuilder = new SQLBuilder<>(StockPoolInfo.class);
        Connection conn = getConnection();
        try{
            poolInfoSQLBuilder.insert(poolInfo, conn);
        }finally {
            conn.close();
        }
    }

    public void delFromPool(Integer stockId) throws Exception {
        SQLBuilder<StockPoolInfo> poolInfoSQLBuilder = new SQLBuilder<>(StockPoolInfo.class);
        Connection conn = getConnection();
        try{
            poolInfoSQLBuilder.delete(conn, "stock_info_id = "+stockId);
        }finally {
            conn.close();
        }
    }

    public List<StockInfo> selectPool() throws Exception {
        SQLBuilder<StockPoolInfo> poolInfoSQLBuilder = new SQLBuilder<>(StockPoolInfo.class);
        Connection conn = getConnection();
        try{
            List<StockPoolInfo> stockPoolInfoList = poolInfoSQLBuilder.selectAll(conn);

            List<Integer> idList = new ArrayList<>();
            for (StockPoolInfo poolInfo : stockPoolInfoList){
                idList.add(poolInfo.getStockInfoId());
            }

            return selectByIds(idList);

        }finally {
            conn.close();
        }
    }

    public List<DailyIndex> selectDailyIndexByStockId(Integer stockId) throws Exception {
        SQLBuilder sqlBuilder = new SQLBuilder(DailyIndex.class);
        Connection conn = getConnection();
        try{
            List<DailyIndex> stockPoolInfoList = sqlBuilder.select(conn, "stock_info_id="+stockId, null);

            return stockPoolInfoList;

        }finally {
            conn.close();
        }
    }

    public List<Algorithm> listAlgorithm() throws Exception {
        SQLBuilder sqlBuilder = new SQLBuilder(Algorithm.class);
        Connection conn = getConnection();
        try{
            List<Algorithm> stockPoolInfoList = sqlBuilder.select(conn, null, null);
            return stockPoolInfoList;

        }finally {
            conn.close();
        }
    }

    public void saveAlgorithm(Algorithm algorithm) throws Exception {
        SQLBuilder<Algorithm> sqlBuilder = new SQLBuilder<>(Algorithm.class);
        Connection conn = getConnection();
        try{
            sqlBuilder.insert(algorithm, conn);
        }finally {
            conn.close();
        }
    }

    public void updateAlgorithm(Algorithm algorithm) throws Exception {
        SQLBuilder<Algorithm> sqlBuilder = new SQLBuilder<>(Algorithm.class);
        Connection conn = getConnection();
        try{
            sqlBuilder.update(algorithm, conn);
        }finally {
            conn.close();
        }
    }

    public void deleteAlgorithmById(int id)throws Exception{
        SQLBuilder<Algorithm> sqlBuilder = new SQLBuilder<>(Algorithm.class);
        Connection conn = getConnection();
        try{
            sqlBuilder.delete(conn, "id="+id);
        }finally {
            conn.close();
        }
    }


    public static void main(String args[]) throws Exception {
        DatabaseManager.getInstance().init();
        Connection con = DatabaseManager.getInstance().getConnection();
        SQLBuilder<StockInfo> sqlBuilder = new SQLBuilder<>(StockInfo.class);
        List<StockInfo> stockInfoList = sqlBuilder.select(con, "name='华东数控'", null);
        System.out.println(stockInfoList.get(0).getId());
        con.close();
    }
}
