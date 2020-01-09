package com.yang.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Created by yangshuang
 * on 2019/7/25.
 */
public class Constants {
    public static final String JVM_VENDOR = System.getProperty("java.vm.vendor");
    public static final String JVM_VERSION = System.getProperty("java.vm.version");
    public static final String JVM_NAME = System.getProperty("java.vm.name");
    public static final String JVM_SPEC_VERSION = System.getProperty("java.specification.version");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean LINUX;
    public static final boolean WINDOWS;
    public static final boolean SUN_OS;
    public static final boolean MAC_OS_X;
    public static final boolean FREE_BSD;
    public static final String OS_ARCH;
    public static final String OS_VERSION;
    public static final String JAVA_VENDOR;
    private static final int JVM_MAJOR_VERSION;
    private static final int JVM_MINOR_VERSION;
    public static final boolean JRE_IS_64BIT;
    public static final boolean JRE_IS_MINIMUM_JAVA8;
    public static final boolean JRE_IS_MINIMUM_JAVA9;
    public static final boolean JRE_IS_MINIMUM_JAVA11;

    private static String ident(String s) {
        return s.toString();
    }

    static {
        LINUX = OS_NAME.startsWith("Linux");
        WINDOWS = OS_NAME.startsWith("Windows");
        SUN_OS = OS_NAME.startsWith("SunOS");
        MAC_OS_X = OS_NAME.startsWith("Mac OS X");
        FREE_BSD = OS_NAME.startsWith("FreeBSD");
        OS_ARCH = System.getProperty("os.arch");
        OS_VERSION = System.getProperty("os.version");
        JAVA_VENDOR = System.getProperty("java.vendor");
        StringTokenizer st = new StringTokenizer(JVM_SPEC_VERSION, ".");
        JVM_MAJOR_VERSION = Integer.parseInt(st.nextToken());
        if (st.hasMoreTokens()) {
            JVM_MINOR_VERSION = Integer.parseInt(st.nextToken());
        } else {
            JVM_MINOR_VERSION = 0;
        }

        boolean is64Bit = false;
        String datamodel = null;

        try {
            datamodel = System.getProperty("sun.arch.data.model");
            if (datamodel != null) {
                is64Bit = datamodel.contains("64");
            }
        } catch (SecurityException var4) {
            ;
        }

        if (datamodel == null) {
            if (OS_ARCH != null && OS_ARCH.contains("64")) {
                is64Bit = true;
            } else {
                is64Bit = false;
            }
        }
        JRE_IS_64BIT = is64Bit;
        JRE_IS_MINIMUM_JAVA8 = JVM_MAJOR_VERSION > 1 || JVM_MAJOR_VERSION == 1 && JVM_MINOR_VERSION >= 8;
        JRE_IS_MINIMUM_JAVA9 = JVM_MAJOR_VERSION > 1 || JVM_MAJOR_VERSION == 1 && JVM_MINOR_VERSION >= 9;
        JRE_IS_MINIMUM_JAVA11 = JVM_MAJOR_VERSION > 1 || JVM_MAJOR_VERSION == 1 && JVM_MINOR_VERSION >= 11;
    }
}
