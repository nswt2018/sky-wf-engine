/*
 * Collections.java
 * modico.net (lihw@jbbis.com.cn), 2007-2-14
 */

package com.sky.workflow.util;

import java.util.Collection;

public class Collections {
	/**
	 * ��һ��Collectionת����String[].
	 * 
	 * @param c
	 *            ����
	 * @return ��� c == null, ���� null.
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
	 * ��һ��Collection�������ƴ�ӳ�һ���ַ���.
	 * 
	 * @param c
	 *            ����
	 * @param delim
	 *            ���Ӵ�
	 * @return ��� c == null, ���� null.
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
