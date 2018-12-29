package com.sky.workflow.util;

/**
 * ʱ�䴦����
 */
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

// import cn.com.jbbis.jbportal.workflow.CommonConst;

/**
 * <p>
 * Title: ��ʱ�䴦��ľ�̬�෽������
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: ������������������Ϣϵͳ���޹�˾
 * </p>
 * Service
 * 
 * @author kangsj@jbbis.com.cn
 * @version 2.0 ƽ̨Ǩ��
 * @Date 2008-1-22 ����04:09:21
 */

public class JBDate {

	public static String jdkver = System.getProperty("java.version");

	/**
	 * ʱ������ ʱ���֣���.���� value="HH:mm:ss.SSS"
	 */
	public final static String DATE_TYPE_TIME_FORMAT = "HH:mm:ss.SSS";

	/**
	 * ���ں�ʱ������,��ʽ��������ڸ�ʽ ������ʱ������� value="yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public final static String DATE_TYPE_TIMESTAMP_FMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * ��������yyyyMMdd
	 */
	public static final String DT_TYPE_DATA = "yyyyMMdd";

	/**
	 * ��������yyyy-MM-dd
	 */
	public static final String DT_TYPE_DATA_FAT = "yyyy-MM-dd";

	/**
	 * ��������yyyy/MM/dd
	 */
	public static final String DT_TYPE_DATA_FAT1 = "yyyy/MM/dd";

	/**
	 * ���ں�ʱ������ ������ʱ���� value="yyyyMMddHHmmss"
	 */
	public final static String DT_TYPE_TIMESTAMP = "yyyyMMddHHmmss";

	public final static int YEAR = 1;
	public final static int MONTH = 2;
	public final static int DATE = 5;

	/**
	 * ���ر���ϵͳʱ�� Date����
	 * 
	 * @param fmt
	 *            ����ʱ����ַ�����ʽ �ο�getDBCurrTime
	 * @see getDBCurrTime
	 * @return
	 */
	public static Date getCurrTime() {
		Calendar cal = Calendar.getInstance();
		if (jdkver.startsWith("1.3"))
			cal.setTime(new Date());
		else
			cal.setTimeInMillis(System.currentTimeMillis());
		return cal.getTime();
	}

	/**
	 * ���ر���ϵͳʱ�� String����
	 * 
	 * @param fmt
	 *            ����ʱ����ַ�����ʽ �ο�getDBCurrTime
	 * @see getDBCurrTime
	 * @return
	 */
	public static String getCurrTime(String fmt) {
		java.text.SimpleDateFormat sdf = null;
		if (fmt == null)
			fmt = "yyyyMMdd";
		sdf = new java.text.SimpleDateFormat(fmt);
		return sdf.format(getCurrTime());
	}

	/**
	 * ���ַ���ת����java.sql.Date����
	 * 
	 * @param value
	 *            �ַ���ֵ ���磺2006��06��06
	 * @param fmt
	 *            �ַ����ĸ�ʽ yyyy-MM-dd,���Ϊ��null��Ĭ��ΪyyyyMMdd
	 * @return
	 * @throws ParseException
	 */
	public static Date String2Date(String value, String fmt) throws ParseException {
		SimpleDateFormat sdf = null;
		if (fmt == null)
			sdf = new SimpleDateFormat("yyyyMMdd");
		else
			sdf = new SimpleDateFormat(fmt);
		return sdf.parse(value);
	}

	/**
	 * ��ʽ������
	 * 
	 * @param strFmt
	 *            ��ȷ�����ڸ�ʽ
	 * @throws Exception
	 */
	public static String formatDate(Date date, String strFmt) throws Exception {
		return getSDF(strFmt).format(date);
	}

	/**
	 * ���ַ�������ת����Date
	 * 
	 * @param date
	 *            �Ϸ�������
	 * @param strFmt
	 *            ���ڸ�ʽ��Ҫ��dateһ��
	 * @throws Exception
	 */
	public static Date formatDate(String date, String strFmt) throws Exception {
		return getSDF(strFmt).parse(date);
	}

