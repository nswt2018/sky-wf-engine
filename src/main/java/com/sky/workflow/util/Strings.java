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
	 * ���ַ��� s �е��������ַ��� t �滻�� r��
	 * 
	 * @param s
	 *            ԭʼ�ַ���
	 * @param t
	 *            ���滻���ַ���
	 * @param r
	 *            �����滻���ַ���
	 */
	public static String replace(String s, String t, String r) {
		// ���û���ҵ����滻���ַ���, ֱ�ӷ���ԭʼ�ַ���
		int i = s.indexOf(t);
		if (i < 0) {
			return s;
		}

		StringBuffer b = new StringBuffer(s);
		int ct = t.length();
		int cr = r.length();

		do {
			// ��ʱ i ָ����滻�ַ�������ʼλ��
			b.replace(i, i + ct, r);
			i += cr; // �˺� i ָ����һ����������ʼλ��
		} while ((i = t.indexOf(t, i)) >= 0);

		return b.toString();
	}

	/** @deprecated */
	public static String[] split(String text, char delimChar) {
		return splist(text, delimChar);
	}

	/**
	 * ��һ���ı�����ָ���ָ����зֳ�һ���ַ������顣 ����: ֧������(���������ڵ�ת���: �������������ű�ʾһ����ͨ�������ַ�);
	 * ���Ϲ�������ַ�����������ͨ���ַ���; ���Էָ���ǰ��Ŀհ׷���
	 * 
	 * @param text
	 *            �ı�
	 * @param delimChar
	 *            �ָ��ַ�
	 */
	public static String[] splist(String text, char delimChar) {
		ArrayList<String> list = new ArrayList<String>();
		int i = 0, len = text.length();
		char ch = '\0';
		boolean hasMore = false; // ����һ���ָ�����ͽ��������־, ����������ʼ, �����������־

		for (;;) {
			// ���Կհ׷�
			for (; i < len && Character.isWhitespace(ch = text.charAt(i)); i++)
				;

			if (i >= len) {
				// Ҫ�����һ���ָ�������û�����ݣ�������һ�����ַ�����
				if (hasMore) {
					list.add("");
				}
				break;
			}

			int ci = i; // ������ʼ��

			if (ch == '"' || ch == '\'') {
				int q2 = -1; // ��β�����Ŵ�
				boolean hasEsc = false; // �������Ƿ�������ת��

				// �Ȳ��������ַ����ĺϹ����Լ�һ��Ҫ��: �߽��Լ��Ƿ�����ַ���ת�塣
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

				// ����Ϲ�������ַ���
				if (q2 >= 0) {
					// ���������ź�ߵĿհ׷�
					for (; i < len && Character.isWhitespace(ch = text.charAt(i)); i++)
						;

					// ����ĺϹ��Լ�飺�����ַ����������Ҫô�ǽ�����Ҫô��һ���ָ������������������κ��ַ���
					if (i >= len || ch == delimChar) {
						if (hasMore = (ch == delimChar)) {
							i++;
						}

						String t = text.substring(ci + 1, q2); // �����ַ�����������

						// ����ת��
						if (hasEsc) {
							ch = text.charAt(ci);
							char[] arr = { ch, ch };
							t = replace(t, String.valueOf(arr), String.valueOf(ch));
						}

						list.add(t);
						continue;
					}
				}

				i = ci; // �������Ÿ�ʽʧ��
			}

			// �綨��ֵͨ��Χ�����ܰ�����һ���ָ���ǰ�Ŀհ׷�������һ�䴦��
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
	 * ��һ���ֽ������ֵ��ʮ��������ʽ���ַ�����ʾ��
	 * 
	 * @param bytes
	 *            �ֽ�����
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
	// ��һ�������� String ��Ϊ CharSequence �ӿ�;
	/**
	 * �ҵ���Χ������һ���ַ���һ�γ��ֵ�λ��, ������ C ���Ա�׼��� strpbrk().
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
	 * �ҵ���һ�����ڷ�Χ�ڵ��ַ���λ��
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
	 * �Ƿ�����ֵ���ʽ. �������¸�ʽ��[+|-] {. [0-9]+ | [0-9]+ [. [0-9]*]} [{e|E} [+|-]
	 * [0-9]+] �������� ���� ��� С�� ��ѧ����
	 */
	public static boolean isNumber(String s) {
		final int c = s != null ? s.length() : 0;
		if (c == 0) {
			return false;
		}

		char ch;
		boolean sign = true; // ��ǰλ���Ƿ����� +/- ��
		boolean dot = false; // �Ƿ��ѳ��ֹ�С����, ���ֻ�ܳ���һ��
		boolean exp = false; // �Ƿ��ѳ��ֹ�ָ����, ���ֻ�ܳ���һ��
		boolean digi = true; // �Ƿ�ȱ����

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
				// ���֮ǰ��û�����֣�����֮ǰ���й� E�����ʾ����
				if (digi || exp) {
					return false;
				}

				exp = true;
				sign = true; // E ������Խ�����������
				digi = true; // E ���ֱ��������֡�
				continue;
			}

			if (ch < '0' || ch > '9') {
				return false;
			}

			digi = false; // ���ֳ��ֹ��ˡ�
		}

		return !digi;
	}

	/**
	 * �жϲ����Ƿ��ǿ�ֵ��մ�.
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * ���ز����б��еĵ�һ���ǿ��ַ���.
	 */
	public static String coalesce(String first, String second) {
		return isEmpty(first) ? second : first;
	}

	/**
	 * �ַ�����ת��.
	 */
	public static String convert(String str, String from_enc, String to_enc) throws UnsupportedEncodingException {
		byte[] bs = str.getBytes(from_enc);
		String ts = new String(bs, to_enc);
		return ts;
	}

	public static final int PAD_LEFT = 1;
	public static final int PAD_RIGHT = 2;

	/**
	 * �����ַ���.
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
	 * ɾ��ǰ��հ׷�, ���м��2�����ϵ������հ׷��滻��1���ո�, ���ǿո�Ŀհ׷��滻�ɿո�. ��� bTrim ����Ϊ false,
	 * ��ǰ��հ׷��Ĵ���ʽ��ͬ�м�Ĵ���.
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
	 * ��·���е� '\' ���� '/', ������ '.' �� '..' ��.
	 * 
	 * @throws IllegalArgumentException
	 *             ��� '..' �ζ��ڿ��õĸ�·��, ����������л��ǻ���� '..' ��
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
		// next ָ���һ����Ƿ�; end ָ��ڶ�����Ƿ�;
		// last ָ��ʼ������λ��; copyFrom ָ��ʼ���Ƶ�λ��.
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
				// ���������ı�Ƿ���ʾһ����Ƿ��ַ�
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
	 * ��ʽ��һ���ַ���, ֧�����ı�ģ����ʹ�� %���� ������һ��������
	 * 
	 * @param st
	 *            �ı�ģ��
	 * @param data
	 *            ������һ������, ����������κλ�����������; Ҳ������һ�����������, ��Ϊ��һ���ݡ�
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
	 * ���ַ���ת����ASCII����
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
	 * unicode�ַ���utf-8���뷽ʽת��Ϊ�ַ��� utf-8��8λ�ı��뷽ʽ�� �纺�֣��й�Ԥ�⼼����ȫ
	 * utf-8��%e4%b8%ad%e5%9b%bd%e9%a2%84%e6%b5%8b%e6%8a%80%e6%9c%af%e5%a4%a7%e5%85%a8
	 * 
	 * @param s
	 *            String
	 * @param bits
	 *            λ��,Ӣ������1���ֽ�2λ������2-3�ֽڰ���%�ż�6-8λ����
	 * @return String
	 */
	public static String unicode2Char(String s) {
		int i = 0, j = 0;

		if (s != null) {
			// ���ո��ascii����ת����GBK
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
		ob[0] = Digit[(ib >>> 4) & 0X0F]; // ���ƺ����λ���˵���λ,0X0F=1111=15
		ob[1] = Digit[ib & 0X0F]; // ��1111�����λȫ����ʧ
		return new String(ob);
	}

	/**
	 * ��1��16��������ASCII���ַ���ת����һ��byte[]����
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
