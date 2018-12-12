package cn.com.jbbis.jbportal.workflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DualBidiMap implements Map {
    
    protected final Map[] maps = new Map[2];

    public DualBidiMap() {
        super();
        maps[0] = new HashMap();
        maps[1] = new HashMap();
    }

    public int size() {
        return maps[0].size();
    }

    public boolean isEmpty() {
        return maps[0].isEmpty();
    }

    public boolean containsKey(Object key) {
        return maps[0].containsKey(key);
    }

    public boolean containsValue(Object value) {
        return maps[1].containsKey(value);
    }

    public Object get(Object key) {
        return maps[0].get(key);
    }
    
    public Object getKey(Object value) {
        return maps[1].get(value);
    }

    public Object put(Object key, Object value) {
        if(maps[0].containsKey(key)) {
            maps[1].remove(maps[0].get(key));
        }
        if(maps[1].containsKey(value)) {
            maps[0].remove(maps[1].get(value));
        }
        final Object obj = maps[0].put(key, value);
        maps[1].put(value, key);
        return obj;
    }

    public Object remove(Object key) {
        Object value = null;
        if(maps[0].containsKey(key)) {
            value = maps[0].remove(key);
            maps[1].remove(value);
        }
        return value;
    }

    public void putAll(Map t) {
        for(Iterator it = t.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        maps[0].clear();
        maps[1].clear();

    }

    public Set keySet() {
        return maps[0].keySet();
    }

    public Collection values() {
        return maps[1].values();
    }

    public Set entrySet() {
        return maps[0].entrySet();
    }

}