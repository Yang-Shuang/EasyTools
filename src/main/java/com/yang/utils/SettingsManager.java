package com.yang.utils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Created by
 * yangshuang on 2020/1/8.
 */
public class SettingsManager {

    private static SettingsManager mUtils;
    private static String DBPATH;

    private static final String CREATE_DB_SQL = "DROP TABLE IF EXISTS settings;CREATE TABLE settings ('settings_key' TEXT (100) PRIMARY KEY UNIQUE NOT NULL, 'settings_value' TEXT (2000));";

    public static SettingsManager get() {
        if (mUtils == null) mUtils = new SettingsManager();
        return mUtils;
    }

    private SettingsManager() {
        init();
    }


    private void init() {

        FileUtil.initConfig();
        DBPATH = new File(FileUtil.getConfigFilePath(), File.separator + "assets" + File.separator + "db" + File.separator + "settings.db").getAbsolutePath();
        MessageUtils.log(DBPATH);
    }


    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + DBPATH);
        return conn;
    }

    public synchronized void setData(String key, String value) {
        try {

            Connection mConnection = getConnection();
            Statement mStatement = mConnection.createStatement();

            if (isExist(mStatement, key)) {
                update(mStatement, key, value);
            } else {
                insert(mStatement, key, value);
            }
            mStatement.close();
            mConnection.close();
        } catch (Exception e) {
            MessageUtils.log(e.getMessage());
        }

    }

    public synchronized String getData(String key) {
        String value = null;
        try {
            Connection mConnection = getConnection();
            Statement mStatement = mConnection.createStatement();

            String v = query(mStatement, key);
            if (v != null) {
                value = v;
            }
            mStatement.close();
            mConnection.close();
        } catch (Exception e) {
            MessageUtils.log(e.getMessage());
        }
        return value;
    }

    private void insert(Statement mStatement, String key, String value) {
        if (key == null || value == null) return;
        String sql = "insert into settings values('" + key + "','" + value + "')";
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            MessageUtils.log(e.getMessage());
        }
    }

    private void delete(Statement mStatement, String key) {
        if (key == null) return;
        String sql = "DELETE FROM settings where settings_key = '" + key + "'";
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            MessageUtils.log(e.getMessage());
        }
    }

    private String query(Statement mStatement, String key) {
        String sql = "select * from settings WHERE settings_key = '" + key + "'";
        String value = null;
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                ResultSet resultSet = mStatement.executeQuery(sql);
                while (resultSet.next()) {
                    value = resultSet.getString("settings_value");
                }
            }
        } catch (SQLException e) {
            MessageUtils.log(e.getMessage());
        }
        return value;
    }

    private void update(Statement mStatement, String key, String value) {
        if (key == null || value == null) return;
        String sql = "UPDATE settings SET settings_value = '" + value + "' where settings_key = '" + key + "'";
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            MessageUtils.log(e.getMessage());
        }
    }

    private boolean isExist(Statement mStatement, String key) {
        return query(mStatement, key) != null;
    }
}
