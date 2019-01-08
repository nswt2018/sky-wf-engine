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
 * Title: ����������
 * </p>
 * <p>
 * Description: �������̵����񣬿������̵���ת
 * </p>
 * 
 * <p>
 * <b>��������ʱ�����Ĺ���</b>
 * </p>
 * <ol>
 * <li>�õ�����ʵ�����,Ҳ��������Ӧ�����wfid,����20λ��17λʱ���3λ��������</li>
 * <li>��������ʵ���������̵�ʵ����Ϣд��dwflowinst����</li>
 * <li>�õ����̵Ŀ�ʼ�ڵ㶨�壬Ҳ�������еĽڵ���Ϣ</li>
 * <li>����ʼ�ڵ�Ĵ����������δ���������б�</li>
 * <li>��������������resume����������δ��ɵĿ�ʼ�ڵ���Ϣ</li>
 * </ol>
 * 
 * <p>
 * <b>���̴���ڵ�����ʱ�����Ĺ���</b>
 * </p>
 * <ol>
 * <li>�õ���ǰ���̵�ʵ����Ϣ</li>
 * <li>��鵱ǰ�����Ƿ��ѷ�����ֻ���ѷ��������̲ſ���������ת</li>
 * <li>�õ���ǰδ�����������Ҫ������δ���������б�dwtask</li>
 * <li>��ʵ����ȡ���������,������и�������ʱʹ��</li>
 * <li>�õ���ǰ�ڵ���Ϣ����Ҫ������dwflownode��</li>
 * <li>�洢��ǰ�ڵ���Ҫ����ı���ֵ������merge�ķ���������dwtaskvars��</li>
 * <li>��ɵ�ǰ�Ĵ������񡢼���������б��������ƶ���dwtaskhis��ʷ����</li>
 * <li>�ж������Ƿ���ת����һ�ڵ㣬��Ҫ����Զ��˴���ģʽ</li>
 * <li>�����Ҫ������һ�ڵ���ת�������һ�ڵ����ת��ʽȡ����һ�ڵ���</li>
 * <li>������һ�ڵ������ж���η������������һ�ڵ��ǽ������ÿ��Ƿ�ƴ������</li>
 * <li>����ʵ�����е��������taskserʼ����dwtask���е����ֵͬ��</li>
 * </ol>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: ������������������Ϣϵͳ���޹�˾
 * </p>
 * 
 * @see WorkflowStorageImpl
 * @author kangsj@jbbis.com.cn
 * @version 1.0.0 , 2008-5-15 ����08:38:00
 */
@Service("Workflow")
public class Workflow extends Logger {

	/**
	 * <p>
	 * ��������������ݿ⹫����
	 * </p>
	 */
	@Resource(name = "WorkflowStorageImpl")
	private IWorkflowStorage storage;

	/**
	 * <p>
	 * �������
	 * </p>
	 */
	protected int taskSerial;

	/**
	 * <p>
	 * ��ǰ�ڵ㶨����Ϣ
	 * </p>
	 */
	private DwFlowNodeTable currentNode = null;

	public Workflow() {
		super();
	}

