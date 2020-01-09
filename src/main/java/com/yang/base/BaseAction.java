package com.yang.base;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yang.utils.MessageUtils;

/**
 * Created by
 * yangshuang on 2019/7/1.
 */
public abstract class BaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
//        try {
            this.actionPerformedCustom(anActionEvent);
//        } catch (Exception e) {
//            toastMsg(e.getMessage());
//        }
    }

    protected abstract void actionPerformedCustom(AnActionEvent anActionEvent);

    protected void toastMsg(String msg) {
        MessageUtils.toastMsg(msg);
    }

    protected void log(String msg) {
        MessageUtils.log(msg);
    }
}
