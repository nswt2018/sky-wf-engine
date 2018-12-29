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
 * ������ṩ�˶������һЩ���������������� ����������ƴ��Ϊ�ַ�����ʽ; �������в���; �ж���������������Ƿ��н���
 */
public class Arrays {
	/**
	 * ����������ƴ��Ϊ�ַ���.
	 * 
	 * @param array
	 *            �������
	 * @param delim
	 *            �ָ��ַ���
	 * @param nullv
	 *            ������nullֵ�������, ����˲���Ϊ null, ��ʾ����nullֵ
	 * @return �ַ�����ʽ��ʾ������
	 * @throws IllegalArgumentException
	 *             ��� array ������������Ļ�
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
	 * �����е�nullֵ��������.
	 * 
	 * @see #join(Object, String, String)
	 */
	public static String join(Object array, String delim) {
		return join(array, delim, null);
	}

	/**
	 * �ָ����� ",", �����е�nullֵ��������.
	 * 
	 * @see #join(Object, String, String)
	 */
	public static String join(Object array) {
		return join(array, ",", null);
	}

	////////////////////////////////////////////////////////////////////

	/**
	 * �������ֽ�����.
	 * 
	 * @param a
	 *            ��������
	 * @param off
	 *            ��������Ĳ�����ʼλ��
	 * @param len
	 *            ��������Ĳ��ҳ���
	 * @param key
	 *            Ҫ���ҵ�������
	 * @return ����ҵ�, ������ʼλ�� + 1; �����β���в���ƥ��, ���� - ��ʼλ��. ���û���ҵ�, ���� 0.
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
	 * ����һ���ַ�����һ���ַ��������е�λ��.
	 * 
	 * @param a
	 *            ������ַ�������
	 * @param key
	 *            Ҫ���ҵ��ַ���
	 * @param case_sensitive
	 *            �Ƿ������Сд
	 * @return �ҵ���λ��(��0��ʼ), δ�ҵ�ʱ���� -1.
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
	 * �ж���������������Ƿ��н���.
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
	 * ƴ��2������, ����1��������.
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
	 * �½�һ��ָ�����ȵ�����, ����ԭ�����ݸ��Ƶ������顣 ����³��ȳ�, ��ʾ���ݣ�����³��ȶ�, ��ʾ��ȡ��������, ��ʾ���ơ�
	 */
	public static Object copyOf(Object oldarr, int newlength) {
		Class<?> ct = oldarr.getClass().getComponentType();
		int oldlength = Array.getLength(oldarr);
		Object newarr = Array.newInstance(ct, newlength);
		System.arraycopy(oldarr, 0, newarr, 0, Math.min(oldlength, newlength));
		return newarr;
	}
}