	/**
	 * <p>
	 * ���̿�ʼ,�����µ����̡������������£�
	 * </p>
	 * <ol>
	 * <li>�½�����,��������ʵ��</li>
	 * <li>�½������б�������д�������б�dwtask</li>
	 * <li>�������������������resume��������</li>
	 * </ol>
	 * 
	 * @param flowid       ���������̱��
	 * @param user         ��ǰ������Աbankid/operid
	 * @param bizData      ����������
	 * @param users        ��һ��������Ա
	 * @param viewUsers    ���������Ա
	 * @param busiWorkflow ҵ�����
	 * @param outTaskNode
	 * @param tranQueue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Transactional
	public String start(String flowid, String user, UnikMap bizData, String[] users, String[] viewUsers, UnikMap busiWorkflow, UnikMap outTaskNode, Queue<UnikMap> tranQueue) throws Exception {

		// ����ʵ�����
		log(INFO, "[WF_IN_start] workflow start, flowid=" + flowid);
		String wfid = generateRandomSeq();
		log(DEBUG, "[WF_start NO:1] create wfid OK, value is " + wfid);

		// ��ȡ�ⲿ�����ҵ�����
		String busiOperateStyle = "";
		if (busiWorkflow != null) {
			busiOperateStyle = CommonConst.WF_BUSIOPERATESTYLE_G;
			log(DEBUG, "[WF_start NO:1.1] generate busiOperateStyle OK.busiOperateStyle=" + busiOperateStyle);
		}

		// ��������ʵ��
		log(DEBUG, "[WF_start NO:2] begin CreateInstance, flowid=" + flowid + ", wfid=" + wfid);
		storage.createInstance(flowid, wfid, user, CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);
		log(DEBUG, "[WF_start NO:3] createInstance OK.");

		// �õ���ʼ�ڵ㶨��
		currentNode = storage.getNodeByType(flowid, CommonConst.WF_NODETYPE_START);

		// �½������б�
		taskSerial = 1;
		ArrayList taskList = new ArrayList();

		// ���������������б�
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfid + ", nodeid=" + currentNode.getNodeid());
		storage.newTask(currentNode, wfid, taskSerial, user.split(","), viewUsers, bizData, taskList);

		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfid + " taskRound=1");
		storage.insertTaskRound(wfid, 1);

		// ��ȡisWriteBusiWFMap������,д����ʵ�����ձ�
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid=" + wfid + "");
		storage.writeBusiWFMap(flowid, wfid, busiWorkflow);

		// ������ת����
		boolean blStart = true;
		boolean blIsDirectForeNode = false;
		resume(wfid, user, bizData, users, viewUsers, blStart, blIsDirectForeNode, outTaskNode, tranQueue);
		log(INFO, "[WF_OUT_start] OUT Workflow.start, flowid = " + flowid + ", wfid = " + wfid);

		return wfid;
	}

	/**
	 * <p>
	 * ����������
	 * </p>
	 * <ol>
	 * <li>��λ��ǰ�ڵ�</li>
	 * <li>����ҵ������</li>
	 * <li>������ǰ����</li>
	 * <li>ת����һ�ڵ�</li>
	 * <li>��������</li>
	 * </ol>
	 * 
	 * @param wfid             ����ʵ�����
	 * @param user             ��ǰ�����µĲ���Ա,ֵΪ��������/�û�����
	 * @param bizData          �ϴ������еĴ������,·�ɵ��жϵ����ݴӴ���ȡ��
	 * @param users            ���ϴ�������ȡusers,lsc.getProperties("users"),�ֹ�����Ӧ������usersֵ
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
		ArrayList taskList = new ArrayList(); // ���������˵�������Ϣ�б�
		if (outTaskNode == null)
			outTaskNode = new UnikMap();
		// �õ�����ʵ��
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfid);
		DwFlowInstTable dwFlowInstVo = storage.getWorkflowInstance(wfid);

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid=" + wfid);
		String flowid = dwFlowInstVo.getFlowid();
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId=" + flowid);
		// ��鵱ǰ�����Ƿ񷢲�
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + flowid);
		storage.checkFlowState(flowid);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// �õ���ǰδ���������
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		DwTaskTable dwTaskVo = storage.getTask(wfid, user);
		String nodeid = dwTaskVo.getNodeid();
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeid);

		// ���ù��������
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(dwFlowInstVo.getTaskser() + "", 10);
		}

		// �õ���ǰ�ڵ㶨��,��������ڿ�˵���ǿ�ʼ�ڵ����ԾͲ����ٲ�ѯһ�νڵ���Ϣ��
		boolean ischeckNode = true;
		if (!blStart) {
			currentNode = storage.getNodeById(flowid, nodeid);
		} else {
			ischeckNode = false;
		}

		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid=" + wfid);
		storage.updateWorkflowVariables(wfid, bizData, taskSerial, flowid, nodeid, currentNode.getNodedesc());
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid=" + wfid);

		// ��ɵ�ǰ�Ĺ������� ���ѵ�ǰ���������ʷ�����ɾ����ǰ�����
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfid);
		storage.completeTask(wfid, dwTaskVo, bizData, currentNode);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfid);

		if (ischeckNode) {
			log(DEBUG, "[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getNodetype(), dwFlowInstVo, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		// ���ö�������Ľڵ�
		outTaskNode.put("currentNode", currentNode); // �����ǰ�ڵ�
		outTaskNode.put("wfid", wfid);
		outTaskNode.put("taskSer", taskList);

		// �������false��˵�������û�û�д�����Ҫ���һ���ڵ���˲���
		if (!storage.isContinue(currentNode, dwTaskVo, blStart)) {
			log(DEBUG, "[WF_IN_Resume] this task has many user to dispose, so need return");
			outTaskNode.put("isLastDealer", "false");
			return;// null;
		}
		outTaskNode.put("isLastDealer", "true");

		// ȡ��һ�ڵ��� �Ȼ�ȡ��������һ����㣬�ж���һ�ڵ����÷�ʽ�������������ʽ����ȡ�����е���һ�����
		UnikMap nextNodeUm = getNextNodeId(flowid, wfid, nodeid, currentNode, isDirectForeNode);
		String nextNodeId = nextNodeUm.getString("nextnodeid");
		routetype = nextNodeUm.getString("routetype");
		taskassignstyle = nextNodeUm.getString("taskassignstyle");
		// �����һ���ڵ�Ϊ��
		Errors.Assert(nextNodeId != null && !nextNodeId.equals(""), "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.getNodetype()))
			return;// null;

		String[] nextIds = splitCsv(nextNodeId);
		DwFlowNodeTable[] outNextNode = new DwFlowNodeTable[nextIds.length]; // ��¼����������еĽ��
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// ȡ����һ�ڵ���׼�������������
			DwFlowNodeTable nextNode = storage.getNodeById(flowid, nextIds[i]);
			// nextNode.put("wfid", wfid);
			// nextNode.put("routetype", routetype);//ȱʡֵ
			// nextNode.put("taskassignstyle", taskassignstyle);//ȱʡֵ
			outNextNode[i] = nextNode;
			// д��"��һ���ڱ��"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			String nodetype = nextNode.getNodetype();
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid=" + nextNodeId + ", nodetype=" + nodetype);
			// �����һ���ڵ��ǿ�ʼ�ڵ㣬��Ҫ���б�Ҫ��ҵ�����ݴ���һ��Ϊ��־����
			if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
				// �����һ�ڵ��ǿ�ʼ�ڵ㣬�����˻ص�ʱ����Ҫִ�����̶���Ļ������
				// storage.doWithDraw(wfDefId, wfInstId); //liuxj
				UnikMap wfVars = storage.getInstVar(wfid, false);
				// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
				wfVars.putAll(formatVars(wfVars)); // ��ʽ������Ϊpara�е��������� liuxj 20141115
				String routetype1 = "";
				if (routetype1 != null && routetype.equals("1")) { // �˻�·��
					// �˻ؽ��������
					UnikMap tranUm = new UnikMap();
					tranUm.put("trancode", currentNode);
					tranUm.put("trantype", "retutrans");
					tranUm.put("vars", wfVars);
					tranQueue.add(tranUm);
				}
				log(DEBUG, "[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// ����ǿ�ʼ���ھ�д����һ���ڱ��
				// nextNode.put("forenodeid", currentNode.getNodeid());
				// System.out.println("�����һ���ڵ��ǿ�ʼ�ڵ�(user)=" + user);
				assignTaskAgain(flowid, wfid, nextNode, user, taskList);
				// �˻أ������ִ�
				int maxTaskRound = storage.getTaskMaxTaskRound(wfid);
				storage.updateTaskRound(wfid, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// �������,�ַ�
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
			// �ϲ� ͨ��·�ɷ�ʽ���нڵ���ת
			else if (CommonConst.WF_NODETYPE_UNITE.equals(nextNode.getNodetype())) {
				String next = nextNode.getNodeid();
				if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextNode.getNextnodemode())) {
					Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
					WorkflowRoute R = (WorkflowRoute) C.newInstance();
					UnikMap wfVars = storage.getInstVar(wfid, false);
					// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
					wfVars.putAll(formatVars(wfVars)); // ��ʽ������Ϊpara�е��������� liuxj 20141115
					LinkedList route = storage.getNodeRoute(flowid, next);
					UnikMap nextNodeUM = R.getNext(route, wfVars);
					nextNodeId = nextNodeUM.getString("nextNodeId");
					taskassignstyle = nextNodeUM.getString("taskAssignStyle");
					routetype = nextNodeUM.getString("routeType");
					// �˻�
					if (nextNodeId == null || nextNodeId.length() == 0)
						continue;
					else {
						nextNode = storage.getNodeById(flowid, nextNodeId);
						// ͨ��·�ɵ�������������䷽ʽ�����жϻ�ȡ�û��ķ�ʽ�����Ǵ�����û���Ϣ
						UnikMap vars = new UnikMap();
						vars.put("taskassignstyle", taskassignstyle);
						vars.put("routetype", routetype);
						assignTask(flowid, wfid, nextNode, user, users, viewUsers, taskList, vars);
						String routetype1 = "";// nextNode.getString("routetype");
						if (routetype1 != null && routetype.equals("1")) // �˻�·��
						{
							// �˻ؽ��������
							UnikMap tranUm = new UnikMap();
							tranUm.put("trancode", currentNode);
							tranUm.put("trantype", "retutrans");
							tranUm.put("vars", wfVars);
							tranQueue.add(tranUm);
						}
						break;
					}
				}
			} else if (CommonConst.WF_NODETYPE_SUBWORKFLOW.equals(nextNode.getNodetype())) { // ������

			}
			// �����ڵ�
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode.getNodetype())) {
				// ���Ĺ�����״̬
				storage.updateWorkflowStatus(wfid, 2);
				// ���ʵ��
				storage.completeInstance(wfid);
				// ��������ҵ����ձ�Ϊ���״̬
				storage.completeBusiWFMap(wfid, "2");
				// ���ý����ڵ�Ľ��� submtrancode
				// =============�������������==============
				// ��ȡ���̱���
				UnikMap wfVars = storage.getInstVar(wfid, false); // liuxj
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", nextNode); // �ڵ���Ϣ
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "endtrans");
				tranQueue.add(tranUm);
				// AppResponse res = null;
				// AppResponse res = storage.doService(nextNode); //liuxj
				// ����Ƿ�ƴ������
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode.getIsunit())) {
					bizData.put("unitwfid", wfid);
					wfVars = storage.getInstVar(wfid, false); // liuxj // 20141115
					// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
					bizData.putAll(formatVars(wfVars)); // liuxj 20141115
					// ����ƴ��
					// ???unite(nextNode.getUnitflowid(), user, viewUsers,bizData,
					// busiOperateStyle,taskList);
					// res = null;
				} else {
					// �����ƴ�����̾ͽ�dwtaskvarsд��dwtaskvarshis�����dwtaskvars
					storage.delDwtaskVars(wfid);
					// ���̽�����ɾ����������ִ�
					storage.deleteTaskRound(wfid);
				}
				// ���������Ϣ
				if (outTaskNode != null) {
					outTaskNode.put("currentNode", currentNode);
					outTaskNode.put("nextNode", outNextNode);
					outTaskNode.put("wfid", wfid);
					outTaskNode.put("taskSer", taskList);
				}
				return;// res;
			}
			// �м�ڵ�����������
			assignTask(flowid, wfid, nextNode, user, users, viewUsers, taskList, new UnikMap());

			// ���·�ɣ����õ�ǰ�ڵ㣨������ؽ��ף��뻷��������������
			String routetype1 = "";// nextNode.getString("routetype");
			if (routetype1 != null && routetype1.equals("1")) {
				// �˻ؽ��������
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", currentNode);
				// ��ȡ���̱���
				UnikMap wfVars = storage.getInstVar(wfid, false); // liuxj
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "retutrans");
				tranQueue.add(tranUm);
			}
		}
		// 20130710����������̶ȣ���������û��dwflowinst.instancyLevel�򲻸���dwflowinst�еĸ��ֶ�
		// �����������
		String instancylevel = bizData.getString("dwflowinst.instancylevel");
		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid=" + wfid + ", taskser=" + taskSerial + ", instancyLevel=" + instancylevel);

		storage.updateTaskSerial(wfid, taskSerial, instancylevel);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfid);

		// ���������Ϣ
		if (outTaskNode != null) {
			outTaskNode.put("currentNode", currentNode);
			outTaskNode.put("nextNode", outNextNode);
			outTaskNode.put("wfid", wfid);
			outTaskNode.put("taskSer", taskList);
		}
		return;// null;
	}

	protected void startSubWF(String wfInstId, String wfDefId, String user, UnikMap bizData, String[] users, String[] viewUsers, UnikMap outTaskNode) throws Exception {
		// ����ʵ�����
		log(DEBUG, "[sub workflow NO:1] sub workflow start, value is " + wfInstId);
		// �õ���ʼ�ڵ㶨��
		currentNode = storage.getNodeByType(wfDefId, CommonConst.WF_NODETYPE_START);

		// �½������б�
		// taskSerial = 1;
		// ���������������б�
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfInstId + ", nodeid=" + currentNode.getNodeid());
		storage.newTask(currentNode, wfInstId, taskSerial, user.split(","), viewUsers, bizData, null);
		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfInstId + " taskRound=1");
		storage.insertTaskRound(wfInstId, 1);
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid=" + wfInstId + "");

		return;// null;
	}

	/**
	 * <p>
	 * ʹ��para�е�ѡ���ʽ������ֵΪ��������
	 * </p>
	 * 
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private UnikMap formatVars(UnikMap vars) throws Exception {
		UnikMap um = new UnikMap();
		// ������̱�����
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
	 * ������һ���ڵı��
	 * </p>
	 * 
	 * @param flowid           ���̱��
	 * @param nodeid           ��ǰ���ڱ��
	 * @param dataParams       ����������
	 * @param isDirectForeNode �Ƿ�ֱ�ӷ��ص���ؽڵ� true ���أ�false ������
	 * @return ��һ���ڵı��
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
	 * ������һ�ڵ���
	 * </p>
	 * 
	 * @param flowid           ���̱��
	 * @param wfid             ���������
	 * @param nodeid           ��ǰ�ڵ���
	 * @param currentNode2     ��ǰ�ڵ���Ϣ
	 * @param isDirectForeNode �Ƿ�ֱ�ӷ��ص���ؽڵ� true ���أ�false ������
	 * @return String ��һ�ڵ���,�����һ�ڵ㲻�����򷵻�null
	 * @throws Exception
	 */
	private UnikMap getNextNodeId(String flowid, String wfid, String nodeid, DwFlowNodeTable currentNode, boolean isDirectForeNode) throws Exception {
		// ����������ʱ���Ѿ�����vars���������Դ˴����Բ���������ȥȡһ�α���
		UnikMap wfVars = storage.getInstVar(wfid, true);
		// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
		wfVars.putAll(formatVars(wfVars)); // liuxj 20141115

		return getNextNodeId(flowid, wfid, nodeid, currentNode, wfVars, isDirectForeNode);
	}

