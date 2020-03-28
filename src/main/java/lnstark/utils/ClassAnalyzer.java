package lnstark.utils;

import lnstark.Server.MethodMappingResolver;
import lnstark.annotations.Bean;
import lnstark.annotations.Component;
import lnstark.annotations.Controller;
import lnstark.aop.AopAnalyzer;
import lnstark.dataStructure.MapList;
import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;
import lnstark.utils.context.DefaultContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer extends Analyzer {

//    private static ClassAnalyzer analyzer = null;

    private ClassLoader loader = this.getClass().getClassLoader();
    

    private MapList<Class<?>, Object> clzInstanceMap = new MapList<>();

    private Log log = LogFactory.getLog(ClassAnalyzer.class);

//    public static ClassAnalyzer getInstance() {
//        if (analyzer == null)
//            analyzer = new ClassAnalyzer();
//        return analyzer;
//    }

    public ClassAnalyzer() {
        super();
    }

    /**
     * 根据名字加载类
     *
     * @param classNames
     */
    public void loadClasses(List<String> classNames) {
        Class<?> clazz = null;
        // 先加载所有类，再逐个解析类的属性和方法
        for (String className : classNames) {
            try {
                clazz = loader.loadClass(className);// 默认初始化
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz == null || clazz.isInterface() || clazz.isAnnotation())
                continue;
//            System.out.println(clazz.getName());
            // load class
            analyzeClass(clazz);
        }

        for (Map.Entry<Class<?>, List<Object>> entry : clzInstanceMap.entrySet()) {
            Class clz = entry.getKey();
            for (Object o : entry.getValue()) {
                analyzeFieldMethod(clz, o);// 现在就写了bean注入解析
            }
        }

        // 解析controller
        for (Object o : context.getAll()) {
            Class<?> clz = o.getClass();//entry.getKey();
            Controller c = clz.getAnnotation(Controller.class);
            if(c != null)
                MethodMappingResolver.getInstance().resolveController(clz);// 解析controller方法
        }

        // 解析AOP
        AopAnalyzer aopAnalyzer = AnalyzerFactory.getAnalyzer(AopAnalyzer.class);
        aopAnalyzer.analyze();
    }

    /**
     * 类解析
     *
     * @param clazz
     */
    public void analyzeClass(Class<?> clazz) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        Object instance = null;
        boolean inject = false;
        String aValue = "";
        for (Annotation a : classAnnotations) {
            if (a instanceof Component) {
                aValue = ((Component) a).value();
                inject = true;
            } else if (a instanceof Controller) {
                aValue = ((Controller) a).value();
                inject = true;
            }
        }
        if(inject) {
            String name = aValue.equals("") ? firstLetterToLower(clazz.getSimpleName()) : aValue;
            instance = newInstance(clazz);
            clzInstanceMap.add(clazz, instance);
            context.addBean(name, instance);
            log.info("--------------" + name + "loaded----------------");
        }
    }

    /**
     * 成员变量和方法解析
     *
     * @param clazz
     * @param instance
     */
    public void analyzeFieldMethod(Class<?> clazz, Object instance) {
        analyzeField(clazz, instance);
        analyzeMethod(clazz, instance);
    }

    /**
     * 成员变量解析
     */
    public void analyzeField(Class<?> clazz, Object instance) {
        Field fields[] = clazz.getDeclaredFields();
        for (Field field : fields) {
        	// TODO
        }
    }

    /**
     * 成员方法解析
     */
    public void analyzeMethod(Class<?> clazz, Object instance) {
        Method methods[] = clazz.getDeclaredMethods();
        for (Method method : methods) {
            injectBeans(method, instance);// 通过bean注入
        }
    }

    private void injectBeans(Method method, Object instance) {
        Annotation a = method.getAnnotation(Bean.class);
        if (!(a instanceof Bean))
            return;
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
            context.addBean(name, methodReturnBean);
        }
    }

    private static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 首字母转小写
     */
    private static String firstLetterToLower(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
