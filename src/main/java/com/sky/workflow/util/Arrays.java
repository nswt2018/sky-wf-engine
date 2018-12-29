/*
 * Arrays.java
 * modico.net (lihw@jbbis.com.cn), 2006-9-5
 *
 * Revisions:
 * 2008-12-11: Add copyOf(). (modico)
 */

package com.sky.workflow.util;

import java.lang.reflect.Array;

/**
 * 这个类提供了对数组的一些操作方法，包括： 将数组内容拼接为字符串形式; 在数组中查找; 判断两个数组的内容是否有交集
 */
public class Arrays {
	/**
	 * 将数组内容拼接为字符串.
	 * 
	 * @param array
	 *            数组对象
	 * @param delim
	 *            分隔字符串
	 * @param nullv
	 *            数组中null值的替代串, 如果此参数为 null, 表示忽略null值
	 * @return 字符串形式表示的数组
	 * @throws IllegalArgumentException
	 *             如果 array 参数不是数组的话
	 */
	public static String join(Object array, String delim, String nullv) {
		StringBuffer buf = new StringBuffer();
		int i, c = Array.getLength(array);
		boolean begin = false;
		Object v;

		for (i = 0; i < c; ++i) {
			v = Array.get(array, i);
			if (v == null) {
				if (nullv == null) {
					continue;
				}
				v = nullv;
			}

			if (begin) {
				buf.append(delim);
			}

			buf.append(v);
			begin = true;
		}

		return buf.toString();
	}

	/**
	 * 数组中的null值将被忽略.
	 * 
	 * @see #join(Object, String, String)
	 */
	public static String join(Object array, String delim) {
		return join(array, delim, null);
	}

	/**
	 * 分隔串是 ",", 数组中的null值将被忽略.
	 * 
	 * @see #join(Object, String, String)
	 */
	public static String join(Object array) {
		return join(array, ",", null);
	}

	////////////////////////////////////////////////////////////////////

	/**
	 * 查找子字节数组.
	 * 
	 * @param a
	 *            被查数组
	 * @param off
	 *            被查数组的查找起始位置
	 * @param len
	 *            被查数组的查找长度
	 * @param key
	 *            要查找的子数组
	 * @return 如果找到, 返回起始位置 + 1; 如果在尾部有部分匹配, 返回 - 起始位置. 如果没有找到, 返回 0.
	 */
	public static int indexOf(byte[] a, int off, int len, byte[] key) {
		final int c = Math.min(off + len, a.length), k = key.length;
		int start = -1, i = off, j = 0;

		for (; i < c && j < k; i++) {
			if (a[i] == key[j]) {
				if (start == -1) {
					start = i;
				}

				j++;
			} else if (j > 0) {
				i = start;
				j = 0;
				start = -1;
			}
		}

		return j == 0 ? 0 : (j == k ? start + 1 : -start);
	}

	/**
	 * 查找一个字符串在一个字符串数组中的位置.
	 * 
	 * @param a
	 *            被查的字符串数组
	 * @param key
	 *            要查找的字符串
	 * @param case_sensitive
	 *            是否在意大小写
	 * @return 找到的位置(从0开始), 未找到时返回 -1.
	 */
	public static int indexOf(String[] a, String key, boolean case_sensitive) {
		return indexOf(a, key, case_sensitive, 0);
	}

	public static int indexOf(String[] a, String key, boolean case_sensitive, int start) {
		for (int i = start, c = a.length; i < c; i++) {
			String s = a[i];

			if (key == s || (key != null && (case_sensitive ? key.equals(s) : key.equalsIgnoreCase(s)))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 判断两个数组的内容是否有交集.
	 */
	public static boolean intersects(Object arr1, Object arr2) {
		if (arr1 == null || arr2 == null)
			return false;
		int c1 = Array.getLength(arr1);
		int c2 = Array.getLength(arr2);
		Object v1, v2;

		for (int i = 0; i < c1; i++) {
			v1 = Array.get(arr1, i);

			for (int j = 0; j < c2; j++) {
				v2 = Array.get(arr2, j);

				if (v1 == v2 || v1 != null && v1.equals(v2)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 拼接2个数组, 返回1个新数组.
	 */
	public static Object concat(Object arr1, Object arr2) {
		if (arr1 == null)
			return arr2;
		if (arr2 == null)
			return arr1;

		Class<?> ct1 = arr1.getClass().getComponentType();
		Class<?> ct2 = arr2.getClass().getComponentType();
		if (ct1 == null || ct2 == null)
			throw new IllegalArgumentException("Not array");

		Class<?> ct;
		if (ct1.isAssignableFrom(ct2))
			ct = ct1;
		else if (ct2.isAssignableFrom(ct1))
			ct = ct2;
		else
			throw new IllegalArgumentException("Not compatible");

		int c1 = Array.getLength(arr1);
		int c2 = Array.getLength(arr2);
		Object arr = Array.newInstance(ct, c1 + c2);
		System.arraycopy(arr1, 0, arr, 0, c1);
		System.arraycopy(arr2, 0, arr, c1, c2);

		return arr;
	}

	/**
	 * 新建一个指定长度的数组, 并将原有内容复制到新数组。 如果新长度长, 表示扩容；如果新长度短, 表示截取；如果相等, 表示复制。
	 */
	public static Object copyOf(Object oldarr, int newlength) {
		Class<?> ct = oldarr.getClass().getComponentType();
		int oldlength = Array.getLength(oldarr);
		Object newarr = Array.newInstance(ct, newlength);
		System.arraycopy(oldarr, 0, newarr, 0, Math.min(oldlength, newlength));
		return newarr;
	}
}
