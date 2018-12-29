/*
 * MapAccessor.java
 * modico.net (lihw@jbbis.com.cn), 2009-3-27
 *
 * Revisions:
 * 2010-03-18, modico:
 *		Add getBigInteger/getBigDecimal/getByteLength/getTimeSpan
 */

package com.sky.workflow.util;

import java.math.*;
import java.util.Date;

/**
 * Map ���ݴ�ȡ�����ṩ����չ�İ����Ͷ�д Map ���ݵķ������� ��û��ʵ�� MapCollector �� MapProvider
 * �������κη������������Ӧ�� ����չ��ȥ��ɡ�
 */
public abstract class MapAccessor implements MapCollector, MapProvider {
	//////////////////////////////////////////////////////////////////
	// �����ǻ����������͵Ķ�д��չ������ String, int, long, float, double
	// Ҳ���� BigInteger �� BigDecimal ��

	/**
	 * ���ַ������Ͷ���
	 */
	public String getString(Object k) {
		Object v = get(k);
		return v == null ? null : v.toString();
	}

	/**
	 * ���ַ������Ͷ��������������ֵ�� null ���ַ�������Ϊ 0����ֱ�ӷ��صڶ���������
	 */
	public String getString(Object k, String fallback) {
		String s = getString(k);
		return Strings.isEmpty(s) ? fallback : s;
	}

	/**
	 * ���������Ͷ�������޷��ӻ������ݷ���һ����Ч���������򷵻� 0��
	 */
	public int getInt(Object k) {
		return getInt(k, 0);
	}

	/**
	 * ���������Ͷ�������޷��ӻ������ݷ���һ����Ч����������ֱ�ӷ��صڶ���������
	 */
	public int getInt(Object k, int fallback) {
		Object v = get(k);

		if (v instanceof Number) {
			return ((Number) v).intValue();
		}

		try {
			return Integer.parseInt(v.toString());
		} catch (Throwable e) {
			return fallback;
		}
	}

	/**
	 * ����������д��ʵ�ʴ洢���ͽ�ת��Ϊ Integer ��
	 */
	public void put(Object k, int i) {
		put(k, new Integer(i));
	}

	/** same as put(Object,int) */
	public void setInt(Object k, int i) {
		put(k, i);
	}

	/**
	 * �������Ͷ�������޷��ӻ������ݷ���һ����Ч���������򷵻� 0��
	 */
	public long getLong(Object k) {
		return getLong(k, 0L);
	}

	/**
	 * �������Ͷ�������޷��ӻ������ݷ���һ����Ч����������ֱ�ӷ��صڶ���������
	 */
	public long getLong(Object k, long fallback) {
		Object v = get(k);

		if (v instanceof Number) {
			return ((Number) v).longValue();
		}

		try {
			return Long.parseLong(v.toString());
		} catch (Throwable e) {
			return fallback;
		}
	}

	/**
	 * ��������д��ʵ�ʴ洢���ͽ�ת��Ϊ Long ��
	 */
	public void put(Object k, long l) {
		put(k, new Long(l));
	}

	/** same as put(Object,long) */
	public void setLong(Object k, long l) {
		put(k, l);
	}

	/**
	 * ����������������޷��ӻ������ݷ���һ����Ч�ĸ��������򷵻� 0��
	 */
	public float getFloat(Object k) {
		return getFloat(k, 0f);
	}

	/**
	 * ����������������޷��ӻ������ݷ���һ����Ч�ĸ���������ֱ�ӷ��صڶ���������
	 */
	public float getFloat(Object k, float fallback) {
		Object v = get(k);

		if (v instanceof Number) {
			return ((Number) v).floatValue();
		}

		try {
			return Float.parseFloat(v.toString());
		} catch (Throwable e) {
			return fallback;
		}
	}

	/**
	 * ��������д��ʵ�ʴ洢���ͽ�ת��Ϊ Float ��
	 */
	public void put(Object k, float f) {
		put(k, new Float(f));
	}

	/** same as put(Object,float) */
	public void setFloat(Object k, float f) {
		put(k, f);
	}

