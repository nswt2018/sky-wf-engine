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
 * Title: ����������
 * </p>
 * <p>
 * Description: �������̵����񣬿������̵���ת
 * </p>
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
public class Workflow implements SupperLog {
	/**
	 * ��������������ݿ⹫����
	 */
	protected WorkflowStorageImpl storage;
	/**
	 * �������
	 */
	protected int taskSerial;

	/**
	 * ��ǰ�ڵ㶨����Ϣ
	 */
	private UnikMap currentNode = null;

	public Workflow(WorkflowStorageImpl wfStorage) {
		this.storage = wfStorage;
	}

	/**
	 * �˹��췽��Ϊ�˼���Ӧ�ý��ף��Ժ���ʹ����
	 * 
	 * @param s
	 */
	public Workflow(WorkflowStorage wfStorage, Service s) {
		this((WorkflowStorageImpl) wfStorage);
	}

	/**
	 * ���̿�ʼ,�����µ����̡������������£�
	 * <ol>
	 * <li>�½�����,��������ʵ��</li>
	 * <li>�½������б�������д�������б�dwtask</li>
	 * <li>�������������������resume��������</li>
	 * </ol>
	 * 
	 * @param wfDefId
	 *            ���������̱��(flowid)
	 * @param user
	 *            ��ǰ������Աbankid/operid
	 * @param bizData
	 *            ����������
	 * @throws java.lang.Exception
	 */
	protected AppResponse start(String wfDefId, String user, UnikMap bizData,
			String[] users, String[] viewUsers, UnikMap umWfid,
			UnikMap umWorkflow, UnikMap outTaskNode,Queue<UnikMap> tranQueue) throws Exception {
		// ����ʵ�����
		log(INFO, "[WF_IN_start] workflow start, flowid=" + wfDefId);
		String wfInstId = generateRandomSeq();
		log(DEBUG, "[WF_start NO:1] create wfid OK, value is " + wfInstId);
		
		ArrayList taskList = new ArrayList();
		// ��ȡ�ⲿ�����ҵ�����
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
		// ��������ʵ��
		log(DEBUG, "[WF_start NO:2] begin CreateInstance, flowid=" + wfDefId
				+ ", wfid=" + wfInstId);
		storage.createInstance(wfDefId, wfInstId, user,
				CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);
		log(DEBUG, "[WF_start NO:3] createInstance OK.");

		// �õ���ʼ�ڵ㶨��
		currentNode = storage.getNodeByType(wfDefId,CommonConst.WF_NODETYPE_START);

		// �½������б�
		taskSerial = 1;

		// ���������������б�
		log(DEBUG, "[WF_start NO:4] begin to newTask, wfid=" + wfInstId
				+ ", nodeid=" + currentNode.getString("nodeid"));
		storage.newTask(currentNode, wfInstId, taskSerial, user.split(","),
				viewUsers, bizData,taskList);
		log(DEBUG, "[WF_start NO:4.1] begin to taskRound, wfid=" + wfInstId
				+ " taskRound=1");
		storage.insertTaskRound(wfInstId, 1);
		log(DEBUG, "[WF_start NO:4.2] begin to WriteBusiWFMap, wfid="
				+ wfInstId + "");
		// ��ȡisWriteBusiWFMap������
		UnikMap umFlowMain = storage.getWorkflowDefinition(wfDefId);
		String isWriteBusiWFMap = umFlowMain.getString("iswritebusiwfmap");

		System.out.println("isWriteBusiWFMap==================="
				+ isWriteBusiWFMap);
		if (busiWorkflow!=null && isWriteBusiWFMap != null && isWriteBusiWFMap.equals("1")) {

			UnikMap umBusiWFMap = new UnikMap();
			// ���մ����ҵ����ϢLoanId��TranSeq��CustId��CustName��ProdId��ProdName��BusiType
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
			// ��ȡ���
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

			// ��֯���̲���FlowId��WFId��NodeName��CurOperId��CurOperIdName��CurBankId��CurBankName��ReceTime

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
			// д��
			boolean blSuccess = false;
			boolean blExist = storage.checkBusiWFMapIsExist(wfInstId);
			if (blExist)
				blSuccess = storage.updateReturnBusiWFMap(umBusiWFMap);
			else
				blSuccess = storage.insertBusiWFMap(umBusiWFMap);

			if (blSuccess) {
				log(DEBUG, "[WF_start NO:4.3] д����ձ�ɹ���������");
				System.out.println("д����ձ�ɹ���������");
			} else {
				log(DEBUG, "[WF_start NO:4.3] д����ձ�ʧ�ܣ�������");
				System.out.println("д����ձ�ʧ�ܣ�������");
			}
		}
		boolean blStart = true;
		boolean blIsDirectForeNode = false;
		// ����

		AppResponse res = resume(wfInstId, user, bizData, users, viewUsers,
				blStart, blIsDirectForeNode, outTaskNode,tranQueue);

		log(INFO, "[WF_OUT_start] OUT Workflow.start, flowid = " + wfDefId);

		// ���������ʵ�����
		log(INFO, "[WF_OUT_start] OUT Workflow.start,Ϊ�ⲿ���������ʵ�� wfInstId = "
				+ wfInstId);
		umWfid.put("wfInstId", wfInstId);
		return res;
	}