	/**
	 * <p>
	 * ������һ�ڵ�ı��,�����һ�ڵ�û���ҵ��ͷ���null
	 * </p>
	 * 
	 * @param flowid      ���̱��
	 * @param wfid        ���������
	 * @param nodeid      ��ǰ�ڵ���
	 * @param currentNode ��ǰ�ڵ���Ϣ
	 * @param var         ·�ɲ���
	 * @return String��һ�ڵ���,�����һ�ڵ㲻�����򷵻�null
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
		String nextnodemode = currentNode.getNextnodemode(); // ��һ�ڵ����÷�ʽ
		String nextNodeId = currentNode.getNextnode();
		log(DEBUG, "[WF_getnextnodeid NO:1] wfid=" + wfid + " nextnodemode=" + nextnodemode + " nextNodeId=" + nextNodeId);

		// ��ʼ����һ�ڵ�
		um.put("nextnodeid", nextNodeId);
		um.put("taskassignstyle", "N");
		um.put("routetype", "0");

		if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextnodemode)) {

			Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
			WorkflowRoute R = (WorkflowRoute) C.newInstance();
			LinkedList route = storage.getNodeRoute(flowid, nodeid);

			UnikMap wfVars = new UnikMap();
			wfVars.putAll(formatVars(var)); // ��ʽ������Ϊpara�е��������� liuxj 20141211
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
	 * wfVars.putAll(formatVars(dataParams)); //��ʽ������Ϊpara�е��������� liuxj 20141211
	 * UnikMap nextNodeUm = getNextNodeID(flowid, nodeid, wfVars, isDirectForeNode);
	 * String nextNode =nextNodeUm.getString("nextnodeid"); UnikMap nextNodeInfo =
	 * storage.getNodeById(flowid, nextNode); if
	 * (CommonConst.WF_TASKASSIGN_MANUAL.equals(nextNodeInfo
	 * .getString("autodisuserflag"))) { dl = part.getUserId(flowid, "******", user,
	 * nextNodeInfo); } return dl; }
	 */

