package cn.com.jbbis.jbportal.workflow;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;
/**
 * <p>Title: 流程引擎与数据库、应用数据相关的处理过程接口</p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: 北京北大青鸟商用信息系统有限公司 </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.0 平台迁移 2008-2-19 下午04:50:26
 */
public interface WorkflowStorage
{
	/**
	 * 得到流程定义
	 * @throws java.lang.Exception
	 */
	public UnikMap getWorkflowDefinition(String wfDefId) throws Exception;

	/**
	 * 得到流程实例
	 * @throws java.lang.Exception
	 */
	public UnikMap getWorkflowInstance(String wfInstId) throws Exception;

	/**
	 * 通过节点类型(起始/中间/结束)得到节点定义
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodeByType(String wfDefId, String type) throws Exception;

	
	/**
	 * 通过节点ID得到节点定义
	 * @throws Exception:java.lang.Exception
	 */
	public UnikMap getNodeById(String wfDefId, String nodeId) throws Exception;

	/**
	 * 创建流程实例
	 * @throws java.lang.Exception
	 */
	public void createInstance(String wfDefId, String wfInstId, String user,
			String wfState, UnikMap var,String busiOperateStyle) throws Exception;

	/**
	 * 查找dwtask表中的最大轮次记录
	 * @param var var中需要有 wfid 
	 * @return int 最大轮次
	 * @throws java.lang.Exception
	 */
	public int getTaskMaxTaskRound(String wfid) throws Exception;
	
	/**
	 * 更新任务的最大轮次
	 */
	public void updateTaskRound(String wfid,int taskRound) throws Exception ;
	/**
	 * 当回退到开始节点时需要做的工作
	 * @throws java.lang.Exception
	 */
	public void doWithDraw(String flowid, String wfInstId) throws Exception;

	/**
	 * 结束流程实例
	 * @throws java.lang.Exception
	 */
	public void completeInstance(String wfInstId) throws Exception;

	/**
	 * 更新流程变量
	 * @throws java.lang.Exception
	 */
	public void updateWorkflowVariables(String wfInstId, UnikMap wfVars,
			int taskser, String wfDefId, String nodeId, String nodeDesc)
			throws Exception;

	/**
	 * 根据机构号和岗位编号查询用户列表,目前只处理一个岗位
	 * 此处的上级机构是指:取上级审批机构信息,并非上级管理机构
	 * @param m m中要有如下参数 org 当前节点机构号 post 岗位 local 机构/用户
	 * wfid 流程编号 nodeid 节点编号
	 * @return String[] 用户信息如下bankid/userid|username,如果没有找到用户返回null
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap m,boolean flag) throws Exception;
	/**
	 * 路由表
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId)
			throws Exception;

	/**
	 * 得到工作任务
	 * @throws java.lang.Exception
	 */
	public UnikMap getTask(String wfInstId, String user) throws Exception;

	/**
	 * 得到流程下的所有工作任务
	 * @throws java.lang.Exception
	 */
	public DataList getTask(String wfInstId) throws Exception;
	
	/**
	 * 新建工作任务,users进行多人分配
	 * @return 当前的序号
	 * @throws java.lang.Exception
	 */
	public int newTask(UnikMap node,String wfInstId, int taskSerial,String[] users,String[] viewUsers,UnikMap var,ArrayList taskList) throws Exception;

	/**
	 * 完成工作任务
	 * @throws java.lang.Exception
	 */
	public void completeTask(String wfInstId, String taskser,String nodephase, UnikMap vars,UnikMap node) throws Exception;

	/**
	 * 删除所有待办任务
	 * @param wfInstId
	 * @throws Exception
	 */
	public void completeAllTask(String wfInstId) throws Exception;
	/**
	 * 更新流程的任务序号
	 * @throws java.lang.Exception
	 */
	public void updateTaskSerial(String wfInstId, int nextSerial)
			throws Exception;

	/**
	 * 更新流程状态
	 * @throws java.lang.Exception
	 */
	public void updateWorkflowStatus(String wfInstId, int status)
			throws Exception;

	/**
	 * 工作列表
	 * @throws java.lang.Exception
	 */
	public Object getTaskList(Object msg, String org, String user)
			throws Exception;

	
	
	/**
	 * 实例变量表
	 * @throws java.lang.Exception
	 */
	public UnikMap getInstVar(String wfInstId, boolean reload) throws Exception;

	/**
	 * 路由表
	 * @throws java.lang.Exception
	 */
	public LinkedList getNodeRoute(String wfDefId, String nodeId)
			throws Exception;

	/**
	 * 调用交易,主要是提供给结束节点
	 * @throws java.lang.Exception
	 */
	public AppResponse doService(UnikMap vars) throws Exception;

	/**
	 * 检查是否往下一个节点走
	 * @throws java.lang.Exception
	 */
	boolean isContinue(UnikMap node,UnikMap var,boolean blStart)throws Exception;
	
	/**
	 * 检查是否最后审批人
	 * @param node  当前节点
	 * @param var   流程变量
	 * @param blStart 是否开始节点
	 * @return
	 * @throws Exception
	 */
	boolean isLastestHandler(UnikMap node,UnikMap var,boolean blStart) throws Exception;
	/**
	 * 检查任务表中谁的任务最少,返回最少的用户
	 * @throws java.lang.Exception
	 */
	public String[] getTaskLeastUser(String[] user,int num)throws Exception;
	
	/**
	 * 检查流程状态,非发布状态不允许提交业务
	 * @param flowid 流程编号
	 * @throws java.lang.Exception
	 */
	void checkFlowState(String flowid) throws Exception;
	
	/**
	 * 将变量表中的数据保存至变量历史表并清空变量表
	 * @param wfid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void delDwtaskVars(String wfid) throws Exception;
}