	protected AppResponse startSubWF(String wfInstId, String wfDefId,
			String user, UnikMap bizData, String[] users, String[] viewUsers,
			UnikMap outTaskNode) throws Exception {
		// ����ʵ�����
		log(DEBUG, "[sub workflow NO:1] sub workflow start, value is " + wfInstId);
		// �õ���ʼ�ڵ㶨��
		currentNode = storage.getNodeByType(wfDefId,CommonConst.WF_NODETYPE_START);

		// �½������б�
		// taskSerial = 1;
		// ���������������б�
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
	 * ʹ��para�е�ѡ���ʽ������ֵΪ��������
	 * 
	 * @param vars
	 * @return
	 * @throws Exception
	 */
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
	 * ����������
	 * <ol>
	 * <li>��λ��ǰ�ڵ�</li>
	 * <li>����ҵ������</li>
	 * <li>������ǰ����</li>
	 * <li>ת����һ�ڵ�</li>
	 * <li>��������</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            Ӧ���е�wfid
	 * @param user
	 *            ��ǰ�����µĲ���Ա,ֵΪ��������/�û�����
	 * @param bizData
	 *            �ϴ������еĴ������,·�ɵ��жϵ����ݴӴ���ȡ��
	 * @param users
	 *            ���ϴ�������ȡusers,lsc.getProperties("users"),�ֹ�����Ӧ������usersֵ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse resume(String wfInstId, String user, UnikMap bizData,
			String[] users, String[] viewUsers, boolean blStart,
			boolean isDirectForeNode, UnikMap outTaskNode,Queue<UnikMap> tranQueue) throws Exception {
		
		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList(); //���������˵�������Ϣ�б�
		if(outTaskNode==null)
			outTaskNode = new UnikMap();
		// �õ�����ʵ��
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid=" + wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// ��鵱ǰ�����Ƿ񷢲�
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// �õ���ǰδ���������
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getTask(wfInstId, user);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// ���ù��������
		if (taskSerial == 0){
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// �õ���ǰ�ڵ㶨��,��������ڿ�˵���ǿ�ʼ�ڵ����ԾͲ����ٲ�ѯһ�νڵ���Ϣ��
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			System.out.println("��������������������������������������������(��ʼ�ڵ�)������������������������������");
			ischeckNode = false;
		}
		
		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="+ wfInstId);
		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="+ wfInstId);

		// ��ɵ�ǰ�Ĺ������� ���ѵ�ǰ���������ʷ�����ɾ����ǰ�����
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeTask(wfInstId, task.getString("taskser"),
				task.getString("nodephase"), bizData, currentNode);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);

		if (ischeckNode) {
			log(DEBUG,"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		//���ö�������Ľڵ�
		outTaskNode.put("currentNode", currentNode); //�����ǰ�ڵ�
		outTaskNode.put("wfid", wfInstId);
		outTaskNode.put("taskSer", taskList);
		
		// �������false��˵�������û�û�д�����Ҫ���һ���ڵ���˲���
		if (!storage.isContinue(currentNode, task, blStart)) {
			log(DEBUG,"[WF_IN_Resume] this task has many user to dispose, so need return");
			outTaskNode.put("isLastDealer", "false");
			return null;
		}
		outTaskNode.put("isLastDealer", "true");
		
		/**
		 * // ת����һ�ڵ� if (NODETYPE_FINAL.equals(currentNode.get("nodetype"))) {
		 * //�����ڵ� storage.updateWorkflowStatus(wfInstId, 0);
		 * storage.completeInstance(wfInstId); return; }
		 */
		// ȡ��һ�ڵ��� �Ȼ�ȡ��������һ����㣬�ж���һ�ڵ����÷�ʽ�������������ʽ����ȡ�����е���һ�����
		UnikMap nextNodeUm =  getnextnodeid(wfDefId, wfInstId, nodeId,currentNode, isDirectForeNode);
		String nextNodeId =nextNodeUm.getString("nextnodeid");
		routetype=nextNodeUm.getString("routetype");
		taskassignstyle=nextNodeUm.getString("taskassignstyle");
		// �����һ���ڵ�Ϊ��
		Errors.Assert(nextNodeId != null && !nextNodeId.equals(""), "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.get("nodetype")))
			return null;

		String[] nextIds = splitCsv(nextNodeId);
		UnikMap[] outNextNode = new UnikMap[nextIds.length]; // ��¼����������еĽ��
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// ȡ����һ�ڵ���׼�������������
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			nextNode.put("routetype", routetype);  //ȱʡֵ
			nextNode.put("taskassignstyle", taskassignstyle);  //ȱʡֵ			
			outNextNode[i] = new UnikMap();
			outNextNode[i] = nextNode;
			// д��"��һ���ڱ��"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));
			// �����һ���ڵ��ǿ�ʼ�ڵ㣬��Ҫ���б�Ҫ��ҵ�����ݴ���һ��Ϊ��־����
			if (CommonConst.WF_NODETYPE_START.equals(nextNode.get("nodetype"))) {
				// �����һ�ڵ��ǿ�ʼ�ڵ㣬�����˻ص�ʱ����Ҫִ�����̶���Ļ������
				//storage.doWithDraw(wfDefId, wfInstId);  //liuxj
				UnikMap wfVars = storage.getInstVar(wfInstId, false);
				// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
				wfVars.putAll(formatVars(wfVars)); //��ʽ������Ϊpara�е��������� liuxj 20141115
				String routetype1 = nextNode.getString("routetype");  
				if(routetype1!=null && routetype.equals("1")){  //�˻�·��
					//�˻ؽ��������
					UnikMap tranUm = new UnikMap();
					tranUm.put("trancode", currentNode);
					tranUm.put("trantype", "retutrans");
					tranUm.put("vars", wfVars);
					tranQueue.add(tranUm);
				}
				log(DEBUG,"[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// ����ǿ�ʼ���ھ�д����һ���ڱ��
				nextNode.put("forenodeid", currentNode.getString("nodeid"));
				// System.out.println("�����һ���ڵ��ǿ�ʼ�ڵ�(user)=" + user);
				assignTaskAgain(wfDefId, wfInstId, nextNode, user,taskList);
				// �˻أ������ִ�
				int maxTaskRound = storage.getTaskMaxTaskRound(wfInstId);
				storage.updateTaskRound(wfInstId, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// �������,�ַ� 
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
			// �ϲ� ͨ��·�ɷ�ʽ���нڵ���ת
			else if (CommonConst.WF_NODETYPE_UNITE.equals(nextNode.get("nodetype"))) {
				String next = nextNode.getString("nodeid");
				if (CommonConst.WF_NODEROUTE_DYNAMIC.equals(nextNode.getString("nextnodemode"))) {
					Class C = Class.forName("cn.com.jbbis.jbportal.workflow.WorkflowRouteImpl");
					WorkflowRoute R = (WorkflowRoute) C.newInstance();
					UnikMap wfVars = storage.getInstVar(wfInstId, false);
					// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
					wfVars.putAll(formatVars(wfVars)); //��ʽ������Ϊpara�е��������� liuxj 20141115
					LinkedList route = storage.getNodeRoute(wfDefId, next);
					UnikMap nextNodeUM = R.getNext(route, wfVars);
					nextNodeId = nextNodeUM.getString("nextNodeId");
					taskassignstyle = nextNodeUM.getString("taskAssignStyle");
					routetype = nextNodeUM.getString("routeType");
					//�˻�					
					if (nextNodeId == null || nextNodeId.length() == 0)
						continue;
					else {
						nextNode = storage.getNodeById(wfDefId, nextNodeId);						
						// ͨ��·�ɵ�������������䷽ʽ�����жϻ�ȡ�û��ķ�ʽ�����Ǵ�����û���Ϣ
						nextNode.put("taskassignstyle", taskassignstyle);
	                    nextNode.put("routetype", routetype);
						assignTask(wfDefId, wfInstId, nextNode, user, users,viewUsers,taskList);
						String routetype1 = nextNode.getString("routetype");
						if(routetype1!=null && routetype.equals("1"))  //�˻�·��
						{
							//�˻ؽ��������
							UnikMap tranUm = new UnikMap();
							tranUm.put("trancode", currentNode);
							tranUm.put("trantype", "retutrans");
							tranUm.put("vars", wfVars);
							tranQueue.add(tranUm);
						}
						break;
					}
				}
			} else if (CommonConst.WF_NODETYPE_SUBWORKFLOW.equals(nextNode.get("nodetype"))){ // ������
			
			}
			// �����ڵ�
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// ���Ĺ�����״̬
				storage.updateWorkflowStatus(wfInstId, 2);
				// ���ʵ��
				storage.completeInstance(wfInstId);
				// ��������ҵ����ձ�Ϊ���״̬
				storage.completeBusiWFMap(wfInstId, "2");
				// ���ý����ڵ�Ľ��� submtrancode
				//=============�������������==============
				// ��ȡ���̱���
				UnikMap wfVars = storage.getInstVar(wfInstId, false); // liuxj
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", nextNode);  //�ڵ���Ϣ
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "endtrans");
				tranQueue.add(tranUm);
				AppResponse res = null;
				//AppResponse res = storage.doService(nextNode);  //liuxj
				// ����Ƿ�ƴ������
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);				
					wfVars = storage.getInstVar(wfInstId, false); // liuxj														// 20141115
					// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
					bizData.putAll(formatVars(wfVars)); // liuxj 20141115
					// ����ƴ��
					unite(nextNode.getString("unitflowid"), user, viewUsers,bizData, busiOperateStyle,taskList);
					//res = null;
				} else {
					// �����ƴ�����̾ͽ�dwtaskvarsд��dwtaskvarshis�����dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// ���̽�����ɾ����������ִ�
					storage.deleteTaskRound(wfInstId);
				}
				//20141218 liuxj
				/*if (res instanceof ServiceOK) {
					// �������ͨ�Ľ��׳ɹ���ʾ,�򽫴���Ϣ�����޸�
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndSucceeded",
							wfInst.getString("flowname")));
				}*/

				// ���������Ϣ
				if (outTaskNode != null) {
					outTaskNode.put("currentNode", currentNode);
					outTaskNode.put("nextNode", outNextNode);
					outTaskNode.put("wfid", wfInstId);
					outTaskNode.put("taskSer", taskList);
				}
				return res;
			}
			// �м�ڵ�����������
			// System.out.println("�м�ڵ�����������" );
			assignTask(wfDefId, wfInstId, nextNode, user, users, viewUsers,taskList);

            //���·�ɣ����õ�ǰ�ڵ㣨������ؽ��ף��뻷��������������
            String routetype1 = nextNode.getString("routetype");
			if(routetype1!=null && routetype1.equals("1"))
			{
				//�˻ؽ��������
				UnikMap tranUm = new UnikMap();
				tranUm.put("trancode", currentNode);
				// ��ȡ���̱���
				UnikMap wfVars = storage.getInstVar(wfInstId, false); // liuxj
				tranUm.put("vars", formatVars(wfVars));
				tranUm.put("trantype", "retutrans");
				tranQueue.add(tranUm);
			}
		}
		// 20130710����������̶ȣ���������û��dwflowinst.instancyLevel�򲻸���dwflowinst�еĸ��ֶ�
		// �����������
		String instancylevel = bizData.getString("dwflowinst.instancylevel");
		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid="
				+ wfInstId + ", taskser=" + taskSerial + ", instancyLevel="
				+ instancylevel);

		storage.updateTaskSerial(wfInstId, taskSerial, instancylevel);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);

