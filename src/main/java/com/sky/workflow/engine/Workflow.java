package com.sky.workflow.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.workflow.model.DbBusiWfMapTable;
import com.sky.workflow.model.DwFlowInstTable;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.model.DwFlowNodeTable;
import com.sky.workflow.model.DwTaskTable;
import com.sky.workflow.util.Errors;
import com.sky.workflow.util.NamedException;
import com.sky.workflow.util.StringUtils;
import com.sky.workflow.util.UnikMap;

/**
 * <p>
 * Title: 工作流引擎
 * </p>
 * <p>
 * Description: 处理流程的任务，控制流程的流转
 * </p>
 * 
 * <p>
 * <b>流程启动时所做的工作</b>
 * </p>
 * <ol>
 * <li>得到流程实例编号,也就是我们应用里的wfid,长度20位由17位时间加3位随机数组成</li>
 * <li>创建流程实例即将流程的实例信息写入dwflowinst表中</li>
 * <li>得到流程的开始节点定义，也就是所有的节点信息</li>
 * <li>将开始节点的处理任务放入未处理任务列表</li>
 * <li>调用任务处理方法来resume来处理这条未完成的开始节点信息</li>
 * </ol>
 * 
 * <p>
 * <b>流程处理节点任务时所做的工作</b>
 * </p>
 * <ol>
 * <li>得到当前流程的实例信息</li>
 * <li>检查当前流程是否已发布，只有已发布的流程才可以正常流转</li>
 * <li>得到当前未处理的任务，主要来自于未处理任务列表dwtask</li>
 * <li>从实例中取得任务序号,方便进行更新任务时使用</li>
 * <li>得到当前节点信息，主要来自于dwflownode表</li>
 * <li>存储当前节点需要保存的变量值，利用merge的方法来操作dwtaskvars表</li>
 * <li>完成当前的处理任务、即清空任务列表，将数据移动到dwtaskhis历史表中</li>
 * <li>判断流程是否流转到下一节点，主要是针对多人处理模式</li>
 * <li>如果需要进行下一节点流转则根据下一节点的流转方式取出下一节点编号</li>
 * <li>根据下一节点类型判断如何分配任务，如果下一节点是结束还得看是否拼接流程</li>
 * <li>更新实例表中的流程序号taskser始终与dwtask表中的最大值同步</li>
 * </ol>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: 北京北大青鸟商用信息系统有限公司
 * </p>
 * 
 * @see WorkflowStorageImpl
 * @author kangsj@jbbis.com.cn
 * @version 1.0.0 , 2008-5-15 上午08:38:00
 */
@Service("Workflow")
public class Workflow extends Logger {

	/**
	 * <p>
	 * 流程引擎操作数据库公共类
	 * </p>
	 */
	@Resource(name = "WorkflowStorageImpl")
	private IWorkflowStorage storage;

	/**
	 * <p>
	 * 流程序号
	 * </p>
	 */
	protected int taskSerial;

	/**
	 * <p>
	 * 当前节点定义信息
	 * </p>
	 */
	private DwFlowNodeTable currentNode = null;

	public Workflow() {
		super();
	}

