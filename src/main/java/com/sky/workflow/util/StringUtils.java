package com.sky.workflow.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * <p>
 * Title: 字符串工具
 * </p>
 * <p>
 * Description: 字符串翻译转换工具
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: BEIDA JADE BIRD
 * </p>
 * 
 * @author fanghao
 * @version 1.0
 */
public class StringUtils {

	public String getVersion() {
		return "version 1.0 2002/12/10 by fanghao";
	}

	public StringUtils() {
	}

	/**
	 * 将字符串以参数sep为分隔符分合成字符串数组。
	 * 
	 * @param in
	 * @param sep
	 * @return
	 */
	public static String[] split(String in, String sep) {
		Vector<String> vector = new Vector<String>();
		for (StringTokenizer stringtokenizer = new StringTokenizer(in, sep); stringtokenizer.hasMoreTokens(); vector
				.addElement(stringtokenizer.nextToken()))
			;
		String as[] = new String[vector.size()];
		for (int i = 0; i < vector.size(); i++)
			as[i] = (String) vector.elementAt(i);
		return as;
	}

	public static String[] split(String in) {
		return split(in, ",");
	}

	/**
	 * 将给定的字符串重复count次
	 * 
	 * @param value
	 * @param count
	 * @return
	 */
	public static String repeat(String value, int count) {
		if (count < 0)
			return "";
		StringBuffer buf = new StringBuffer(count * value.length());
		for (int i = 0; i < count; i++) {
			buf.append(value);
		}
		return buf.toString();
	}

	/**
	 * 去掉多余的"0"。如："12.980000000"则返回"12.9800"，"12.98877"则返回"12.98877"
	 * 
	 * @param value
	 * @return
	 */
	public static String removeTailZero(String value) {
		String ret = "";
		int i;
		if (value.indexOf(".") <= 0)
			return value;
		for (i = value.length() - 1; i >= 0; i--) {
			if (!value.substring(i, i + 1).equals("0"))
				break;
		}

		if (value.substring(i, i + 1).equals(".")) {
			ret = value.substring(0, i + 1) + "00";
		} else {
			int pos = value.indexOf(".");
			int len = i - pos;
			ret = value.substring(0, i + 1);
			if (len == 1) {
				ret += "0";
			}
		}
		return ret;
	}

	/**
	 * 格式化给定的double类型的值，小数点后保留8位。
	 * 
	 * @param value
	 * @return
	 */
	public static String formatDecimal(double value) {
		String sValue = formatDecimal(value, 8);
		sValue = removeTailZero(sValue);
		return sValue;
	}

	/**
	 * 格式化给定的double类型的值，小数点后保留digit位。
	 * 
	 * @param value
	 * @param digit
	 * @return
	 */
	public static String formatDecimal(double value, int digit) {
		DecimalFormat f = new DecimalFormat("0." + repeat("0", digit));
		String sValue = f.format(value);
		return sValue;
	}

	/**
	 *
	 * <p>
	 * Description: 字符串翻译转换工具，不支持叠代替换
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2002
	 * </p>
	 * 
	 * @param org
	 *            原始字符串
	 * @param s1
	 *            被替换掉的字符串
	 * @param s2
	 *            新的字符串
	 * @return 替换后的字符串
	 */
	public static String replace(String org, String s1, String s2) {
		int i = 0, j = 0;
		StringBuffer buf = new StringBuffer();
		while ((j = org.indexOf(s1, i)) >= 0) {
			buf.append(org.substring(i, j));
			buf.append(s2);
			i = j + s1.length();
		}
		buf.append(org.substring(i));
		return buf.toString();
	}

	/**
	 * 将字符串数组转换成string
	 * 
	 * @param args
	 *            字符串数组
	 * @return 如果为null或数组为空则返回空串
	 * @throws Exception
	 */
	public static String toString(String[] args) throws Exception {
		StringBuffer buf = new StringBuffer();
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				buf.append(args[i]).append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
		}
		return buf.toString();
	}

	private static char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String byte2Hex(byte ib) {
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F]; // 右移后高四位成了低四位,0X0F=1111=15
		ob[1] = Digit[ib & 0X0F]; // 与1111后高四位全部丢失
		return new String(ob);
	}

	/**
	 * 把1个16进制数的ASCII码字符串转换成一个byte[]数组
	 */
	public static byte[] hex2Bytes(String s) {
		char[] c = s.toCharArray();
		if (c.length % 2 != 0)
			return new byte[] { 0 };
		byte[] b = new byte[c.length / 2];
		byte b0, b1;
		int j = 0;

		for (int i = 0; i < c.length; i = i + 2) {
			b0 = Byte.parseByte("0" + c[i], 16);
			b1 = Byte.parseByte("0" + c[i + 1], 16);
			b[j++] = (byte) ((b0 << 4) | b1);
		}
		return b;
	}

	public static String bytes2Hex(byte[] ib) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < ib.length; i++) {
			buffer.append(byte2Hex(ib[i]));
		}
		return buffer.toString();
	}

	public static byte[] gzipBytes(byte[] bytes) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(baos);

		zos.write(bytes);
		zos.close();
		byte[] result = baos.toByteArray();
		baos.close();
		return result;
	}

	public static byte[] ungzipBytes(byte[] bytes) throws Exception {
		int realLength; // 实际的长度
		final int length = 1024; // 每次读取的长度
		byte[] buffer = new byte[length];
		byte[] result = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPInputStream zos = new GZIPInputStream(bais);

		while ((realLength = zos.read(buffer, 0, length)) != -1) {
			baos.write(buffer, 0, realLength);
		}
		result = baos.toByteArray();
		baos.close();
		bais.close();
		return result;
	}

}
