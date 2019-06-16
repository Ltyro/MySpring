package utils;

import java.util.*;

public class Context {

    private Map<String, Object> container;

    private static Context context;

    private Context() {
        container = new HashMap<String, Object>();
    }

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    public void addBean(String name, Object bean) {
        container.put(name, bean);
    }

    /**
     * 根据类型获取bean
     * @param clazz
     * @return
     */
    public List<Object> getBeansByType(Class<?> clazz) {
        List<Object> result = new ArrayList<Object>();

        for(Map.Entry<String, Object> entry : container.entrySet()) {
            if(entry.getValue().getClass() == clazz)
                result.add(entry.getValue());
        }
        return result;
    }

    public Object getBeanByName(String name) {
        return container.get(name);
    }

    public void destroy() {
        context = null;
    }
}
