package cn.com.jbbis.jbportal.workflow;

public interface SupperLog {
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 4;
	public static final int ERROR = 8;
	public static final int FATAL = 16;
	public static final int RUNINFO = 30;
	public static final int COST = 32;
	public static final int SQL = 64;
	public static final int NETIO = 128;
	public static final String LOGCATNAMES[] = { "DEBUG", "INFO", "WARN",
			"ERROR", "FATAL", "COST", "SQL", "NETIO" };
}