	/**
	 * <p>
	 * 流程开始,启动新的流程。所做工作如下：
	 * </p>
	 * <ol>
	 * <li>新建流程,创建流程实例</li>
	 * <li>新建工作列表，将任务写入任务列表dwtask</li>
	 * <li>继续，调用流程引擎的resume处理任务</li>
	 * </ol>
	 * 
	 * @param flowid       工作流流程编号
	 * @param user         当前处理人员bankid/operid
	 * @param bizData      工作流变量
	 * @param users        下一任务处理人员
	 * @param viewUsers    任务浏览人员
	 * @param busiWorkflow 业务参数
	 * @param outTaskNode
	 * @param tranQueue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Transactional
	public String start(String flowid, String user, UnikMap bizData, String[] users, String[] viewUsers, UnikMap busiWorkflow, UnikMap outTaskNode, Queue<UnikMap> tranQueue) throws Exception {

		// 流程实例编号
		log(INFO, "[WF_IN_start] workflow start, flowid=" + flowid);
		String wfid = generateRandomSeq();
		log(DEBUG, "[WF_start NO:1] create wfid OK, value is " + wfid);

		// 获取外部传入的业务参数
		String busiOperateStyle = "";
		if (busiWorkflow != null) {
			busiOperateStyle = CommonConst.WF_BUSIOPERATESTYLE_G;
			log(DEBUG, "[WF_start NO:1.1] generate busiOperateStyle OK.busiOperateStyle=" + busiOperateStyle);
		}

		// 创建流程实例
		log(DEBUG, "[WF_start NO:2] begin CreateInstance, flowid=" + flowid + ", wfid=" + wfid);
		storage.createInstance(flowid, wfid, user, CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);
		log(DEBUG, "[WF_start NO:3] createInstance OK.");

		// 得到开始节点定义
		currentNode = storage.getNodeByType(flowid, CommonConst.WF_NODETYPE_START);

		// 新建工作列表
		taskSerial = 1;
		ArrayList taskList = new ArrayList();

		// 分配新任务到任务列表
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfid + ", nodeid=" + currentNode.getNodeid());
		storage.newTask(currentNode, wfid, taskSerial, user.split(","), viewUsers, bizData, taskList);

		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfid + " taskRound=1");
		storage.insertTaskRound(wfid, 1);

		// 获取isWriteBusiWFMap配置项,写流程实例对照表
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid=" + wfid + "");
		storage.writeBusiWFMap(flowid, wfid, busiWorkflow);

		// 继续流转任务
		boolean blStart = true;
		boolean blIsDirectForeNode = false;
		resume(wfid, user, bizData, users, viewUsers, blStart, blIsDirectForeNode, outTaskNode, tranQueue);
		log(INFO, "[WF_OUT_start] OUT Workflow.start, flowid = " + flowid + ", wfid = " + wfid);

		return wfid;
	}

	/**
	 * <p>
	 * 流程任务处理
	 * </p>
	 * <ol>
	 * <li>定位当前节点</li>
	 * <li>保存业务数据</li>
	 * <li>结束当前任务</li>
	 * <li>转至下一节点</li>
	 * <li>分派任务</li>
	 * </ol>
	 * 
	 * @param wfid             流程实例编号
	 * @param user             当前机构下的操作员,值为机构代码/用户代码
	 * @param bizData          上传报文中的处理意见,路由的判断的数据从此中取得
	 * @param users            从上传报文中取users,lsc.getProperties("users"),手工分配应该设置users值
	 * @param viewUsers
	 * @param blStart
	 * @param isDirectForeNode
	 * @param outTaskNode
	 * @param tranQueue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void resume(String wfid, String user, UnikMap bizData, String[] users, String[] viewUsers, boolean blStart, boolean isDirectForeNode, UnikMap outTaskNode, Queue<UnikMap> tranQueue) throws Exception {

		String routetype = "0", taskassignstyle = "N";
		ArrayList taskList = new ArrayList(); // 输出分配多人的任务信息列表
		if (outTaskNode == null)
			outTaskNode = new UnikMap();
		// 得到流程实例
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfid);
		DwFlowInstTable dwFlowInstVo = storage.getWorkflowInstance(wfid);

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid=" + wfid);
		String flowid = dwFlowInstVo.getFlowid();
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId=" + flowid);
		// 检查当前流程是否发布
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + flowid);
		storage.checkFlowState(flowid);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// 得到当前未处理的任务
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		DwTaskTable dwTaskVo = storage.getTask(wfid, user);
		String nodeid = dwTaskVo.getNodeid();
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeid);

		// 设置工作流序号
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(dwFlowInstVo.getTaskser() + "", 10);
		}

		// 得到当前节点定义,如果不等于空说明是开始节点所以就不用再查询一次节点信息了
		boolean ischeckNode = true;
		if (!blStart) {
			currentNode = storage.getNodeById(flowid, nodeid);
		} else {
			ischeckNode = false;
		}

		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid=" + wfid);
		storage.updateWorkflowVariables(wfid, bizData, taskSerial, flowid, nodeid, currentNode.getNodedesc());
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid=" + wfid);

		// 完成当前的工作任务 【把当前任务插入历史任务表，删除当前任务表】
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfid);
		storage.completeTask(wfid, dwTaskVo, bizData, currentNode);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfid);

		if (ischeckNode) {
			log(DEBUG, "[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getNodetype(), dwFlowInstVo, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		// 设置对外输出的节点
		outTaskNode.put("currentNode", currentNode); // 输出当前节点
		outTaskNode.put("wfid", wfid);
		outTaskNode.put("taskSer", taskList);

		// 如果返回false则说明还有用户没有处理，主要针对一个节点多人操作
		if (!storage.isContinue(currentNode, dwTaskVo, blStart)) {
			log(DEBUG, "[WF_IN_Resume] this task has many user to dispose, so need return");
			outTaskNode.put("isLastDealer", "false");
			return;// null;
		}
		outTaskNode.put("isLastDealer", "true");

		// 取下一节点编号 先获取正常的下一个结点，判断下一节点设置方式，如果是条件方式，获取条件中的下一个结点
		UnikMap nextNodeUm = getNextNodeId(flowid, wfid, nodeid, currentNode, isDirectForeNode);
		String nextNodeId = nextNodeUm.getString("nextnodeid");
		routetype = nextNodeUm.getString("routetype");
		taskassignstyle = nextNodeUm.getString("taskassignstyle");
		// 如果下一个节点为空
		Errors.Assert(nextNodeId != null && !nextNodeId.equals(""), "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.getNodetype()))
			return;// null;

		String[] nextIds = splitCsv(nextNodeId);
		DwFlowNodeTable[] outNextNode = new DwFlowNodeTable[nextIds.length]; // 记录输出到交易中的结点
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// 取出下一节点编号准备进行任务分配
			DwFlowNodeTable nextNode = storage.getNodeById(flowid, nextIds[i]);
			// nextNode.put("wfid", wfid);
			// nextNode.put("routetype", routetype);//缺省值
			// nextNode.put("taskassignstyle", taskassignstyle);//缺省值
			outNextNode[i] = nextNode;
			// 写入"上一环节编号"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			String nodetype = nextNode.getNodetype();
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid=" + nextNodeId + ", nodetype=" + nodetype);
			// 如果下一个节点是开始节点，需要进行必要的业务数据处理。一般为标志数据
			if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
				// 如果下一节点是开始节点，进行退回的时候，需要执行流程定义的回退语句
				// storage.doWithDraw(wfDefId, wfInstId); //liuxj
				UnikMap wfVars = storage.getInstVar(wfid, false);
				// 使用para中定义的选择项格式化变量值为对应的名称
				wfVars.putAll(formatVars(wfVars)); // 格式化参数为para中的文字描述 liuxj 20141115
				String routetype1 = "";
				if (routetype1 != null && routetype.equals("1")) { // 退回路由
					// 退回交易入队列
					UnikMap tranUm = new UnikMap();
					tranUm.put("trancode", currentNode);
					tranUm.put("trantype", "retutrans");
					tranUm.put("vars", wfVars);
					tranQueue.add(tranUm);
				}
				log(DEBUG, "[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// 如果是开始环节就写入上一环节编号
				// nextNode.put("forenodeid", currentNode.getNodeid());
				// System.out.println("如果下一个节点是开始节点(user)=" + user);
				assignTaskAgain(flowid, wfid, nextNode, user, taskList);
				// 退回，更新轮次
				int maxTaskRound = storage.getTaskMaxTaskRound(wfid);
				storage.updateTaskRound(wfid, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// 任务分派,分发
			else if (CommonConst.WF_NODETYPE_OUT.equals(nextNode.getNodetype())) {
				String next[] = splitCsv(nextNode.getNextnode());
				for (int j = 0; j < next.length; j++) {
					DwFlowNodeTable dwFlowNode = storage.getNodeById(flowid, next[j]);
					// dwFlowNode.put("routetype", routetype);
					// dwFlowNode.put("taskassignstyle", taskassignstyle);
					UnikMap vars = new UnikMap();
					assignTask(flowid, wfid, dwFlowNode, user, users, viewUsers, taskList, vars);
				}
				continue;
			}
			// 合并 通过路由方式进行节点跳转
			else if (CommonConst.WF_NODETYPE_UNITE.equals(nextNode.getNodetype())) {
				String next = nextNode.getNodeid();
				if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextNode.getNextnodemode())) {
					Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
					WorkflowRoute R = (WorkflowRoute) C.newInstance();
					UnikMap wfVars = storage.getInstVar(wfid, false);
					// 使用para中定义的选择项格式化变量值为对应的名称
					wfVars.putAll(formatVars(wfVars)); // 格式化参数为para中的文字描述 liuxj 20141115
					LinkedList route = storage.getNodeRoute(flowid, next);
					UnikMap nextNodeUM = R.getNext(route, wfVars);
					nextNodeId = nextNodeUM.getString("nextNodeId");
					taskassignstyle = nextNodeUM.getString("taskAssignStyle");
					routetype = nextNodeUM.getString("routeType");
					// 退回
					if (nextNodeId == null || nextNodeId.length() == 0)
						continue;
					else {
						nextNode = storage.getNodeById(flowid, nextNodeId);
						// 通过路由的类型与任务分配方式（）判断获取用户的方式，覆盖传入的用户信息
						UnikMap vars = new UnikMap();
						vars.put("taskassignstyle", taskassignstyle);
						vars.put("routetype", routetype);
						assignTask(flowid, wfid, nextNode, user, users, viewUsers, taskList, vars);
						String routetype1 = "";// nextNode.getString("routetype");
						if (routetype1 != null && routetype.equals("1")) // 退回路由
						{
							// 退回交易入队列
							UnikMap tranUm = new UnikMap();
							tranUm.put("trancode", currentNode);
							tranUm.put("trantype", "retutrans");
							tranUm.put("vars", wfVars);
							tranQueue.add(tranUm);
						}
						break;
					}
				}
			} else if (CommonConst.WF_NODETYPE_SUBWORKFLOW.equals(nextNode.getNodetype())) { // 子流程

			}
			// 结束节点
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode.getNodetype())) {
				// 更改工作流状态
				storage.updateWorkflowStatus(wfid, 2);
				// 完成实例
				storage.completeInstance(wfid);
				// 更改流程业务对照表为完成状态
				storage.completeBusiWFMap(wfid, "2");
				// 调用结束节点的交易 submtrancode
				// =============结束交易入队列==============
				// 获取流程变量
				UnikMap wfVars = storage.getInstVar(wfid, false); // liuxj
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", nextNode); // 节点信息
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "endtrans");
				tranQueue.add(tranUm);
				// AppResponse res = null;
				// AppResponse res = storage.doService(nextNode); //liuxj
				// 检查是否拼接流程
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode.getIsunit())) {
					bizData.put("unitwfid", wfid);
					wfVars = storage.getInstVar(wfid, false); // liuxj // 20141115
					// 使用para中定义的选择项格式化变量值为对应的名称
					bizData.putAll(formatVars(wfVars)); // liuxj 20141115
					// 流程拼接
					// ???unite(nextNode.getUnitflowid(), user, viewUsers,bizData,
					// busiOperateStyle,taskList);
					// res = null;
				} else {
					// 如果不拼接流程就将dwtaskvars写入dwtaskvarshis并清空dwtaskvars
					storage.delDwtaskVars(wfid);
					// 流程结束，删除最大任务轮次
					storage.deleteTaskRound(wfid);
				}
				// 输出任务信息
				if (outTaskNode != null) {
					outTaskNode.put("currentNode", currentNode);
					outTaskNode.put("nextNode", outNextNode);
					outTaskNode.put("wfid", wfid);
					outTaskNode.put("taskSer", taskList);
				}
				return;// res;
			}
			// 中间节点进行任务分配
			assignTask(flowid, wfid, nextNode, user, users, viewUsers, taskList, new UnikMap());

			// 打回路由，设置当前节点（包含打回交易）与环境变量到队列中
			String routetype1 = "";// nextNode.getString("routetype");
			if (routetype1 != null && routetype1.equals("1")) {
				// 退回交易入队列
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", currentNode);
				// 获取流程变量
				UnikMap wfVars = storage.getInstVar(wfid, false); // liuxj
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "retutrans");
				tranQueue.add(tranUm);
			}
		}
		// 20130710，加入紧急程度，若变量中没有dwflowinst.instancyLevel则不更新dwflowinst中的该字段
		// 更新流程序号
		String instancylevel = bizData.getString("dwflowinst.instancylevel");
		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid=" + wfid + ", taskser=" + taskSerial + ", instancyLevel=" + instancylevel);

		storage.updateTaskSerial(wfid, taskSerial, instancylevel);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfid);

		// 输出任务信息
		if (outTaskNode != null) {
			outTaskNode.put("currentNode", currentNode);
			outTaskNode.put("nextNode", outNextNode);
			outTaskNode.put("wfid", wfid);
			outTaskNode.put("taskSer", taskList);
		}
		return;// null;
	}

	protected void startSubWF(String wfInstId, String wfDefId, String user, UnikMap bizData, String[] users, String[] viewUsers, UnikMap outTaskNode) throws Exception {
		// 流程实例编号
		log(DEBUG, "[sub workflow NO:1] sub workflow start, value is " + wfInstId);
		// 得到开始节点定义
		currentNode = storage.getNodeByType(wfDefId, CommonConst.WF_NODETYPE_START);

		// 新建工作列表
		// taskSerial = 1;
		// 分配新任务到任务列表
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfInstId + ", nodeid=" + currentNode.getNodeid());
		storage.newTask(currentNode, wfInstId, taskSerial, user.split(","), viewUsers, bizData, null);
		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfInstId + " taskRound=1");
		storage.insertTaskRound(wfInstId, 1);
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid=" + wfInstId + "");

		return;// null;
	}

	/**
	 * <p>
	 * 使用para中的选项格式化参数值为中文描述
	 * </p>
	 * 
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private UnikMap formatVars(UnikMap vars) throws Exception {
		UnikMap um = new UnikMap();
		// 如果流程变量中
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			if (name.indexOf(".") > 0) {
				String tmpName = "";
				String tmpValue = "";
				tmpName = name.substring(name.indexOf(".") + 1);
				tmpValue = storage.getSelectText(tmpName, value);
				if (!"".equals(tmpValue))
					value = tmpValue;
			}
			um.put(name, value);
		}

		return um;
	}

	/**
	 * <p>
	 * 返回下一环节的编号
	 * </p>
	 * 
	 * @param flowid           流程编号
	 * @param nodeid           当前环节编号
	 * @param dataParams       工作流变量
	 * @param isDirectForeNode 是否直接返回到打回节点 true 返回，false 不返回
	 * @return 下一环节的编号
	 * @throws Exception
	 */
	/*
	 * public UnikMap getNextNodeID(String flowid, String nodeid, UnikMap
	 * dataParams, boolean isDirectForeNode) throws Exception { UnikMap node =
	 * storage.getNodeById(flowid, nodeid); return getNextNodeId(flowid, "******",
	 * nodeid, node, dataParams, isDirectForeNode); }
	 */

