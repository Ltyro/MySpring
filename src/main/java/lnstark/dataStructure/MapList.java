package lnstark.dataStructure;

import java.io.Serializable;
import java.util.*;

public class MapList<K, V> implements Map<K, List<V>>, Cloneable, Serializable {

    private Map<K, List<V>> map;

    public MapList() {
        map = new HashMap();
    }

    public void add(K key, V value) {
        List<V> values = map.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }

    public void set(K key, V value) {
        List<V> values = new ArrayList<>();
        values.add(value);
        map.put(key, values);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (List<V> l : map.values())
            for (V v : l)
                if (value.equals(v))
                    return true;

        return false;
    }

    @Override
    public List<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public List<V> put(K k, List<V> v) {
        return map.put(k, v);
    }

    @Override
    public List<V> remove(Object key) {
        return map.remove(key);
    }


    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }


}
