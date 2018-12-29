package com.sky.workflow.util;

/**
 * 时间处理类
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
 * Title: 对时间处理的静态类方法集合
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: 北京北大青鸟商用信息系统有限公司
 * </p>
 * Service
 * 
 * @author kangsj@jbbis.com.cn
 * @version 2.0 平台迁移
 * @Date 2008-1-22 下午04:09:21
 */

public class JBDate {

	public static String jdkver = System.getProperty("java.version");

	/**
	 * 时间类型 时：分：秒.毫秒 value="HH:mm:ss.SSS"
	 */
	public final static String DATE_TYPE_TIME_FORMAT = "HH:mm:ss.SSS";

	/**
	 * 日期和时间类型,格式化后的日期格式 年月日时分秒毫秒 value="yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public final static String DATE_TYPE_TIMESTAMP_FMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 日期类型yyyyMMdd
	 */
	public static final String DT_TYPE_DATA = "yyyyMMdd";

	/**
	 * 日期类型yyyy-MM-dd
	 */
	public static final String DT_TYPE_DATA_FAT = "yyyy-MM-dd";

	/**
	 * 日期类型yyyy/MM/dd
	 */
	public static final String DT_TYPE_DATA_FAT1 = "yyyy/MM/dd";

	/**
	 * 日期和时间类型 年月日时分秒 value="yyyyMMddHHmmss"
	 */
	public final static String DT_TYPE_TIMESTAMP = "yyyyMMddHHmmss";

	public final static int YEAR = 1;
	public final static int MONTH = 2;
	public final static int DATE = 5;

	/**
	 * 返回本机系统时间 Date对象
	 * 
	 * @param fmt
	 *            返回时间的字符串格式 参考getDBCurrTime
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
	 * 返回本机系统时间 String对象
	 * 
	 * @param fmt
	 *            返回时间的字符串格式 参考getDBCurrTime
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
	 * 将字符串转换成java.sql.Date对象
	 * 
	 * @param value
	 *            字符串值 比如：2006－06－06
	 * @param fmt
	 *            字符串的格式 yyyy-MM-dd,如果为空null，默认为yyyyMMdd
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
	 * 格式化日期
	 * 
	 * @param strFmt
	 *            正确的日期格式
	 * @throws Exception
	 */
	public static String formatDate(Date date, String strFmt) throws Exception {
		return getSDF(strFmt).format(date);
	}

	/**
	 * 将字符串日期转换成Date
	 * 
	 * @param date
	 *            合法的日期
	 * @param strFmt
	 *            日期格式需要和date一致
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
	 * 读取两个日期之间的天数
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
	 * 读取两个日期之间的天数
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
	 * 计算该日期(datetime)从18991231到现在的天数
	 * 
	 * @param datetime
	 *            格式:20060606
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
	 * @discription:返回当前系统时间yyyyMMDDKKmmss
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
	 * 按照指定的时间格式得到当前的系统时间,时区选择的是"Asia/Shanghai"
	 * 
	 * @param strFmt
	 *            合法的时间或时间格式
	 * @return 指定格式的日期字符串
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
	 *            开始时间
	 * @param second
	 *            结束时间
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
	 * 返回当前工作时间即工作日期加当前系统时间的时分秒
	 * 
	 * @param workdate
	 *            字符串工作日期yyyyMMdd,别传个null过来了,此值没有进行判断
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
	 * 将毫秒数转换成标准的时:分:秒.毫秒格式,一般用来计算两个日期之间的时间差,
	 * 只需要将差值毫秒long传进来即可,如1毫秒可以转换成00:00:00.001,采用的时
	 * 区为ETC/GMT-8(北京时间,因为北京属于格林威治的东八区) 这里的t不要超过24小时,此方法一般提供给记录日志用的
	 * 
	 * @param t
	 *            毫秒数
	 * @return 格式化后的时间差
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
	 * 计算一个日期加上指定天数后的日期
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
	 * 根据传入参数计算相应时间
	 * 
	 * @param date
	 *            起始时间(yyyyMMdd)
	 * @param type
	 *            时间单位
	 * @param term
	 *            计算时间的差值
	 * @return
	 * @throws ParseException
	 */
	public static String getEndDate(String date, int type, int term) throws ParseException {

		Calendar C = Calendar.getInstance();// 得到当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		C.setTime(df.parse(date));

		C.add(type, term);// 计算到期日期

		return df.format(C.getTime());
	}

	/**
	 * 比较date1和date2的大小，如果date1等于date2则返回0，如果date1小于date2则返回小于0的值，
	 * 如果date1大于date2则返回大于0的值
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
