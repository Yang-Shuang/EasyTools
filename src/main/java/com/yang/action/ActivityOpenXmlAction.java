package com.yang.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.yang.base.BaseAction;
import com.yang.utils.FileUtil;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created
 * by yangshuang on 2019/7/1.
 */
public class ActivityOpenXmlAction extends BaseAction {

    @Override
    protected void actionPerformedCustom(AnActionEvent event) {
        Project project = getEventProject(event);
        VirtualFile selectedFile = (VirtualFile) event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile == null) {
            return;
        }

        String fileName = selectedFile.getName();
        // 必须是xml或者java文件
        if ((!fileName.endsWith(".xml")) && (!fileName.endsWith(".java"))) {
            toastMsg("文件类型不支持:" + fileName);
            return;
        }
        if (fileName.equals(".xml")) {
            // xml文件必须是fragment或者activity的页面文件，其他如listitem等不可以
            if (!fileName.contains("activity") && !fileName.contains("fragment") && !fileName.contains("dialog")) {
                toastMsg("不支持此文件命名:" + fileName);
                return;
            }
        }
        String name = "";
        if (fileName.endsWith(".java")) {
            if (fileName.endsWith("Activity.java")) {
                name = getXmlForActivity(selectedFile);
            } else if (fileName.endsWith("Fragment.java")) {
                name = getXmlForFragment(selectedFile);
            } else if (fileName.endsWith("Dialog.java")) {
                toastMsg("不支持此文件命名:" + fileName);
                return;
            } else {
                toastMsg("不支持此文件命名:" + fileName);
                return;
            }
            name = name + ".xml";
        } else {
            if (!fileName.contains("activity") && !fileName.contains("fragment")) {
                toastMsg("不支持此文件命名:" + fileName);
                return;
            }
        }
        String rootpath = project.getBasePath();
        VirtualFile openFile = null;
        if (fileName.endsWith(".xml")) {
            VirtualFile javaPackage = project.getBaseDir().findFileByRelativePath(FileUtil.getJavaPackagePath(selectedFile).replace(rootpath, ""));
            openFile = FileUtil.findFile(javaPackage, fileName, project);
        } else {
            String repath = FileUtil.getResPackagePath(selectedFile).replace(rootpath, "");
            openFile = project.getBaseDir().findFileByRelativePath(repath + name);
        }
        if (openFile != null) {
            FileEditorManager.getInstance(project).openFile(openFile, true);
        } else {
            toastMsg("未找到对应文件");
        }
    }

    private String getFileName(VirtualFile file) {
        return "";
    }

    private String getXmlForActivity(VirtualFile selectedFile) {
        String layoutName = null;
        try {
            InputStreamReader r = new InputStreamReader(selectedFile.getInputStream());
            BufferedReader reader = new BufferedReader(r);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("setContentView(R.layout.")) {
                    layoutName = line.split("R.layout.")[1];
                    layoutName = layoutName.split("\\)\\;")[0];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (layoutName == null) {
            String file = selectedFile.getName();
            String xmlFile = file.replace(".java", "");
            xmlFile = xmlFile.substring(0, xmlFile.length() - 8);
            Pattern pattern = Pattern.compile("[A-Z]{1}");
            Matcher matcher = pattern.matcher(xmlFile);
            layoutName = xmlFile;
            while (matcher.find()) {
                String s = matcher.group();
                layoutName = layoutName.replace(s, "_" + s.toLowerCase());
            }
            layoutName = "activity" + layoutName;
        }
        return layoutName;
    }

    private String getXmlForFragment(VirtualFile selectedFile) {
        String layoutName = null;
        try {
            InputStreamReader r = new InputStreamReader(selectedFile.getInputStream());
            BufferedReader reader = new BufferedReader(r);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(".inflate(R.layout.")) {
                    layoutName = line.split(".inflate\\(R.layout.")[1];
                    layoutName = layoutName.split(",")[0];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (layoutName == null) {
            String file = selectedFile.getName();
            String xmlFile = file.replace(".java", "");
            xmlFile = xmlFile.substring(0, xmlFile.length() - 8);
            Pattern pattern = Pattern.compile("[A-Z]{1}");
            Matcher matcher = pattern.matcher(xmlFile);
            layoutName = xmlFile;
            while (matcher.find()) {
                String s = matcher.group();
                layoutName = layoutName.replace(s, "_" + s.toLowerCase());
            }
            layoutName = "fragment" + layoutName;
        }
        return layoutName;
    }

    private List<String> getActivityForXml(String file) {
        List<String> names = new ArrayList<>();
        String javaFile = file.replace(".xml", "");
        javaFile = javaFile.replaceFirst("activity_", "");
        String[] arr = javaFile.split("_");
        javaFile = "";
        for (String s : arr) {
            names.add(StringUtils.capitalize(s));
            javaFile = javaFile + StringUtils.capitalize(s);
        }
        names.add("Activity");
        return names;
    }

    private String getFragmentForXml(String file) {
        String javaFile = file.replace(".xml", "");
        javaFile = javaFile.replaceFirst("fragment_", "");
        String[] arr = javaFile.split("_");
        javaFile = "";
        for (String s : arr) {
            javaFile = javaFile + StringUtils.capitalize(s);
        }
        return javaFile + "Fragment";
    }
}
