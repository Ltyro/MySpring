package lnstark.utils.context;

import java.util.*;

public abstract class AbstractContext implements Context {

    private Map<String, Object> beans;

    public AbstractContext() {
        beans = new HashMap<>();
    }

    public void addBean(String name, Object o) {
        beans.put(name, o);
    }

    public Object getBeanByName(String name) {
        return beans.get(name);
    }

    public List<Object> getBeanByType(Class<?> clz) {
        List<Object> result = new ArrayList<Object>();
        for (Object o : beans.values())
            if (o.getClass() == clz || o.getClass().getSuperclass() == clz)// 代理类也算
                result.add(o);
        return result;
    }
    
    public List<Object> getAll() {
    	return new ArrayList<>(beans.values());
    }

    public List<Class<?>> getAllClass() {
    	List<Class<?>> classes = new ArrayList<>();
    	for(Object o : beans.values()) {
    		classes.add(o.getClass());
    	}
    	return classes;
    }

    @Override
    public Set<String> getNameSet() {
        return beans.keySet();
    }
}