	/**
	 * <p>
	 * 返回下一节点编号
	 * </p>
	 * 
	 * @param flowid           流程编号
	 * @param wfid             工作流序号
	 * @param nodeid           当前节点编号
	 * @param currentNode2     当前节点信息
	 * @param isDirectForeNode 是否直接返回到打回节点 true 返回，false 不返回
	 * @return String 下一节点编号,如果下一节点不存在则返回null
	 * @throws Exception
	 */
	private UnikMap getNextNodeId(String flowid, String wfid, String nodeid, DwFlowNodeTable currentNode, boolean isDirectForeNode) throws Exception {
		// 在完成任务的时候已经更新vars变量了所以此处可以不用再重新去取一次变量
		UnikMap wfVars = storage.getInstVar(wfid, true);
		// 使用para中定义的选择项格式化变量值为对应的名称
		wfVars.putAll(formatVars(wfVars)); // liuxj 20141115

		return getNextNodeId(flowid, wfid, nodeid, currentNode, wfVars, isDirectForeNode);
	}

	/**
	 * <p>
	 * 返回下一节点的编号,如果下一节点没有找到就返回null
	 * </p>
	 * 
	 * @param flowid      流程编号
	 * @param wfid        工作流序号
	 * @param nodeid      当前节点编号
	 * @param currentNode 当前节点信息
	 * @param var         路由参数
	 * @return String下一节点编号,如果下一节点不存在则返回null
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("rawtypes")
	public UnikMap getNextNodeId(String flowid, String wfid, String nodeid, DwFlowNodeTable currentNode, UnikMap var, boolean isDirectForeNode) throws Exception {

		UnikMap um = new UnikMap();
		log(DEBUG, "[WF_getnextnodeid NO:-1] flowid=" + flowid + " wfid=" + wfid + " nodeid=" + nodeid);

		log(DEBUG, "[WF_getnextnodeid NO:0] node=" + (currentNode != null ? currentNode.getNodeid() : "node is null"));
		currentNode = currentNode == null ? storage.getNodeById(flowid, nodeid) : currentNode;

		String forenodeid = "";
		if (isDirectForeNode) {
			forenodeid = storage.getForeNodeId(wfid, nodeid);
			if (forenodeid != null)
				um.put("nextnodeid", forenodeid);
			um.put("taskassignstyle", "N");
			um.put("routetype", "0");
			return um;
		}
		String nextnodemode = currentNode.getNextnodemode(); // 下一节点设置方式
		String nextNodeId = currentNode.getNextnode();
		log(DEBUG, "[WF_getnextnodeid NO:1] wfid=" + wfid + " nextnodemode=" + nextnodemode + " nextNodeId=" + nextNodeId);

		// 初始化下一节点
		um.put("nextnodeid", nextNodeId);
		um.put("taskassignstyle", "N");
		um.put("routetype", "0");

		if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextnodemode)) {

			Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
			WorkflowRoute R = (WorkflowRoute) C.newInstance();
			LinkedList route = storage.getNodeRoute(flowid, nodeid);

			UnikMap wfVars = new UnikMap();
			wfVars.putAll(formatVars(var)); // 格式化参数为para中的文字描述 liuxj 20141211
			UnikMap nextNodeUM = R.getNext(route, wfVars);
			if (nextNodeUM != null) {
				nextNodeId = nextNodeUM.getString("nextNodeId");
				String taskassignstyle = nextNodeUM.getString("taskAssignStyle");
				String routetype = nextNodeUM.getString("routeType");

				um.put("nextnodeid", nextNodeId);
				um.put("taskassignstyle", taskassignstyle);
				um.put("routetype", routetype);
			}

		}
		log(DEBUG, "[WF_getnextnodeid NO:2] wfid=" + wfid + " nextNodeId=" + nextNodeId);

		return um;
	}

	/*
	 * public DataList getNextNodeManualUsers(String flowid, String nodeid, AppUser
	 * user, UnikMap dataParams, IParticipant part, boolean isDirectForeNode) throws
	 * Exception {
	 * 
	 * DataList dl = null; UnikMap wfVars = new UnikMap();
	 * wfVars.putAll(formatVars(dataParams)); //格式化参数为para中的文字描述 liuxj 20141211
	 * UnikMap nextNodeUm = getNextNodeID(flowid, nodeid, wfVars, isDirectForeNode);
	 * String nextNode =nextNodeUm.getString("nextnodeid"); UnikMap nextNodeInfo =
	 * storage.getNodeById(flowid, nextNode); if
	 * (CommonConst.WF_TASKASSIGN_MANUAL.equals(nextNodeInfo
	 * .getString("autodisuserflag"))) { dl = part.getUserId(flowid, "******", user,
	 * nextNodeInfo); } return dl; }
	 */

