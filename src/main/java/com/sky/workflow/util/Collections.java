/*
 * Collections.java
 * modico.net (lihw@jbbis.com.cn), 2007-2-14
 */

package com.sky.workflow.util;

import java.util.Collection;

public class Collections {
	/**
	 * 将一个Collection转换成String[].
	 * 
	 * @param c
	 *            集合
	 * @return 如果 c == null, 返回 null.
	 */
	public static String[] toStringArray(Collection<?> c) {
		if (c == null) {
			return null;
		}

		String[] a = new String[c.size()];
		c.toArray(a);
		return a;
	}

	/**
	 * 将一个Collection里的内容拼接成一个字符串.
	 * 
	 * @param c
	 *            集合
	 * @param delim
	 *            连接串
	 * @return 如果 c == null, 返回 null.
	 */
	public static String join(Collection<?> c, String delim) {
		if (c == null) {
			return null;
		}

		Object[] a = c.toArray();
		String s = Arrays.join(a, delim);
		return s;
	}

	public static void ensureSize(Collection<Object> coll, int size) {
		for (int i = coll.size(); i < size; i++) {
			coll.add(null);
		}
	}
}
