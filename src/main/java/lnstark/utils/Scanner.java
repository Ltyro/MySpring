package lnstark.utils;

import lnstark.annotations.Bean;
import lnstark.annotations.Component;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描并加载所有类
 */
public class Scanner {

    private static Scanner scanner;

    private List<String> classNames = new ArrayList();


    public static Scanner getInstance() {
        if (scanner == null) {
            scanner = new Scanner();
        }
        return scanner;
    }

    private Scanner() {

    }

    public void scanBeans(String basePath, String packageName) {

        scanFiles(basePath, packageName);
        ((ClassAnalyzer)AnalyzerFactory.getAnalyzer(ClassAnalyzer.class)).loadClasses(classNames);

    }

    public void scanFiles(String basePath, String packagePath) {
        File baseDir = new File(basePath);
        if (baseDir.isDirectory()) {
            scanFiles(baseDir, packagePath);
        }
    }

    public void scanFiles(File baseDir, String packagePath) {
        // handle file
        if (baseDir.isFile()) {
            String fileName = baseDir.getName();
            if (fileName.endsWith(".class")) {
                String fullClassName = packagePath.replace(".class", "");
                classNames.add(fullClassName);
            }
        } else if (baseDir.isDirectory()) {// recur sub files
            File[] subfiles = baseDir.listFiles();
            if (!packagePath.equals(""))
                packagePath += ".";
            for (File file : subfiles) {
                scanFiles(file, packagePath + file.getName());
            }
        }
    }


}
