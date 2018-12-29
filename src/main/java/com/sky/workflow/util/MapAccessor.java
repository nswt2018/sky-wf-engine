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
 * Map 数据存取器，提供了扩展的按类型读写 Map 数据的方法集。 它没有实现 MapCollector 和 MapProvider
 * 声明的任何方法，这个任务应该 由扩展类去完成。
 */
public abstract class MapAccessor implements MapCollector, MapProvider {
	//////////////////////////////////////////////////////////////////
	// 以下是基本数据类型的读写扩展：包括 String, int, long, float, double
	// 也包括 BigInteger 和 BigDecimal 。

	/**
	 * 按字符串类型读。
	 */
	public String getString(Object k) {
		Object v = get(k);
		return v == null ? null : v.toString();
	}

	/**
	 * 按字符串类型读。但是如果基础值是 null 或字符串长度为 0，则直接返回第二个参数。
	 */
	public String getString(Object k, String fallback) {
		String s = getString(k);
		return Strings.isEmpty(s) ? fallback : s;
	}

	/**
	 * 按整数类型读。如果无法从基础数据返回一个有效的整数，则返回 0。
	 */
	public int getInt(Object k) {
		return getInt(k, 0);
	}

	/**
	 * 按整数类型读。如果无法从基础数据返回一个有效的整数，则直接返回第二个参数。
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
	 * 按整数类型写。实际存储类型将转换为 Integer 。
	 */
	public void put(Object k, int i) {
		put(k, new Integer(i));
	}

	/** same as put(Object,int) */
	public void setInt(Object k, int i) {
		put(k, i);
	}

	/**
	 * 按长整型读。如果无法从基础数据返回一个有效的整数，则返回 0。
	 */
	public long getLong(Object k) {
		return getLong(k, 0L);
	}

	/**
	 * 按长整型读。如果无法从基础数据返回一个有效的整数，则直接返回第二个参数。
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
	 * 按长整型写。实际存储类型将转换为 Long 。
	 */
	public void put(Object k, long l) {
		put(k, new Long(l));
	}

	/** same as put(Object,long) */
	public void setLong(Object k, long l) {
		put(k, l);
	}

	/**
	 * 按浮点数读。如果无法从基础数据返回一个有效的浮点数，则返回 0。
	 */
	public float getFloat(Object k) {
		return getFloat(k, 0f);
	}

	/**
	 * 按浮点数读。如果无法从基础数据返回一个有效的浮点数，则直接返回第二个参数。
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
	 * 按浮点数写。实际存储类型将转换为 Float 。
	 */
	public void put(Object k, float f) {
		put(k, new Float(f));
	}

	/** same as put(Object,float) */
	public void setFloat(Object k, float f) {
		put(k, f);
	}

	/**
	 * 按双精度浮点数读。如果无法从基础数据返回一个有效的双精度浮点数，则返回 0。
	 */
	public double getDouble(Object k) {
		return getDouble(k, 0d);
	}

	/**
	 * 按双精度浮点数读。如果无法从基础数据返回一个有效的双精度浮点数，则直接返回第二个参数。
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
	 * 存放双精度浮点数对象，但是实际存储类型将转换为字符串（保留最多8位小数）。
	 */
	public Object put(Object k, Double d) {
		return put(k, d.doubleValue());
	}

	/**
	 * 按双精度浮点数写。实际存储类型将转换为字符串（保留最多8位小数）。
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
	 * 按大整数读。如果基础数据为 null， 则返回 null。
	 * 
	 * @throws NumberFormatException
	 *             如果基础数据不符合要求的话。
	 */
	public BigInteger getBigInteger(Object k) {
		Object v = get(k);

		if (v instanceof BigInteger) {
			return (BigInteger) v;
		}

		return v != null ? new BigInteger(v.toString()) : null;
	}

	/**
	 * 按大定点数读。如果基础数据为 null， 则返回 null。
	 * 
	 * @throws NumberFormatException
	 *             如果基础数据不符合要求的话。
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
	// 以下是扩展类型的读写: 包括 Class, Array, byte length, time span

	/**
	 * 按类读。如果基础数据为 null，则返回 null。
	 * 
	 * @throws ClassNotFoundException
	 *             如果找不到类。
	 */
	public Class<?> getClass(Object k) throws ClassNotFoundException {
		return getClass(k, null);
	}

	/**
	 * 按类读。如果基础数据为 null，则直接返回第二个参数。
	 * 
	 * @throws ClassNotFoundException
	 *             如果找不到类。
	 */
	public Class<?> getClass(Object k, Class<?> fallback) throws ClassNotFoundException {
		Object v = get(k);

		if (v instanceof Class) {
			return (Class<?>) v;
		}

		return v == null ? fallback : Class.forName(v.toString());
	}

	/**
	 * 按字符串数组读，默认以 ',' 分隔。如果基础数据为 null，则返回 null。 字符串语法请参考 Strings.splist 方法。
	 */
	public String[] getArray(Object k) {
		return getArray(k, ',');
	}

	/**
	 * 按字符串数组读，以指定分隔符解析。如果基础数据为 null，则返回 null。 字符串语法请参考 Strings.splist 方法。
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
	 * 按时间长度（以毫秒计）读。如果无法从基础数据返回一个时间长度，则返回 0。 支持的时间单位有：d,h,m,s,ms。没有单位表示毫秒数。
	 */
	public long getDuration(Object k) {
		return getDuration(k, "0ms");
	}

	/**
	 * 按时间长度（以毫秒计）读。如果无法从基础数据返回一个时间长度，则解析第二个参数返回。 支持的时间单位有：d,h,m,s,ms。没有单位时参照
	 * fallback 的单位；如果它也没有，则表示毫秒数。
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
	 * 按字节长度读。如果无法从基础数据返回一个字节长度，则返回 0。 支持的长度单位有：G,M,K 。没有单位表示字节数。
	 */
	public long getByteLength(Object k) {
		return getByteLength(k, 0L);
	}

	/**
	 * 按字节长度读。如果无法从基础数据返回一个字节长度，则解析第二个参数返回。 支持的长度单位有：G,M,K 。没有单位表示字节数。
	 */
	public long getByteLength(Object k, String fallback) {
		String s = getString(k);
		return parseByteLength(Strings.isEmpty(s) ? fallback : s);
	}

	/**
	 * 按字节长度读。如果无法从基础数据返回一个字节长度，则返回第二个参数。 支持的长度单位有：G,M,K 。没有单位表示字节数。
	 */
	public long getByteLength(Object k, long fallback) {
		String s = getString(k);
		return Strings.isEmpty(s) ? fallback : parseByteLength(s);
	}

	/**
	 * 解析一个字节长度。
	 * 
	 * @param s
	 *            不能为null。单位字符不区分大小写。
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
