package com.yang.test;


import com.yang.bean.BugBean;
import com.yang.utils.BugDBHelper;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by
 * yangshuang on 2019/7/25.
 */
public class Test {

    public static void main(String[] args) {
//        String bugHtml = ZenTaoDataManager.syncGetHtml();
//        Document document = Jsoup.parse(bugHtml);
//        Element table = document.body().getElementById("bugList");
//        Element tbody = table.child(1);
//
//        Elements trs = tbody.children();
//        List<BugBean> bugBeans = new ArrayList<>();
//        for (int i = 0; i < trs.size(); i++) {
//            if (trs.get(i).nodeName().equals("tr")){
//                Elements tds = trs.get(i).children();
//
//                //<td class='cell-id bug-resolved strong text-left'> <input type='checkbox' name='bugIDList[]' value='7155' /> <a href='/index.php?m=bug&f=view&bugID=7155'>7155</a> </td>
//                int bugId = Integer.parseInt(tds.get(0).child(0).attr("value"));
//                //<td><span class='severity2'>2</span></td>
//                int level = Integer.parseInt(tds.get(1).child(0).text());
//                // <td class='text-left'> <span class='confirm1'>[已确认]</span> <span title='平台' class='label label-branch label-badge'>IOS</span> <a href='/index.php?m=bug&f=iew&bugID=7155' style='color: '>【线上】惠买庄园点击任务去社区点赞，跳转到社区 ，点击首页按钮 推荐顶部显示空白</a> </td>
//                String status = tds.get(3).child(0).text();
//                String name = tds.get(3).attr("title");
//
//                String state = tds.get(4).text();
//
//                String creator = tds.get(5).text();
//                String appoint = tds.get(6).text();
//                String fix = tds.get(7).text();
//
//                BugBean bugBean = new BugBean(bugId,name,level,state,status,creator,appoint,fix);
//                bugBeans.add(bugBean);
//                System.out.println(bugBean.toPrintString());
//            }
//        }
//
//        System.out.println(bugHtml);
//        testWindow();
        testAudio();
    }

    private static void testAudio(){
        String p = File.separator + "assets" + File.separator + "tips.mp3";
        String path = Test.class.getResource(p).getPath();

        try {

            System.out.println("play");

            //声明一个File对象
            File mp3 = new File(path);

            //创建一个输入流
            FileInputStream fileInputStream = new FileInputStream(mp3);

            //创建一个缓冲流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            //创建播放器对象，把文件的缓冲流传入进去
            Player player = new Player(bufferedInputStream);

            //调用播放方法进行播放
            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testWindow(){
        JFrame f=new JFrame();
        Container contentpane=f.getContentPane();
        f.setLayout(new GridLayout(1, 2));
        String[] s=new String[]{"日本","英国","法国","中国","美国"};
        Vector v=new Vector();
        v.addElement("nokia 8850");
        v.addElement("nokia 8250");
        v.addElement("notorola v8088");
        v.addElement("motorola v3688");
        v.addElement("panasonic GD92");
        v.addElement("其他");

        JList jList=new JList(s);
        jList.setBorder(BorderFactory.createTitledBorder("您最喜欢到哪个国家玩呢"));

        JList jList2=new JList(v);
        jList2.setBorder(BorderFactory.createTitledBorder("你最喜欢哪部手机呢"));
        contentpane.add(new JScrollPane(jList));
        contentpane.add(new JScrollPane(jList2));
        contentpane.add(jList2);
        f.pack();
        f.show();
        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                System.exit(0);
            }

        });
    }

    private void testMessenger() {
//        Messenger messager = Messenger.create(10101,Messenger.TYPE_SERVER);
    }

    private void testDB() {
        BugDBHelper helper = BugDBHelper.getInstance();
        BugBean b0 = new BugBean(0, "name0", 0, "state0", "statu0", "creator0", "appoint0", "fix0");
        BugBean b1 = new BugBean(1, "name1", 1, "state1", "statu1", "creator1", "appoint1", "fix1");
        BugBean b2 = new BugBean(2, "name2", 2, "state2", "statu2", "creator2", "appoint2", "fix2");
        BugBean b3 = new BugBean(3, "name5", 3, "state3", "statu3", "creator3", "appoint3", "fix3");
        BugBean b4 = new BugBean(4, "name4", 4, "state4", "statu4", "creator4", "appoint4", "fix4");

        List<BugBean> bs = new ArrayList<>();
        bs.add(b0);
        bs.add(b1);
        bs.add(b2);
        bs.add(b3);
        bs.add(b4);
        helper.setData(bs);
//        helper.setData(b0,true);
//        helper.setData(b1,true);
//        helper.setData(b2,true);
//        helper.setData(b3,true);

        List<BugBean> bugBeans = helper.getData(null, true);
        for (BugBean b : bugBeans) {
            System.out.println(b.toPrintString());
        }
    }


    private void testSize() {
        DataBean dataBean = new DataBean();
        dataBean.setAa("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dataBean.setBb("abaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dataBean.setCc("acaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dataBean.setDd("adaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            strings.add("qwertyuiopasdfghjklzxcvbnm" + i);
        }
        dataBean.setStrs(strings);
        System.out.println(com.yang.utils.RamUsageEstimator.sizeOf(dataBean));
        System.out.println(com.yang.utils.RamUsageEstimator.shallowSizeOf(dataBean));
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(dataBean));

        List<DataBean> dataBeans = new ArrayList<>();
        dataBeans.add(dataBean);

        DataBean a = dataBeans.get(0);
        System.out.println(dataBean);
        System.out.println(a);
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(dataBeans));
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(new int[2]));
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(new int[20]));
        int[] is = new int[200];
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(is));
        is[0] = 1;
        is[1] = 11;
        is[2] = 111;
        is[3] = 1111;
        is[4] = 11111;
        DataBean[] ds = new DataBean[10];
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(is));
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(ds));
        ds[0] = dataBean;
        System.out.println(com.yang.utils.RamUsageEstimator.humanSizeOf(ds));
    }

    private static class DataBean {
        private String aa;
        private String bb;
        private String cc;
        private String dd;

        private List<String> strs;

        public String getAa() {
            return aa;
        }

        public void setAa(String aa) {
            this.aa = aa;
        }

        public String getBb() {
            return bb;
        }

        public void setBb(String bb) {
            this.bb = bb;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }

        public String getDd() {
            return dd;
        }

        public void setDd(String dd) {
            this.dd = dd;
        }

        public List<String> getStrs() {
            return strs;
        }

        public void setStrs(List<String> strs) {
            this.strs = strs;
        }
    }

}
