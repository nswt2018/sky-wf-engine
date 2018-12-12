package com.nswt.workflow.service;

import cn.com.jbbis.jbportal.workflow.WFParams;
import cn.com.jbbis.jbportal.workflow.WorkFlowAdapter;

public class Demo extends WorkFlowAdapter {
	
	//初始化
	public void bizInit(WFParams params) {
		System.out.println(".........................");
		System.out.println("**********初始化***********");
		System.out.println(".........................");
	}
	
	//启动前
	public void preStarter(WFParams params) {
		System.out.println(".........................");
		System.out.println("**********启动前***********");
		System.out.println(".........................");
	}
	
	//启动后
	public void postStarter(WFParams params) {
		
	}
	
	//提交前
	public void preApprove(WFParams params) {
		
	}
	
	//提交后
	public void postApprove(WFParams params) {
		
	}
	
	//结束
	public void finish(WFParams params) {
		System.out.println(".........................");
		System.out.println("***********结束***********");
		System.out.println(".........................");
	}
	
}
