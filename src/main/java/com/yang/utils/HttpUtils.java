package com.yang.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by
 * yangshuang on 2020/1/6.
 */
public class HttpUtils {

    public static final String KEY_HEADER = "KEY_HEADER";
    public static final String KEY_CONTENT = "KEY_CONTENT";
    public static final String KEY_SPLIT = "_:_";

    private final static String COOKIE_SPACE = "; ";

    public static HashMap<String, Object> syncRequest(String url, HashMap<String, String> parms, String method, HashMap<String, String> headers, String cookies) {
        if (url == null || "".equals(url)) return null;
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (method.equalsIgnoreCase("get") && parms != null) {
                url = url + (url.contains("?") ? "&" : "?");
                for (Map.Entry<String, String> entry : parms.entrySet()) {
                    url = url + entry.getKey() + "=" + entry.getValue() + "&";
                }
            }

            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod(method);

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);//允许写出
            connection.setDoInput(true);//允许读入
            connection.setUseCaches(false);//不使用缓存

            String paramBody = "";

            if (method.equalsIgnoreCase("post") && parms != null) {
                int i = 0;
                for (Map.Entry<String, String> entry : parms.entrySet()) {
                    if (i != 0) {
                        paramBody = paramBody + "&";
                    }
                    paramBody = paramBody + entry.getKey() + "=" + entry.getValue();
                    i++;
                }
            }


            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (cookies != null && !"".equals(cookies)) {
                connection.setRequestProperty("Cookie", cookies);
            }

            if (!paramBody.equals("")){
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(paramBody);
                out.flush();
                out.close(); // flush and close
            }

            StringBuffer buffer = new StringBuffer();
            if (connection.getResponseCode() == 200) {

                String key = null;
                ArrayList<String> headerList = new ArrayList<>();
                for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                    headerList.add(key + KEY_SPLIT + connection.getHeaderField(i));
                }

                map.put(KEY_HEADER, headerList);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "utf-8"));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    buffer.append(lines);
                }
                map.put(KEY_CONTENT, buffer.toString());
                reader.close();
            }
            // 断开连接
            connection.disconnect();
            return map;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCookie(String url, String cookie) {
        if (url == null || "".equals(url)) return null;
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);//允许写出
            connection.setDoInput(true);//允许读入
            connection.setUseCaches(false);//不使用缓存

            if (cookie != null) {
                connection.setRequestProperty("Cookie", cookie);
            }

            StringBuffer buffer = new StringBuffer();
            if (connection.getResponseCode() == 200) {
                String key = null;
                for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                    if (key.equalsIgnoreCase("set-cookie")) {
                        buffer.append(connection.getHeaderField(i) + COOKIE_SPACE);
                    }
                }
            }
            // 断开连接
            connection.disconnect();
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