	/**
	 * <p>
	 * 根据节点信息自动选择人员进行任务分配,目前是随机分配和最少工作量两种方式
	 * </p>
	 * 
	 * @param wfDefId         工作流编号
	 * @param wfInstId        流程实例编号
	 * @param dwFlowNode      节点信息,
	 * @param local           当前机构信息bankid/userid
	 * @param user            所选用户-如果是自动分配则user=null
	 * @param vars
	 * @param routetype       路由类型：0:正常流转 1:打回
	 * @param taskassignstyle 任务分配方式 ：常规分配人员 从结点的历史处理人员中分配
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void assignTask(String wfDefId, String wfInstId, DwFlowNodeTable dwFlowNode, String local, String[] user, String[] viewUser, ArrayList taskList, UnikMap vars) throws Exception {
		String[] users = user;
		String routetype = vars.getString("routetype"); // 路由类型 正常 退回
		String taskassignstyle = vars.getString("taskassignstyle"); // 任务分配方式 正常分配 从历史人员中分配

		log(DEBUG, "routetype=" + routetype + ";taskassignstyle=" + taskassignstyle);
		// 不管是自动分配还是手工分配,users里存放的是所有的用户信息,如果为手工分配
		if (users == null) {
			// 根据
			if (taskassignstyle != null && taskassignstyle.equals("H")) {
				// 从流程任务的历史表获取处理人员信息,替换传入的用户信息
				// SELECT bankid,operid FROM DWTASKHIS WHERE WFID='20140917001324810561' AND
				// NODEID='FX102' ORDER BY TASKSER desc
				user = storage.selectHistoryUser(wfInstId, dwFlowNode.getNodeid(), "1", false);
				// 查到用户不为空则为历史用户
				if (user != null) {
					users = user;
				} else { // 如果历史用户不存在或者已注销；重新获取。
					UnikMap m = new UnikMap();
					m.put("org", vars.getString("bankid"));
					m.put("post", vars.getString("postidset"));
					m.put("postbankid", vars.getString("postbankid"));
					m.put("local", local);
					m.put("wfid", wfInstId);
					m.put("nodeid", dwFlowNode.getNodeid());
					m.put("hisflag", dwFlowNode.getHisflag());
					m.put("superbankscope", vars.getString("superbankscope"));
					m.put("bindprodid", vars.getString("bindprodid"));
					users = storage.selectUsers(m, false);

					// 进行自动分配用户信息
					log(DEBUG, "进行自动分配用户信息!");
				}

			} else {
				UnikMap m = new UnikMap();
				m.put("org", vars.getString("bankid"));
				m.put("post", vars.getString("postidset"));
				m.put("postbankid", vars.getString("postbankid"));
				m.put("local", local);
				m.put("wfid", wfInstId);
				m.put("nodeid", dwFlowNode.getNodeid());
				m.put("hisflag", dwFlowNode.getHisflag());
				m.put("superbankscope", vars.getString("superbankscope"));
				m.put("bindprodid", vars.getString("bindprodid"));
				users = storage.selectUsers(m, false);

				// 进行自动分配用户信息
				log(DEBUG, "进行自动分配用户信息!");
			}
		}

		// 检查人员是否符合分配原则,如果users来自于历史列表则就返回
		compareUsers(users, dwFlowNode);

		int num = Integer.parseInt(dwFlowNode.getMindealnum() + ""); // 最少处理人数
		log(DEBUG, "mindealnum:" + num);
		/*
		 * 之所有这里只判断是否自动分配,如果为手工分配那么users里的值一定和处理人数相同,
		 * 如果为分配所有人员那么users里的所有用户都得分配任务,如果为自动分配并且符合
		 * 条件的用户数和最少处理人数相等的话没必要去判断是随机还是最少工作量,当处理人 员大于最少处理人数时再去判断处理方式,最终将users修改为符合条件的用户
		 */
		if (CommonConst.WF_TASKASSIGN_AUTO.equals(dwFlowNode.getAutodisuserflag()) && users.length > num) {
			String processmode = dwFlowNode.getProcessmode();// 处理方式
			if (CommonConst.WF_PROCEMODE_WORKLEAST.equals(processmode)) { // 最少工作量
				users = storage.getTaskLeastUser(users, num);
			} else if (CommonConst.WF_PROCEMODE_RANDOM.equals(processmode)) {
				users = getRandomUser(num, users);
			}
		} else if (CommonConst.WF_TASKASSIGN_MANUAL.equals(dwFlowNode.getAutodisuserflag())) {
			// 自动分配，不需要对users进行处理，以下代码没有意义。
// 取出最少处理人员的随机数
//			if ((assignmindealnumstyle == null || assignmindealnumstyle
//					.equals("2")) && users.length > num) // 2 静态分配
//			{
//				users = getRandomUser(num, users);
//				}

		} else if (CommonConst.WF_TASKASSIGN_WFOUT.equals(dwFlowNode.getAutodisuserflag()) && users.length > num) {
			// users = getRandomUser(num, users);
		}

