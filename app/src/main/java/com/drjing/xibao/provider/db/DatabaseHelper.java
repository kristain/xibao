package com.drjing.xibao.provider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.library.third.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.provider.db.entity.BaseEntity;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库访问帮助类
 * Created by kristain on 15/12/17.
 *
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static String databaseName = "default_database";
    private static int databaseVersion = 1;
    private static DatabaseHelper instance = null;
    private static String TAG = DatabaseHelper.class.getSimpleName();
    private Map<String, Dao> daoMap = new HashMap<String, Dao>();
    private static List<Class<? extends BaseEntity>> table = new ArrayList<Class<? extends BaseEntity>>();

    /**
     * 对外实例化对象不采用该构造方法
     * @param context
     */
    @Deprecated
    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    /**
     * 实例化对象
     * @param dbName 数据库名称
     * @param version  数据库版本
     * @return
     */
    public static synchronized DatabaseHelper gainInstance(Context context,String dbName, int version) {
        if (instance == null) {
            databaseName = dbName;
            databaseVersion = version;
            //会隐式调用public DatabaseHelper(Context context){}
            instance = com.android.library.third.ormlite.android.apptools.OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return instance;
    }

    /**
     * 注册表实体
     * @param entity 表实体
     */
    public static void registerTables(Class<? extends BaseEntity> entity){
        table.add(entity);
    }

    /**
     * 创建表
     * @param entity 实体
     */
    public void createTable(Class<? extends BaseEntity> entity) {
        try {
            com.android.library.third.ormlite.table.TableUtils.createTableIfNotExists(getConnectionSource(), entity);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to create table-->"+entity.getSimpleName(),e);
        }
    }

    /**
     * 清除表数据
     * @param entity 实体
     */
    public void clearTableData(Class<? extends BaseEntity> entity) {
        try {
            com.android.library.third.ormlite.table.TableUtils.clearTable(getConnectionSource(), entity);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to clear table data for -->"+entity.getSimpleName(),e);
        }
    }

    /**
     * 删除表
     * @param entity 实体
     */
    public void dropTable(Class<? extends BaseEntity> entity) {
        try {
            com.android.library.third.ormlite.table.TableUtils.dropTable(getConnectionSource(), entity, true);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to drop table-->"+entity.getSimpleName(),e);
        }
    }


    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws SQLException
     */
    public com.android.library.third.ormlite.support.DatabaseConnection getConnection() throws SQLException{
        com.android.library.third.ormlite.support.DatabaseConnection connection = null;
        if(isOpen()){
            connection = getConnectionSource().getReadWriteConnection();
        }else{
            connection = new com.android.library.third.ormlite.android.AndroidDatabaseConnection(getWritableDatabase(), true);
        }
        return connection;
    }


    /**
     * 关闭数据库连接
     */
    public void closeConnection(com.android.library.third.ormlite.support.DatabaseConnection connection) {
        try {
            if (connection != null) {
                if(!connection.isClosed()){
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "关闭数据库失败，原因："+e.getMessage());
        }
    }

    /**
     * 提交事务
     * @param connection 数据库连接
     * @param savePoint 事务点
     */
    public void commit(com.android.library.third.ormlite.support.DatabaseConnection connection,Savepoint savePoint){
        try {
            connection.commit(savePoint);
        } catch (SQLException e) {
            Log.e(TAG, "提交事务失败，原因："+e.getMessage());
        }
    }

    /**
     * 回滚事务
     * @param connection 数据库连接
     * @param savePoint 事务点
     */
    public void rollback(com.android.library.third.ormlite.support.DatabaseConnection connection,Savepoint savePoint){
        try {
            connection.rollback(savePoint);
        } catch (SQLException e) {
            Log.e(TAG, "回滚事务失败，原因："+e.getMessage());
        }
    }

    /**
     * 获取Dao
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public synchronized <D extends com.android.library.third.ormlite.dao.Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        com.android.library.third.ormlite.dao.Dao dao = null;
        try {
            String keyClassName = clazz.getSimpleName();
            if (daoMap.containsKey(keyClassName)) {
                dao = daoMap.get(keyClassName);
            }
            if (dao == null) {
                dao = super.getDao(clazz);
                daoMap.put(keyClassName, dao);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Unable to getDao", e);
            throw e;
        }
        return (D) dao;
    }

    /**
     * 释放数据库连接
     */
    public void releaseAll() {
        if (instance != null) {
            com.android.library.third.ormlite.android.apptools.OpenHelperManager.releaseHelper();
            instance = null;
        }
    }

    /**
     * 释放资源
     */
    @SuppressWarnings("unused")
    @Override
    public void close() {
        super.close();
        for (String key : daoMap.keySet()) {
            com.android.library.third.ormlite.dao.Dao dao = daoMap.get(key);
            dao = null;
        }
    }

    /**
     * 创建数据库时执行，初始化表
     */
    @Override
    public void onCreate(SQLiteDatabase database, com.android.library.third.ormlite.support.ConnectionSource connectionSource) {
        try {
            for (Class<? extends BaseEntity> entity : table) {
                Log.e(TAG, "初始化表entity:"+entity.getName());
                com.android.library.third.ormlite.table.TableUtils.createTableIfNotExists(connectionSource, entity);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Unable to create datbases", e);
        }
    }

    /**
     * 更新数据库时执行， 历史数据会被清空，残留这个bug，待改进
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, com.android.library.third.ormlite.support.ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            //先清表
            for (Class<? extends BaseEntity> entity : table) {
                com.android.library.third.ormlite.table.TableUtils.dropTable(connectionSource, entity, true);
            }
            //重建表
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG,"Unable to upgrade database from version " + oldVersion+ " to new " + newVersion, e);
        }
    }
}
