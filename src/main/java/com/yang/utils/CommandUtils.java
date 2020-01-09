package com.yang.utils;

import java.io.IOException;

/**
 * Created by
 * yangshuang on 2019/7/25.
 */
public class CommandUtils {

    public static String execCmd(String cmd) {
        Process p;
        String result = "";
        try {
            p = Runtime.getRuntime().exec(cmd);
            result = StreamUtils.input2String(p.getInputStream());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