	/**
	 * <p>
	 * ���ݽڵ���Ϣ�Զ�ѡ����Ա�����������,Ŀǰ�������������ٹ��������ַ�ʽ
	 * </p>
	 * 
	 * @param wfDefId         ���������
	 * @param wfInstId        ����ʵ�����
	 * @param dwFlowNode      �ڵ���Ϣ,
	 * @param local           ��ǰ������Ϣbankid/userid
	 * @param user            ��ѡ�û�-������Զ�������user=null
	 * @param vars
	 * @param routetype       ·�����ͣ�0:������ת 1:���
	 * @param taskassignstyle ������䷽ʽ �����������Ա �ӽ�����ʷ������Ա�з���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void assignTask(String wfDefId, String wfInstId, DwFlowNodeTable dwFlowNode, String local, String[] user, String[] viewUser, ArrayList taskList, UnikMap vars) throws Exception {
		String[] users = user;
		String routetype = vars.getString("routetype"); // ·������ ���� �˻�
		String taskassignstyle = vars.getString("taskassignstyle"); // ������䷽ʽ �������� ����ʷ��Ա�з���

		log(DEBUG, "routetype=" + routetype + ";taskassignstyle=" + taskassignstyle);
		// �������Զ����仹���ֹ�����,users���ŵ������е��û���Ϣ,���Ϊ�ֹ�����
		if (users == null) {
			// ����
			if (taskassignstyle != null && taskassignstyle.equals("H")) {
				// �������������ʷ���ȡ������Ա��Ϣ,�滻������û���Ϣ
				// SELECT bankid,operid FROM DWTASKHIS WHERE WFID='20140917001324810561' AND
				// NODEID='FX102' ORDER BY TASKSER desc
				user = storage.selectHistoryUser(wfInstId, dwFlowNode.getNodeid(), "1", false);
				// �鵽�û���Ϊ����Ϊ��ʷ�û�
				if (user != null) {
					users = user;
				} else { // �����ʷ�û������ڻ�����ע�������»�ȡ��
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

					// �����Զ������û���Ϣ
					log(DEBUG, "�����Զ������û���Ϣ!");
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

				// �����Զ������û���Ϣ
				log(DEBUG, "�����Զ������û���Ϣ!");
			}
		}

		// �����Ա�Ƿ���Ϸ���ԭ��,���users��������ʷ�б���ͷ���
		compareUsers(users, dwFlowNode);

		int num = Integer.parseInt(dwFlowNode.getMindealnum() + ""); // ���ٴ�������
		log(DEBUG, "mindealnum:" + num);
		/*
		 * ֮��������ֻ�ж��Ƿ��Զ�����,���Ϊ�ֹ�������ôusers���ֵһ���ʹ���������ͬ,
		 * ���Ϊ����������Ա��ôusers��������û����÷�������,���Ϊ�Զ����䲢�ҷ���
		 * �������û��������ٴ���������ȵĻ�û��Ҫȥ�ж�������������ٹ�����,�������� Ա�������ٴ�������ʱ��ȥ�жϴ���ʽ,���ս�users�޸�Ϊ�����������û�
		 */
		if (CommonConst.WF_TASKASSIGN_AUTO.equals(dwFlowNode.getAutodisuserflag()) && users.length > num) {
			String processmode = dwFlowNode.getProcessmode();// ����ʽ
			if (CommonConst.WF_PROCEMODE_WORKLEAST.equals(processmode)) { // ���ٹ�����
				users = storage.getTaskLeastUser(users, num);
			} else if (CommonConst.WF_PROCEMODE_RANDOM.equals(processmode)) {
				users = getRandomUser(num, users);
			}
		} else if (CommonConst.WF_TASKASSIGN_MANUAL.equals(dwFlowNode.getAutodisuserflag())) {
			// �Զ����䣬����Ҫ��users���д������´���û�����塣
// ȡ�����ٴ�����Ա�������
//			if ((assignmindealnumstyle == null || assignmindealnumstyle
//					.equals("2")) && users.length > num) // 2 ��̬����
//			{
//				users = getRandomUser(num, users);
//				}

		} else if (CommonConst.WF_TASKASSIGN_WFOUT.equals(dwFlowNode.getAutodisuserflag()) && users.length > num) {
			// users = getRandomUser(num, users);
		}

