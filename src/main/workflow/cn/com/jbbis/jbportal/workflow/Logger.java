package cn.com.jbbis.jbportal.workflow;

import cn.com.jbbis.afx.AppContext;

public class Logger implements SupperLog{
	private AppContext context;
	public Logger(AppContext ctx) {
		context = ctx;
	}

	/**
	 * 记录日志信息
	 * 
	 * @param i
	 * @param obj
	 */
	protected void log(int i, Object obj) {
		context.log(i, obj);
	}
}
