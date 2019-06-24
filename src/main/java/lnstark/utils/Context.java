package lnstark.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context<T> {
    private Map<String, Object> beans;

    private static Context context = null;

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    private Context() {
        beans = new HashMap();
    }

    public void addBean(String name, Object o) {
        beans.put(name, o);
    }

    public Object getBeanByName(String name) {
        for(Map.Entry<String, Object> entry : beans.entrySet())
            if(entry.getKey().equals(name))
                return entry.getValue();
        return null;
    }

    public List<T> getBeanByType(Class<T> clz) {
        List<T> result = new ArrayList<T>();
        for(Object o : beans.values())
            if(o.getClass() == clz)
                result.add((T) o);
        return result;
    }
}