		// ===============================��ʱ��Ȩ��ʼ=====================================
		// ����Ƿ�������ʱ��Ȩ
		boolean blCheckIsStartTempAuth = storage.checkTempAuthStart(wfDefId);
		log(DEBUG, "===================================��ʼ��ʱ��Ȩ���" + (blCheckIsStartTempAuth ? "������ʱ��Ȩ" : "�ر���ʱ��Ȩ"));
		if (blCheckIsStartTempAuth) {
			// �������̱��flowid��ȡҵ��Χ wfDefId
			SingleResult rangeSr = storage.getBusiRange(wfDefId);
			Errors.Assert(rangeSr != null, "workflow/BusinessScopeNotExist");
			String busiRange = rangeSr.getString("busiscope");

			// ����ʱ��Ȩ���ȡ��ʱ��Ȩ�û� ҵ��Χ������ҵ��Χ������ӳ����е�ҵ�����ఴ��˳���1,2,3.����
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

					if (custScope != null && custScope.equals("2")) // ָ���ͻ�
					{
						// ��������ʵ�����wfInstId������ҵ����ձ��л�ȡ�ͻ���Ϣ
						DbBusiWfMapTable dbBusiWfMapVo = storage.getBusiWFMap(wfInstId);
						String custid = dbBusiWfMapVo.getCustid();

						// ���ݿͻ���ŵ�ָ���ͻ������Ƿ��趨�˸ÿͻ�
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
							// ����ʱ��Ȩ�û����浱ǰ�û�
							log(DEBUG, "ָ���ͻ���ʱ��Ȩ�����û�" + recvPerson + "����" + tmpusers[1] + "����������");
							users[i] = tmpusers[0] + "/" + recvPerson;
						}
					} else {
						// ����ʱ��Ȩ�û����浱ǰ�û�
						log(DEBUG, "ȫ���ͻ���ʱ��Ȩ�����û�" + recvPerson + "����" + tmpusers[1] + "����������");
						users[i] = tmpusers[0] + "/" + recvPerson;
					}
				}
			}
		}
		// ===============================��ʱ��Ȩ����=====================================

		// �������,������һ��users�ﶼ�Ƿ���������,���Կ���ֱ�ӽ��з���
		taskSerial = storage.newTask(dwFlowNode, wfInstId, ++taskSerial, users, viewUser, null, taskList);

		// ���������ڵ㣬��¼�����Ϣ��ҵ�����̶��ձ�
		String curOperUser = local;
		String isPrimaryAuditNode = dwFlowNode.getIsprimaryauditnode();
		storage.updateBusiWFMap(dwFlowNode, wfInstId, curOperUser, users, isPrimaryAuditNode);
	}

	/**
	 * <p>
	 * ���ҿ�����������Ա�Ƿ��������
	 * </p>
	 * 
	 * @param users      �û��б�
	 * @param dwFlowNode �ڵ���ϸ��Ϣ
	 * @throws Exception usersΪnull��û��ֵ�׳�WF_NoDealUserLogon,
	 *                   �ڸ�λ��ԱС�ڴ�����ԱWF_MindealnumLTUsers
	 */
	private void compareUsers(String[] users, DwFlowNodeTable dwFlowNode) throws Exception {
		String flowid = dwFlowNode.getFlowid();
		if (users == null || users.length == 0) {
			String[] msg = new String[2];
			msg[0] = flowid;
			msg[1] = dwFlowNode.getNodename();
			throw new NamedException(formatMessage("WF_NoDealUserLogon", msg));
		}
		// ��һ��:�ȼ������������������������
		String autodisuserflag = dwFlowNode.getAutodisuserflag(); // ����������
		String assignmindealnumstyle = dwFlowNode.getAssignmindealnumstyle(); // �Ƿ�������Ϊ�ֶ���������

		Long assignnum = 0L;
		if (CommonConst.WF_TASKASSIGN_ALL.equals(autodisuserflag)) {
			assignnum = users.length + 0L; // �����������Ƿ���������Ա��ô���������Ϊ�����ڸ���Ա
			// ����Ƿ����������,�򽫽ڵ�Ĵ�����Ա����Ϊ��ǰ����������
			storage.updateDealNum(flowid, dwFlowNode.getNodeid(), assignnum + 0);
		} else {
			// ����û���������ʷ�б�����Ҫ���м�飬ֻҪ�����е�������ɾͿ�����
			if (checkHisFlag(dwFlowNode, users))
				return;

			assignnum = dwFlowNode.getMindealnum(); // �ֶ�������Զ�����,������Ա����ȡ��������

			//
			if (assignmindealnumstyle == null || assignmindealnumstyle.equals("2")) {
				if (users.length < assignnum || assignnum == 0) { // �ڸ���ԱС�ڴ�����Ա
					String msg[] = new String[2];
					msg[0] = dwFlowNode.getNodename();
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage("WF_MindealnumLTUsers", msg));
				}
			} else if (assignmindealnumstyle.equals("1")) {
				if (users.length == 0) { // �ڸ���ԱС�ڴ�����Ա
					String msg[] = new String[2];
					msg[0] = dwFlowNode.getNodename();
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage("WF_MindealnumLTUsers", msg));
				}
			}
		}

		String taskoverpolicy = dwFlowNode.getTaskoverpolicy();// ������ɲ���
		// �ڶ���:�ҳ������������,�����ɲ�������ɰٷֱȺͱ����������,��ô�����϶�С�ڻ���ڷ��������,
		// ������������һ����ô�������Ϊ1,����ֻ��Ҫ�жϰ��������
		Long compnum = 1L;
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) { // ���������
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
	 * ��鴦����Ա�Ƿ����ʷ�б���ȡ��
	 * </p>
	 * 
	 * @param storage WorkflowStorageImpl��hisisemptyΪtrue
	 * @param request request����hisisemptyΪtrue
	 */
	private boolean checkHisFlag(DwFlowNodeTable dwFlowNode, String[] users) {
		boolean f = false;
		char isempty = '0'; // storage.hisisempty;
		// ����ʷ�б���û�в�ѯ��������Ա��������ʷ�б��в鴦����Ա���������������
		if (isempty == '0') { // û���ڵ�ǰ�����в�ѯ������Ա,��Ա���ֹ�����
			Long minNum = dwFlowNode.getMindealnum();
			if (CommonConst.WF_HISUSER_ON.equals(dwFlowNode.getHisflag())) {
				f = users.length == minNum ? false : true;
			}
		} else if (isempty == '1') { // ����ʷ�б��в�ѯ��������Ա
			f = true;
		}
		return f;
	}

	/**
	 * <p>
	 * ȡ�����ٴ�����Ա�������
	 * </p>
	 * 
	 * @param num  ��Ա����
	 * @param user ������Ա����
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	private String[] getRandomUser(int num, String[] user) throws Exception {
		// ȡ�����ٴ�����Ա�������
		int[] t = WorkFlowFunc.getRandom(num, user.length);
		String[] u = new String[num];
		for (int k = 0; k < num; k++) {
			u[k] = user[t[k]];
		}
		return u;
	}

	/**
	 * <p>
	 * ��ʼ�ڵ���������,��Ҫ�Ǵ������˻ص���ʼ�ڵ���������
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void assignTaskAgain(String flowid, String wfid, DwFlowNodeTable nextNode, String local, ArrayList taskList) throws Exception {
		// ����ʷ������ȡ�û�
		String nodeid = nextNode.getNodeid();
		String[] users = storage.selectTaskUsers(wfid, nodeid, CommonConst.WF_TASKTYPE_DEAL); // ��������
		String[] viewusers = storage.selectTaskUsers(wfid, nodeid, CommonConst.WF_TASKTYPE_VIEW); // �������

		// �ӽڵ�õ�������䴦������
		Long mindealnum = nextNode.getMindealnum();
		String assignmindealnumstyle = nextNode.getAssignmindealnumstyle();

		// �����ȡ���������û�
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

		// ����������
		taskSerial = storage.newTask(nextNode, wfid, ++taskSerial, usrs, viewusers, null, taskList);

		// ���������ڵ㣬��¼�����Ϣ��ҵ�����̶��ձ�
		String curOperUser = local;
		String isPrimaryAuditNode = nextNode.getIsprimaryauditnode();

		storage.updateBusiWFMap(nextNode, wfid, curOperUser, usrs, isPrimaryAuditNode);
	}

	protected UnikMap updateVariables(UnikMap wfDef, UnikMap node, UnikMap bizData, UnikMap wfInst) {
		// ��ȡ node �ڵ�Ҫ�����ҵ��������
		String KWN = "busikeyword";
		String keySetStr = node.getString(KWN + "set");

		if (keySetStr == null) {
			return null;
		}

		String[] keySetArr = splitCsv(keySetStr);
		int c = keySetArr.length;

		// ��ȡҵ�������������ͨ���ֶε�����ӳ��
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

		// ����ҵ������
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
	 * ������: ʱ��17+3λ�����
	 * </p>
	 * 
	 * @return String �������
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
	 * ������Ϣ��ʾ,��ʽ�����ص���Ϣ���Դ�����
	 * </p>
	 * 
	 * @throws Exception
	 */
	protected String formatMessage(String arg0, Object obj) throws Exception {
		return storage.formatMessage(arg0, obj);
	}

	/**
	 * <p>
	 * ��鵱ǰ�Ƿ��ǿ�ʼ�ڵ�,����ǿ�ʼ�ڵ���Ҫ��������������Ϣ
	 * </p>
	 * 
	 * @param nodetype     �ڵ�����
	 * @param dwFlowInstVo ����ʵ����Ϣ
	 * @param bizData      ���µĲ�����Ϣ
	 * @throws Exception
	 */
	private void checkIsStartNode(String nodetype, DwFlowInstTable dwFlowInstVo, UnikMap bizData) throws Exception {
		if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
			String flowid = dwFlowInstVo.getFlowid();
			String wfid = dwFlowInstVo.getWfid();
			DwFlowMainTable dwFlowMainVo = storage.getWorkflowDefinition(flowid);
			if (wfid.length() > 0) {
				// ȡ�����̶����е�����������Ϣ�����滻
				String wfdesc = storage.replaceFlowDesc(dwFlowMainVo.getFlowdesc(), bizData);
				DwFlowInstTable dwFlowInstVoNw = new DwFlowInstTable();
				dwFlowInstVoNw.setWfid(wfid);
				dwFlowInstVoNw.setFlowdesc(wfdesc);
				storage.saveDwFlowInstTable(dwFlowInstVoNw);
			}
		}
	}
}