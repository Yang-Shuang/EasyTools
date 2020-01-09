package com.yang.utils;

import com.yang.bean.BugBean;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * yangshuang on 2020/1/6.
 */
public class BugDBHelper {

    private static BugDBHelper mHelper;

    private static SQLiteDataSource mSQLiteDataSource;

    private Connection mConnection;
    private Statement mStatement;

    public static BugDBHelper getInstance() {
        if (mHelper == null) mHelper = new BugDBHelper();
        return mHelper;
    }

    private BugDBHelper() {
        String dbPath = File.separator + "assets" + File.separator + "db" + File.separator + "bug.db";
        String path = this.getClass().getResource(dbPath).getPath();
        mSQLiteDataSource = new SQLiteDataSource();
        mSQLiteDataSource.setUrl("jdbc:sqlite:" + path);

    }

    public synchronized void setData(BugBean bugBean, boolean autoClose) {
        openDB();
        if (isExist(bugBean)) {
            update(bugBean);
        } else {
            insert(bugBean);
        }
        if (autoClose) {
            closeDB();
        }
    }

    public synchronized void setData(List<BugBean> bugBean) {
        openDB();
        for (BugBean b : bugBean) {
            setData(b, false);
        }
        closeDB();
    }

    public synchronized BugBean getData(int id, boolean autoClose) {
        openDB();
        List<BugBean> bugBeans = getData("where bid = " + id, false);
        BugBean bugBean = null;
        if (bugBeans.size() > 0) {
            bugBean =  bugBeans.get(0);
        }
        if (autoClose) {
            closeDB();
        }
        return bugBean;
    }

    public synchronized List<BugBean> getData(String condition, boolean autoClose) {
        openDB();
        ArrayList<BugBean> bugBeans = query(condition);
        if (autoClose) {
            closeDB();
        }
        return bugBeans;
    }

    private void insert(BugBean bugBean) {
        if (bugBean == null || bugBean.getName() == null) return;
        String sql = "insert into buglist values(" + bugBean.getId()
                + ",'" + bugBean.getName()
                + "'," + bugBean.getLevel()
                + ",'" + bugBean.getState()
                + "','" + bugBean.getStatus()
                + "','" + bugBean.getCreator()
                + "','" + bugBean.getAppoint()
                + "','" + bugBean.getFix()
                + "')";
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void delete(BugBean bugBean) {
        if (bugBean == null || bugBean.getName() == null) return;
        String sql = "DELETE FROM buglist where bid = " + bugBean.getId();
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<BugBean> query(String condition) {
        String sql = "select * from buglist";

        if (condition != null && !"".equals(condition)) {
            sql = sql + " " + condition;
        }
        ArrayList<BugBean> bugBeans = null;
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                ResultSet resultSet = mStatement.executeQuery(sql);
//                resultSet.beforeFirst();
                bugBeans = new ArrayList<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("bid");
                    String name = resultSet.getString("bug_name");
                    int level = resultSet.getInt("bug_level");
                    String state = resultSet.getString("bug_state");
                    String status = resultSet.getString("bug_statu");
                    String creator = resultSet.getString("bug_creator");
                    String apoint = resultSet.getString("bug_appoint");
                    String fix = resultSet.getString("bug_fix");
                    BugBean bugBean = new BugBean(id, name, level, state, status, creator, apoint, fix);
                    bugBeans.add(bugBean);
//                    System.out.println(bugBean.toPrintString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bugBeans;
    }

    private void update(BugBean bugBean) {
        if (bugBean == null || bugBean.getName() == null) return;
        String sql = "UPDATE buglist SET bug_name = '" + bugBean.getName()
                + "',bug_level = " + bugBean.getLevel()
                + ",bug_state = '" + bugBean.getState()
                + "',bug_statu = '" + bugBean.getStatus()
                + "',bug_creator = '" + bugBean.getCreator()
                + "',bug_appoint = '" + bugBean.getAppoint()
                + "',bug_fix = '" + bugBean.getFix()
                + "' where bid = " + bugBean.getId();
        try {
            if (mStatement != null && !mStatement.isClosed()) {
                mStatement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isExist(BugBean bugBean) {
        return getData(bugBean.getId(), false) != null;
    }

    private synchronized void openDB() {
        try {
            if (mConnection == null || mConnection.isClosed()) {
                mConnection = mSQLiteDataSource.getConnection("", "");
            }

            if ((mConnection != null && !mConnection.isClosed()) && (mStatement == null || mStatement.isClosed())) {
                mStatement = mConnection.createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void closeDB() {
        try {
            if (mStatement != null) {
                mStatement.close();
                mStatement = null;
//                System.out.println("close Statement");
            }
            if (mConnection != null) {
                mConnection.close();
                mConnection = null;
//                System.out.println("close Connection");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
