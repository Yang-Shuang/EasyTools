package com.yang.utils;

import com.intellij.openapi.project.Project;
import com.yang.bean.BugBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by
 * yangshuang on 2020/1/6.
 */
public class ZenTaoDataManager {

    //    private final static String BUG_URL = "http://pms.huimai365.com/index.php?m=bug&f=browse&t=html&productID=1&branch=&browseType=unclosed&param=0&orderBy=&recTotal=2000&recPerPage=2000&pageID=1";
    private final static String BUG_URL = "http://pms.huimai365.com/index.php?m=bug&f=browse&t=html&productID=1&branch=&browseType=unclosed&param=0&orderBy=&recTotal=2000&recPerPage=2000&pageID=1";
    private final static String LOGIN_URL = "http://pms.huimai365.com/index.php?m=user&f=login";

    private static String zp = "";
    private static String za = "";

    private static String zentaosid = "";

    private static String account = "";
    private static String name = "";
    private static String password = "";

    private static String expiredStr = "location='/index.php?m=user&f=login";

    private static ZenTaoDataManager manager;


    public static ZenTaoDataManager getManager() {
        if (manager == null) manager = new ZenTaoDataManager();
        return manager;
    }


    public static interface DataListener {
        void onGetData(List<BugBean> bugBeans);
    }


    private Timer mTimer;
    private Task task;
    private boolean isStart = false;

    private List<DataListener> listeners;
    private List<BugBean> lastdata;
    private List<Integer> myBugs;

    private ZenTaoDataManager() {
        listeners = new ArrayList<>();
        lastdata = new ArrayList<>();
        myBugs = new ArrayList<>();
    }

    public void addListener(DataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public String getName() {
        return name;
    }

    public String getTaken() {
        return zp;
    }

    public String getAccount() {
        return za;
    }

    public String getAccountInfo() {
        return "Account : " + za + "  Name : " + name + " Token : " + zp;
    }

    public void clearMessageList() {
        myBugs.clear();
        MessageUtils.toastMsg("通知历史列表已清除");
    }

    public void setUserLogin(String a, String p, Project project) {
        account = a;
        password = p;
        zentaosid = "";
        zp = "";
        za = "";
        lastdata.clear();
        for (DataListener listener : listeners) {
            listener.onGetData(lastdata);
        }

        refreshZP();
        if (zp != null && !zp.equals("")) {
            SettingsManager manager = SettingsManager.get();
            manager.setData("account", a);
            manager.setData("password", p);
            start();
        } else {
            MessageUtils.toastMsg("登录禅道失败");
        }
    }

    public void initData(Project project) {
        SettingsManager manager = SettingsManager.get();
        account = manager.getData("account");
        password = manager.getData("password");
        zp = manager.getData("zp");
        za = manager.getData("za");
        zentaosid = manager.getData("zentaosid");
    }

    public synchronized void start() {
        if (account == null || account.equals("") || password == null || password.equals("")) return;

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (task != null) {
            task.cancel();
        }

        mTimer = new Timer();
        task = new Task();
        mTimer.schedule(task, 0, 1000 * 120);
        isStart = true;
    }

    private class Task extends TimerTask {

        @Override
        public void run() {
            getBugList();
//            test();
        }
    }

    public List<BugBean> getLastdata() {
        return lastdata;
    }

    private void getBugList() {
        String bugHtml = syncGetHtml();
        if (bugHtml == null || "".equals(bugHtml)) return;
        if (bugHtml.contains(expiredStr)) {
            zp = null;
            za = null;
            zentaosid = null;
            bugHtml = syncGetHtml();
        }
        Document document = Jsoup.parse(bugHtml);

        name = document.body().getElementById("userMenu").child(0).text().trim();
        MessageUtils.log("name : " + "[" + name + "]");
        Element table = document.body().getElementById("bugList");
        Element tbody = table.child(1);

        Elements trs = tbody.children();
        lastdata.clear();
        for (int i = 0; i < trs.size(); i++) {
            if (trs.get(i).nodeName().equals("tr")) {
                Elements tds = trs.get(i).children();

                //<td class='cell-id bug-resolved strong text-left'> <input type='checkbox' name='bugIDList[]' value='7155' /> <a href='/index.php?m=bug&f=view&bugID=7155'>7155</a> </td>
                int bugId = Integer.parseInt(tds.get(0).child(0).attr("value"));
                //<td><span class='severity2'>2</span></td>
                int level = Integer.parseInt(tds.get(1).child(0).text());
                // <td class='text-left'> <span class='confirm1'>[已确认]</span> <span title='平台' class='label label-branch label-badge'>IOS</span> <a href='/index.php?m=bug&f=iew&bugID=7155' style='color: '>【线上】惠买庄园点击任务去社区点赞，跳转到社区 ，点击首页按钮 推荐顶部显示空白</a> </td>
                String status = tds.get(3).child(0).text();
                String title = tds.get(3).attr("title");

                String state = tds.get(4).text();

                String creator = tds.get(5).text();
                String appoint = tds.get(6).text();
                String fix = tds.get(7).text();

                BugBean bugBean = new BugBean(bugId, title, level, state, status, creator, appoint, fix);

                lastdata.add(bugBean);
                if (appoint != null && name != null && appoint.equals(name) && !myBugs.contains(bugId)) {
                    myBugs.add(bugId);
                    MessageUtils.toastMsg("禅道Bug ：" + title, true);
                }
            }
        }
        MessageUtils.log("当前未关闭bug ： " + lastdata.size() + " 个；");
        for (DataListener listener : listeners) {
            listener.onGetData(lastdata);
        }
    }

    private int size = 1;
    private int max = 30;

    private void test() {
        for (DataListener listener : listeners) {
            List<BugBean> beans = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                BugBean bugBean = new BugBean();
                bugBean.setId(i);
                bugBean.setLevel(9);
                bugBean.setName("" + (new Random().nextInt(99999) + 100000));
                beans.add(bugBean);
            }
            listener.onGetData(beans);
            if (size >= 30) {
                size = 1;
            } else {
                size++;
            }
        }
    }