		// ���������Ϣ
		if (outTaskNode != null) {
			outTaskNode.put("currentNode", currentNode);
			outTaskNode.put("nextNode", outNextNode);
			outTaskNode.put("wfid", wfInstId);
			outTaskNode.put("taskSer", taskList);
		}
		return null;
	}

	/**
	 * ������ǰ�������񣬲�������ת����һ�ڵ㡣
	 * <ol>
	 * <li>��λ��ǰ�ڵ�</li>
	 * <li>����ҵ������</li>
	 * <li>������ǰ����</li>
	 * <li>ת����һ�ڵ�</li>
	 * <li>��������</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            Ӧ���е�wfid
	 * @param user
	 *            ��ǰ�����µĲ���Ա,ֵΪ��������/�û�����
	 * @param bizData
	 *            �ϴ������еĴ������,·�ɵ��жϵ����ݴӴ���ȡ��
	 * @param users
	 *            ���ϴ�������ȡusers,lsc.getProperties("users"),�ֹ�����Ӧ������usersֵ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse breakWorkflowNext(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode, UnikMap outTaskNode)
			throws Exception {

		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList();
		// �õ�����ʵ��
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// ��鵱ǰ�����Ƿ񷢲�
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// �õ���ǰ����һ��δ��������� ��������Ļ�������������
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// ���ù��������
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// �õ���ǰ�ڵ㶨��,��������ڿ�˵���ǿ�ʼ�ڵ����ԾͲ����ٲ�ѯһ�νڵ���Ϣ��
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			ischeckNode = false;
		}

		// �õ����̶���
		// Map wfDef = storage.getWorkflowDefinition(wfDefId);

		// �洢�ýڵ�Ҫ��������̱���
		log(DEBUG, "[WF_resume NO:6] begin to update workflow variables, wfid="
				+ wfInstId);

		storage.updateWorkflowVariables(wfInstId, bizData, taskSerial, wfDefId,
				nodeId, currentNode.getString("nodedesc"));
		log(DEBUG, "[WF_resume NO:7] update workflow variables OK, wfid="
				+ wfInstId);

		// ������еĹ������� ��ɾ����ǰ��������
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
		 * // ת����һ�ڵ� if (NODETYPE_FINAL.equals(currentNode.get("nodetype"))) {
		 * //�����ڵ� storage.updateWorkflowStatus(wfInstId, 0);
		 * storage.completeInstance(wfInstId); return; }
		 */

		// ȡ��һ�ڵ��� �Ȼ�ȡ��������һ����㣬�ж���һ�ڵ����÷�ʽ�������������ʽ����ȡ�����е���һ�����
        UnikMap nextNodeUm = getnextnodeid(wfDefId, wfInstId, nodeId,
				currentNode, isDirectForeNode);
		String nextNodeId = nextNodeUm.getString("nextnodeid");
		// �����һ���ڵ�Ϊ��
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");

		if (CommonConst.WF_NODETYPE_UNITE.equals(currentNode.get("nodetype")))
			return null;

		UnikMap[] outNextNode = null;
		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// ȡ����һ�ڵ���׼�������������
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			outNextNode[i] = nextNode;

			// д��"��һ���ڱ��"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));

			// �����һ���ڵ��ǿ�ʼ�ڵ㣬��Ҫ���б�Ҫ��ҵ�����ݴ���һ��Ϊ��־����
			if (CommonConst.WF_NODETYPE_START.equals(nextNode.get("nodetype"))) {
				// �����һ�ڵ��ǿ�ʼ�ڵ㣬�����˻ص�ʱ����Ҫִ�����̶���Ļ������
				//storage.doWithDraw(wfDefId, wfInstId);
				log(DEBUG,"[WF_IN_Resume] call WorkflowStorage.doWithDraw OK, begin CALL resume.assignTaskAgain");
				// ����ǿ�ʼ���ھ�д����һ���ڱ��
				nextNode.put("forenodeid", currentNode.getString("nodeid"));
				// System.out.println("�����һ���ڵ��ǿ�ʼ�ڵ�(user)=" + user);
				assignTaskAgain(wfDefId, wfInstId, nextNode, user,taskList);

				// �˻أ������ִ�
				int maxTaskRound = storage.getTaskMaxTaskRound(wfInstId);
				storage.updateTaskRound(wfInstId, maxTaskRound + 1);

				log(DEBUG, "[WF_IN_Resume] call resume.assignTaskAgain OK");
				continue;
			}
			// �������,�ַ�
			else if (CommonConst.WF_NODETYPE_OUT.equals(nextNode
					.get("nodetype"))) {
				System.out.println("�������,�ַ�");
				String next[] = splitCsv(nextNode.getString("nextnode"));
				for (int j = 0; j < next.length; j++) {
					UnikMap nodeUm =storage.getNodeById(wfDefId, next[j]); 
					nodeUm.put("routetype", routetype);
					nodeUm.put("taskassignstyle", taskassignstyle);
					assignTask(wfDefId, wfInstId,nodeUm, user, users,viewUsers,taskList);
				}
				continue;
			}
			// �ϲ� ͨ��·�ɷ�ʽ���нڵ���ת
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
						
						//ִ�д�ؽ���
						
						break;
					}
				}
			}
			// �����ڵ�
			else if (CommonConst.WF_NODETYPE_END.equals(nextNode
					.get("nodetype"))) {
				// ���Ĺ�����״̬
				storage.updateWorkflowStatus(wfInstId, 2);
				// ���ʵ��
				storage.completeInstance(wfInstId);
				// ��������ҵ����ձ�Ϊ���״̬
				storage.completeBusiWFMap(wfInstId, "2");

				// ���ý����ڵ�Ľ��� submtrancode
				AppResponse res = storage.doService(nextNode);
				// ����Ƿ�ƴ������
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// ����ƴ��
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// �����ƴ�����̾ͽ�dwtaskvarsд��dwtaskvarshis�����dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// ���̽�����ɾ����������ִ�
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// �������ͨ�Ľ��׳ɹ���ʾ,�򽫴���Ϣ�����޸�
					res = new ServiceOK(formatMessage(
							"WF_OutWorkflowAndSucceeded",
							wfInst.getString("flowname")));
				}
				return res;
			}
			// �м�ڵ�����������
			nextNode.put("routetype", routetype);
			nextNode.put("taskassignstyle", taskassignstyle);
			assignTask(wfDefId, wfInstId, nextNode, user, users, viewUsers,taskList);

		}
		// �����������

		log(DEBUG, "[WF_resume NO:10] begin to update taskser, wfid="
				+ wfInstId + ", taskser=" + taskSerial);
		storage.updateTaskSerial(wfInstId, taskSerial);
		log(DEBUG, "[WF_resume NO:11] taskser update OK.");

		log(INFO, "[WF_Resume] OUT Workflow.resume, wfid=" + wfInstId);

		// ���������Ϣ
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
	 * ������ǰ��������
	 * <ol>
	 * <li>��λ��ǰ�ڵ�</li>
	 * <li>����ҵ������</li>
	 * <li>������ǰ����</li>
	 * <li>ת����һ�ڵ�</li>
	 * <li>��������</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            Ӧ���е�wfid
	 * @param user
	 *            ��ǰ�����µĲ���Ա,ֵΪ��������/�û�����
	 * @param bizData
	 *            �ϴ������еĴ������,·�ɵ��жϵ����ݴӴ���ȡ��
	 * @param users
	 *            ���ϴ�������ȡusers,lsc.getProperties("users"),�ֹ�����Ӧ������usersֵ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected AppResponse breakWorkflow(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode) throws Exception {
		String routetype="0",taskassignstyle="N";
		ArrayList taskList = new ArrayList();
		// �õ�����ʵ��
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);

		String busiOperateStyle = wfInst.getString("busiOperateStyle");

		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);

		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// ��鵱ǰ�����Ƿ񷢲�
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");

		// �õ���ǰ����һ��δ��������� ��������Ļ�������������
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);

		// ���ù��������
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}

		// �õ���ǰ�ڵ㶨��,��������ڿ�˵���ǿ�ʼ�ڵ����ԾͲ����ٲ�ѯһ�νڵ���Ϣ��
		boolean ischeckNode = true;
		if (currentNode == null) {
			currentNode = new UnikMap();
			currentNode.putAll(storage.getNodeById(wfDefId, nodeId));
		} else {
			ischeckNode = false;
		}

		// �洢�ýڵ�Ҫ��������̱���
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

		// ������еĹ������� ��ɾ����ǰ��������
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeAllTask(wfInstId);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);

		if (ischeckNode) {
			log(DEBUG,
					"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}

		// ��������ȡ�����ڵ�
		// System.out.println("wfDefId" + wfDefId + ",wfInstId=" + wfInstId +
		// ",nodeId=" + nodeId + ",currentNode=");
		String nextNodeId = getEndNode(wfDefId);
		// �����һ���ڵ�Ϊ��
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");

		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// ȡ����һ�ڵ���׼�������������
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);

			// д��"��һ���ڱ��"
			// nextNode.setField("forenodeid", currentNode.getString("nodeid"));
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));

			if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// ���Ĺ�����״̬
				storage.updateWorkflowStatus(wfInstId, 2);
				// ���ʵ��
				storage.completeInstance(wfInstId);
				// ��������ҵ����ձ�Ϊ���״̬
				storage.completeBusiWFMap(wfInstId, "2");

				// ���ý����ڵ�Ľ��� submtrancode
				// System.out.println(nextNode.get("submtrancode"));
				AppResponse res = storage.doService(nextNode);
				// ����Ƿ�ƴ������
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// ����ƴ��
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// �����ƴ�����̾ͽ�dwtaskvarsд��dwtaskvarshis�����dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// ���̽�����ɾ����������ִ�
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// �������ͨ�Ľ��׳ɹ���ʾ,�򽫴���Ϣ�����޸�
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
	 * ������ǰ��������
	 * <ol>
	 * <li>��λ��ǰ�ڵ�</li>
	 * <li>����ҵ������</li>
	 * <li>������ǰ����</li>
	 * <li>ת����һ�ڵ�</li>
	 * <li>��������</li>
	 * </ol>
	 * 
	 * @param wfInstId
	 *            Ӧ���е�wfid
	 * @param user
	 *            ��ǰ�����µĲ���Ա,ֵΪ��������/�û�����
	 * @param bizData
	 *            �ϴ������еĴ������,·�ɵ��жϵ����ݴӴ���ȡ��
	 * @param users
	 *            ���ϴ�������ȡusers,lsc.getProperties("users"),�ֹ�����Ӧ������usersֵ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("deprecation")
	public AppResponse ForceBreakWorkflow(String wfInstId, String user,
			UnikMap bizData, String[] users, String[] viewUsers,
			boolean blStart, boolean isDirectForeNode) throws Exception {
		
		@SuppressWarnings("rawtypes")
		ArrayList taskList = new ArrayList();
		// �õ�����ʵ��
		log(INFO, "[WF_IN_Resume] IN Workflow.resume, wfid=" + wfInstId);
		UnikMap wfInst = storage.getWorkflowInstance(wfInstId);
		
		String busiOperateStyle = wfInst.getString("busiOperateStyle");
		
		log(DEBUG, "[WF_resume NO:1] get workflow instance OK, wfid="
				+ wfInstId);
		
		String wfDefId = wfInst.getString("flowid");
		log(DEBUG, "[WF_resume NO:1.1] get workflow instance OK, wfDefId="
				+ wfDefId);
		// ��鵱ǰ�����Ƿ񷢲�
		log(DEBUG, "[WF_resume NO:2] check flow state, flowid=" + wfDefId);
		storage.checkFlowState(wfDefId);
		log(DEBUG, "[WF_resume NO:3] check flow state OK.");
		
		// �õ���ǰ����һ��δ��������� ��������Ļ�������������
		log(DEBUG, "[WF_resume NO:4] begin to get undisposed task.");
		UnikMap task = storage.getAnyOneTask(wfInstId);
		String nodeId = task.getString("nodeid");
		log(DEBUG, "[WF_resume NO:5] get currently task, nodeid=" + nodeId);
		
		// ���ù��������
		if (taskSerial == 0) {
			taskSerial = Integer.parseInt(wfInst.getString("taskser"), 10);
		}
		
		// �õ���ǰ�ڵ㶨��,��������ڿ�˵���ǿ�ʼ�ڵ����ԾͲ����ٲ�ѯһ�νڵ���Ϣ��
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
		
		// ����ǰ���������ʷ�����У������������ǿ����ֹ�ġ�
		currentNode.put("nodedesc", "ǿ����ֹ����[����Ա��"+user+"]");
		storage.completeTask(wfInstId, task.getString("taskser"),
				task.getString("nodephase"), bizData, currentNode);
		
		// ������еĹ������� ��ɾ����ǰ��������
		log(DEBUG, "[WF_resume NO:8] begin complete task, wfid=" + wfInstId);
		storage.completeAllTask(wfInstId);
		log(DEBUG, "[WF_resume NO:9] complete task OK, wfid=" + wfInstId);
		
		if (ischeckNode) {
			log(DEBUG,
			"[WF_IN_Resume] begin to check nodetype = start Node ? update dwtaskvars : return");
			checkIsStartNode(currentNode.getString("nodetype"), wfInst, bizData);
			log(DEBUG, "[WF_IN_Resume] check nodetype OK.");
		}
		
		// ��������ȡ�����ڵ�
		String nextNodeId = getEndNode(wfDefId);
		// �����һ���ڵ�Ϊ��
		Errors.Assert(nextNodeId != null, "workflow/NextNodeNotFound");
		
		String[] nextIds = splitCsv(nextNodeId);
		for (int i = 0, c = nextIds.length; i < c; i++) {
			// ȡ����һ�ڵ���׼�������������
			UnikMap nextNode = storage.getNodeById(wfDefId, nextIds[i]);
			nextNode.put("wfid", wfInstId);
			
			// д��"��һ���ڱ��"
			log(DEBUG, "[WF_IN_Resume] begin to assignTask, nodeid="
					+ nextNodeId + ", nodetype=" + nextNode.get("nodetype"));
			
			if (CommonConst.WF_NODETYPE_END.equals(nextNode.get("nodetype"))) {
				// ���Ĺ�����״̬
				storage.updateWorkflowStatus(wfInstId, 2);
				// ���ʵ��
				storage.completeInstance(wfInstId);
				// ��������ҵ����ձ�Ϊ��ǿ����ֹ��״̬
				storage.completeBusiWFMap(wfInstId, "3");
				
				// ���ý����ڵ�Ľ��� submtrancode
				//AppResponse res = storage.doService(nextNode);
				AppResponse res = storage.doEndService(nextNode, bizData);
				// ����Ƿ�ƴ������
				if (CommonConst.WF_ISYESORNO_YES.equals(nextNode
						.getField("isunit"))) {
					bizData.put("unitwfid", wfInstId);
					// ����ƴ��
					unite(nextNode.getString("unitflowid"), user, viewUsers,
							bizData, busiOperateStyle,taskList);
					res = null;
				} else {
					// �����ƴ�����̾ͽ�dwtaskvarsд��dwtaskvarshis�����dwtaskvars
					storage.delDwtaskVars(wfInstId);
					// ���̽�����ɾ����������ִ�
					storage.deleteTaskRound(wfInstId);
				}
				if (res instanceof ServiceOK) {
					// �������ͨ�Ľ��׳ɹ���ʾ,�򽫴���Ϣ�����޸�
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
	 * ����ƴ��,��һ����������ʵ����������������һ���������� unitwfid��ƴ������ʱ����һ������ʵ�����
	 * 
	 * @param wfDefId
	 *            �������̱��
	 * @param user
	 *            bankid/userid
	 * @param bizData
	 *            ��Ҫ��ʵ���Ĳ�����unitwfid��ֵ
	 * @throws Exception
	 *             :java.lang.Exception
	 */
	protected void unite(String wfDefId, String user, String[] viewUsers,
			UnikMap bizData, String busiOperateStyle,ArrayList taskList) throws Exception {
		String routetype="0",taskassignstyle="N";
		
		// ����ʵ�����
		String wfInstId = generateRandomSeq();

		log(INFO, "[WF_unite NO:1] IN Workflow.unite, wfid=" + wfInstId);

		// ��������ʵ��
		storage.createInstance(wfDefId, wfInstId, user,
				CommonConst.WF_NODETYPE_START, bizData, busiOperateStyle);

		// �õ���ʼ�ڵ㶨��
		UnikMap startNode = storage.getNodeByType(wfDefId,
				CommonConst.WF_NODETYPE_START);

		// �½������б�
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
	 * ������һ���ڵı��.
	 * 
	 * @param flowid
	 *            ���̱��
	 * @param nodeid
	 *            ��ǰ���ڱ��
	 * @param dataParams
	 *            ����������
	 * @param isDirectForeNode
	 *            �Ƿ�ֱ�ӷ��ص���ؽڵ� true ���أ�false ������
	 * @return ��һ���ڵı��
	 * @throws Exception
	 */
	public UnikMap getnextnodeid(String flowid, String nodeid,
			UnikMap dataParams, boolean isDirectForeNode) throws Exception {
		UnikMap node = storage.getNodeById(flowid, nodeid);
		return getnextnodeid(flowid, "******", nodeid, node, dataParams,
				isDirectForeNode);
	}

	/**
	 * ������һ�ڵ���
	 * 
	 * @param flowid
	 *            ���̱��
	 * @param wfid
	 *            ���������
	 * @param nodeid
	 *            ��ǰ�ڵ���
	 * @param node
	 *            ��ǰ�ڵ���Ϣ
	 * @param isDirectForeNode
	 *            �Ƿ�ֱ�ӷ��ص���ؽڵ� true ���أ�false ������
	 * @return String ��һ�ڵ���,�����һ�ڵ㲻�����򷵻�null
	 * @throws Exception
	 *             :java.lang.Exception
	 */
	private UnikMap getnextnodeid(String flowid, String wfid, String nodeid,
			UnikMap node, boolean isDirectForeNode) throws Exception {
		// ����������ʱ���Ѿ�����vars���������Դ˴����Բ���������ȥȡһ�α���
		UnikMap wfVars = storage.getInstVar(wfid, true);
		// ʹ��para�ж����ѡ�����ʽ������ֵΪ��Ӧ������
		wfVars.putAll(formatVars(wfVars)); // liuxj 20141115

		/*
		 * Iterator it = wfVars.keySet().iterator(); while(it.hasNext()){
		 * 
		 * String key = String.valueOf(it.next()); // ȡ��������key
		 * 
		 * //System.out.println("liuxj(workflow)==============================="+
		 * key+"(key)=" + wfVars.getString(key)); }
		 */
		return getnextnodeid(flowid, wfid, nodeid, node, wfVars,
				isDirectForeNode);
	}

	/**
	 * ������һ�ڵ�ı��,�����һ�ڵ�û���ҵ��ͷ���null
	 * 
	 * @param flowid
	 *            ���̱��
	 * @param wfid
	 *            ���������
	 * @param nodeid
	 *            ��ǰ�ڵ���
	 * @param node
	 *            ��ǰ�ڵ���Ϣ
	 * @param var
	 *            ·�ɲ���
	 * @return String��һ�ڵ���,�����һ�ڵ㲻�����򷵻�null
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
		String nextnodemode = node.getString("nextnodemode"); // ��һ�ڵ����÷�ʽ
		String nextNodeId = node.getString("nextnode");
		log(DEBUG, "[WF_getnextnodeid NO:1] wfid=" + wfid + " nextnodemode="
				+ nextnodemode + " nextNodeId=" + nextNodeId);
		
		//��ʼ����һ�ڵ�
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
			wfVars.putAll(formatVars(var)); //��ʽ������Ϊpara�е��������� liuxj 20141211
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
		wfVars.putAll(formatVars(dataParams)); //��ʽ������Ϊpara�е��������� liuxj 20141211
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
	 * ���ݽڵ���Ϣ�Զ�ѡ����Ա�����������,Ŀǰ�������������ٹ��������ַ�ʽ
	 * 
	 * @param wfDefId
	 *            ���������
	 * @param wfInstId
	 *            ����ʵ�����
	 * @param node
	 *            �ڵ���Ϣ,
	 * @param local
	 *            ��ǰ������Ϣbankid/userid
	 * @param user
	 *            ��ѡ�û�-������Զ�������user=null
	 * @param routetype
	 *            ·�����ͣ�0:������ת  1:���
	 * @param taskassignstyle
	 *            ������䷽ʽ �����������Ա �ӽ�����ʷ������Ա�з���
	 * @throws java.lang.Exception
	 */
	protected void assignTask(String wfDefId, String wfInstId, UnikMap node,
			String local, String[] user, String[] viewUser,ArrayList taskList) throws Exception {
		String[] users = user;
		String routetype = node.getString("routetype");  //·������   ����  �˻�
		String taskassignstyle = node.getString("taskassignstyle");  //������䷽ʽ  ��������   ����ʷ��Ա�з���

		log(DEBUG, "routetype=" + routetype + ";taskassignstyle=" +taskassignstyle);
		// �������Զ����仹���ֹ�����,users���ŵ������е��û���Ϣ,���Ϊ�ֹ�����
		if (users == null) {
			// ����
			if (taskassignstyle!= null && taskassignstyle.equals("H")) {
				//�������������ʷ���ȡ������Ա��Ϣ,�滻������û���Ϣ
				//SELECT bankid,operid FROM DWTASKHIS WHERE WFID='20140917001324810561' AND NODEID='FX102' ORDER BY TASKSER desc
				user = storage.selectHistoryUser(wfInstId, node.getString("nodeid"), "1" , false);
				//�鵽�û���Ϊ����Ϊ��ʷ�û� 
				if (user != null){  
					users = user;
				}else{  //�����ʷ�û������ڻ�����ע�������»�ȡ��
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
		
					// �����Զ������û���Ϣ
					log(DEBUG, "�����Զ������û���Ϣ!");
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
	
				// �����Զ������û���Ϣ
				log(DEBUG, "�����Զ������û���Ϣ!");
			}
		}
		/*
		 * for(int i=0;i<users.length;i++) { System.out.println("assign user=" +
		 * users[i]);
		 * 
		 * }
		 */
		// �����Ա�Ƿ���Ϸ���ԭ��,���users��������ʷ�б���ͷ���
		compareUsers(users, node);

		int num = node.getInt("mindealnum"); // ���ٴ�������
		log(DEBUG, "mindealnum:" + num);
		// ���䴦������ 0721
		String assignmindealnumstyle = node.getString("assignmindealnumstyle");

		/*
		 * ֮��������ֻ�ж��Ƿ��Զ�����,���Ϊ�ֹ�������ôusers���ֵһ���ʹ���������ͬ,
		 * ���Ϊ����������Ա��ôusers��������û����÷�������,���Ϊ�Զ����䲢�ҷ���
		 * �������û��������ٴ���������ȵĻ�û��Ҫȥ�ж�������������ٹ�����,��������
		 * Ա�������ٴ�������ʱ��ȥ�жϴ���ʽ,���ս�users�޸�Ϊ�����������û�
		 */
		if (CommonConst.WF_TASKASSIGN_AUTO.equals(node
				.getString("autodisuserflag")) && users.length > num) {
			String processmode = node.getString("processmode"); // ����ʽ
			if (CommonConst.WF_PROCEMODE_WORKLEAST.equals(processmode)) { // ���ٹ�����
				users = storage.getTaskLeastUser(users, num);
			} else if (CommonConst.WF_PROCEMODE_RANDOM.equals(processmode)) {
				users = getRandomUser(num, users);
			}
		} else if (CommonConst.WF_TASKASSIGN_MANUAL.equals(node
				.getString("autodisuserflag"))) {
			//�Զ����䣬����Ҫ��users���д������´���û�����塣
// ȡ�����ٴ�����Ա�������
//			if ((assignmindealnumstyle == null || assignmindealnumstyle
//					.equals("2")) && users.length > num) // 2 ��̬����
//			{
//				users = getRandomUser(num, users);
//				}

		} else if (CommonConst.WF_TASKASSIGN_WFOUT.equals(node
				.getString("autodisuserflag")) && users.length > num) {
			// users = getRandomUser(num, users);
		}

		// ===============================��ʱ��Ȩ��ʼ=====================================
		// ����Ƿ�������ʱ��Ȩ
		boolean blCheckIsStartTempAuth = storage.checkTempAuthStart(wfDefId);
		log(DEBUG, "===================================��ʼ��ʱ��Ȩ���"
				+ (blCheckIsStartTempAuth ? "������ʱ��Ȩ" : "�ر���ʱ��Ȩ"));
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
						SingleResult sr = storage.getBusiWFMap(wfInstId);
						String custid = sr.getString("custid");

						// ���ݿͻ���ŵ�ָ���ͻ������Ƿ��趨�˸ÿͻ�
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
							// ����ʱ��Ȩ�û����浱ǰ�û�
							log(DEBUG, "ָ���ͻ���ʱ��Ȩ�����û�" + recvPerson + "����"
									+ tmpusers[1] + "����������");
							users[i] = tmpusers[0] + "/" + recvPerson;
						}
					} else {
						// ����ʱ��Ȩ�û����浱ǰ�û�
						log(DEBUG, "ȫ���ͻ���ʱ��Ȩ�����û�" + recvPerson + "����"
								+ tmpusers[1] + "����������");
						users[i] = tmpusers[0] + "/" + recvPerson;
					}
				}
			}
		}
		// ===============================��ʱ��Ȩ����=====================================

		// �������,������һ��users�ﶼ�Ƿ���������,���Կ���ֱ�ӽ��з���
		taskSerial = storage.newTask(node, wfInstId, ++taskSerial, users,viewUser, null,taskList);

		// ���������ڵ㣬��¼�����Ϣ��ҵ�����̶��ձ�
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
	 * ���ҿ�����������Ա�Ƿ��������
	 * 
	 * @param users
	 *            �û��б�
	 * @param node
	 *            �ڵ���ϸ��Ϣ
	 * @throws java.lang.Exception
	 *             usersΪnull��û��ֵ�׳�WF_NoDealUserLogon,
	 *             �ڸ�λ��ԱС�ڴ�����ԱWF_MindealnumLTUsers
	 */
	private void compareUsers(String[] users, UnikMap node) throws Exception {
		String flowid = node.getString("flowid");
		if (users == null || users.length == 0) {
			String[] msg = new String[2];
			msg[0] = flowid;
			msg[1] = node.getString("nodename");
			throw new NamedException(formatMessage("WF_NoDealUserLogon", msg));
		}
		// ��һ��:�ȼ������������������������
		String autodisuserflag = node.getString("autodisuserflag"); // ����������
		String assignmindealnumstyle = node.getString("assignmindealnumstyle"); // �Ƿ�������Ϊ�ֶ���������

		int assignnum = 0;
		if (CommonConst.WF_TASKASSIGN_ALL.equals(autodisuserflag)) {
			assignnum = users.length; // �����������Ƿ���������Ա��ô���������Ϊ�����ڸ���Ա
			// ����Ƿ����������,�򽫽ڵ�Ĵ�����Ա����Ϊ��ǰ����������
			storage.updateDealNum(flowid, node.getString("nodeid"), assignnum);
		} else {
			// ����û���������ʷ�б�����Ҫ���м�飬ֻҪ�����е�������ɾͿ�����
			if (checkHisFlag(node, users))
				return;

			assignnum = node.getInt("mindealnum"); // �ֶ�������Զ�����,������Ա����ȡ��������

			//
			if (assignmindealnumstyle == null
					|| assignmindealnumstyle.equals("2")) {
				if (users.length < assignnum || assignnum == 0) { // �ڸ���ԱС�ڴ�����Ա
					String msg[] = new String[2];
					msg[0] = node.getString("nodename");
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage(
							"WF_MindealnumLTUsers", msg));
				}
			} else if (assignmindealnumstyle.equals("1")) {
				if (users.length == 0) { // �ڸ���ԱС�ڴ�����Ա
					String msg[] = new String[2];
					msg[0] = node.getString("nodename");
					msg[1] = String.valueOf(assignnum);
					throw new NamedException(formatMessage(
							"WF_MindealnumLTUsers", msg));
				}
			}
		}

		String taskoverpolicy = node.getString("taskoverpolicy"); // ������ɲ���
		// �ڶ���:�ҳ������������,�����ɲ�������ɰٷֱȺͱ����������,��ô�����϶�С�ڻ���ڷ��������,
		// ������������һ����ô�������Ϊ1,����ֻ��Ҫ�жϰ��������
		int compnum = 1;
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) { // ���������
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
	 * ��鴦����Ա�Ƿ����ʷ�б���ȡ��
	 * 
	 * @param storage
	 *            WorkflowStorageImpl��hisisemptyΪtrue
	 * @param request
	 *            request����hisisemptyΪtrue
	 */
	private boolean checkHisFlag(UnikMap node, String[] users) {
		boolean f = false;
		char isempty = storage.hisisempty;
		// ����ʷ�б���û�в�ѯ��������Ա��������ʷ�б��в鴦����Ա���������������
		if (isempty == '0') { // û���ڵ�ǰ�����в�ѯ������Ա,��Ա���ֹ�����
			int minNum = node.getInt("mindealnum");
			if (CommonConst.WF_HISUSER_ON.equals(node.getString("hisflag"))) {
				f = users.length == minNum ? false : true;
			}
		} else if (isempty == '1') { // ����ʷ�б��в�ѯ��������Ա
			f = true;
		}
		return f;
	}

	/**
	 * ȡ�����ٴ�����Ա�������
	 * 
	 * @param num
	 *            ��Ա����
	 * @param user
	 *            ������Ա����
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
	 * ��ʼ�ڵ���������,��Ҫ�Ǵ������˻ص���ʼ�ڵ���������
	 * 
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	protected void assignTaskAgain(String wfDefId, String wfInstId,
			UnikMap node, String local,ArrayList taskList) throws Exception {
		// ����ʷ������ȡ�û�
		String[] users = storage.selectTaskUsers(wfInstId,
				node.getString("nodeid"), CommonConst.WF_TASKTYPE_DEAL); // ��������
		// for(int i=0;i<users.length;i++)
		// {
		// System.out.println("assignTaskAgain=" + users[i]);
		//
		// }

		String[] viewusers = storage.selectTaskUsers(wfInstId,
				node.getString("nodeid"), CommonConst.WF_TASKTYPE_VIEW); // �������

		// �ӽڵ�õ�������䴦������
		String numStr = node.getString("mindealnum");
		String assignmindealnumstyle = node.getString("assignmindealnumstyle");

		// �����ȡ���������û�
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

		// ============================��ʱ��Ȩ����ʼ=======================================

		// ===========================��ʱ��Ȩ�������====================================

		// ����������
		taskSerial = storage.newTask(node, wfInstId, ++taskSerial, usrs,
				viewusers, null,taskList);

		// ���������ڵ㣬��¼�����Ϣ��ҵ�����̶��ձ�
		String curOperUser = local;
		String isPrimaryAuditNode = node.getString("isprimaryauditnode");

		storage.updateBusiWFMap(node, wfInstId, curOperUser, usrs,
				isPrimaryAuditNode);

	}

	protected UnikMap updateVariables(UnikMap wfDef, UnikMap node,
			UnikMap bizData, UnikMap wfInst) {
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
	 * ������: ʱ��17+3λ�����
	 * 
	 * @return String �������
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
	 * ������Ϣ��ʾ,��ʽ�����ص���Ϣ���Դ�����
	 * 
	 * @throws java.lang.Exception
	 */
	protected String formatMessage(String arg0, Object obj) throws Exception {
		return storage.formatMessage(arg0, obj);
	}

	/**
	 * ��鵱ǰ�Ƿ��ǿ�ʼ�ڵ�,����ǿ�ʼ�ڵ���Ҫ��������������Ϣ
	 * 
	 * @param nodetype
	 *            �ڵ�����
	 * @param wfinst
	 *            ����ʵ����Ϣ
	 * @param vars
	 *            ���µĲ�����Ϣ
	 * @throws Exception
	 */
	private void checkIsStartNode(String nodetype, UnikMap wfinst, UnikMap vars)
			throws Exception {
		if (CommonConst.WF_NODETYPE_START.equals(nodetype)) {
			String flowid = wfinst.getString("flowid");
			String wfid = wfinst.getString("wfid");
			UnikMap wfmain = storage.getWorkflowDefinition(flowid);
			if (wfid.length() > 0) {
				// ȡ�����̶����е�����������Ϣ�����滻
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