/*
 * UnikMap.java
 * modico.net (lihw@jbbis.com.cn), 2008-5-30
 *
 * Revisions:
 * 2008-12-19, modico:
 *	Add constructor UnikMap(Map).
 * 2010-03-19, modico:
 *	基类从 MapPod 改为 MapWrapper.
 */

package com.sky.workflow.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class UnikMap extends MapWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -676129747058515774L;

	public UnikMap() {
		this(false);
	}

	public UnikMap(boolean sync) {
		super(sync ? (Map<Object, Object>) new Hashtable<Object, Object>() : new HashMap<Object, Object>());
	}

	public UnikMap(Map<Object, Object> m) {
		super(m);
	}

	/**
	 * 将外部键值名称转换为内部键值名称（比如大小写处理）
	 */
	protected final Object K(Object k) {
		if (k instanceof String) {
			return k.toString().toLowerCase();
		}
		return k;
	}

	///////////////////////////////////////////////////////////////////////
	// Redefine Map interface

	public boolean containsKey(Object k) {
		return super.containsKey(K(k));
	}

	public Object get(Object k) {
		return super.get(K(k));
	}

	public Object put(Object k, Object value) {
		return super.put(K(k), value);
	}

	public Object remove(Object k) {
		return super.remove(K(k));
	}

	///////////////////////////////////////////////////////////////////////
	// FOR BACKWARD COMPATIBILITY !

	/** @deprecated */
	public String getField(Object k) {
		Object v = get(k);
		return v == null ? "" : v.toString();
	}

	/** @deprecated */
	public void setField(Object k, Object v) {
		put(k, v);
	}
}
