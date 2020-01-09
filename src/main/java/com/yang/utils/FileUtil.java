package com.yang.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by
 * yangshuang on 2019/7/1.
 */
public class FileUtil {

    private static final String SPLIT = File.separator + "EasyTools" + File.separator;
    private static String CONFIG_FILE_PATH;

    public static String getRelatedFileName(String name) {
        return "";
    }

    private static List<String> getJavaNameForXml(String file) {
        List<String> names = new ArrayList<>();
        String javaFile = file.replace(".xml", "");
        String[] arr = javaFile.split("_");
        javaFile = "";
        for (String s : arr) {
            names.add(org.apache.commons.lang.StringUtils.capitalize(s));
            javaFile = javaFile + org.apache.commons.lang.StringUtils.capitalize(s);
        }
        return names;
    }


    public static VirtualFile findFile(VirtualFile baseFile, String filename, Project project) {
        if (!baseFile.isDirectory()) {
            return null;
        }
        List<String> names = getJavaNameForXml(filename);
        VirtualFile file = FileFinder.FileFinderGetter.getFinder(baseFile).withNames(names).withXmlName(filename).find(project);
        return file;
    }

    public static String getJavaPackagePath(VirtualFile selectedFile) {
        String filePath = selectedFile.getPath();
        String projectPath = filePath.substring(0, filePath.lastIndexOf("/res/"));
        if (projectPath.contains("/src/")) {
            return projectPath + "/java/"; //  app/main/java   app/main/res
        } else {
            return projectPath + "/src/"; // app/src/com....  app/res
        }
    }

    public static String getResPackagePath(VirtualFile selectedFile) {
        String filePath = selectedFile.getPath();
        String projectPath = filePath.substring(0, filePath.lastIndexOf("/src/"));
        File file = new File(projectPath + "/src/");
        String[] childsname = file.list();
        for (String name : childsname) {
            if (name.equals("main")) {
                return projectPath + "/src/main/res/layout/";
            }
        }
        return projectPath + "/res/layout/";
    }

    private static String getRelativePath(String path) {
        String projectPath = path.substring(0, path.lastIndexOf("/src/"));
        String[] folders = projectPath.split("\\/");
        String moduleName = folders[folders.length - 1];
        String needReplace = path.split("/src/")[0];
        return projectPath.replace(needReplace, "/" + moduleName);
    }

    static class FileFinder {
        private VirtualFile baseFile;
        private List<String> names;
        private String fullName, xmlName;
        private HashMap<String, String> allFiles = new HashMap<>();

        private FileFinder(VirtualFile baseFile) {
            this.baseFile = baseFile;
            getFiles(baseFile);
        }

        private FileFinder(VirtualFile baseFile, List<String> names) {
            this.baseFile = baseFile;
            this.names = names;
            generateFullName();
            getFiles(baseFile);
        }

        private FileFinder withNames(List<String> names) {
            this.names = names;
            generateFullName();
            getFiles(baseFile);
            return this;
        }

        private FileFinder withXmlName(String xmlName) {
            this.xmlName = xmlName;
            return this;
        }

        private void generateFullName() {
            String name = "";
            String suffix = "";
            for (String n : names) {
                if (n.equals("Fragment") || n.equals("Activity")) {
                    suffix = n + suffix;
                } else {
                    name = name + n;
                }
            }
            fullName = name + suffix;
        }

        private void getFiles(VirtualFile baseFile) {
            VirtualFile[] files = baseFile.getChildren();
            for (VirtualFile f : files) {
                if (f.isDirectory()) {
                    getFiles(f);
                } else {
                    allFiles.put(f.getName(), f.getPath());
                }
            }
        }

