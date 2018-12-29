package com.sky.workflow.engine;

public interface WorkFlowTemplate {
	/**
	 * 流程初始化要做的业务操作
	 * @param params
	 */
	public void initialize(WFParams params) ;
	
	/**
	 * 流程启动前的业务操作
	 * @param params
	 */
	public void preStarter(WFParams params) ;
	
	/**
	 * 流程启动后的业务操作
	 * @param params
	 */
	public void postStarter(WFParams params) ;

	/**
	 * 流程提交前的业务操作
	 * @param params
	 */
	public void preApprove(WFParams params) ;
	
	/**
	 * 流程提交后的业务操作
	 * @param params
	 */
    public void postApprove(WFParams params) ;

    /**
	 * 流程结束后的业务操作
	 * @param params
	 */
    public void finish(WFParams params) ;
}
