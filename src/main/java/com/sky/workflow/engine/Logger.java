package com.sky.workflow.engine;

public class Logger implements SupperLog {
	/**
	 * 记录日志信息
	 * 
	 * @param i
	 * @param obj
	 */
	protected void log(int i, Object obj) {
		System.out.println("LogLeve" + i + obj);
	}
}
