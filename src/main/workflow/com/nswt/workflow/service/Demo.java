package com.nswt.workflow.service;

import cn.com.jbbis.jbportal.workflow.WFParams;
import cn.com.jbbis.jbportal.workflow.WorkFlowAdapter;

public class Demo extends WorkFlowAdapter {
	
	//��ʼ��
	public void bizInit(WFParams params) {
		System.out.println(".........................");
		System.out.println("**********��ʼ��***********");
		System.out.println(".........................");
	}
	
	//����ǰ
	public void preStarter(WFParams params) {
		System.out.println(".........................");
		System.out.println("**********����ǰ***********");
		System.out.println(".........................");
	}
	
	//������
	public void postStarter(WFParams params) {
		
	}
	
	//�ύǰ
	public void preApprove(WFParams params) {
		
	}
	
	//�ύ��
	public void postApprove(WFParams params) {
		
	}
	
	//����
	public void finish(WFParams params) {
		System.out.println(".........................");
		System.out.println("***********����***********");
		System.out.println(".........................");
	}
	
}