	/**
	 * ��˫���ȸ�������������޷��ӻ������ݷ���һ����Ч��˫���ȸ��������򷵻� 0��
	 */
	public double getDouble(Object k) {
		return getDouble(k, 0d);
	}

	/**
	 * ��˫���ȸ�������������޷��ӻ������ݷ���һ����Ч��˫���ȸ���������ֱ�ӷ��صڶ���������
	 */
	public double getDouble(Object k, double fallback) {
		Object v = get(k);

		if (v instanceof Number) {
			return ((Number) v).doubleValue();
		}

		try {
			return Double.parseDouble(v.toString());
		} catch (Throwable e) {
			return fallback;
		}
	}

	/**
	 * ���˫���ȸ��������󣬵���ʵ�ʴ洢���ͽ�ת��Ϊ�ַ������������8λС������
	 */
	public Object put(Object k, Double d) {
		return put(k, d.doubleValue());
	}

	/**
	 * ��˫���ȸ�����д��ʵ�ʴ洢���ͽ�ת��Ϊ�ַ������������8λС������
	 */
	public Object put(Object k, double d) {
		String s = new java.text.DecimalFormat("0.########").format(d);
		return put(k, s);
	}

	/** same as put(Object,double) */
	public void setDouble(Object k, double d) {
		put(k, d);
	}

	/**
	 * �����������������������Ϊ null�� �򷵻� null��
	 * 
	 * @throws NumberFormatException
	 *             ����������ݲ�����Ҫ��Ļ���
	 */
	public BigInteger getBigInteger(Object k) {
		Object v = get(k);

		if (v instanceof BigInteger) {
			return (BigInteger) v;
		}

		return v != null ? new BigInteger(v.toString()) : null;
	}

	/**
	 * ���󶨵������������������Ϊ null�� �򷵻� null��
	 * 
	 * @throws NumberFormatException
	 *             ����������ݲ�����Ҫ��Ļ���
	 */
	public BigDecimal getBigDecimal(Object k) {
		Object v = get(k);

		if (v instanceof BigDecimal) {
			return (BigDecimal) v;
		}

		return v != null ? new BigDecimal(v.toString()) : null;
	}

	public Date getDate(Object k) {
		return getDate(k, null);
	}

	public Date getDate(Object k, String format) {
		Object v = get(k);

		if (v == null) {
			return null;
		} else if (v instanceof Date) {
			return (Date) v;
		}

		try {
			return Dates.parse(v.toString(), format);
		} catch (Throwable e) {
			return null;
		}
	}

	/** same as put(Object,Object) */
	public void setDate(Object k, Date d) {
		put(k, d);
	}

	//////////////////////////////////////////////////////////////////
	// ��������չ���͵Ķ�д: ���� Class, Array, byte length, time span

	/**
	 * ������������������Ϊ null���򷵻� null��
	 * 
	 * @throws ClassNotFoundException
	 *             ����Ҳ����ࡣ
	 */
	public Class<?> getClass(Object k) throws ClassNotFoundException {
		return getClass(k, null);
	}

	/**
	 * ������������������Ϊ null����ֱ�ӷ��صڶ���������
	 * 
	 * @throws ClassNotFoundException
	 *             ����Ҳ����ࡣ
	 */
	public Class<?> getClass(Object k, Class<?> fallback) throws ClassNotFoundException {
		Object v = get(k);

		if (v instanceof Class) {
			return (Class<?>) v;
		}

		return v == null ? fallback : Class.forName(v.toString());
	}

	/**
	 * ���ַ����������Ĭ���� ',' �ָ��������������Ϊ null���򷵻� null�� �ַ����﷨��ο� Strings.splist ������
	 */
	public String[] getArray(Object k) {
		return getArray(k, ',');
	}

	/**
	 * ���ַ������������ָ���ָ��������������������Ϊ null���򷵻� null�� �ַ����﷨��ο� Strings.splist ������
	 */
	public String[] getArray(Object k, char delim) {
		Object v = get(k);

		if (v == null) {
			return null;
		}

		if (k.getClass().isArray()) {
			return (String[]) v;
		}

		return Strings.splist(v.toString(), delim);
	}

	private final static String[] ts_unit = { "ms", "s", "m", "h", "d" };