    private String syncGetHtml() {
        if (zp == null || "".equals(zp))
            refreshZP();

        if (zp == null || zp.equals("")) return null;
        String cookie = "keepLogin=on; lang=zh-cn; theme=default; za=" + za + "; zp=" + zp + "; zentaosid=" + zentaosid + "; " + "lastProduct=1; bugModule=0; preBranch=0; preProductID=1; qaBugOrder=id_desc; pagerBugBrowse=2000";

        HashMap<String, Object> data = HttpUtils.syncRequest(BUG_URL, null, "GET", null, cookie);

        return (String) data.get(HttpUtils.KEY_CONTENT);
    }

    private void refreshZP() {
        boolean login = false;
        try {
            HashMap<String, Object> data = HttpUtils.syncRequest(LOGIN_URL, null, "GET", null, null);
            if (data != null && data.get(HttpUtils.KEY_HEADER) != null) {
                List<String> headerList = (List<String>) data.get(HttpUtils.KEY_HEADER);
                for (String head : headerList) {
                    String[] heads = head.split(HttpUtils.KEY_SPLIT);
                    String key = heads[0];
                    String value = heads[1];
                    boolean get = false;
                    if (key.equalsIgnoreCase("set-cookie")) {
                        if (value.contains("zentaosid")) {
                            String[] cookies = value.split(";");
                            for (String c : cookies) {
                                if (c.contains("zentaosid")) {
                                    zentaosid = c.split("=")[1];
                                    get = true;
                                    break;
                                }
                            }

                        }
                    }
                    if (get) {
                        break;
                    }
                }

            }
            MessageUtils.log("zentaosid : " + zentaosid);
            String loginCookie = "zentaosid=" + zentaosid + "; lang='zh-cn'; keepLogin='on'";
            HashMap<String, String> params = new HashMap<>();
            params.put("account", account);
            params.put("password", password);
            params.put("keepLogin", "on");
            params.put("referer", "http://pms.huimai365.com/index.php?m=my&f=index");

            HashMap<String, Object> loginData = HttpUtils.syncRequest(LOGIN_URL, params, "POST", null, loginCookie);

            if (loginData != null && loginData.get(HttpUtils.KEY_HEADER) != null) {
                List<String> loginHeaderList = (List<String>) loginData.get(HttpUtils.KEY_HEADER);
                for (String loginHead : loginHeaderList) {
                    String[] loginHeads = loginHead.split(HttpUtils.KEY_SPLIT);
                    String key = loginHeads[0];
                    String value = loginHeads[1];
                    if (key.equalsIgnoreCase("set-cookie")) {
                        if (value.contains("zp")) {
                            String[] cookies = value.split(";");
                            for (String c : cookies) {
                                if (c.contains("zp")) {
                                    zp = c.split("=")[1];
                                }
                            }

                        }
                        if (value.contains("za")) {
                            String[] cookies = value.split(";");
                            for (String c : cookies) {
                                if (c.contains("za")) {
                                    za = c.split("=")[1];
                                }
                            }

                        }
                    }
                }
                MessageUtils.log("zp : " + zp);
                MessageUtils.log("za : " + za);
                if (zp != null && za != null && !zp.equals("") && !za.equals("")) {
                    SettingsManager.get().setData("zp", zp);
                    SettingsManager.get().setData("za", za);
                    SettingsManager.get().setData("zentaosid", zentaosid);
                }
            }
        } catch (Exception e) {
            login = false;
        }
    }

}

