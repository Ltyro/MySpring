package utils;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描并加载所有类
 */
public class Scanner {

    private static Scanner scanner;

    private List<String> classNames = new ArrayList();

    private ClassLoader loader = this.getClass().getClassLoader();

    public static Scanner getInstance() {
        if(scanner == null) {
            scanner = new Scanner();
            return scanner;
        }
        return scanner;
    }

    public void scanBeans(String basePath) {

        scanFiles(basePath, "");
        loadClasses();
    }

    public void scanFiles(String basePath, String packagePath) {
        File baseDir = new File(basePath);
        if(baseDir.isDirectory()) {
            scanFiles(baseDir, "");
        }
    }

    public void scanFiles(File baseDir, String packagePath) {
        // handle file
        if(baseDir.isFile()) {
            String fileName = baseDir.getName();
            if(fileName.endsWith(".class")) {
                String fullClassName = packagePath.replace(".class", "");
                classNames.add(fullClassName);
            }
        } else if(baseDir.isDirectory()) {// recur sub files
            File[] subfiles = baseDir.listFiles();

            for (File file : subfiles) {
                if(!packagePath.equals(""))
                    packagePath += ".";
                scanFiles(file, packagePath + file.getName());
            }
        }
    }

    public void loadClasses() {
        try {
            for(String className : classNames) {
                Class.forName(className);
                Class<?> clazz = loader.loadClass(className);// 默认初始化
                System.out.println(clazz.getName());
                analyseClass(clazz);

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void analyseClass(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation a : annotations) {
            System.out.println(a);

        }
    }
}
