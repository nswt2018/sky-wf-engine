package com.sky.workflow.engine;

public class Logger implements SupperLog {
	/**
	 * ��¼��־��Ϣ
	 * 
	 * @param i
	 * @param obj
	 */
	protected void log(int i, Object obj) {
		System.out.println("LogLeve" + i + obj);
	}
}
