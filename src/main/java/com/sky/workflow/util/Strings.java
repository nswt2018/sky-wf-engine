/*
 * Strings.java
 * modico.net (lihw@jbbis.com.cn), 2006-09-05
 *
 * Revisions:
 *	2009-05-08, modico:
 *		Rewrite normalizeSpace;
 *	2009-09-17, modico:
 *		Add method replace;
 *		Rename method split to splist;
 *	2010-03-29, modico:
 *		BUGFIX: method isNumber - add 'digi' logic;
 *	2010-04-20, modico:
 *		Add method normalizePath;
 *	2010-08-26, modico:
 *		Overload method toHexString(), add parameter 'prefix';
 *		Add method encode();
 */

package com.sky.workflow.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public final class Strings {
	// JDK 5 supports replace(CharSequence t, CharSequence r);
	/**
	 * 将字符串 s 中的所有子字符串 t 替换成 r。
	 * 
	 * @param s
	 *            原始字符串
	 * @param t
	 *            待替换的字符串
	 * @param r
	 *            用于替换的字符串
	 */
	public static String replace(String s, String t, String r) {
		// 如果没有找到待替换的字符串, 直接返回原始字符串
		int i = s.indexOf(t);
		if (i < 0) {
			return s;
		}

		StringBuffer b = new StringBuffer(s);
		int ct = t.length();
		int cr = r.length();

		do {
			// 此时 i 指向待替换字符串的起始位置
			b.replace(i, i + ct, r);
			i += cr; // 此后 i 指向下一次搜索的起始位置
		} while ((i = t.indexOf(t, i)) >= 0);

		return b.toString();
	}

	/** @deprecated */
	public static String[] split(String text, char delimChar) {
		return splist(text, delimChar);
	}

	/**
	 * 将一个文本按照指定分隔符切分出一个字符串数组。 特性: 支持引号(包括引号内的转义符: 两个连续的引号表示一个普通的引号字符);
	 * 不合规的引号字符串被当作普通的字符串; 忽略分隔符前后的空白符。
	 * 
	 * @param text
	 *            文本
	 * @param delimChar
	 *            分隔字符
	 */
	public static String[] splist(String text, char delimChar) {
		ArrayList<String> list = new ArrayList<String>();
		int i = 0, len = text.length();
		char ch = '\0';
		boolean hasMore = false; // 遇到一个分隔符后就将置这个标志, 遇到内容起始, 就消除这个标志

		for (;;) {
			// 忽略空白符
			for (; i < len && Character.isWhitespace(ch = text.charAt(i)); i++)
				;

			if (i >= len) {
				// 要是最后一个分隔符后面没有内容，得算作一个空字符串。
				if (hasMore) {
					list.add("");
				}
				break;
			}

			int ci = i; // 内容起始处

			if (ch == '"' || ch == '\'') {
				int q2 = -1; // 结尾的引号处
				boolean hasEsc = false; // 引文内是否有引号转义

				// 先测试引号字符串的合规性以及一般要素: 边界以及是否存在字符串转义。
				while (++i < len && q2 < 0) {
					if (text.charAt(i) == ch) {
						if (i + 1 < len && text.charAt(i + 1) == ch) {
							hasEsc = true;
							i++;
						} else {
							q2 = i;
						}
					}
				}

				// 处理合规的引号字符串
				if (q2 >= 0) {
					// 忽略右引号后边的空白符
					for (; i < len && Character.isWhitespace(ch = text.charAt(i)); i++)
						;

					// 额外的合规性检查：引号字符串后面必须要么是结束，要么是一个分隔符，而不能是其它任何字符。
					if (i >= len || ch == delimChar) {
						if (hasMore = (ch == delimChar)) {
							i++;
						}

						String t = text.substring(ci + 1, q2); // 引号字符不包含在内

						// 处理转义
						if (hasEsc) {
							ch = text.charAt(ci);
							char[] arr = { ch, ch };
							t = replace(t, String.valueOf(arr), String.valueOf(ch));
						}

						list.add(t);
						continue;
					}
				}

				i = ci; // 尝试引号格式失败
			}

			// 界定普通值范围（可能包括下一个分隔符前的空白符，在下一句处理）
			for (; i < len && delimChar != text.charAt(i); i++)
				;

			list.add(text.substring(ci, i).trim());

			if (hasMore = (i < len)) {
				i++;
			}
		}

		return Collections.toStringArray(list);
	}

	private static char[] hex_chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	/**
	 * 将一个字节数组的值以十六进制形式的字符串表示。
	 * 
	 * @param bytes
	 *            字节数组
	 */
	public static String toHexString(byte[] bytes) {
		return toHexString(bytes, null);
	}

	public static String toHexString(byte[] bytes, String prefix) {
		StringBuffer b = new StringBuffer();

		for (int i = 0, c = bytes.length; i < c; i++) {
			if (prefix != null)
				b.append(prefix);
			b.append(hex_chars[bytes[i] >> 4 & 0x0F]).append(hex_chars[bytes[i] & 0x0F]);
		}

		return b.toString();
	}

	// Revisions:
	// 2008-05-05, modico:
	// 第一个参数由 String 变为 CharSequence 接口;
	/**
	 * 找到范围内任意一个字符第一次出现的位置, 类似于 C 语言标准库的 strpbrk().
	 */
	public static int findAny(CharSequence s, String range, int i) {
		for (int c = s.length(); i < c; i++) {
			if (range.indexOf(s.charAt(i)) >= 0) {
				return i;
			}
		}

		return -1;
	}

	// lihw@jbbis.com.cn, 2008-5-5
	/**
	 * 找到第一个不在范围内的字符的位置
	 */
	public static int findAnyNot(CharSequence s, String range, int i) {
		for (int c = s.length(); i < c; i++) {
			if (range.indexOf(s.charAt(i)) < 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 是否是数值表达式. 满足以下格式：[+|-] {. [0-9]+ | [0-9]+ [. [0-9]*]} [{e|E} [+|-]
	 * [0-9]+] 正负符号 整数 句点 小数 科学计数
	 */
	public static boolean isNumber(String s) {
		final int c = s != null ? s.length() : 0;
		if (c == 0) {
			return false;
		}

		char ch;
		boolean sign = true; // 当前位置是否允许 +/- 号
		boolean dot = false; // 是否已出现过小数点, 最多只能出现一次
		boolean exp = false; // 是否已出现过指数符, 最多只能出现一次
		boolean digi = true; // 是否缺数字

		for (int i = 0; i < c; i++) {
			ch = s.charAt(i);

			if (sign) {
				sign = false;

				if (ch == '-' || ch == '+') {
					continue;
				}
			}

			if (ch == '.') {
				if (dot || exp) {
					return false;
				}

				dot = true;
				continue;
			}

			if (ch == 'E' || ch == 'e') {
				// 如果之前还没有数字，或是之前已有过 E，则表示错误。
				if (digi || exp) {
					return false;
				}

				exp = true;
				sign = true; // E 后面可以紧跟正负符号
				digi = true; // E 部分必须有数字。
				continue;
			}

			if (ch < '0' || ch > '9') {
				return false;
			}

			digi = false; // 数字出现过了。
		}

		return !digi;
	}

	/**
	 * 判断参数是否是空值或空串.
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * 返回参数列表中的第一个非空字符串.
	 */
	public static String coalesce(String first, String second) {
		return isEmpty(first) ? second : first;
	}

	/**
	 * 字符编码转换.
	 */
	public static String convert(String str, String from_enc, String to_enc) throws UnsupportedEncodingException {
		byte[] bs = str.getBytes(from_enc);
		String ts = new String(bs, to_enc);
		return ts;
	}

	public static final int PAD_LEFT = 1;
	public static final int PAD_RIGHT = 2;

	/**
	 * 补齐字符串.
	 */
	public static String pad(String s, int length, char stuff, int side) {
		int d = length - s.length();

		if (d > 0) {
			char[] a = new char[d];
			Arrays.fill(a, stuff);
			String p = String.valueOf(a);

			if (side == PAD_LEFT) {
				return p + s;
			} else if (side == PAD_RIGHT) {
				return s + p;
			}
		}

		return s;
	}

	/**
	 * 删除前后空白符, 将中间的2个以上的连续空白符替换成1个空格, 将非空格的空白符替换成空格. 如果 bTrim 参数为 false,
	 * 则前后空白符的处理方式等同中间的处理.
	 */
	public static String normalizeSpace(String str, boolean bTrim) {
		final int len = str.length();
		boolean leading = bTrim;
		boolean hit = false;
		StringBuffer b = new StringBuffer(len);

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (Character.isWhitespace(ch)) {
				hit = !leading;
			} else {
				if (leading) {
					leading = false;
				} else if (hit) {
					hit = false;
					b.append(' ');
				}

				b.append(ch);
			}
		}

		if (!bTrim && hit) {
			b.append(' ');
		}

		return b.toString();
	}

	/**
	 * 把路径中的 '\' 换成 '/', 并消除 '.' 和 '..' 段.
	 * 
	 * @throws IllegalArgumentException
	 *             如果 '..' 段多于可用的父路径, 导致最后结果中还是会出现 '..' 段
	 */
	public static String normalizePath(String str) {
		try {
			String s = new URI(str.replace('\\', '/')).normalize().toString();
			if (s.indexOf("..") < 0) {
				return s;
			}
		} catch (Throwable e) {
		}

		throw new IllegalArgumentException("String:" + str);
	}

	//////////////////////////////////////////////////////////////////
	// Format facility

	public static String format(String st, MapProvider named) {
		// next 指向第一个标记符; end 指向第二个标记符;
		// last 指向开始搜索的位置; copyFrom 指向开始复制的位置.
		int next, end, last = 0, copyFrom = 0;
		boolean changed = false;
		StringBuffer buf = null;
		Object value;
		String result = st;
		char mark = '%', ch;

		while (true) {
			next = st.indexOf(mark, last);

			if (next < 0) {
				break;
			}

			end = st.indexOf(mark, next + 1);

			if (end < 0) {
				break;
			}

			ch = st.charAt(next + 1);

			if (ch >= '0' && ch <= '9') {
				last = next + 2;
				continue;
			}

			if (!changed) {
				changed = true;
				buf = new StringBuffer();
			}

			if (next > copyFrom) {
				buf.append(st.substring(copyFrom, next));
			}

			last = end + 1;
			copyFrom = last;

			if (end == ++next) {
				// 两个连续的标记符表示一个标记符字符
				buf.append(mark);
			} else {
				value = named.get(st.substring(next, end));

				if (value != null) {
					buf.append(value);
				}
			}
		}

		if (changed) {
			buf.append(st.substring(copyFrom));
			result = buf.toString();
		}

		return result;
	}

	/**
	 * 格式化一个字符串, 支持在文本模板中使用 %数字 来代表一个参数。
	 * 
	 * @param st
	 *            文本模板
	 * @param data
	 *            可以是一个数组, 对象数组或任何基本类型数组; 也可以是一个非数组对象, 作为单一数据。
	 */
	public static String format(String st, Object data) {
		Object array = data;

		if (data != null && !data.getClass().isArray()) {
			array = new Object[] { data };
		}

		int next, last = 0, copyFrom = 0;
		int index;
		int length = st.length();
		int dataCount = (array == null ? 0 : Array.getLength(array));
		boolean changed = false;
		StringBuffer buf = null;
		String result = st;
		char mark = '%', ch = '\0';

		while (true) {
			next = st.indexOf(mark, last);

			if (next < 0 || next + 1 == length) {
				break;
			}

			ch = st.charAt(next + 1);

			if (ch != '%' && (ch < '0' || ch > '9')) {
				last++;
				continue;
			}

			if (!changed) {
				changed = true;
				buf = new StringBuffer();
			}

			if (next > copyFrom) {
				buf.append(st.substring(copyFrom, next));
			}

			if (ch == '%') {
				buf.append(ch);
				last += 2;
			} else {
				for (last = ++next; last < length; last++) {
					ch = st.charAt(last);

					if (ch < '0' || ch > '9') {
						break;
					}
				}
			}

			copyFrom = last;
			index = Integer.parseInt(st.substring(next, last), 10) - 1;

			if (index >= 0 && index < dataCount) {
				buf.append(Array.get(array, index));
			}
		}

		if (changed) {
			buf.append(st.substring(copyFrom));
			result = buf.toString();
		}

		return result;
	}

	/**
	 * 将字符串转化成ASCII编码
	 * 
	 * @param args
	 *            String[]
	 */
	public static String String2Ascii(String str, String charset, String suffix) throws Exception {
		String _s = null;

		byte[] b = str.getBytes(charset);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < b.length; i++) {
			_s = Integer.toHexString((int) b[i]);
			sb.append(suffix).append(_s.substring(_s.length() - 2));
		}
		return sb.toString();
	}

	/**
	 * unicode字符集utf-8编码方式转换为字符串 utf-8是8位的编码方式， 如汉字：中国预测技术大全
	 * utf-8：%e4%b8%ad%e5%9b%bd%e9%a2%84%e6%b5%8b%e6%8a%80%e6%9c%af%e5%a4%a7%e5%85%a8
	 * 
	 * @param s
	 *            String
	 * @param bits
	 *            位数,英文数字1个字节2位，汉字2-3字节包括%号即6-8位不等
	 * @return String
	 */
	public static String unicode2Char(String s) {
		int i = 0, j = 0;

		if (s != null) {
			// 将空格的ascii编码转换成GBK
			s = s.replaceAll("%20", " ");
		}

		s = s.replaceAll("%", "");
		byte[] sBytes = new byte[s.length() / 2];
		try {
			while (true) {
				sBytes[j++] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
				if ((i += 2) == s.length())
					break;
			}
			s = new String(sBytes, "utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return s;
	}

	private static StringBuffer buffer;

	public static String bytes2HEX(byte[] ib) {
		buffer = new StringBuffer();
		for (int i = 0; i < ib.length; i++) {
			buffer.append(byte2HEX(ib[i]));
		}
		return buffer.toString();
	}

	private static char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String byte2HEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F]; // 右移后高四位成了低四位,0X0F=1111=15
		ob[1] = Digit[ib & 0X0F]; // 与1111后高四位全部丢失
		return new String(ob);
	}

	/**
	 * 把1个16进制数的ASCII码字符串转换成一个byte[]数组
	 */
	public static byte[] HEX2bytes(String s) {
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

	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			// System.out.println(tmp);
			bString += tmp.substring(tmp.length() - 4);
			// System.out.println(bString);
		}
		return bString;
	}

	// ------------------------------------------------------
	public static String binaryString2hexString(String bString) {
		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
			return null;
		StringBuffer tmp = new StringBuffer();
		int iTmp = 0;
		for (int i = 0; i < bString.length(); i += 4) {
			iTmp = 0;
			for (int j = 0; j < 4; j++) {
				iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
			}
			tmp.append(Integer.toHexString(iTmp));
		}
		return tmp.toString();
	}

	/*
	 * public String tranScienTobasic(String ss) { if(ss.indexOf("e")==-1 ||
	 * ss.indexOf("E")==-1) return ss; if(ss==null || ss.equals("")) return ss;
	 * int len, len1, len2; len = ss.length() - 2; len1 = }
	 */

	public static void main(String[] args) throws Exception {
		// Strings s = new Strings();
		// String s1, s2;
		// s1 =
		// "%2B%e4%b8%ad%e5%9b%bd%e9%a2%84%e6%b5%8b%e6%8a%80%e6%9c%af%e5%a4%a7%e5%85%a8";
		// s1 = "%20%2B";
		// s2 = s.unicode2Char(s1);
		//
		// System.out.println(s2);
		String s = "+ADwAaAB0AG0APgA8AGIAbwBkAHkAPgA8AHMAYwByAGkAcAB0AD4AYQBsAGUAcgB0ACgAMQApADsAPAAvAHMAYwByAGkAcAB0AD4APAAvAGIAbwBkAHkAPgA8AC8AaAB0AG0APg-";
		// System.out.println(Strings.String2Ascii(s, "utf-8", "%"));
		System.out.println(Strings.String2Ascii(s, "utf-7", "%"));
	}

}