	private static SimpleDateFormat getSDF(String strFmt) throws Exception {
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
		SimpleDateFormat sdf = null;
		if (strFmt == null)
			sdf = new SimpleDateFormat();
		else
			sdf = new SimpleDateFormat(strFmt);
		sdf.setTimeZone(timeZone);
		return sdf;
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

	/**
	 * ���������(datetime)��18991231�����ڵ�����
	 * 
	 * @param datetime
	 *            ��ʽ:20060606
	 * @return
	 */
	public static long DateToDays(String datetime) throws Exception {
		long longDatetime = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		java.util.Date date = format.parse(datetime);
		long longdatetime = (date.getTime() + 8 * 3600 * 1000) / (1000 * 24 * 3600);
		date = format.parse("18991231");
		long daysBefore1970 = (date.getTime() + 8 * 3600 * 1000) / (1000 * 24 * 3600);
		longDatetime = longdatetime - daysBefore1970;

		return longDatetime;
	}

	/**
	 * @discription:���ص�ǰϵͳʱ��yyyyMMDDKKmmss
	 * @param:strFmt:yyyyMMddKKmmss
	 * @return:
	 * @throws Exception
	 */
	public static String getSysCurrTime(String strFmt) throws Exception {
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
		Date nowTime = new Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(strFmt);
		sdf.setTimeZone(timeZone);
		String d = sdf.format(nowTime);
		return d;
	}

	/**
	 * ����ָ����ʱ���ʽ�õ���ǰ��ϵͳʱ��,ʱ��ѡ�����"Asia/Shanghai"
	 * 
	 * @param strFmt
	 *            �Ϸ���ʱ���ʱ���ʽ
	 * @return ָ����ʽ�������ַ���
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public static String getSysCurrTime2(String strFmt) throws Exception {
		return formatDate(new Date(), strFmt);
	}

	public static String convertDateStr2In(String datestr) {
		if (datestr == null || "".equals(datestr)) {
			return "";
		}
		String resultstr = datestr.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

		return resultstr;
	}

	/**
	 * @param first
	 *            ��ʼʱ��
	 * @param second
	 *            ����ʱ��
	 * @return
	 * @throws SQLException
	 */
	public static int compTime(String first, String second) throws Exception {
		if (first.length() == 6) {
			String h1 = first.substring(0, 2);
			String h2 = first.substring(0, 2);
			String m1 = first.substring(3, 4);
			String m2 = first.substring(3, 4);
			String s1 = first.substring(5);
			String s2 = first.substring(5);

			int h = Integer.parseInt(h2) - Integer.parseInt(h1);
			int m = Integer.parseInt(m2) - Integer.parseInt(m1);
			int s = Integer.parseInt(s2) - Integer.parseInt(s1);
			return h * 3600 + m * 60 + s;
		}

		return 0;
	}

	// 20070731add

	public static int minus(String data1, String data2) throws Exception {
		long d1 = DateToDays(data1);
		long d2 = DateToDays(data2);
		Long l = new Long(d1 - d2);
		return l.intValue();
	}

	public static String formatData(Date d) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(d);
	}

	public static String formatDate(String date) throws Exception {
		return formatData(String2Date(date, null));
	}

	/**
	 * ���ص�ǰ����ʱ�伴�������ڼӵ�ǰϵͳʱ���ʱ����
	 * 
	 * @param workdate
	 *            �ַ�����������yyyyMMdd,�𴫸�null������,��ֵû�н����ж�
	 * @return yyyyMMddHHmmss
	 * @throws Exception
	 */
	public static String getWorkDateCurrTime(String workdate) throws Exception {
		String sysTime = getSysCurrTime("yyyyMMddHHmmss");
		return workdate + sysTime.substring(8);
	}

	/**
	 * @see #convertTime(long, String)
	 */
	public static String convertTime(long t) throws Exception {
		return convertTime(t, DATE_TYPE_TIME_FORMAT);
	}

	/**
	 * ��������ת���ɱ�׼��ʱ:��:��.�����ʽ,һ������������������֮���ʱ���,
	 * ֻ��Ҫ����ֵ����long����������,��1�������ת����00:00:00.001,���õ�ʱ
	 * ��ΪETC/GMT-8(����ʱ��,��Ϊ�������ڸ������εĶ�����) �����t��Ҫ����24Сʱ,�˷���һ���ṩ����¼��־�õ�
	 * 
	 * @param t
	 *            ������
	 * @return ��ʽ�����ʱ���
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public static String convertTime(long t, String strFmt) throws Exception {
		Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("ETC/GMT-8"));
		cl.setTimeInMillis(t);
		SimpleDateFormat sdf = new SimpleDateFormat(strFmt);
		StringBuffer buf = new StringBuffer();
		buf.append(sdf.format(cl.getTime()));
		int hours = Integer.parseInt(buf.substring(0, 2)) - 8;
		buf.delete(0, 2);
		String hour = hours < 10 ? "0" + hours : hours + "";
		buf.insert(0, hour);
		return buf.toString();
	}

	public static String getEndDate(String begindate, int term) throws Exception {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date dd = format.parse(begindate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dd);
		calendar.add(Calendar.MONTH, term);
		String ChangeDate = format.format(calendar.getTime());
		return ChangeDate;
	}

	/**
	 * ����һ�����ڼ���ָ�������������
	 */
	public static String getEndDate2(String begindate, int term) throws Exception {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date dd = format.parse(begindate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dd);
		calendar.add(Calendar.DAY_OF_YEAR, term);
		String ChangeDate = format.format(calendar.getTime());
		return ChangeDate;
	}

	/**
	 * ���ݴ������������Ӧʱ��
	 * 
	 * @param date
	 *            ��ʼʱ��(yyyyMMdd)
	 * @param type
	 *            ʱ�䵥λ
	 * @param term
	 *            ����ʱ��Ĳ�ֵ
	 * @return
	 * @throws ParseException
	 */
	public static String getEndDate(String date, int type, int term) throws ParseException {

		Calendar C = Calendar.getInstance();// �õ���ǰʱ��
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		C.setTime(df.parse(date));

		C.add(type, term);// ���㵽������

		return df.format(C.getTime());
	}

	/**
	 * �Ƚ�date1��date2�Ĵ�С�����date1����date2�򷵻�0�����date1С��date2�򷵻�С��0��ֵ��
	 * ���date1����date2�򷵻ش���0��ֵ
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(String date1, String date2) {
		int result = 0;
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");

		try {
			c1.setTime(f.parse(date1));
			c2.setTime(f.parse(date2));
			result = c1.compareTo(c2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
