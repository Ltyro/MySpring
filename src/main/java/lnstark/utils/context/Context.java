package lnstark.utils.context;

import java.util.List;
import java.util.Set;

public interface Context<T> {
    List<T> getBeanByType(Class<T> clz);

    Object getBeanByName(String name);

    void addBean(String name, Object o);
    
    List<Object> getAll();
    
    List<Class<?>> getAllClass();

    Set<String> getNameSet();

}
