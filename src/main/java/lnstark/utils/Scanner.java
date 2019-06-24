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

    private ClassLoader loader = this.getClass().getClassLoader();

    public static Scanner getInstance() {
        if(scanner == null) {
            scanner = new Scanner();
        }
        return scanner;
    }

    public void scanBeans(String basePath, String packageName) {

        scanFiles(basePath, packageName);
        loadClasses();
    }

    public void scanFiles(String basePath, String packagePath) {
        File baseDir = new File(basePath);
        if(baseDir.isDirectory()) {
            scanFiles(baseDir, packagePath);
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
            if(!packagePath.equals(""))
                packagePath += ".";
            for (File file : subfiles) {
                scanFiles(file, packagePath + file.getName());
            }
        }
    }

    public void loadClasses() {
        Class<?> clazz = null;
        for(String className : classNames) {
            try {
                clazz = loader.loadClass(className);// 默认初始化
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(clazz == null || clazz.isInterface() || clazz.isAnnotation())
                continue;
//            System.out.println(clazz.getName());
            analyseClass(clazz);
        }

    }

    public void analyseClass(Class<?> clazz) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        Object instance = null;
        for (Annotation a : classAnnotations) {
            if(a instanceof Component) {
                String aValue = ((Component) a).value();
                String name = aValue.equals("") ? clazz.getSimpleName() : aValue;
                instance = newInstance(clazz);
                Context.getInstance().addBean(name, instance);
            }

            System.out.println(a);
        }
        if(instance == null) // 如果class没有注入，则不扫描属性和方法的注解
            return;
        Field fields[] = clazz.getDeclaredFields();
        for(Field field : fields) {

        }

        Method methods[] = clazz.getDeclaredMethods();
        for(Method method : methods) {
            Annotation a = method.getAnnotation(Bean.class);
            if(!(a instanceof Bean))
                continue;
            String aValue = ((Bean) a).value();
            String name = aValue.equals("") ? method.getName() : aValue;
            Object methodReturnBean = null;
            try {
                methodReturnBean = method.invoke(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                Context.getInstance().addBean(name, methodReturnBean);
            }

        }
    }

    private Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
