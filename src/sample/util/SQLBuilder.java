package sample.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import sample.db.ColumnDefine;
import sample.db.TableDefine;
import sample.model.StockInfo;
import sample.util.convert.DecimalConvert;
import sample.util.convert.TimestampConvert;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLBuilder<T> {
    private Class<T> cls;
    private String tableName;
    private static List<TypeConvert> converts = new CopyOnWriteArrayList<>();

    static {
        converts.add(new DecimalConvert());
        converts.add(new TimestampConvert());
    }

    public SQLBuilder(Class<T> cls) {
        this.cls = cls;
        TableDefine def = cls.getAnnotation(TableDefine.class);
        if (def == null){
            throw new IllegalArgumentException("table define class should annotation by TableDefine");
        }
        tableName = def.value();
    }

    public String getTableName(){
        return tableName;
    }

    public List<T> selectAll(Connection conn) throws Exception{
        return select(conn, null, null);
    }

    public void truncate(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("DELETE FROM  "+tableName);
        statement.close();
    }

    public List<T> select(Connection conn, String whereCondition, String orderBy) throws Exception {
        List<Field> allFieldList = listAllField();
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            String cv = f.getName();
            String columnName = camelTo_(cv);
            sb.append(columnName).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ").append(tableName);
        if (StringUtils.isNotBlank(whereCondition)){
            sb.append(" where ").append(whereCondition);
        }
        if (StringUtils.isNotBlank(orderBy)){
            sb.append(" ").append(orderBy);
        }
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(sb.toString());
        List<T> list = new ArrayList<>();
        while (rs.next()){
            T t = read(rs, cls);
            list.add(t);
        }
        rs.close();
        statement.close();
        return list;
    }

    public int delete(Connection conn, String whereCondition) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(tableName);
        if (StringUtils.isNotBlank(whereCondition)){
            sb.append(" where ").append(whereCondition);
        }
        Statement statement = conn.createStatement();
        return statement.executeUpdate(sb.toString());
    }

    private <T>  T read(ResultSet rs, Class<T> cls) throws Exception {
        List<Field> fs = listAllField();
        T t = cls.newInstance();
        for (Field f : fs){
            f.setAccessible(true);
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            String name = f.getName();
            String fieldName = SQLBuilder.camelTo_(name);
            Object value = rs.getObject(fieldName);
            for (TypeConvert tc : converts){
                if (tc.isSupport(f.getType())){
                    value = tc.convert(value);
                }
            }
            f.set(t, value);
        }
        return t;
    }

    public void update(Object o, Connection conn) throws Exception {
        List<Field> allFieldList = listAllField();
        StringBuilder sb = new StringBuilder();
        sb.append("update  ").append(tableName).append(" set ");
        Field pk = null;
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                pk = f;
                continue;
            }
            String cv = f.getName();
            String columnName = camelTo_(cv);
            sb.append(columnName).append("=?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" where ").append(camelTo_(pk.getName())).append("=?");
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        int idx = 1;
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                continue;
            }
            f.setAccessible(true);
            ps.setObject(idx++, f.get(o));
        }
        pk.setAccessible(true);
        ps.setObject(idx, pk.get(o));
        ps.executeUpdate();
        ps.close();
    }

    public void batchInsert(List<T> objectList, Connection conn) throws Exception {
        if (CollectionUtils.isEmpty(objectList)){
            return;
        }
        List<Field> allFieldList = listAllField();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into  ").append(tableName).append("(");
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                continue;
            }
            String cv = f.getName();
            String columnName = camelTo_(cv);
            sb.append(columnName).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values");
        for (Object o : objectList){
            sb.append("(");
            for (Field f : allFieldList){
                ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
                if (cd == null){
                    continue;
                }
                if (cd.autoIncrementPk()){
                    continue;
                }
                sb.append("?").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("),");
        }
        sb.deleteCharAt(sb.length() - 1);
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        int idx = 1;
        for (Object o : objectList) {
            for (Field f : allFieldList) {
                ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
                if (cd == null) {
                    continue;
                }
                if (cd.autoIncrementPk()) {
                    continue;
                }
                f.setAccessible(true);
                ps.setObject(idx++, f.get(o));
            }
        }
        ps.executeUpdate();
        ps.close();
    }

    public void insert(Object o, Connection conn) throws Exception {
        List<Field> allFieldList = listAllField();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into  ").append(tableName).append("(");
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                continue;
            }
            String cv = f.getName();
            String columnName = camelTo_(cv);
            sb.append(columnName).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values(");
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                continue;
            }
            sb.append("?").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        int idx = 1;
        for (Field f : allFieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            if (cd.autoIncrementPk()){
                continue;
            }
            f.setAccessible(true);
            ps.setObject(idx++, f.get(o));
        }
        ps.executeUpdate();
        ps.close();
    }

    private List<Field> listAllField(){
        Field[]fs = cls.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>();
        for (Field f : fs){
            fieldList.add(f);
        }
        Class supperCls = cls.getSuperclass();
        do {
            if (supperCls != null){
                Field [] sfs = supperCls.getDeclaredFields();
                for (Field f : sfs){
                    fieldList.add(f);
                }
            }
            supperCls = supperCls.getSuperclass();
        }while (supperCls != null);
        return fieldList;
    }

    public String generateDDL(){
        List<Field> fieldList = listAllField();
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ").append(tableName).append("(");
        for (Field f : fieldList){
            ColumnDefine cd = f.getAnnotation(ColumnDefine.class);
            if (cd == null){
                continue;
            }
            String cv = f.getName();
            String columnName = camelTo_(cv);
            sb.append(columnName).append(" ");
            if (cd.autoIncrementPk()){
                sb.append("INTEGER PRIMARY KEY");
            }else {
                sb.append(cd.define());
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(new SQLBuilder(StockInfo.class).generateDDL());
    }


    public static String camelTo_(String camel){
        StringBuilder sb = new StringBuilder();
        int diff = 'A' - 'a';
        for (int i=0 ; i<camel.length() ; i++){
            char c = camel.charAt(i);
            if (c >= 'A' && c <= 'Z'){
                sb.append("_").append((char)(c - diff));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