		// ===============================临时授权开始=====================================
		// 检查是否启用临时授权
		boolean blCheckIsStartTempAuth = storage.checkTempAuthStart(wfDefId);
		log(DEBUG, "===================================开始临时授权检测" + (blCheckIsStartTempAuth ? "启用临时授权" : "关闭临时授权"));
		if (blCheckIsStartTempAuth) {
			// 根据流程编号flowid获取业务范围 wfDefId
			SingleResult rangeSr = storage.getBusiRange(wfDefId);
			Errors.Assert(rangeSr != null, "workflow/BusinessScopeNotExist");
			String busiRange = rangeSr.getString("busiscope");

			// 到临时授权表获取临时受权用户 业务范围定义在业务范围与流程映射表中的业务种类按照顺序从1,2,3.。。
			UnikMap um = new UnikMap();

			um.put("busiscope", busiRange.trim());
			um.put("sysdate", storage.getWorkdateCurrTime());

			for (int i = 0; i < users.length; i++) {
				String tmpusers[] = users[i].split("/");
				um.put("authbankid", tmpusers[0]);
				um.put("authperson", tmpusers[1]);
				DataList dlist = storage.getTempAuth(um);
				while (dlist.next()) {
					String custScope = dlist.getString("CustScope");
					String recvPerson = dlist.getString("RecvPerson");
					String tempAuthId = dlist.getString("TempAuthId");

					if (custScope != null && custScope.equals("2")) // 指定客户
					{
						// 根据流程实例编号wfInstId到流程业务对照表中获取客户信息
						DbBusiWfMapTable dbBusiWfMapVo = storage.getBusiWFMap(wfInstId);
						String custid = dbBusiWfMapVo.getCustid();

						// 根据客户编号到指定客户表检查是否设定了该客户
						UnikMap umCust = new UnikMap();
						umCust.put("custid", custid);
						umCust.put("tempauthid", tempAuthId);
						boolean blPass = false;
						SingleResult custSr = new SingleResult();// storage.performAction("","dbAppTempAuthCustList", umCust);
						if (custSr != null) {
							String tempAuthStat = (String) custSr.get("TempAuthStat");
							if (tempAuthStat != null && tempAuthStat.equals("2")) {
								blPass = true;
							}
						}

						if (blPass) {
							// 用临时受权用户代替当前用户
							log(DEBUG, "指定客户临时授权，由用户" + recvPerson + "代替" + tmpusers[1] + "进行任务处理");
							users[i] = tmpusers[0] + "/" + recvPerson;
						}
					} else {
						// 用临时受权用户代替当前用户
						log(DEBUG, "全部客户临时授权，由用户" + recvPerson + "代替" + tmpusers[1] + "进行任务处理");
						users[i] = tmpusers[0] + "/" + recvPerson;
					}
				}
			}
		}
		// ===============================临时授权结束=====================================

