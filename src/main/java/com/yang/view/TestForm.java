package com.yang.view;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

/**
 * Created by
 * yangshuang on 2020/1/8.
 */
public class TestForm {
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;

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
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));

        final JLabel label1 = new JLabel();
        label1.setText("Label");
        CellConstraints cc = new CellConstraints();
        panel1.add(label1, cc.xy(1, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Label");
        panel1.add(label2, cc.xy(1, 3));
        textField1 = new JTextField();
        panel1.add(textField1, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        textField2 = new JTextField();
        panel1.add(textField2, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
