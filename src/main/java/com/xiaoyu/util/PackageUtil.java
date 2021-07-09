package com.xiaoyu.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxy
 */
public class PackageUtil {

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     */
    public static List<Class<?>> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName  包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    public static List<Class<?>> getClassName(String packageName, boolean childPackage) {
        List<Class<?>> fileClassNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                fileClassNames = getClassNameByFile(url.getPath(), packageName, childPackage);
            }
        }
        return fileClassNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath     文件路径
     * @param packageName  包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static List<Class<?>> getClassNameByFile(String filePath, String packageName, boolean childPackage) {
        List<Class<?>> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        assert childFiles != null;
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), packageName.concat(".").concat(childFile.getName()), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    String newPath = childFilePath.replace("\\", "/");
                    String[] pathArray = newPath.split("/");
                    String classPackageName =packageName + "." + pathArray[pathArray.length - 1].replace(".class", "");
                    try {
                        myClassName.add(Class.forName(classPackageName));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return myClassName;
    }


}