		// 任务分配,到了这一步users里都是符合条件的,所以可以直接进行分配
		taskSerial = storage.newTask(dwFlowNode, wfInstId, ++taskSerial, users, viewUser, null, taskList);

		// 如果是终审节点，记录相关信息到业务流程对照表
		String curOperUser = local;
		String isPrimaryAuditNode = dwFlowNode.getIsprimaryauditnode();
		storage.updateBusiWFMap(dwFlowNode, wfInstId, curOperUser, users, isPrimaryAuditNode);
	}

	/**
	 * <p>
	 * 查找看任务分配的人员是否够完成任务
	 * </p>
	 * 
	 * @param users      用户列表
	 * @param dwFlowNode 节点详细信息
	 * @throws Exception users为null或没有值抛出WF_NoDealUserLogon,
	 *                   在岗位人员小于处理人员WF_MindealnumLTUsers
	 */
	private void compareUsers(String[] users, DwFlowNodeTable dwFlowNode) throws Exception {
		String flowid = dwFlowNode.getFlowid();
		if (users == null || users.length == 0) {
			String[] msg = new String[2];
			msg[0] = flowid;
			msg[1] = dwFlowNode.getNodename();
			throw new NamedException(formatMessage("WF_NoDealUserLogon", msg));
		}
		// 第一步:先检查在线人数够不够分配任务
		String autodisuserflag = dwFlowNode.getAutodisuserflag(); // 任务分配策略
		String assignmindealnumstyle = dwFlowNode.getAssignmindealnumstyle(); // 是否处理人数为手动分配人数

		Long assignnum = 0L;
		if (CommonConst.WF_TASKASSIGN_ALL.equals(autodisuserflag)) {
			assignnum = users.length + 0L; // 如果分配策略是分配所有人员那么分配的人数为所有在岗人员
			// 如果是分配给所有人,则将节点的处理人员更新为当前的在线人数
			storage.updateDealNum(flowid, dwFlowNode.getNodeid(), assignnum + 0);
		} else {
			// 如果用户来自于历史列表，则不需要进行检查，只要将所有的任务都完成就可以了
			if (checkHisFlag(dwFlowNode, users))
				return;

			assignnum = dwFlowNode.getMindealnum(); // 手动分配或自动分配,分配人员个数取处理人数

			//
			if (assignmindealnumstyle == null || assignmindealnumstyle.equals("2")) {
				if (users.length < assignnum || assignnum == 0) { // 在岗人员小于处理人员
					String msg[] = new String[2];
					msg[0] = dwFlowNode.getNodename();
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage("WF_MindealnumLTUsers", msg));
				}
			} else if (assignmindealnumstyle.equals("1")) {
				if (users.length == 0) { // 在岗人员小于处理人员
					String msg[] = new String[2];
					msg[0] = dwFlowNode.getNodename();
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage("WF_MindealnumLTUsers", msg));
				}
			}
		}

		String taskoverpolicy = dwFlowNode.getTaskoverpolicy();// 任务完成策略
		// 第二步:找出任务完成人数,如果完成策略是完成百分比和必须所有完成,那么人数肯定小于或等于分配的人数,
		// 如果是任意完成一个那么完成人数为1,所以只需要判断按完成数量
		Long compnum = 1L;
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) { // 按完成数量
			compnum = dwFlowNode.getAssignminnum();
		}

		if (assignnum < compnum) {
			String message[] = new String[2];
			message[0] = dwFlowNode.getNodename();
			message[1] = String.valueOf(compnum);
			throw new NamedException(formatMessage("WF_MindealnumLTUsers", message));
		}
	}

	/**
	 * <p>
	 * 检查处理人员是否从历史列表中取出
	 * </p>
	 * 
	 * @param storage WorkflowStorageImpl的hisisempty为true
	 * @param request request中有hisisempty为true
	 */
	private boolean checkHisFlag(DwFlowNodeTable dwFlowNode, String[] users) {
		boolean f = false;
		char isempty = '0'; // storage.hisisempty;
		// 从历史列表中没有查询出处理人员、不从历史列表中查处理人员这两种情况不考虑
		if (isempty == '0') { // 没有在当前引擎中查询处理人员,人员是手工分配
			Long minNum = dwFlowNode.getMindealnum();
			if (CommonConst.WF_HISUSER_ON.equals(dwFlowNode.getHisflag())) {
				f = users.length == minNum ? false : true;
			}
		} else if (isempty == '1') { // 从历史列表中查询出处理人员
			f = true;
		}
		return f;
	}

	/**
	 * <p>
	 * 取出最少处理人员的随机数
	 * </p>
	 * 
	 * @param num  人员个数
	 * @param user 处理人员数组
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	private String[] getRandomUser(int num, String[] user) throws Exception {
		// 取出最少处理人员的随机数
		int[] t = WorkFlowFunc.getRandom(num, user.length);
		String[] u = new String[num];
		for (int k = 0; k < num; k++) {
			u[k] = user[t[k]];
		}
		return u;
	}

	/**
	 * <p>
	 * 开始节点的任务分配,主要是处理像退回到开始节点这种流程
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void assignTaskAgain(String flowid, String wfid, DwFlowNodeTable nextNode, String local, ArrayList taskList) throws Exception {
		// 从历史任务表获取用户
		String nodeid = nextNode.getNodeid();
		String[] users = storage.selectTaskUsers(wfid, nodeid, CommonConst.WF_TASKTYPE_DEAL); // 处理任务
		String[] viewusers = storage.selectTaskUsers(wfid, nodeid, CommonConst.WF_TASKTYPE_VIEW); // 浏览任务

		// 从节点得到任务分配处理人数
		Long mindealnum = nextNode.getMindealnum();
		String assignmindealnumstyle = nextNode.getAssignmindealnumstyle();

		// 随机获取任务分配的用户
		int num = 1, i;
		Random R = new Random();

		if (assignmindealnumstyle == null || assignmindealnumstyle.equals("2")) {
			if (mindealnum > 0) {
				num = Integer.parseInt(mindealnum + "", 10);
			}
		} else {
			num = users.length;
		}
		String[] usrs = new String[num];

		if (assignmindealnumstyle == null || assignmindealnumstyle.equals("2")) {
			for (i = num; --i >= 0;) {
				int j = R.nextInt(num);

				if (users[j] != null) {
					usrs[i] = users[j];
					users[j] = null;
				} else {
					++i;
				}
			}
		} else {
			usrs = users;
		}

		// 创建新任务
		taskSerial = storage.newTask(nextNode, wfid, ++taskSerial, usrs, viewusers, null, taskList);

		// 如果是终审节点，记录相关信息到业务流程对照表
		String curOperUser = local;
		String isPrimaryAuditNode = nextNode.getIsprimaryauditnode();

		storage.updateBusiWFMap(nextNode, wfid, curOperUser, usrs, isPrimaryAuditNode);
	}

	protected UnikMap updateVariables(UnikMap wfDef, UnikMap node, UnikMap bizData, UnikMap wfInst) {
		// 获取 node 节点要保存的业务数据项
		String KWN = "busikeyword";
		String keySetStr = node.getString(KWN + "set");

		if (keySetStr == null) {
			return null;
		}

		String[] keySetArr = splitCsv(keySetStr);
		int c = keySetArr.length;

		// 获取业务数据项和流程通用字段的名称映射
		UnikMap kwMap = new UnikMap();
		String n, s, v;

		for (int i = 1;; i++) {
			n = KWN + i;

			if (!wfDef.containsKey(n)) {
				break;
			}

			s = wfDef.getString(n);

			if (s != null && s.length() > 0) {
				kwMap.put(s, n);
			}
		}

		// 保存业务数据
		UnikMap wfVars = new UnikMap();

		while (--c >= 0) {
			n = keySetArr[c];
			s = kwMap.getString(n);
			v = bizData.getString(n);
			wfVars.put(s, v);
			wfInst.put(s, v);
		}

		return wfVars;
	}

	protected UnikMap getVariables(UnikMap wfDef, UnikMap wfInst) {
		String KWN = "busikeyword";
		UnikMap wfVars = new UnikMap();
		String n, s;

		for (int i = 1;; i++) {
			n = KWN + i;

			if (!wfDef.containsKey(n)) {
				break;
			}

			s = wfDef.getString(n);

			if (s != null && s.length() > 0) {
				wfVars.put(s, wfInst.getString(n));
			}
		}

		return wfVars;
	}

	/**
	 * <p>
	 * 随机序号: 时间17+3位随机数
	 * </p>
	 * 
	 * @return String 随机数字
	 */
	protected String generateRandomSeq() {
		SimpleDateFormat fmt = new SimpleDateFormat(CommonConst.DATE_TYPE_TIMESTAMP_MSEL);
		StringBuffer buf = new StringBuffer(fmt.format(new Date()));
		Random R = new Random();
		for (int i = 0; i < 3; i++) {
			buf.append(R.nextInt(10));
		}
		return buf.toString();
	}

	protected String[] splitCsv(String str) {
		return StringUtils.split(str, ",");
	}

	/**
	 * <p>
	 * 返回消息提示,格式化返回的消息可以带参数
	 * </p>
	 * 
	 * @throws Exception
	 */
	protected String formatMessage(String arg0, Object obj) throws Exception {
		return storage.formatMessage(arg0, obj);
	}

	/**
	 * <p>
	 * 检查当前是否是开始节点,如果是开始节点需要更新流程描述信息
	 * </p>
	 * 
	 * @param nodetype     节点类型
	 * @param dwFlowInstVo 流程实例信息
	 * @param bizData      最新的参数信息
	 * @throws Exception
	 */
	private void checkIsStartNode(String nodetype, DwFlowInstTable dwFlowInstVo, UnikMap bizData) throws Exception {
		if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
			String flowid = dwFlowInstVo.getFlowid();
			String wfid = dwFlowInstVo.getWfid();
			DwFlowMainTable dwFlowMainVo = storage.getWorkflowDefinition(flowid);
			if (wfid.length() > 0) {
				// 取出流程定义中的流程描述信息进行替换
				String wfdesc = storage.replaceFlowDesc(dwFlowMainVo.getFlowdesc(), bizData);
				DwFlowInstTable dwFlowInstVoNw = new DwFlowInstTable();
				dwFlowInstVoNw.setWfid(wfid);
				dwFlowInstVoNw.setFlowdesc(wfdesc);
				storage.saveDwFlowInstTable(dwFlowInstVoNw);
			}
		}
	}
}