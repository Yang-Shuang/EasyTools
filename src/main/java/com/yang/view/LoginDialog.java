package com.yang.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.yang.utils.ZenTaoDataManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by
 * yangshuang on 2020/1/8.
 */
public class LoginDialog extends DialogWrapper {


    private JTextField account;
    private JPasswordField password;
    private Project mProject;

    protected LoginDialog(Project project) {
        super(project);
        mProject = project;
        init();
        setTitle("登陆禅道");
        setOKButtonText("登录");
        setCancelButtonText("取消");
        setButtonsAlignment(0);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
//        JPanel dialogPanel = new JPanel(new GridLayout(2,2));
//        dialogPanel.setPreferredSize(new Dimension(250, 80));
//        dialogPanel.setMaximumSize(new Dimension(250, 80) );
//
//        JLabel accountLabel = new JLabel("Account: ");
//        JLabel passWordLabel = new JLabel("Password: ");
//        accountLabel.setMaximumSize(new Dimension(60,30));
//        accountLabel.setPreferredSize(new Dimension(60,30));
//        passWordLabel.setMaximumSize(new Dimension(60,30));
//        passWordLabel.setPreferredSize(new Dimension(60,30));
//
//        JTextField account = new JTextField("");
//        JTextField password = new JTextField("");
//
//        account.setPreferredSize(new Dimension(100,30));
//        password.setPreferredSize(new Dimension(100,30));
//        account.setMinimumSize(new Dimension(100,30));
//        password.setMinimumSize(new Dimension(100,30));
//
//        dialogPanel.add(accountLabel);
//        dialogPanel.add(account);
//
//        dialogPanel.add(passWordLabel);
//        dialogPanel.add(password);


        JPanel dialogPanel = new JPanel(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
        dialogPanel.setPreferredSize(new Dimension(250, 80));
        dialogPanel.setMaximumSize(new Dimension(250, 80));


        final JLabel label1 = new JLabel();
        label1.setText("Account  : ");
        CellConstraints cc = new CellConstraints();
        dialogPanel.add(label1, cc.xy(1, 1));
        final JLabel label2 = new JLabel();
        label2.setText("PassWord : ");
        dialogPanel.add(label2, cc.xy(1, 3));
        account = new JTextField();
        dialogPanel.add(account, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        password = new JPasswordField();
        dialogPanel.add(password, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
//        super.doOKAction();
        String a = account.getText().trim();
        String p = password.getText().trim();
        if (a == null || "".equals(a) || p == null || p.equals("")) {
            Messages.showInfoMessage("Input Error", "Error");
            return;
        }
        ZenTaoDataManager.getManager().setUserLogin(a, p, mProject);
        super.doOKAction();
    }
}
