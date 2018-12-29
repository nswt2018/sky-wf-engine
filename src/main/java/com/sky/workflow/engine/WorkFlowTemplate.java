package com.sky.workflow.engine;

public interface WorkFlowTemplate {
	/**
	 * ���̳�ʼ��Ҫ����ҵ�����
	 * @param params
	 */
	public void initialize(WFParams params) ;
	
	/**
	 * ��������ǰ��ҵ�����
	 * @param params
	 */
	public void preStarter(WFParams params) ;
	
	/**
	 * �����������ҵ�����
	 * @param params
	 */
	public void postStarter(WFParams params) ;

	/**
	 * �����ύǰ��ҵ�����
	 * @param params
	 */
	public void preApprove(WFParams params) ;
	
	/**
	 * �����ύ���ҵ�����
	 * @param params
	 */
    public void postApprove(WFParams params) ;

    /**
	 * ���̽������ҵ�����
	 * @param params
	 */
    public void finish(WFParams params) ;
}
