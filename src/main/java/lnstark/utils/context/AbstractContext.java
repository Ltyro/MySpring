package lnstark.utils.context;

import java.util.*;

public abstract class AbstractContext<T> implements Context<T> {

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

    public List<T> getBeanByType(Class<T> clz) {
        List<T> result = new ArrayList<T>();
        for (Object o : beans.values())
            if (o.getClass() == clz)
                result.add((T) o);
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