	/**
	 * ��ʱ�䳤�ȣ��Ժ���ƣ���������޷��ӻ������ݷ���һ��ʱ�䳤�ȣ��򷵻� 0�� ֧�ֵ�ʱ�䵥λ�У�d,h,m,s,ms��û�е�λ��ʾ��������
	 */
	public long getDuration(Object k) {
		return getDuration(k, "0ms");
	}

	/**
	 * ��ʱ�䳤�ȣ��Ժ���ƣ���������޷��ӻ������ݷ���һ��ʱ�䳤�ȣ�������ڶ����������ء� ֧�ֵ�ʱ�䵥λ�У�d,h,m,s,ms��û�е�λʱ����
	 * fallback �ĵ�λ�������Ҳû�У����ʾ��������
	 */
	public long getDuration(Object k, String fallback) {
		String[] arr = tokenizeDuration(getString(k));
		String[] arr2 = null;
		float f;

		if (arr[1].length() == 0) {
			arr2 = tokenizeDuration(fallback);
			if (arr[0].length() == 0)
				arr = arr2;
			else
				arr[1] = Strings.coalesce(arr2[1], ts_unit[0]);
		}

		try {
			f = Float.parseFloat(arr[0]);
		} catch (Throwable e) {
			return 0L;
		}

		long[] scale = { 1L, 1000L, 60000L, 3600000L, 3600000L * 24L };

		for (int i = 0; i < ts_unit.length; i++) {
			if (ts_unit[i] == arr[1]) {
				return (long) (f * scale[i]);
			}
		}

		return (long) f;
	}

	private String[] tokenizeDuration(String s) {
		String[] arr = { "", "" };
		if (s == null)
			return arr;
		s = s.trim();
		int len = s.length();
		if (len == 0)
			return arr;

		for (int i = 0; i < ts_unit.length; i++) {
			String u = ts_unit[i];
			int l = u.length();
			if (s.regionMatches(true, len - l, u, 0, l)) {
				if (len > l)
					arr[0] = s.substring(0, len - l);
				arr[1] = u;
				return arr;
			}
		}

		arr[0] = s;
		return arr;
	}

	/**
	 * ���ֽڳ��ȶ�������޷��ӻ������ݷ���һ���ֽڳ��ȣ��򷵻� 0�� ֧�ֵĳ��ȵ�λ�У�G,M,K ��û�е�λ��ʾ�ֽ�����
	 */
	public long getByteLength(Object k) {
		return getByteLength(k, 0L);
	}

	/**
	 * ���ֽڳ��ȶ�������޷��ӻ������ݷ���һ���ֽڳ��ȣ�������ڶ����������ء� ֧�ֵĳ��ȵ�λ�У�G,M,K ��û�е�λ��ʾ�ֽ�����
	 */
	public long getByteLength(Object k, String fallback) {
		String s = getString(k);
		return parseByteLength(Strings.isEmpty(s) ? fallback : s);
	}

	/**
	 * ���ֽڳ��ȶ�������޷��ӻ������ݷ���һ���ֽڳ��ȣ��򷵻صڶ��������� ֧�ֵĳ��ȵ�λ�У�G,M,K ��û�е�λ��ʾ�ֽ�����
	 */
	public long getByteLength(Object k, long fallback) {
		String s = getString(k);
		return Strings.isEmpty(s) ? fallback : parseByteLength(s);
	}

	/**
	 * ����һ���ֽڳ��ȡ�
	 * 
	 * @param s
	 *            ����Ϊnull����λ�ַ������ִ�Сд��
	 */
	private long parseByteLength(String s) {
		char[] unit = { 'G', 'M', 'K' };
		long[] scale = { 1024L * 1024L * 1024L, 1024L * 1024L, 1024L, 1L };
		int i = unit.length, c = s.length();

		if (c > 0) {
			char ch = s.charAt(--c);

			if (!Character.isDigit(ch)) {
				for (i = 0;;) {
					if (Character.toUpperCase(ch) == unit[i]) {
						s = s.substring(0, c);
						break;
					}
					if (++i == unit.length) {
						return 0L;
					}
				}
			}
		}

		try {
			return Long.parseLong(s) * scale[i];
		} catch (Throwable e) {
			return 0L;
		}
	}
}
