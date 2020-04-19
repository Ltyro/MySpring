package lnstark.utils;

import lnstark.aop.AopAnalyzer;
import lnstark.schedule.ScheduleAnalyzer;

import java.io.File;
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
        BeanAnalyzer beanAnalyzer = AnalyzerFactory.getAnalyzer(BeanAnalyzer.class);
        beanAnalyzer.setClassNames(classNames);
        beanAnalyzer.analyze();

        // analyze AOP
        AopAnalyzer aopAnalyzer = AnalyzerFactory.getAnalyzer(AopAnalyzer.class);
        aopAnalyzer.analyze();

        // analyze schedule
        ScheduleAnalyzer scheduleAnalyzer = AnalyzerFactory.getAnalyzer(ScheduleAnalyzer.class);
        scheduleAnalyzer.analyze();
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
