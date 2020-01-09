package com.yang.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.yang.base.BaseToolWindow;
import com.yang.bean.BugBean;
import com.yang.utils.MessageUtils;
import com.yang.utils.ZenTaoDataManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by
 * yangshuang on 2020/1/7.
 */
public class ZenTaoBugListWindow extends BaseToolWindow implements ZenTaoDataManager.DataListener {

    private JPanel jPanel;
    private JTable table1;
    private JButton commitButton;
    private JLabel label1;
    private JButton clearButton;
    private DataModel dataModel;
    private Project mProject;

    private final Color SINGLE_LINE_COLOR = new Color(0x00ffffff); //单行背景色
    private final Color EVEN_LINE_COLOR = new Color(0x00f9f9f9); // 偶数行背景色
    private final Color SELECTED_LINE_COLOR = new Color(0x00fbe5a6); // 选中背景色

    private final Color TEXT_BLUE_COLOR = new Color(0x00093ac9);
    private final Color TEXT_BLUE1_COLOR = new Color(0x005193fb);
    private final Color TEXT_RED_COLOR = new Color(0x00ff0000);
    private final Color TEXT_BLACK_COLOR = new Color(0x00141414);
    private final Color TEXT_GRAY_COLOR = new Color(0x008e8e8e);
    private final Color TEXT_YELLOW_COLOR = new Color(0x00dbbd32);
    private final Color TEXT_ORANGE_COLOR = new Color(0x00d97c26);
    private final Color TEXT_GREEN_COLOR = new Color(0x002a8d2c);

    private final Color[] textColors = {TEXT_BLUE_COLOR, TEXT_RED_COLOR, TEXT_GRAY_COLOR, TEXT_BLUE_COLOR, TEXT_BLUE1_COLOR, TEXT_BLACK_COLOR, TEXT_RED_COLOR, TEXT_BLACK_COLOR};


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        mProject = project;
        table1.setAutoCreateColumnsFromModel(true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);

        ZenTaoDataManager manager = ZenTaoDataManager.getManager();
        manager.addListener(this);
        if (manager.getLastdata().size() > 0) {
            dataModel.setBugs(manager.getLastdata());
        }
        configTableStyle();
        table1.setModel(dataModel);
        label1.setText(manager.getAccountInfo());
        configColumnWidth();
    }


    private LoginDialog dialog;

    @Override
    public void init(ToolWindow window) {
        MessageUtils.log("ZenTaoBugListWindow - init");
        ZenTaoDataManager.getManager().initData(mProject);
        ZenTaoDataManager.getManager().start();
        dataModel = new DataModel();

        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog = new LoginDialog(mProject);
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZenTaoDataManager.getManager().clearMessageList();
            }
        });

    }

    private void configColumnWidth() {
        table1.getColumnModel().getColumn(0).setMaxWidth(70); // id
        table1.getColumnModel().getColumn(1).setMaxWidth(30); // level
        table1.getColumnModel().getColumn(2).setMaxWidth(60); // level
        table1.getColumnModel().getColumn(4).setMaxWidth(60); // level
        table1.getColumnModel().getColumn(5).setMaxWidth(60); // level
        table1.getColumnModel().getColumn(6).setMaxWidth(60); // level
        table1.getColumnModel().getColumn(7).setMaxWidth(60); // level
    }

    private void configTableStyle() {
        table1.setRowHeight(30);
        table1.setShowGrid(false);
        table1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                component.setForeground(dataModel.getTextColor(row, column));

                if (isSelected) {
                    component.setBackground(SELECTED_LINE_COLOR);
                } else {
                    //设置奇偶行颜色
                    if (row % 2 == 0) {
                        component.setBackground(EVEN_LINE_COLOR);// 设置奇数行底色
                    } else if (row % 2 == 1) {
                        component.setBackground(SINGLE_LINE_COLOR); // 设置偶数行底色
                    }
                }

                if (column == 3) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }

                return component;
            }
        });
    }

    @Override
    public void onGetData(List<BugBean> bugBeans) {
        if (bugBeans.size() == 0) return;
        dataModel.setBugs(bugBeans);
        table1.updateUI();
        label1.setText(ZenTaoDataManager.getManager().getAccountInfo());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        jPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        commitButton = new JButton();
        commitButton.setText("登录");
        commitButton.setToolTipText("登录获取切换账号");
        jPanel.add(commitButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1 = new JLabel();
        label1.setText("Label");
        jPanel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        clearButton.setText("清除已通知列表");
        clearButton.setToolTipText("当Bug已解决后，可点击此按钮，清空已通知的BGU历史列表");
        jPanel.add(clearButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jPanel;
    }

    private class DataModel implements TableModel {

        private List<BugBean> mBugs;
        private final String[] columnNames = {"ID", "级别", "确认", "标题", "状态", "创建", "指派", "解决"};

        public DataModel() {
            this.mBugs = new ArrayList<>();
        }

        public void setBugs(List<BugBean> bugs) {
            mBugs.clear();
            mBugs.addAll(bugs);
        }

        @Override
        public int getRowCount() {
            return mBugs.size();
        }

        @Override
        public int getColumnCount() {
            return 8;
        }

        public Color getTextColor(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return TEXT_BLUE_COLOR;
                case 1:
                    int level = (int) getValueAt(rowIndex, columnIndex);
                    if (level == 1 || level == 2) {
                        return TEXT_RED_COLOR;
                    } else if (level == 3) {
                        return TEXT_ORANGE_COLOR;
                    } else {
                        return TEXT_YELLOW_COLOR;
                    }
                case 2:
                    String status = (String) getValueAt(rowIndex, columnIndex);
                    if (status.contains("已确认")) {
                        return TEXT_GREEN_COLOR;
                    } else {
                        return TEXT_GRAY_COLOR;
                    }
                case 3:
                    return TEXT_BLUE_COLOR;
                case 4:
                    String state = (String) getValueAt(rowIndex, columnIndex);
                    if (state.contains("已解决")) {
                        return TEXT_GREEN_COLOR;
                    } else {
                        return TEXT_ORANGE_COLOR;
                    }
                case 5:
                    return TEXT_BLACK_COLOR;
                case 6:
                    String appoint = (String) getValueAt(rowIndex, columnIndex);
                    if (appoint != null) {
                        if (ZenTaoDataManager.getManager().getName().contains(appoint)) {
                            return TEXT_RED_COLOR;
                        } else {
                            return TEXT_BLACK_COLOR;
                        }
                    } else {
                        return TEXT_BLACK_COLOR;
                    }
                case 7:
                    return TEXT_BLACK_COLOR;
            }
            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {

            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return mBugs.get(rowIndex).getId();
                case 1:
                    return mBugs.get(rowIndex).getLevel();
                case 2:
                    return mBugs.get(rowIndex).getStatus();
                case 3:
                    return mBugs.get(rowIndex).getName();
                case 4:
                    return mBugs.get(rowIndex).getState();
                case 5:
                    return mBugs.get(rowIndex).getCreator();
                case 6:
                    return mBugs.get(rowIndex).getAppoint();
                case 7:
                    return mBugs.get(rowIndex).getFix();
            }
            return "NULL";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }
}
