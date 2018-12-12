package cn.com.jbbis.jbportal.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.AppUser;
import cn.com.jbbis.afx.ServiceOK;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.sql.DataContext;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.Errors;
import cn.com.jbbis.util.NamedException;
import cn.com.jbbis.util.StringHelper;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>
 * Title: 工作流引擎
 * </p>
 * <p>
 * Description: 处理流程的任务，控制流程的流转
 * </p>
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
public class Workflow implements SupperLog {
	/**
	 * 流程引擎操作数据库公共类
	 */
	protected WorkflowStorageImpl storage;
	/**
	 * 流程序号
	 */
	protected int taskSerial;

	/**
	 * 当前节点定义信息
	 */
	private UnikMap currentNode = null;

	public Workflow(WorkflowStorageImpl wfStorage) {
		this.storage = wfStorage;
	}

	/**
	 * 此构造方法为了兼容应用交易，以后不再使用了
	 * 
	 * @param s
	 */
	public Workflow(WorkflowStorage wfStorage, Service s) {
		this((WorkflowStorageImpl) wfStorage);
	}

	/**
	 * 流程开始,启动新的流程。所做工作如下：
	 * <ol>
	 * <li>新建流程,创建流程实例</li>
	 * <li>新建工作列表，将任务写入任务列表dwtask</li>
	 * <li>继续，调用流程引擎的resume处理任务</li>
	 * </ol>
	 * 
	 * @param wfDefId
	 *            工作流流程编号(flowid)
	 * @param user
	 *            当前处理人员bankid/operid
	 * @param bizData
	 *            工作流变量
	 * @throws java.lang.Exception
	 */
	protected AppResponse start(String wfDefId, String user, UnikMap bizData,
			String[] users, String[] viewUsers, UnikMap umWfid,
			UnikMap umWorkflow, UnikMap outTaskNode,Queue<UnikMap> tranQueue) throws Exception {
		// 流程实例编号
		log(INFO, "[WF_IN_start] workflow start, flowid=" + wfDefId);
		String wfInstId = generateRandomSeq();
		log(DEBUG, "[WF_start NO:1] create wfid OK, value is " + wfInstId);
		
		ArrayList taskList = new ArrayList();
		// 获取外部传入的业务参数
		String busiOperateStyle = "";
		UnikMap busiWorkflow = (UnikMap) umWorkflow;
		if (busiWorkflow != null) {
			String busioperatestyleparam = busiWorkflow
					.getString("busioperatestyle");

			// System.out.println("busioperatestyleparam=====" +
			// busioperatestyleparam);

			busiOperateStyle = CommonConst.WF_BUSIOPERATESTYLE_G;

			/*
			 * if(busioperatestyleparam!=null &&
			 * !busioperatestyleparam.equals("")
			 * &&(busioperatestyleparam.equals("G") ||
			 * busioperatestyleparam.equals("S") ||
			 * busioperatestyleparam.equals("O") ||
			 * busioperatestyleparam.equals("P") ||
			 * busioperatestyleparam.equals("N") ||
			 * busioperatestyleparam.equals("F")) ){
			 * if(busioperatestyleparam.equals("G")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_G; else
			 * if(busioperatestyleparam.equals("S")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_S; else
			 * if(busioperatestyleparam.equals("P")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_P; else
			 * if(busioperatestyleparam.equals("O")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_O; else
			 * if(busioperatestyleparam.equals("N")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_N; else
			 * if(busioperatestyleparam.equals("F")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_F; } else { String prodid =
			 * busiWorkflow.getString("prodid"); if(prodid!=null &&
			 * !prodid.equals("")&& (prodid.substring(0,1).equals("A") ||
			 * prodid.substring(0,1).equals("H") ||
			 * prodid.substring(0,1).equals("G")||
			 * prodid.substring(0,1).equals("E"))) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_G; else if(prodid!=null &&
			 * !prodid.equals("") && prodid.substring(0,1).equals("C"))
			 * busiOperateStyle = CommonConst.WF_BUSIOPERATESTYLE_S; else
			 * if(prodid!=null && !prodid.equals("") &&
			 * prodid.substring(0,1).equals("B")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_P; else if(prodid!=null &&
			 * !prodid.equals("") && prodid.substring(0,1).equals("D"))
			 * busiOperateStyle = CommonConst.WF_BUSIOPERATESTYLE_N; else
			 * if(prodid!=null && !prodid.equals("") &&
			 * prodid.substring(0,1).equals("F")) busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_F; else busiOperateStyle =
			 * CommonConst.WF_BUSIOPERATESTYLE_O; }
			 */

			log(DEBUG,
					"[WF_start NO:1.1] generate busiOperateStyle OK.busiOperateStyle="
							+ busiOperateStyle);
		}
		// 创建流程实例
		log(DEBUG, "[WF_start NO:2] begin CreateInstance, flowid=" + wfDefId
				+ ", wfid=" + wfInstId);
		storage.createInstance(wfDefId, wfInstId, user,
				CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);
		log(DEBUG, "[WF_start NO:3] createInstance OK.");

		// 得到开始节点定义
		currentNode = storage.getNodeByType(wfDefId,CommonConst.WF_NODETYPE_START);

		// 新建工作列表
		taskSerial = 1;

		// 分配新任务到任务列表
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfInstId
				+ ", nodeid=" + currentNode.getString("nodeid"));
		storage.newTask(currentNode, wfInstId, taskSerial, user.split(","),
				viewUsers, bizData,taskList);
		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfInstId
				+ " taskRound=1");
		storage.insertTaskRound(wfInstId, 1);
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid="
				+ wfInstId + "");
		// 获取isWriteBusiWFMap配置项
		UnikMap umFlowMain = storage.getWorkflowDefinition(wfDefId);
		String isWriteBusiWFMap = umFlowMain.getString("iswritebusiwfmap");

		System.out.println("isWriteBusiWFMap==================="
				+ isWriteBusiWFMap);
		if (busiWorkflow!=null && isWriteBusiWFMap != null && isWriteBusiWFMap.equals("1")) {

			UnikMap umBusiWFMap = new UnikMap();
			// 接收传入的业务信息LoanId，TranSeq，CustId，CustName，ProdId，ProdName，BusiType
			String loanid = busiWorkflow.getString("loanid");
			if(loanid==null || loanid.equals(""))
				loanid = busiWorkflow.getString("bizserno");
			String custid = busiWorkflow.getString("custid");
			String custname = busiWorkflow.getString("custname");
			String prodid = busiWorkflow.getString("prodid");
			String prodName = busiWorkflow.getString("prodname");
			String busitype = busiWorkflow.getString("busitype");
			String busitypedetail = busiWorkflow.getString("busitypedetail");
			// System.out.println("loanid=" + loanid + ";custid=" + custid +
			// ";custname=" + custname + ";prodName=" + prodName + ";prodid=" +
			// prodid + ";busitype===" + busitype);
			// 获取序号
			int transeq = storage.getBusiMaxNo(loanid);
			transeq += 1;
			umBusiWFMap.put("loanid", loanid);

			umBusiWFMap.put("transeq", transeq);
			umBusiWFMap.put("custid", custid);
			umBusiWFMap.put("custname", custname);
			umBusiWFMap.put("prodid", prodid);
			umBusiWFMap.put("prodName", prodName);
			if (busitypedetail == null || busitypedetail.equals(""))
				umBusiWFMap.put("busitype", busitype);
			else
				umBusiWFMap.put("busitype", busitypedetail);

			// 组织流程参数FlowId，WFId，NodeName，CurOperId，CurOperIdName，CurBankId，CurBankName，ReceTime

			umBusiWFMap.put("wfid", wfInstId);
			umBusiWFMap.put("flowId", wfDefId);
			umBusiWFMap.put("nodename", currentNode.getString("nodename"));
			umBusiWFMap.put("flowId", wfDefId);

			/*
			 * String curusers[] = user.split("/"); umBusiWFMap.put("curoperid",
			 * curusers[1]); umBusiWFMap.put("curbankid", curusers[0]);
			 * System.out.println("curbankid=" + curusers[0] + ";curoperid=" +
			 * curusers[1]); UnikMap bankMap = storage.getBankInfo(curusers[0]);
			 * UnikMap userMap = storage.getUserInfo(curusers[0],curusers[1]);
			 * umBusiWFMap.put("curoperidname", userMap.getString("username"));
			 * umBusiWFMap.put("curbankname", umBusiWFMap.get("curbankname"));
			 */

			umBusiWFMap.put("isfinish", "1");
			// 写入
			boolean blSuccess = false;
			boolean blExist = storage.checkBusiWFMapIsExist(wfInstId);
			if (blExist)
				blSuccess = storage.updateReturnBusiWFMap(umBusiWFMap);
			else
				blSuccess = storage.insertBusiWFMap(umBusiWFMap);

			if (blSuccess) {
				log(DEBUG, "[WF_start NO:4.3] 写入对照表成功！！！！");
				System.out.println("写入对照表成功！！！！");
			} else {
				log(DEBUG, "[WF_start NO:4.3] 写入对照表失败！！！！");
				System.out.println("写入对照表失败！！！！");
			}
		}
		boolean blStart = true;
		boolean blIsDirectForeNode = false;
		// 继续

		AppResponse res = resume(wfInstId, user, bizData, users, viewUsers,
				blStart, blIsDirectForeNode, outTaskNode,tranQueue);

		log(INFO, "[WF_OUT_start] OUT Workflow.start, flowid = " + wfDefId);

		// 输出工作流实例编号
		log(INFO, "[WF_OUT_start] OUT Workflow.start,为外部输出工作流实例 wfInstId = "
				+ wfInstId);
		umWfid.put("wfInstId", wfInstId);
		return res;
	}

	protected AppResponse startSubWF(String wfInstId, String wfDefId,
			String user, UnikMap bizData, String[] users, String[] viewUsers,
			UnikMap outTaskNode) throws Exception {
		// 流程实例编号
		log(DEBUG, "[sub workflow NO:1] sub workflow start, value is " + wfInstId);
		// 得到开始节点定义
		currentNode = storage.getNodeByType(wfDefId,CommonConst.WF_NODETYPE_START);

		// 新建工作列表
		// taskSerial = 1;
		// 分配新任务到任务列表
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfInstId
				+ ", nodeid=" + currentNode.getString("nodeid"));
		storage.newTask(currentNode, wfInstId, taskSerial, user.split(","),
				viewUsers, bizData,null);
		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfInstId
				+ " taskRound=1");
		storage.insertTaskRound(wfInstId, 1);
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid="
				+ wfInstId + "");

		return null;
	}

	/**
	 * 使用para中的选项格式化参数值为中文描述
	 * 
	 * @param vars
	 * @return
	 * @throws Exception
	 */
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
			/*else
			{
				String tmpValue = "";
				tmpValue = storage.getSelectText(name, value);
				if (!"".equals(tmpValue))
					value = tmpValue;
			}*/

			um.put(name, value);
		}

		return um;
	}

	/**
	 * 流程任务处理。
	 * <ol>
	 * <li>定位当前节点</li>
	 * <li>保存业务数据</li>
	 * <li>结束当前任务</li>
	 * <li>转至下一节点</li>
	 * <li>分派任务</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            应用中的wfid
	 * @param user
	 *            当前机构下的操作员,值为机构代码/用户代码
	 * @param bizData
	 *            上传报文中的处理意见,路由的判断的数据从此中取得
	 * @param users
	 *            从上传报文中取users,lsc.getProperties("users"),手工分配应该设置users值
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse resume(String wfInstId, String user, UnikMap bizData,
			String[] users, String[] viewUsers, boolean blStart,
			boolean isDirectForeNode, UnikMap outTaskNode,Queue<UnikMap> tranQueue) throws Exception {
		
		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList(); //输出分配多人的任务信息列表
		if(outTaskNode==null)
			outTaskNode = new UnikMap();
		// 得到流程实例
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid=" + wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// 检查当前流程是否发布
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// 得到当前未处理的任务
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getTask(wfInstId, user);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// 设置工作流序号
		if (taskSerial == 0){
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// 得到当前节点定义,如果不等于空说明是开始节点所以就不用再查询一次节点信息了
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝测试(开始节点)＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
			ischeckNode = false;
		}
		
		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="+ wfInstId);
		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="+ wfInstId);

		// 完成当前的工作任务 【把当前任务插入历史任务表，删除当前任务表】
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeTask(wfInstId, task.getString("taskser"),
				task.getString("nodephase"), bizData, currentNode);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);

		if (ischeckNode) {
			log(DEBUG,"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		//设置对外输出的节点
		outTaskNode.put("currentNode", currentNode); //输出当前节点
		outTaskNode.put("wfid", wfInstId);
		outTaskNode.put("taskSer", taskList);
		
		// 如果返回false则说明还有用户没有处理，主要针对一个节点多人操作
		if (!storage.isContinue(currentNode, task, blStart)) {
			log(DEBUG,"[WF_IN_Resume] this task has many user to dispose, so need return");
			outTaskNode.put("isLastDealer", "false");
			return null;
		}
		outTaskNode.put("isLastDealer", "true");
		
		/**
		 * // 转至下一节点 if (NODETYPE_FINAL.equals(currentNode.get("nodetype"))) {
		 * //结束节点 storage.updateWorkflowStatus(wfInstId, 0);
		 * storage.completeInstance(wfInstId); return; }
		 */
		// 取下一节点编号 先获取正常的下一个结点，判断下一节点设置方式，如果是条件方式，获取条件中的下一个结点
		UnikMap nextNodeUm =  getnextnodeid(wfDefId, wfInstId, nodeId,currentNode, isDirectForeNode);
		String nextNodeId =nextNodeUm.getString("nextnodeid");
		routetype=nextNodeUm.getString("routetype");
		taskassignstyle=nextNodeUm.getString("taskassignstyle");
		// 如果下一个节点为空
		Errors.Assert(nextNodeId != null && !nextNodeId.equals(""), "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.get("nodetype")))
			return null;

		String[] nextIds = splitCsv(nextNodeId);
		UnikMap[] outNextNode = new UnikMap[nextIds.length]; // 记录输出到交易中的结点
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// 取出下一节点编号准备进行任务分配
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			nextNode.put("routetype", routetype);  //缺省值
			nextNode.put("taskassignstyle", taskassignstyle);  //缺省值			
			outNextNode[i] = new UnikMap();
			outNextNode[i] = nextNode;
			// 写入"上一环节编号"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));
			// 如果下一个节点是开始节点，需要进行必要的业务数据处理。一般为标志数据
			if (CommonConst.WF_NODETYPE_START.equals(nextNode.get("nodetype"))) {
				// 如果下一节点是开始节点，进行退回的时候，需要执行流程定义的回退语句
				//storage.doWithDraw(wfDefId, wfInstId);  //liuxj
				UnikMap wfVars = storage.getInstVar(wfInstId, false);
				// 使用para中定义的选择项格式化变量值为对应的名称
				wfVars.putAll(formatVars(wfVars)); //格式化参数为para中的文字描述 liuxj 20141115
				String routetype1 = nextNode.getString("routetype");  
				if(routetype1!=null && routetype.equals("1")){  //退回路由
					//退回交易入队列
					UnikMap tranUm = new UnikMap();
					tranUm.put("trancode", currentNode);
					tranUm.put("trantype", "retutrans");
					tranUm.put("vars", wfVars);
					tranQueue.add(tranUm);
				}
				log(DEBUG,"[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// 如果是开始环节就写入上一环节编号
				nextNode.put("forenodeid", currentNode.getString("nodeid"));
				// System.out.println("如果下一个节点是开始节点(user)=" + user);
				assignTaskAgain(wfDefId, wfInstId, nextNode, user,taskList);
				// 退回，更新轮次
				int maxTaskRound = storage.getTaskMaxTaskRound(wfInstId);
				storage.updateTaskRound(wfInstId, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// 任务分派,分发 
			else if (CommonConst.WF_NODETYPE_OUT.equals(nextNode.get("nodetype"))) {
				String next[] = splitCsv(nextNode.getString("nextnode"));
				for (int j = 0; j < next.length; j++) {
					UnikMap nodeUm = storage.getNodeById(wfDefId, next[j]);
					nodeUm.put("routetype", routetype);
					nodeUm.put("taskassignstyle", taskassignstyle);
					assignTask(wfDefId, wfInstId,nodeUm, user, users,viewUsers,taskList);					
				}
				continue;
			}
			// 合并 通过路由方式进行节点跳转
			else if (CommonConst.WF_NODETYPE_UNITE.equals(nextNode.get("nodetype"))) {
				String next = nextNode.getString("nodeid");
				if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextNode.getString("nextnodemode"))) {
					Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
					WorkflowRoute R = (WorkflowRoute) C.newInstance();
					UnikMap wfVars = storage.getInstVar(wfInstId, false);
					// 使用para中定义的选择项格式化变量值为对应的名称
					wfVars.putAll(formatVars(wfVars)); //格式化参数为para中的文字描述 liuxj 20141115
					LinkedList route = storage.getNodeRoute(wfDefId, next);
					UnikMap nextNodeUM = R.getNext(route, wfVars);
					nextNodeId = nextNodeUM.getString("nextNodeId");
					taskassignstyle = nextNodeUM.getString("taskAssignStyle");
					routetype = nextNodeUM.getString("routeType");
					//退回					
					if (nextNodeId == null || nextNodeId.length() == 0)
						continue;
					else {
						nextNode = storage.getNodeById(wfDefId, nextNodeId);						
						// 通过路由的类型与任务分配方式（）判断获取用户的方式，覆盖传入的用户信息
						nextNode.put("taskassignstyle", taskassignstyle);
	                    nextNode.put("routetype", routetype);
						assignTask(wfDefId, wfInstId, nextNode, user, users,viewUsers,taskList);
						String routetype1 = nextNode.getString("routetype");
						if(routetype1!=null && routetype.equals("1"))  //退回路由
						{
							//退回交易入队列
							UnikMap tranUm = new UnikMap();
							tranUm.put("trancode", currentNode);
							tranUm.put("trantype", "retutrans");
							tranUm.put("vars", wfVars);
							tranQueue.add(tranUm);
						}
						break;
					}
				}
			} else if (CommonConst.WF_NODETYPE_SUBWORKFLOW.equals(nextNode.get("nodetype"))){ // 子流程
			
			}
			// 结束节点
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// 更改工作流状态
				storage.updateWorkflowStatus(wfInstId, 2);
				// 完成实例
				storage.completeInstance(wfInstId);
				// 更改流程业务对照表为完成状态
				storage.completeBusiWFMap(wfInstId, "2");
				// 调用结束节点的交易 submtrancode
				//=============结束交易入队列==============
				// 获取流程变量
				UnikMap wfVars = storage.getInstVar(wfInstId, false); // liuxj
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", nextNode);  //节点信息
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "endtrans");
				tranQueue.add(tranUm);
				AppResponse res = null;
				//AppResponse res = storage.doService(nextNode);  //liuxj
				// 检查是否拼接流程
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);				
					wfVars = storage.getInstVar(wfInstId, false); // liuxj														// 20141115
					// 使用para中定义的选择项格式化变量值为对应的名称
					bizData.putAll(formatVars(wfVars)); // liuxj 20141115
					// 流程拼接
					unite(nextNode.getString("unitflowid"), user, viewUsers,bizData, busiOperateStyle,taskList);
					//res = null;
				} else {
					// 如果不拼接流程就将dwtaskvars写入dwtaskvarshis并清空dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// 流程结束，删除最大任务轮次
					storage.deleteTaskRound(wfInstId);
				}
				//20141218 liuxj
				/*if (res instanceof ServiceOK) {
					// 如果是普通的交易成功提示,则将此消息进行修改
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndSucceeded",
							wfInst.getString("flowname")));
				}*/

				// 输出任务信息
				if (outTaskNode != null) {
					outTaskNode.put("currentNode", currentNode);
					outTaskNode.put("nextNode", outNextNode);
					outTaskNode.put("wfid", wfInstId);
					outTaskNode.put("taskSer", taskList);
				}
				return res;
			}
			// 中间节点进行任务分配
			// System.out.println("中间节点进行任务分配" );
			assignTask(wfDefId, wfInstId, nextNode, user, users, viewUsers,taskList);

            //打回路由，设置当前节点（包含打回交易）与环境变量到队列中
            String routetype1 = nextNode.getString("routetype");
			if(routetype1!=null && routetype1.equals("1"))
			{
				//退回交易入队列
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", currentNode);
				// 获取流程变量
				UnikMap wfVars = storage.getInstVar(wfInstId, false); // liuxj
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "retutrans");
				tranQueue.add(tranUm);
			}
		}
		// 20130710，加入紧急程度，若变量中没有dwflowinst.instancyLevel则不更新dwflowinst中的该字段
		// 更新流程序号
		String instancylevel = bizData.getString("dwflowinst.instancylevel");
		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid="
				+ wfInstId + ", taskser=" + taskSerial + ", instancyLevel="
				+ instancylevel);

		storage.updateTaskSerial(wfInstId, taskSerial, instancylevel);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);

		// 输出任务信息
		if (outTaskNode != null) {
			outTaskNode.put("currentNode", currentNode);
			outTaskNode.put("nextNode", outNextNode);
			outTaskNode.put("wfid", wfInstId);
			outTaskNode.put("taskSer", taskList);
		}
		return null;
	}

	/**
	 * 结束当前所有任务，并把任务转到下一节点。
	 * <ol>
	 * <li>定位当前节点</li>
	 * <li>保存业务数据</li>
	 * <li>结束当前任务</li>
	 * <li>转至下一节点</li>
	 * <li>分派任务</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            应用中的wfid
	 * @param user
	 *            当前机构下的操作员,值为机构代码/用户代码
	 * @param bizData
	 *            上传报文中的处理意见,路由的判断的数据从此中取得
	 * @param users
	 *            从上传报文中取users,lsc.getProperties("users"),手工分配应该设置users值
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse breakWorkflowNext(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode, UnikMap outTaskNode)
			throws Exception {

		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList();
		// 得到流程实例
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// 检查当前流程是否发布
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// 得到当前任意一个未处理的任务 并行任务的话不能这样处理
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// 设置工作流序号
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// 得到当前节点定义,如果不等于空说明是开始节点所以就不用再查询一次节点信息了
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			ischeckNode = false;
		}

		// 得到流程定义
		// Map wfDef = storage.getWorkflowDefinition(wfDefId);

		// 存储该节点要保存的流程变量
		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="
				+ wfInstId);

		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,
				nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="
				+ wfInstId);

		// 完成所有的工作任务 【删除当前所有任务】
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeAllTask(wfInstId);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);

		if (ischeckNode) {
			log(DEBUG,
					"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		/**
		 * // 转至下一节点 if (NODETYPE_FINAL.equals(currentNode.get("nodetype"))) {
		 * //结束节点 storage.updateWorkflowStatus(wfInstId, 0);
		 * storage.completeInstance(wfInstId); return; }
		 */

		// 取下一节点编号 先获取正常的下一个结点，判断下一节点设置方式，如果是条件方式，获取条件中的下一个结点
        UnikMap nextNodeUm = getnextnodeid(wfDefId, wfInstId, nodeId,
				currentNode, isDirectForeNode);
		String nextNodeId = nextNodeUm.getString("nextnodeid");
		// 如果下一个节点为空
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.get("nodetype")))
			return null;

		UnikMap[] outNextNode = null;
		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// 取出下一节点编号准备进行任务分配
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			outNextNode[i] = nextNode;

			// 写入"上一环节编号"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));

			// 如果下一个节点是开始节点，需要进行必要的业务数据处理。一般为标志数据
			if (CommonConst.WF_NODETYPE_START.equals(nextNode.get("nodetype"))) {
				// 如果下一节点是开始节点，进行退回的时候，需要执行流程定义的回退语句
				//storage.doWithDraw(wfDefId, wfInstId);
				log(DEBUG,"[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// 如果是开始环节就写入上一环节编号
				nextNode.put("forenodeid", currentNode.getString("nodeid"));
				// System.out.println("如果下一个节点是开始节点(user)=" + user);
				assignTaskAgain(wfDefId, wfInstId, nextNode, user,taskList);

				// 退回，更新轮次
				int maxTaskRound = storage.getTaskMaxTaskRound(wfInstId);
				storage.updateTaskRound(wfInstId, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// 任务分派,分发
			else if (CommonConst.WF_NODETYPE_OUT.equals(nextNode
					.get("nodetype"))) {
				System.out.println("任务分派,分发");
				String next[] = splitCsv(nextNode.getString("nextnode"));
				for (int j = 0; j < next.length; j++) {
					UnikMap nodeUm =storage.getNodeById(wfDefId, next[j]); 
					nodeUm.put("routetype", routetype);
					nodeUm.put("taskassignstyle", taskassignstyle);
					assignTask(wfDefId, wfInstId,nodeUm, user, users,viewUsers,taskList);
				}
				continue;
			}
			// 合并 通过路由方式进行节点跳转
			else if (CommonConst.WF_NODETYPE_UNITE.equals(nextNode.get("nodetype"))) {
				String next = nextNode.getString("nodeid");
				if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextNode
						.getString("nextnodemode"))) {
					Class C = Class
							.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
					WorkflowRoute R = (WorkflowRoute) C.newInstance();
					UnikMap wfVars = storage.getInstVar(wfInstId, false);

					LinkedList route = storage.getNodeRoute(wfDefId, next);
					// nextNodeId = R.getNext(route, wfVars);
					UnikMap nextNodeUM = R.getNext(route, wfVars);
					nextNodeId = nextNodeUM.getString("nextNodeId");
					taskassignstyle = nextNodeUM.getString("taskAssignStyle");
					routetype = nextNodeUM.getString("routeType");

					if (nextNodeId == null || nextNodeId.length() == 0)
						continue;
					else {
						nextNode = storage.getNodeById(wfDefId, nextNodeId);
						nextNode.put("routetype", routetype);
						nextNode.put("taskassignstyle", taskassignstyle);
						assignTask(wfDefId, wfInstId, nextNode, user, users,viewUsers,taskList);
						
						//执行打回交易
						
						break;
					}
				}
			}
			// 结束节点
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode
					.get("nodetype"))) {
				// 更改工作流状态
				storage.updateWorkflowStatus(wfInstId, 2);
				// 完成实例
				storage.completeInstance(wfInstId);
				// 更改流程业务对照表为完成状态
				storage.completeBusiWFMap(wfInstId, "2");

				// 调用结束节点的交易 submtrancode
				AppResponse res = storage.doService(nextNode);
				// 检查是否拼接流程
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// 流程拼接
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// 如果不拼接流程就将dwtaskvars写入dwtaskvarshis并清空dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// 流程结束，删除最大任务轮次
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// 如果是普通的交易成功提示,则将此消息进行修改
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndSucceeded",
							wfInst.getString("flowname")));
				}
				return res;
			}
			// 中间节点进行任务分配
			nextNode.put("routetype", routetype);
			nextNode.put("taskassignstyle", taskassignstyle);
			assignTask(wfDefId, wfInstId, nextNode, user, users, viewUsers,taskList);

		}
		// 更新流程序号

		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid="
				+ wfInstId + ", taskser=" + taskSerial);
		storage.updateTaskSerial(wfInstId, taskSerial);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);

		// 输出任务信息
		// UnikMap nodeMap = new UnikMap();
		if (outTaskNode != null) {
			outTaskNode.put("currentNode", currentNode);
			outTaskNode.put("nextNode", outNextNode);
			outTaskNode.put("wfid", wfInstId);
			outTaskNode.put("taskSer", taskList);

			log(INFO, "currentNode" + currentNode.getString("nodeid") + ":"
					+ currentNode.getString("nodename"));
			for (int i = 0; i < outNextNode.length; i++)
				log(INFO, "nextNode" + outNextNode[i].getString("nodeid") + ":"
						+ outNextNode[i].getString("nodename"));

			log(INFO, "wfid" + outTaskNode.getString("wfid") + ":"
					+ outTaskNode.getString("taskSer"));
		}
		return null;
	}

	/**
	 * 结束当前所有任务。
	 * <ol>
	 * <li>定位当前节点</li>
	 * <li>保存业务数据</li>
	 * <li>结束当前任务</li>
	 * <li>转至下一节点</li>
	 * <li>分派任务</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            应用中的wfid
	 * @param user
	 *            当前机构下的操作员,值为机构代码/用户代码
	 * @param bizData
	 *            上传报文中的处理意见,路由的判断的数据从此中取得
	 * @param users
	 *            从上传报文中取users,lsc.getProperties("users"),手工分配应该设置users值
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse breakWorkflow(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode) throws Exception {
		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList();
		// 得到流程实例
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// 检查当前流程是否发布
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// 得到当前任意一个未处理的任务 并行任务的话不能这样处理
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// 设置工作流序号
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// 得到当前节点定义,如果不等于空说明是开始节点所以就不用再查询一次节点信息了
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			ischeckNode = false;
		}

		// 存储该节点要保存的流程变量
		// Map wfVars = bizData;
		/*
		 * Iterator it = bizData.keySet().iterator(); while(it.hasNext()) {
		 * String key = (String)it.next(); System.out.println("bizData==" + key
		 * + "=" + bizData.getString(key)); System.out.println("wfInstId==" +
		 * wfInstId + ";wfDefId=" + wfDefId + ";nodeId=" + nodeId + ";nodedesc="
		 * + currentNode.getString("nodedesc")); }
		 */
		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="
				+ wfInstId);

		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,
				nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="
				+ wfInstId);

		// 完成所有的工作任务 【删除当前所有任务】
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeAllTask(wfInstId);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);

		if (ischeckNode) {
			log(DEBUG,
					"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		// 无条件获取结束节点
		// System.out.println("wfDefId" + wfDefId + ",wfInstId=" + wfInstId +
		// ",nodeId=" + nodeId + ",currentNode=");
		String nextNodeId = getEndNode(wfDefId);
		// 如果下一个节点为空
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");

		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// 取出下一节点编号准备进行任务分配
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);

			// 写入"上一环节编号"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));

			if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// 更改工作流状态
				storage.updateWorkflowStatus(wfInstId, 2);
				// 完成实例
				storage.completeInstance(wfInstId);
				// 更改流程业务对照表为完成状态
				storage.completeBusiWFMap(wfInstId, "2");

				// 调用结束节点的交易 submtrancode
				// System.out.println(nextNode.get("submtrancode"));
				AppResponse res = storage.doService(nextNode);
				// 检查是否拼接流程
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// 流程拼接
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// 如果不拼接流程就将dwtaskvars写入dwtaskvarshis并清空dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// 流程结束，删除最大任务轮次
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// 如果是普通的交易成功提示,则将此消息进行修改
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndBreakSucceeded",
							wfInst.getString("flowname")));
				}
				return res;
			}

		}

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);

		return null;
	}
	
	/**
	 * 结束当前所有任务。
	 * <ol>
	 * <li>定位当前节点</li>
	 * <li>保存业务数据</li>
	 * <li>结束当前任务</li>
	 * <li>转至下一节点</li>
	 * <li>分派任务</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            应用中的wfid
	 * @param user
	 *            当前机构下的操作员,值为机构代码/用户代码
	 * @param bizData
	 *            上传报文中的处理意见,路由的判断的数据从此中取得
	 * @param users
	 *            从上传报文中取users,lsc.getProperties("users"),手工分配应该设置users值
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("deprecation")
	public AppResponse ForceBreakWorkflow(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode) throws Exception {
		
		@SuppressWarnings("rawtypes")
		ArrayList taskList = new ArrayList();
		// 得到流程实例
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);
		
		String busiOperateStyle = wfInst.getString("busiOperateStyle");
		
		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);
		
		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// 检查当前流程是否发布
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");
		
		// 得到当前任意一个未处理的任务 并行任务的话不能这样处理
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);
		
		// 设置工作流序号
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}
		
		// 得到当前节点定义,如果不等于空说明是开始节点所以就不用再查询一次节点信息了
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			ischeckNode = false;
		}

		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="
				+ wfInstId);
		
		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,
				nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="
				+ wfInstId);
		
		// 将当前任务放入历史任务中，并描述清楚是强制终止的。
		currentNode.put("nodedesc", "强制终止流程[管理员："+user+"]");
		storage.completeTask(wfInstId, task.getString("taskser"),
				task.getString("nodephase"), bizData, currentNode);
		
		// 完成所有的工作任务 【删除当前所有任务】
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeAllTask(wfInstId);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);
		
		if (ischeckNode) {
			log(DEBUG,
			"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}
		
		// 无条件获取结束节点
		String nextNodeId = getEndNode(wfDefId);
		// 如果下一个节点为空
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");
		
		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// 取出下一节点编号准备进行任务分配
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			
			// 写入"上一环节编号"
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));
			
			if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// 更改工作流状态
				storage.updateWorkflowStatus(wfInstId, 2);
				// 完成实例
				storage.completeInstance(wfInstId);
				// 更改流程业务对照表为【强制终止】状态
				storage.completeBusiWFMap(wfInstId, "3");
				
				// 调用结束节点的交易 submtrancode
				//AppResponse res = storage.doService(nextNode);
				AppResponse res = storage.doEndService(nextNode, bizData);
				// 检查是否拼接流程
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// 流程拼接
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// 如果不拼接流程就将dwtaskvars写入dwtaskvarshis并清空dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// 流程结束，删除最大任务轮次
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// 如果是普通的交易成功提示,则将此消息进行修改
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndBreakSucceeded",
							wfInst.getString("flowname")));
				}
				return res;
			}
		}
		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);
		
		return null;
	}

	/**
	 * 流程拼接,在一个工作流程实例结束后启动另外一个工作流程 unitwfid是拼接流程时的上一个流程实例编号
	 * 
	 * @param wfDefId
	 *            工作流程编号
	 * @param user
	 *            bankid/userid
	 * @param bizData
	 *            需要有实例的参数和unitwfid的值
	 * @throws Exception
	 *             :java.lang.Exception
	 */
	protected void unite(String wfDefId, String user, String[] viewUsers,
			UnikMap bizData, String busiOperateStyle,ArrayList taskList) throws Exception {
		String routetype="0",taskassignstyle="N";
		
		// 流程实例编号
		String wfInstId = generateRandomSeq();

		log(INFO, "[WF_unite NO:1] IN Workflow.unite, wfid=" + wfInstId);

		// 创建流程实例
		storage.createInstance(wfDefId, wfInstId, user,
				CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);

		// 得到开始节点定义
		UnikMap startNode = storage.getNodeByType(wfDefId,
				CommonConst.WF_NODETYPE_START);

		// 新建工作列表
		taskSerial = 1;
		startNode.put("routetype", routetype);
		startNode.put("taskassignstyle", taskassignstyle);
		assignTask(wfDefId, wfInstId, startNode, user, null, viewUsers,taskList);

		log(INFO, "[WF_unite NO:2] OUT Workflow.unite, wfid = " + wfInstId);
	}

	public String getEndNode(String flowid) throws Exception {
		UnikMap node = storage.getNodeByType(flowid,
				CommonConst.WF_NODETYPE_END);

		return node.getString("nodeid");

	}

	/**
	 * 返回下一环节的编号.
	 * 
	 * @param flowid
	 *            流程编号
	 * @param nodeid
	 *            当前环节编号
	 * @param dataParams
	 *            工作流变量
	 * @param isDirectForeNode
	 *            是否直接返回到打回节点 true 返回，false 不返回
	 * @return 下一环节的编号
	 * @throws Exception
	 */
	public UnikMap getnextnodeid(String flowid, String nodeid,
			UnikMap dataParams, boolean isDirectForeNode) throws Exception {
		UnikMap node = storage.getNodeById(flowid, nodeid);
		return getnextnodeid(flowid, "******", nodeid, node, dataParams,
				isDirectForeNode);
	}

	/**
	 * 返回下一节点编号
	 * 
	 * @param flowid
	 *            流程编号
	 * @param wfid
	 *            工作流序号
	 * @param nodeid
	 *            当前节点编号
	 * @param node
	 *            当前节点信息
	 * @param isDirectForeNode
	 *            是否直接返回到打回节点 true 返回，false 不返回
	 * @return String 下一节点编号,如果下一节点不存在则返回null
	 * @throws Exception
	 *             :java.lang.Exception
	 */
	private UnikMap getnextnodeid(String flowid, String wfid, String nodeid,
			UnikMap node, boolean isDirectForeNode) throws Exception {
		// 在完成任务的时候已经更新vars变量了所以此处可以不用再重新去取一次变量
		UnikMap wfVars = storage.getInstVar(wfid, true);
		// 使用para中定义的选择项格式化变量值为对应的名称
		wfVars.putAll(formatVars(wfVars)); // liuxj 20141115

		/*
		 * Iterator it = wfVars.keySet().iterator(); while(it.hasNext()){
		 * 
		 * String key = String.valueOf(it.next()); // 取出参数的key
		 * 
		 * //System.out.println("liuxj(workflow)==============================="+
		 * key+"(key)=" + wfVars.getString(key)); }
		 */
		return getnextnodeid(flowid, wfid, nodeid, node, wfVars,
				isDirectForeNode);
	}

	/**
	 * 返回下一节点的编号,如果下一节点没有找到就返回null
	 * 
	 * @param flowid
	 *            流程编号
	 * @param wfid
	 *            工作流序号
	 * @param nodeid
	 *            当前节点编号
	 * @param node
	 *            当前节点信息
	 * @param var
	 *            路由参数
	 * @return String下一节点编号,如果下一节点不存在则返回null
	 * @throws java.lang.Exception
	 */
	public UnikMap getnextnodeid(String flowid, String wfid, String nodeid,
			UnikMap node, UnikMap var, boolean isDirectForeNode)
			throws Exception {

		UnikMap um = new UnikMap();
		log(DEBUG, "[WF_getnextnodeid NO:-1] flowid=" + flowid + " wfid="
				+ wfid + " nodeid=" + nodeid);

		log(DEBUG,
				"[WF_getnextnodeid NO:0] node="
						+ (node != null ? node.get("nodeid") : "node is null"));
		node = node == null ? storage.getNodeById(flowid, nodeid) : node;

		String forenodeid = "";
		if (isDirectForeNode) {
			forenodeid = storage.getForeNodeId(wfid, nodeid);
			if (forenodeid != null)
				um.put("nextnodeid", forenodeid);
				um.put("taskassignstyle", "N");
				um.put("routetype", "0");
				return um;
		}
		String nextnodemode = node.getString("nextnodemode"); // 下一节点设置方式
		String nextNodeId = node.getString("nextnode");
		log(DEBUG, "[WF_getnextnodeid NO:1] wfid=" + wfid + " nextnodemode="
				+ nextnodemode + " nextNodeId=" + nextNodeId);
		
		//初始化下一节点
		um.put("nextnodeid", nextNodeId);
		um.put("taskassignstyle", "N");
		um.put("routetype", "0");

		if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextnodemode)) {

			Class C = Class
					.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
			WorkflowRoute R = (WorkflowRoute) C.newInstance();
			LinkedList route = storage.getNodeRoute(flowid, nodeid);
			/*
			 * for(int i=0;i<route.size();i++) {
			 * System.out.println("route list==" + route.get(i));
			 * 
			 * } Iterator it = var.keySet().iterator(); while(it.hasNext()) {
			 * System.out.println("var=" + (String)it.next());
			 * 
			 * }
			 */
			// nextNodeId = R.getNext(route, var);
			UnikMap wfVars = new UnikMap();
			wfVars.putAll(formatVars(var)); //格式化参数为para中的文字描述 liuxj 20141211
			UnikMap nextNodeUM = R.getNext(route, wfVars);
			if(nextNodeUM!=null)
			{
				nextNodeId = nextNodeUM.getString("nextNodeId");
				String taskassignstyle = nextNodeUM.getString("taskAssignStyle");
				String routetype = nextNodeUM.getString("routeType");
				
				um.put("nextnodeid", nextNodeId);
				um.put("taskassignstyle", taskassignstyle);
				um.put("routetype", routetype);
			}

		}
		log(DEBUG, "[WF_getnextnodeid NO:2] wfid=" + wfid + " nextNodeId="
				+ nextNodeId);

		return um;
	}

	public DataList getNextNodeManualUsers(String flowid, String nodeid,
			AppUser user, UnikMap dataParams, IParticipant part,
			boolean isDirectForeNode) throws Exception {
		
		DataList dl = null;
		UnikMap wfVars = new UnikMap();
		wfVars.putAll(formatVars(dataParams)); //格式化参数为para中的文字描述 liuxj 20141211
		UnikMap nextNodeUm =  getnextnodeid(flowid, nodeid, wfVars,
				isDirectForeNode);
		String nextNode =nextNodeUm.getString("nextnodeid");
		UnikMap nextNodeInfo = storage.getNodeById(flowid, nextNode);
		if (CommonConst.WF_TASKASSIGN_MANUAL.equals(nextNodeInfo
				.getString("autodisuserflag"))) {
			dl = part.getUserId(flowid, "******", user, nextNodeInfo);
		}
		return dl;
	}

	/**
	 * 根据节点信息自动选择人员进行任务分配,目前是随机分配和最少工作量两种方式
	 * 
	 * @param wfDefId
	 *            工作流编号
	 * @param wfInstId
	 *            流程实例编号
	 * @param node
	 *            节点信息,
	 * @param local
	 *            当前机构信息bankid/userid
	 * @param user
	 *            所选用户-如果是自动分配则user=null
	 * @param routetype
	 *            路由类型：0:正常流转  1:打回
	 * @param taskassignstyle
	 *            任务分配方式 ：常规分配人员 从结点的历史处理人员中分配
	 * @throws java.lang.Exception
	 */
	protected void assignTask(String wfDefId, String wfInstId, UnikMap node,
			String local, String[] user, String[] viewUser,ArrayList taskList) throws Exception {
		String[] users = user;
		String routetype = node.getString("routetype");  //路由类型   正常  退回
		String taskassignstyle = node.getString("taskassignstyle");  //任务分配方式  正常分配   从历史人员中分配

		log(DEBUG, "routetype=" + routetype + ";taskassignstyle=" +taskassignstyle);
		// 不管是自动分配还是手工分配,users里存放的是所有的用户信息,如果为手工分配
		if (users == null) {
			// 根据
			if (taskassignstyle!= null && taskassignstyle.equals("H")) {
				//从流程任务的历史表获取处理人员信息,替换传入的用户信息
				//SELECT bankid,operid FROM DWTASKHIS WHERE WFID='20140917001324810561' AND NODEID='FX102' ORDER BY TASKSER desc
				user = storage.selectHistoryUser(wfInstId, node.getString("nodeid"), "1" , false);
				//查到用户不为空则为历史用户 
				if (user != null){  
					users = user;
				}else{  //如果历史用户不存在或者已注销；重新获取。
					UnikMap m = new UnikMap();
					m.put("org", node.getString("bankid"));
					m.put("post", node.getString("postidset"));
					m.put("postbankid", node.getString("postbankid"));
					m.put("local", local);
					m.put("wfid", wfInstId);
					m.put("nodeid", node.getString("nodeid"));
					m.put("hisflag", node.getString("hisflag"));
					m.put("superbankscope", node.getString("superbankscope"));
					m.put("bindprodid", node.getString("bindprodid"));
					users = storage.selectUsers(m, false);
		
					// 进行自动分配用户信息
					log(DEBUG, "进行自动分配用户信息!");
				}
				
			}
			else
			{
				UnikMap m = new UnikMap();
				m.put("org", node.getString("bankid"));
				m.put("post", node.getString("postidset"));
				m.put("postbankid", node.getString("postbankid"));
				m.put("local", local);
				m.put("wfid", wfInstId);
				m.put("nodeid", node.getString("nodeid"));
				m.put("hisflag", node.getString("hisflag"));
				m.put("superbankscope", node.getString("superbankscope"));
				m.put("bindprodid", node.getString("bindprodid"));
				users = storage.selectUsers(m, false);
	
				// 进行自动分配用户信息
				log(DEBUG, "进行自动分配用户信息!");
			}
		}
		/*
		 * for(int i=0;i<users.length;i++) { System.out.println("assign user=" +
		 * users[i]);
		 * 
		 * }
		 */
		// 检查人员是否符合分配原则,如果users来自于历史列表则就返回
		compareUsers(users, node);

		int num = node.getInt("mindealnum"); // 最少处理人数
		log(DEBUG, "mindealnum:" + num);
		// 分配处理人数 0721
		String assignmindealnumstyle = node.getString("assignmindealnumstyle");

		/*
		 * 之所有这里只判断是否自动分配,如果为手工分配那么users里的值一定和处理人数相同,
		 * 如果为分配所有人员那么users里的所有用户都得分配任务,如果为自动分配并且符合
		 * 条件的用户数和最少处理人数相等的话没必要去判断是随机还是最少工作量,当处理人
		 * 员大于最少处理人数时再去判断处理方式,最终将users修改为符合条件的用户
		 */
		if (CommonConst.WF_TASKASSIGN_AUTO.equals(node
				.getString("autodisuserflag")) && users.length > num) {
			String processmode = node.getString("processmode"); // 处理方式
			if (CommonConst.WF_PROCEMODE_WORKLEAST.equals(processmode)) { // 最少工作量
				users = storage.getTaskLeastUser(users, num);
			} else if (CommonConst.WF_PROCEMODE_RANDOM.equals(processmode)) {
				users = getRandomUser(num, users);
			}
		} else if (CommonConst.WF_TASKASSIGN_MANUAL.equals(node
				.getString("autodisuserflag"))) {
			//自动分配，不需要对users进行处理，以下代码没有意义。
// 取出最少处理人员的随机数
//			if ((assignmindealnumstyle == null || assignmindealnumstyle
//					.equals("2")) && users.length > num) // 2 静态分配
//			{
//				users = getRandomUser(num, users);
//				}

		} else if (CommonConst.WF_TASKASSIGN_WFOUT.equals(node
				.getString("autodisuserflag")) && users.length > num) {
			// users = getRandomUser(num, users);
		}

		// ===============================临时授权开始=====================================
		// 检查是否启用临时授权
		boolean blCheckIsStartTempAuth = storage.checkTempAuthStart(wfDefId);
		log(DEBUG, "===================================开始临时授权检测"
				+ (blCheckIsStartTempAuth ? "启用临时授权" : "关闭临时授权"));
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
						SingleResult sr = storage.getBusiWFMap(wfInstId);
						String custid = sr.getString("custid");

						// 根据客户编号到指定客户表检查是否设定了该客户
						UnikMap umCust = new UnikMap();
						umCust.put("custid", custid);
						umCust.put("tempauthid", tempAuthId);
						boolean blPass = false;
						SingleResult custSr = storage
								.performAction(Service.SELECT,
										"dbAppTempAuthCustList", umCust);
						if (custSr != null) {
							String tempAuthStat = (String) custSr
									.get("TempAuthStat");
							if (tempAuthStat != null
									&& tempAuthStat.equals("2")) {
								blPass = true;
							}
						}

						if (blPass) {
							// 用临时受权用户代替当前用户
							log(DEBUG, "指定客户临时授权，由用户" + recvPerson + "代替"
									+ tmpusers[1] + "进行任务处理");
							users[i] = tmpusers[0] + "/" + recvPerson;
						}
					} else {
						// 用临时受权用户代替当前用户
						log(DEBUG, "全部客户临时授权，由用户" + recvPerson + "代替"
								+ tmpusers[1] + "进行任务处理");
						users[i] = tmpusers[0] + "/" + recvPerson;
					}
				}
			}
		}
		// ===============================临时授权结束=====================================

		// 任务分配,到了这一步users里都是符合条件的,所以可以直接进行分配
		taskSerial = storage.newTask(node, wfInstId, ++taskSerial, users,viewUser, null,taskList);

		// 如果是终审节点，记录相关信息到业务流程对照表
		String curOperUser = local;
		String isPrimaryAuditNode = node.getString("isprimaryauditnode");

		storage.updateBusiWFMap(node, wfInstId, curOperUser, users,
				isPrimaryAuditNode);

		/*
		 * if(isPrimaryAuditNode!=null && isPrimaryAuditNode.equals("1")) {
		 * storage.updateBusiWFMap(node, wfInstId,curOperUser, users); } else {
		 * storage.updateNormalBusiWFMap(node, wfInstId,curOperUser); }
		 */

	}

	/**
	 * 查找看任务分配的人员是否够完成任务
	 * 
	 * @param users
	 *            用户列表
	 * @param node
	 *            节点详细信息
	 * @throws java.lang.Exception
	 *             users为null或没有值抛出WF_NoDealUserLogon,
	 *             在岗位人员小于处理人员WF_MindealnumLTUsers
	 */
	private void compareUsers(String[] users, UnikMap node) throws Exception {
		String flowid = node.getString("flowid");
		if (users == null || users.length == 0) {
			String[] msg = new String[2];
			msg[0] = flowid;
			msg[1] = node.getString("nodename");
			throw new NamedException(formatMessage("WF_NoDealUserLogon", msg));
		}
		// 第一步:先检查在线人数够不够分配任务
		String autodisuserflag = node.getString("autodisuserflag"); // 任务分配策略
		String assignmindealnumstyle = node.getString("assignmindealnumstyle"); // 是否处理人数为手动分配人数

		int assignnum = 0;
		if (CommonConst.WF_TASKASSIGN_ALL.equals(autodisuserflag)) {
			assignnum = users.length; // 如果分配策略是分配所有人员那么分配的人数为所有在岗人员
			// 如果是分配给所有人,则将节点的处理人员更新为当前的在线人数
			storage.updateDealNum(flowid, node.getString("nodeid"), assignnum);
		} else {
			// 如果用户来自于历史列表，则不需要进行检查，只要将所有的任务都完成就可以了
			if (checkHisFlag(node, users))
				return;

			assignnum = node.getInt("mindealnum"); // 手动分配或自动分配,分配人员个数取处理人数

			//
			if (assignmindealnumstyle == null
					|| assignmindealnumstyle.equals("2")) {
				if (users.length < assignnum || assignnum == 0) { // 在岗人员小于处理人员
					String msg[] = new String[2];
					msg[0] = node.getString("nodename");
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage(
							"WF_MindealnumLTUsers", msg));
				}
			} else if (assignmindealnumstyle.equals("1")) {
				if (users.length == 0) { // 在岗人员小于处理人员
					String msg[] = new String[2];
					msg[0] = node.getString("nodename");
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage(
							"WF_MindealnumLTUsers", msg));
				}
			}
		}

		String taskoverpolicy = node.getString("taskoverpolicy"); // 任务完成策略
		// 第二步:找出任务完成人数,如果完成策略是完成百分比和必须所有完成,那么人数肯定小于或等于分配的人数,
		// 如果是任意完成一个那么完成人数为1,所以只需要判断按完成数量
		int compnum = 1;
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) { // 按完成数量
			compnum = node.getInt("assignminnum");
		}

		if (assignnum < compnum) {
			String message[] = new String[2];
			message[0] = node.getString("nodename");
			message[1] = String.valueOf(compnum);
			throw new NamedException(formatMessage("WF_MindealnumLTUsers",
					message));
		}
	}

	/**
	 * 检查处理人员是否从历史列表中取出
	 * 
	 * @param storage
	 *            WorkflowStorageImpl的hisisempty为true
	 * @param request
	 *            request中有hisisempty为true
	 */
	private boolean checkHisFlag(UnikMap node, String[] users) {
		boolean f = false;
		char isempty = storage.hisisempty;
		// 从历史列表中没有查询出处理人员、不从历史列表中查处理人员这两种情况不考虑
		if (isempty == '0') { // 没有在当前引擎中查询处理人员,人员是手工分配
			int minNum = node.getInt("mindealnum");
			if (CommonConst.WF_HISUSER_ON.equals(node.getString("hisflag"))) {
				f = users.length == minNum ? false : true;
			}
		} else if (isempty == '1') { // 从历史列表中查询出处理人员
			f = true;
		}
		return f;
	}

	/**
	 * 取出最少处理人员的随机数
	 * 
	 * @param num
	 *            人员个数
	 * @param user
	 *            处理人员数组
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
	 * 开始节点的任务分配,主要是处理像退回到开始节点这种流程
	 * 
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected void assignTaskAgain(String wfDefId, String wfInstId,
			UnikMap node, String local,ArrayList taskList) throws Exception {
		// 从历史任务表获取用户
		String[] users = storage.selectTaskUsers(wfInstId,
				node.getString("nodeid"), CommonConst.WF_TASKTYPE_DEAL); // 处理任务
		// for(int i=0;i<users.length;i++)
		// {
		// System.out.println("assignTaskAgain=" + users[i]);
		//
		// }

		String[] viewusers = storage.selectTaskUsers(wfInstId,
				node.getString("nodeid"), CommonConst.WF_TASKTYPE_VIEW); // 浏览任务

		// 从节点得到任务分配处理人数
		String numStr = node.getString("mindealnum");
		String assignmindealnumstyle = node.getString("assignmindealnumstyle");

		// 随机获取任务分配的用户
		int num = 1, i;
		Random R = new Random();

		if (assignmindealnumstyle == null || assignmindealnumstyle.equals("2")) {
			if (numStr != null && numStr.length() > 0) {
				num = Integer.parseInt(numStr, 10);
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
			/*
			 * for (i = 0;i<users.length;i++) { usrs[i] = users[i]; }
			 */
		}

		// ============================临时授权管理开始=======================================

		// ===========================临时授权管理结束====================================

		// 创建新任务
		taskSerial = storage.newTask(node, wfInstId, ++taskSerial, usrs,
				viewusers, null,taskList);

		// 如果是终审节点，记录相关信息到业务流程对照表
		String curOperUser = local;
		String isPrimaryAuditNode = node.getString("isprimaryauditnode");

		storage.updateBusiWFMap(node, wfInstId, curOperUser, usrs,
				isPrimaryAuditNode);

	}

	protected UnikMap updateVariables(UnikMap wfDef, UnikMap node,
			UnikMap bizData, UnikMap wfInst) {
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
	 * 随机序号: 时间17+3位随机数
	 * 
	 * @return String 随机数字
	 */
	protected String generateRandomSeq() {
		SimpleDateFormat fmt = new SimpleDateFormat(
				CommonConst.DATE_TYPE_TIMESTAMP_MSEL);
		StringBuffer buf = new StringBuffer(fmt.format(new Date()));
		Random R = new Random();

		for (int i = 0; i < 3; i++) {
			buf.append(R.nextInt(10));
		}

		return buf.toString();
	}

	protected String[] splitCsv(String str) {
		return StringHelper.split(str, ',');
	}

	/**
	 * 返回消息提示,格式化返回的消息可以带参数
	 * 
	 * @throws java.lang.Exception
	 */
	protected String formatMessage(String arg0, Object obj) throws Exception {
		return storage.formatMessage(arg0, obj);
	}

	/**
	 * 检查当前是否是开始节点,如果是开始节点需要更新流程描述信息
	 * 
	 * @param nodetype
	 *            节点类型
	 * @param wfinst
	 *            流程实例信息
	 * @param vars
	 *            最新的参数信息
	 * @throws Exception
	 */
	private void checkIsStartNode(String nodetype, UnikMap wfinst, UnikMap vars)
			throws Exception {
		if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
			String flowid = wfinst.getString("flowid");
			String wfid = wfinst.getString("wfid");
			UnikMap wfmain = storage.getWorkflowDefinition(flowid);
			if (wfid.length() > 0) {
				// 取出流程定义中的流程描述信息进行替换
				String wfdesc = storage.replaceFlowDesc(
						wfmain.getString("flowdesc"), vars);
				UnikMap m = new UnikMap();
				m.put("wfid", wfid);
				m.put("flowdesc", wfdesc);
				storage.performAction(Service.UPDATE, "dwflowinst", m);
			}
		}
	}

	private void log(int i, Object obj) {
		storage.log(i, obj);
	}
}