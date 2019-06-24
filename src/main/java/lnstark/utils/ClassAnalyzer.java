package lnstark.utils;

import lnstark.annotations.Bean;
import lnstark.annotations.Component;
import lnstark.dataStructure.MapList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer {

    private static ClassAnalyzer analyzer = null;

    private ClassLoader loader = this.getClass().getClassLoader();

    private Context context = Context.getInstance();;

    private MapList<Class<?>, Object> clzInstanceMap = new MapList();

    private Log log = LogFactory.getLog(ClassAnalyzer.class);

    public static ClassAnalyzer getInstance() {
        if(analyzer == null)
            analyzer = new ClassAnalyzer();
        return analyzer;
    }

    /**
     * 根据名字加载类
     * @param classNames
     */
    public void loadClasses(List<String> classNames) {
        Class<?> clazz = null;
        // 先加载所有类，再逐个解析类的属性和方法
        for(String className : classNames) {
            try {
                clazz = loader.loadClass(className);// 默认初始化
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(clazz == null || clazz.isInterface() || clazz.isAnnotation())
                continue;
//            System.out.println(clazz.getName());
            // load class
            ClassAnalyzer.getInstance().analyzeClass(clazz, context);
        }

        for(Map.Entry<Class<?>, List<Object>> entry : clzInstanceMap.entrySet()) {
            Class clz = entry.getKey();
            for(Object o : entry.getValue()) {
                analyzeFieldMethod(clz, o);
            }
        }
    }

    /**
     * 类解析
     * @param clazz
     * @param ctx
     */
    public void analyzeClass(Class<?> clazz, Context ctx) {
        Annotation[] classAnnotations = clazz.getAnnotations();
        Object instance = null;
        for (Annotation a : classAnnotations) {
            if(a instanceof Component) {
                String aValue = ((Component) a).value();
                String name = aValue.equals("") ? firstLetterToLower(clazz.getSimpleName()) : aValue;
                instance = newInstance(clazz);
                clzInstanceMap.add(clazz, instance);
                context.addBean(name, instance);
            }

            log.info(a);
        }

    }

    /**
     * 成员变量和方法解析
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
        for(Field field : fields) {

        }
    }

    /**
     * 成员方法解析
     */
    public void analyzeMethod(Class<?> clazz, Object instance) {
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
                context.addBean(name, methodReturnBean);
            }

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
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
