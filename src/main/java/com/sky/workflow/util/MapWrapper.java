/*
 * MapWrapper.java
 * modico.net (lihw@jbbis.com.cn), 2009-03-26
 *
 * Revisions:
 *	2010-07-23, modico:
 *		Add method exportTo(MapCollector);
 */

package com.sky.workflow.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * һ�� MapWrapper ������һ��ͬʱ������ Map �� MapAccessor ���͵Ķ��� �������ж����ĵ��ã���Ȼ�������� Map
 * �ӿڷ�Χ���򵥵�ת��������ʱ���ݸ��� �� Map ����
 *
 * ��������������һ�� Map ������Ϊ�������ݸ�һ��ֻ���� MapCollector �� MapProvider ���͵ķ�������Ϊ MapAccessor
 * ͬʱʵ�����������ӿڡ�
 */
public class MapWrapper extends MapAccessor implements Map<Object, Object>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2702469262501148767L;
	private Map<Object, Object> map;

	/**
	 * @throws NullPointerException
	 *             ��� m == null
	 */
	public MapWrapper(Map<Object, Object> m) {
		if (m == null) {
			throw new NullPointerException();
		}

		map = m;
	}

	/**
	 * @throws NullPointerException
	 *             ��� m == null
	 */
	public static MapWrapper wrap(Map<Object, Object> m) {
		if (m instanceof MapWrapper) {
			return (MapWrapper) m;
		} else {
			return new MapWrapper(m);
		}
	}

	//////////////////////////////////////////////////////////////////
	// implements Map, MapCollector, MapProvider

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<Entry<Object, Object>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object obj) {
		return map.equals(obj);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public int hashCode() {
		return map.hashCode();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Object> keySet() {
		return map.keySet();
	}

	public Object put(Object key, Object value) {
		return map.put(key, value);
	}

	public void putAll(Map<?, ?> m) {
		map.putAll(m);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<Object> values() {
		return map.values();
	}

	//////////////////////////////////////////////////////////////////
	// Extra

	/**
	 * ��Ϊ MapCollector û�� putAll �������������������һ������ķ�����
	 */
	public void exportTo(MapCollector mc) {
		Iterator<Entry<Object, Object>> it = entrySet().iterator();
		Entry<Object, Object> e;

		while (it.hasNext()) {
			e = it.next();
			mc.put(e.getKey(), e.getValue());
		}
	}
}
