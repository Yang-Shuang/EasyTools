package com.yang.utils;

import com.intellij.notification.*;
import com.intellij.openapi.diagnostic.Logger;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by
 * yangshuang on 2019/7/1.
 */
public class MessageUtils {

    private static final String GROUP_ID = "EasyTools";
    private static final Logger mLogger = Logger.getInstance("#com.yang.utils.MessageUtils");
    //    private static final NotificationGroup ballonGroup = new NotificationGroup(GROUP_ID, NotificationDisplayType.TOOL_WINDOW,true);
    private static final NotificationGroup logGroup = NotificationGroup.logOnlyGroup(GROUP_ID);

    private static Player mPlayer;
    private static File mp3File = null;

    public static void toastMsg(String msg) {
        toastMsg(msg, false);
    }

    public static void toastMsg(String msg, boolean playSound) {
        if (!StringUtils.isEmpty(msg)) {
            Notification notification = new Notification("Toast", "Toast", msg, NotificationType.WARNING);
            Notifications.Bus.notify(notification);
        }
        if (playSound) {
            playSound();
        }
    }

    public static void log(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            mLogger.info(msg);
        }
        if (!StringUtils.isEmpty(msg)) {
            Notifications.Bus.notify(logGroup.createNotification(msg, NotificationType.WARNING));
        }
    }

    public static void toastError(String error, boolean playSound) {
        if (!StringUtils.isEmpty(error)) {
            Notification notification = new Notification("Toast", "Error", error, NotificationType.ERROR);
            Notifications.Bus.notify(notification);
        }
        if (playSound) {
            playSound();
        }
    }

    private static void playSound() {
        try {
            if (mPlayer != null) {
                mPlayer.close();
            }
            if (mp3File == null){
                mp3File = new File(FileUtil.getConfigFilePath(),File.separator + "assets" + File.separator + "tips.mp3");
                if (!mp3File.exists()){
                    FileUtil.initConfig();
                }
            }
            mPlayer = new Player(new BufferedInputStream(new FileInputStream(mp3File)));
            mPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