        public VirtualFile find(Project project) {
            if (names == null || names.equals("")) return null;
            int level = 0;
            ArrayList<String> ns = new ArrayList<>();
            for (String name : allFiles.keySet()) {
                int l = getMatchLevel(name);
                if (l > level) {
                    level = l;
                    ns = new ArrayList<>();
                    ns.add(name);
                } else if (l == level) {
                    ns.add(name);
                }
            }
            if (ns.size() == 0) return null;
            String name = "";
            if (level == 999) {
                String path = allFiles.get(ns.get(0));
                path = path.replace(project.getBaseDir().getPath(), "");
                return project.getBaseDir().findFileByRelativePath(path);
            }
            if (xmlName != null && !"".equals(xmlName)) {
                for (String n : ns) {
                    if (hasContentFile(new File(allFiles.get(n)))) {
                        String path = allFiles.get(n);
                        path = path.replace(project.getBaseDir().getPath(), "");
                        return project.getBaseDir().findFileByRelativePath(path);
                    }
                }
            }
            name = ns.get(0);
            String path = allFiles.get(name);
            path = path.replace(project.getBaseDir().getPath(), "");
            return project.getBaseDir().findFileByRelativePath(path);
        }

        private int getMatchLevel(String name) {
            int l = 0;
            if (name.startsWith(fullName)) {
                return 999;
            }
            for (String n : names) {
                if (name.contains(n)) l++;
            }
            return l;
        }

        private boolean hasContentFile(File selectedFile) {
            try {
                if (!selectedFile.exists()) return false;
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line = null;
                String str = selectedFile.getName().endsWith("Activity.java") ? "setContentView(R.layout." : ".inflate(R.layout.";
                str = str + xmlName.replace(".xml", "");
                while ((line = reader.readLine()) != null) {
                    if (line.contains(str)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtils.toastMsg("openFile:" + e.getMessage());
            }
            return false;
        }

        static class FileFinderGetter {
            private static HashMap<String, FileFinder> map = new HashMap<>();

            static FileFinder getFinder(VirtualFile baseFile) {
                if (map.containsKey(baseFile.getPath())) {
                    return map.get(baseFile.getPath());
                } else {
                    FileFinder fileFinder = new FileFinder(baseFile);
                    map.put(baseFile.getPath(), fileFinder);
                    return fileFinder;
                }
            }

        }
    }

    public static void copyFile(InputStream in, OutputStream out) {
        try {

            byte[] flush = new byte[1024];
            int len = -1;

            // 边读边写
            while ((len = in.read(flush)) != -1) {
                out.write(flush, 0, len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭原则，先用后关
            // 关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 关闭输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getConfigFilePath(){
        return CONFIG_FILE_PATH;
    }

    public static boolean configIsExist(){
        return CONFIG_FILE_PATH != null && !"".equals(CONFIG_FILE_PATH) && new File(CONFIG_FILE_PATH).exists();
    }

    public static void initConfig(){

        if (!configIsExist()){
            String path = FileUtil.class.getResource(File.separator + "assets" + File.separator + "db").getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5, path.length() - 1);
            }
            CONFIG_FILE_PATH = path.split(SPLIT)[0] + SPLIT + "config";
            File assets = new File(CONFIG_FILE_PATH);
            if (!assets.exists()) {
                copyAssets();
            }
        }
    }

    private static void copyAssets() {
        try {
            URL path = SettingsManager.class.getClassLoader().getResource(File.separator + "assets" + File.separator);
            URLConnection urlConnection = path.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entrys = jarFile.entries();
                while (entrys.hasMoreElements()) {
                    JarEntry entry = entrys.nextElement();
                    if (entry.getName().startsWith(jarURLConnection.getEntryName()) && !entry.getName().endsWith("/")) {
                        loadRecourseFromJar("/" + entry.getName(),CONFIG_FILE_PATH);
                    }
                }
                jarFile.close();
            }
        } catch (Exception e) {
            MessageUtils.log(e.getMessage());
        }
    }

    private static void loadRecourseFromJar(String path,String recourseFolder) throws IOException {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        if (path.endsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (cat not end with '/').");
        }

        int index = path.lastIndexOf('/');

        String filename = path.substring(index + 1);
        String folderPath = recourseFolder + path.substring(0, index + 1);

        // If the folder does not exist yet, it will be created. If the folder
        // exists already, it will be ignored
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // If the file does not exist yet, it will be created. If the file
        // exists already, it will be ignored
        filename = folderPath + filename;
        File file = new File(filename);

        if (!file.exists() && !file.createNewFile()) {
            MessageUtils.log("create file :{} failed .fileName:"+ filename);
            return;
        }

        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;

        // Open and check input stream
        URL url = SettingsManager.class.getResource(path);
        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();

        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
        OutputStream os = new FileOutputStream(file);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            os.close();
            is.close();
        }

    }

}
