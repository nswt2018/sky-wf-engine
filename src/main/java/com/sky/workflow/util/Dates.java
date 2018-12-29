/*
 * Dates.java
 * modico.net (lihw@jbbis.com.cn), 2006-8-10
 */

package com.sky.workflow.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	private static String std1 = "yyyy-MM-dd";
	private static String std2 = "yyyy-MM-dd HH:mm:ss";
	private static String std3 = "yyyy-MM-dd HH:mm:ss.SSS";
	private static String cmp1 = "yyyyMMdd";
	private static String cmp2 = "yyyyMMddHHmmss";
	private static String cmp3 = "yyyyMMddHHmmss.SSS";

	public static String today() {
		return format(new Date(), std1);
	}

	public static String now() {
		return format(new Date(), std2);
	}

	public static String nowms() {
		return format(new Date(), std3);
	}

	// /////////////////////////////////////////////////////////////////////
	// formatToXXX family

	public static String format(Date d, String pattern) {
		return new SimpleDateFormat(pattern).format(d);
	}

	/**
	 * ��ʽ��Ϊ��׼����ʱ���ʽ.
	 */
	public static String formatToStandard(Date d) {
		return format(d, std2);
	}

	public static String formatToStandardTS(Date d) {
		return format(d, std3);
	}

	public static String formatToStandardDate(Date d) {
		return format(d, std1);
	}

	/**
	 * ��ʽ��Ϊ���ո�ʽ.
	 */
	public static String formatToCompact(Date d) {
		return format(d, cmp2);
	}

	public static String formatToCompactTS(Date d) {
		return format(d, cmp3);
	}

	public static String formatToCompactDate(Date d) {
		return format(d, cmp1);
	}

	// /////////////////////////////////////////////////////////////////////
	// parse family

	/** @deprecated */
	public static Date parseFromCompact(String s) {
		return parse(s);
	}

	/**
	 * �Զ��ж����õ�6��ģʽ�͵�ǰϵͳĬ�ϵ�ģʽ.
	 */
	public static Date parse(String s) {
		int len = s.length();
		if (len == 0) {
			return null;
		}

		String pattern = null;

		if (s.indexOf('-') > 0) {
			if (s.indexOf('T') > 0)
				s = s.replace('T', ' ');
			switch (len) {
			case 10:
				pattern = "yyyy-MM-dd";
				break;
			case 19:
				pattern = "yyyy-MM-dd HH:mm:ss";
				break;
			case 23:
				pattern = "yyyy-MM-dd HH:mm:ss.SSS";
				break;
			}
		} else {
			switch (len) {
			case 8:
				pattern = "yyyyMMdd";
				break;
			case 14:
				pattern = "yyyyMMddHHmmss";
				break;
			case 18:
				pattern = "yyyyMMddHHmmss.SSS";
				break;
			}
		}

		DateFormat fmt = pattern == null ? DateFormat.getDateTimeInstance() : new SimpleDateFormat(pattern);
		fmt.setLenient(false);

		return parse(s, fmt);
	}

	/**
	 * ��ָ����ʽ���ַ����н���һ������.
	 */
	public static Date parse(String source, String pattern) {
		if (pattern == null) {
			return parse(source);
		}

		DateFormat format = new SimpleDateFormat(pattern);
		format.setLenient(false);
		return parse(source, format);
	}

	public static Date parse(String source, DateFormat format) {
		try {
			return format.parse(source);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date expression: " + source);
		}
	}

	/**
	 * ��ȡ��������֮�������
	 * 
	 * @param begin
	 *            20060606
	 * @param end
	 *            20070606
	 * @return
	 */
	public static int getDays(String begin, String end) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		long time = format.parse(end).getTime() - format.parse(begin).getTime();
		int n = (int) (time / (1000 * 24 * 3600));
		return n;
	}

	/**
	 * ��ȡ��������֮�������
	 * 
	 * @param begin
	 *            20060606
	 * @param end
	 *            20070606
	 * @return
	 */
	public static int getDays(Date begin, Date end) {
		long time = end.getTime() - begin.getTime();
		return (int) time / (1000 * 24 * 3600);
	}

	/** Field DAY */
	public static final int DAY = 1;
	/** Field WEEK */
	public static final int WEEK = 2;
	/** Field MONTH */
	public static final int MONTH = 3;
	/** Field QUANTER */
	public static final int QUANTER = 4;
	/** Field YEAR */
	public static final int YEAR = 5;

	// ʱ������ͣ�1, Day; 2, Week; 3, Month; 4, Quanter; 5, Year
	private int termtype;

	// ʱ��ο�ʼ����
	private String termBegin = null;
	// ʱ��ν�������
	private String termEnd = null;

	/**
	 * ��һ��ʱ���
	 */
	public Dates last = null;

	/**
	 * ��һ��ʱ���
	 */
	public Dates next = null;

	/**
	 * Method convertTermType
	 * 
	 * @param termtype
	 * @return int
	 */
	public static int convertTermType(String termtype) {
		int nTermType = 0;
		if (termtype.equalsIgnoreCase("DAY")) {
			nTermType = Dates.DAY;
		} else if (termtype.equalsIgnoreCase("WEEK")) {
			nTermType = Dates.WEEK;
		} else if (termtype.equalsIgnoreCase("QUANTER")) {
			nTermType = Dates.QUANTER;
		} else if (termtype.equalsIgnoreCase("YEAR")) {
			nTermType = Dates.YEAR;
		}
		return nTermType;
	}

	/**
	 * ���췽��
	 * 
	 * @param someday
	 *            ĳһ��
	 * @param termtype
	 *            ʱ�������
	 */
	public Dates() {
	}

	public Dates(String someday, int termtype) {
		this(getTermBegin(someday, termtype), getTermEnd(someday, termtype));
		this.termtype = termtype;
		this.last = new Dates();
		last.termBegin = getTermBegin(someday, termtype, -1);
		last.termEnd = getTermEnd(someday, termtype, -1);
		this.next = new Dates();
		next.termBegin = getTermBegin(someday, termtype, 1);
		next.termEnd = getTermEnd(someday, termtype, 1);
	}

	public Dates getLast() {
		Dates last2 = new Dates();
		last2.termBegin = getTermBegin(termBegin, termtype, -1);
		last2.termEnd = getTermEnd(termBegin, termtype, -1);
		last2.termtype = termtype;
		return last2;
	}

	public Dates getNext() {
		Dates next2 = new Dates();
		next2.termBegin = getTermBegin(termBegin, termtype, 1);
		next2.termEnd = getTermEnd(termBegin, termtype, 1);
		next2.termtype = termtype;
		return next2;
	}

	/**
	 * ���췽��
	 * 
	 * @param termbegin
	 *            ʱ��ο�ʼ����
	 * @param termend
	 *            ʱ��ν�������
	 */
	public Dates(String termbegin, String termend) {
		this.termBegin = termbegin;
		this.termEnd = termend;
	}

	/**
	 * ���췽��
	 * 
	 * @param termbegin
	 *            ʱ��ο�ʼ����
	 * @param termend
	 *            ʱ��ν�������
	 */
	public Dates(Date termbegin, Date termend) {
		this.termBegin = formatToStandardDate(termbegin);
		this.termEnd = formatToStandardDate(termend);
	}

	/**
	 * @return ʱ��ο�ʼ����
	 */
	public String getTermBegin() {
		return termBegin;
	}

	/**
	 * @return ʱ��ν�������
	 */
	public String getTermEnd() {
		return termEnd;
	}

	/**
	 * @return ʱ�������
	 */
	public int getTermType() {
		return termtype;
	}

	/**
	 * �ж�����ʱ����Ƿ���ȣ��ж������� ��ʼ���ڡ��������ڡ�ʱ�������
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof Dates && getTermBegin().equals(((Dates) obj).getTermBegin())
				&& getTermEnd().equals(((Dates) obj).getTermEnd()) && getTermType() == ((Dates) obj).getTermType();
	}

	/**
	 * �ж�����ʱ���֮��Ĵ�С���ж������� ��ʼ���������С
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object anotherDateTerm) {

		Date thisTermBegin = parse(this.termBegin);
		Date anotherTermBegin = parse(((Dates) anotherDateTerm).termBegin);
		return thisTermBegin.compareTo(anotherTermBegin);

	}

	/**
	 * �ж�ʱ����Ƿ�����һ��ʱ���֮��
	 * 
	 * @param when
	 * @return boolean
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public boolean after(Dates when) {
		boolean result = false;

		result = parse(this.termBegin).getTime() > parse(when.termEnd).getTime();

		return result;
	}

	public boolean contain(Dates another) {
		boolean result = false;

		result = parse(this.termBegin).getTime() <= parse(another.termBegin).getTime()
				&& parse(this.termEnd).getTime() >= parse(another.termEnd).getTime();

		return result;
	}

	/**
	 * �ж�ʱ����Ƿ�����һ��ʱ���֮ǰ
	 * 
	 * @param when
	 * @return boolean
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public boolean before(Dates when) {
		boolean result = false;

		result = parse(this.termEnd).getTime() < parse(when.termBegin).getTime();

		return result;
	}

	/**
	 * �жϱ�ʱ����Ƿ����ĳ����
	 * 
	 * @param date
	 * @return
	 */
	public boolean contain(String date) {

		Date thisTermBegin = parse(this.termBegin);
		Date thisTermEnd = parse(this.termEnd);
		Date anotherDate = parse(date);
		return thisTermBegin.compareTo(anotherDate) < 1 && thisTermEnd.compareTo(anotherDate) > -1;

	}

	/**
	 * Method clone
	 * 
	 * @see Object#clone()
	 */
	protected Object clone() {
		Dates d = null;
		try {
			d = (Dates) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return d;
	}

	/**
	 * Method toString
	 * 
	 * @see Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[begin:" + termBegin + "][end:" + termEnd + "]");
		return sb.toString();
	}

	/*
	 * ���� Javadoc�� hashCode������������ʼ���ڵ�hashCode���Ͻ������ڵ�hashCode
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * Method hashCode
	 * 
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.termBegin.hashCode() + this.termEnd.hashCode();
	}

	/**
	 * �õ���ʱ���n��ƫ�����Ŀ�ʼ����
	 * 
	 * @param offset
	 *            ƫ����
	 * @return
	 */
	public String getTermBegin(int offset) {
		String result = getTermBegin(this.termBegin, this.termtype, offset);
		return result;
	}

	/**
	 * �õ���ʱ���n��ƫ�����Ľ�������
	 * 
	 * @param offset
	 *            ƫ����
	 * @return
	 */
	public String getTermEnd(int offset) {
		String result = getTermEnd(this.termBegin, this.termtype, offset);
		return result;
	}

	/**
	 * ����ĳʱ��εĿ�ʼ����
	 * 
	 * @param somedate
	 * @param termtype
	 *            ʱ�������
	 * @return
	 */
	public static String getTermBegin(String somedate, int termtype) {
		return getTermBegin(somedate, termtype, 0);
	}

	/**
	 * ����ĳʱ��εĽ�������
	 * 
	 * @param somedate
	 * @param termtype
	 *            ʱ�������
	 * @return
	 */
	public static String getTermEnd(String somedate, int termtype) {
		return getTermEnd(somedate, termtype, 0);
	}

	/**
	 * ����ĳʱ���n��ƫ��ǰ���ʱ��εĿ�ʼ����
	 * 
	 * @param somedate
	 * @param termtype
	 *            ʱ�������
	 * @param offset
	 *            ƫ����
	 * @return
	 */
	public static String getTermBegin(String somedate, int termtype, int offset) {
		Calendar cal = Calendar.getInstance();
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
		String termbegin = null;

		try {
			if (somedate != null) {
				cal.setTime(format.parse(somedate));
			}

			switch (termtype) {
			case DAY:
				final int ONE_DAY = 1;
				cal.add(Calendar.DATE, offset * ONE_DAY);
				// cal.add(Calendar.DATE, 1);
				break;
			case WEEK:
				final int ONE_WEEK = 7;
				cal.set(Calendar.DAY_OF_WEEK, 1);
				cal.add(Calendar.DATE, offset * ONE_WEEK);
				cal.add(Calendar.DATE, 1);
				break;
			case MONTH:
				cal.add(Calendar.MONTH, offset);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				break;
			case QUANTER:
				int tmp = cal.get(Calendar.MONTH) % 3;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.MONTH, -tmp + offset * 3);
				break;
			case YEAR:
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.YEAR, offset);
				break;
			default:
				break;
			}
		} catch (ParseException e) {
			// log(ERROR, e.getMessage());
		}

		termbegin = format.format(cal.getTime());

		return termbegin;
	}

	/**
	 * ����ĳʱ���n��ƫ��ǰ���ʱ��εĽ�������
	 * 
	 * @param somedate
	 * @param termtype
	 *            ʱ�������
	 * @param offset
	 *            ƫ����
	 * @return
	 */
	public static String getTermEnd(String somedate, int termtype, int offset) {
		Calendar cal = Calendar.getInstance();
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
		String termend = null;

		try {
			if (somedate != null) {
				cal.setTime(format.parse(somedate));
			}
			switch (termtype) {
			case DAY:
				final int ONE_DAY = 1;
				cal.add(Calendar.DATE, offset * ONE_DAY);
				break;
			case WEEK:
				final int ONE_WEEK = 7;
				cal.set(Calendar.DAY_OF_WEEK, 1);
				cal.add(Calendar.DATE, ONE_WEEK + offset * ONE_WEEK);
				break;
			case MONTH:
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.MONTH, 1 + offset);
				cal.add(Calendar.DATE, -1);
				break;
			case QUANTER:
				int tmp = cal.get(Calendar.MONTH) % 3;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.MONTH, -tmp + (offset + 1) * 3);
				cal.add(Calendar.DATE, -1);
				break;
			case YEAR:
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.YEAR, 1 + offset);
				cal.add(Calendar.DATE, -1);
				break;
			default:
				break;
			}
		} catch (ParseException e) {
			// log.error(e);
		}

		termend = format.format(cal.getTime());

		return termend;
	}

}
