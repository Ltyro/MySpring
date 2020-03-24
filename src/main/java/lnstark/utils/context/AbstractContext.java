package lnstark.utils.context;

import lnstark.utils.ConfigurationResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractContext<T> implements Context<T> {

    private Map<String, Object> beans;

    public AbstractContext() {
        beans = new HashMap();
    }

    public void addBean(String name, Object o) {
        beans.put(name, o);
    }

    public Object getBeanByName(String name) {
        return beans.get(name);
    }

    public List<T> getBeanByType(Class<T> clz) {
        List<T> result = new ArrayList<T>();
        for (Object o : beans.values())
            if (o.getClass() == clz)
                result.add((T) o);
        return result;
    }

}
