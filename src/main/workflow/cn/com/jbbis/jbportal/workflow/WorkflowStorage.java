package cn.com.jbbis.jbportal.workflow;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;
/**
 * <p>Title: �������������ݿ⡢Ӧ��������صĴ�����̽ӿ�</p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: ������������������Ϣϵͳ���޹�˾ </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.0 ƽ̨Ǩ�� 2008-2-19 ����04:50:26
 */
public interface WorkflowStorage
{
	/**
	 * �õ����̶���
	 * @throws java.lang.Exception
	 */
	public UnikMap getWorkflowDefinition(String wfDefId) throws Exception;

	/**
	 * �õ�����ʵ��
	 * @throws java.lang.Exception
	 */
	public UnikMap getWorkflowInstance(String wfInstId) throws Exception;

	/**
	 * ͨ���ڵ�����(��ʼ/�м�/����)�õ��ڵ㶨��
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodeByType(String wfDefId, String type) throws Exception;

	
	/**
	 * ͨ���ڵ�ID�õ��ڵ㶨��
	 * @throws Exception:java.lang.Exception
	 */
	public UnikMap getNodeById(String wfDefId, String nodeId) throws Exception;

	/**
	 * ��������ʵ��
	 * @throws java.lang.Exception
	 */
	public void createInstance(String wfDefId, String wfInstId, String user,
			String wfState, UnikMap var,String busiOperateStyle) throws Exception;

	/**
	 * ����dwtask���е�����ִμ�¼
	 * @param var var����Ҫ�� wfid 
	 * @return int ����ִ�
	 * @throws java.lang.Exception
	 */
	public int getTaskMaxTaskRound(String wfid) throws Exception;
	
	/**
	 * �������������ִ�
	 */
	public void updateTaskRound(String wfid,int taskRound) throws Exception ;
	/**
	 * �����˵���ʼ�ڵ�ʱ��Ҫ���Ĺ���
	 * @throws java.lang.Exception
	 */
	public void doWithDraw(String flowid, String wfInstId) throws Exception;

	/**
	 * ��������ʵ��
	 * @throws java.lang.Exception
	 */
	public void completeInstance(String wfInstId) throws Exception;

	/**
	 * �������̱���
	 * @throws java.lang.Exception
	 */
	public void updateWorkflowVariables(String wfInstId, UnikMap wfVars,
			int taskser, String wfDefId, String nodeId, String nodeDesc)
			throws Exception;

	/**
	 * ���ݻ����ź͸�λ��Ų�ѯ�û��б�,Ŀǰֻ����һ����λ
	 * �˴����ϼ�������ָ:ȡ�ϼ�����������Ϣ,�����ϼ��������
	 * @param m m��Ҫ�����²��� org ��ǰ�ڵ������ post ��λ local ����/�û�
	 * wfid ���̱�� nodeid �ڵ���
	 * @return String[] �û���Ϣ����bankid/userid|username,���û���ҵ��û�����null
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap m,boolean flag) throws Exception;
	/**
	 * ·�ɱ�
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId)
			throws Exception;

	/**
	 * �õ���������
	 * @throws java.lang.Exception
	 */
	public UnikMap getTask(String wfInstId, String user) throws Exception;

	/**
	 * �õ������µ����й�������
	 * @throws java.lang.Exception
	 */
	public DataList getTask(String wfInstId) throws Exception;
	
	/**
	 * �½���������,users���ж��˷���
	 * @return ��ǰ�����
	 * @throws java.lang.Exception
	 */
	public int newTask(UnikMap node,String wfInstId, int taskSerial,String[] users,String[] viewUsers,UnikMap var,ArrayList taskList) throws Exception;

	/**
	 * ��ɹ�������
	 * @throws java.lang.Exception
	 */
	public void completeTask(String wfInstId, String taskser,String nodephase, UnikMap vars,UnikMap node) throws Exception;

	/**
	 * ɾ�����д�������
	 * @param wfInstId
	 * @throws Exception
	 */
	public void completeAllTask(String wfInstId) throws Exception;
	/**
	 * �������̵��������
	 * @throws java.lang.Exception
	 */
	public void updateTaskSerial(String wfInstId, int nextSerial)
			throws Exception;

	/**
	 * ��������״̬
	 * @throws java.lang.Exception
	 */
	public void updateWorkflowStatus(String wfInstId, int status)
			throws Exception;

	/**
	 * �����б�
	 * @throws java.lang.Exception
	 */
	public Object getTaskList(Object msg, String org, String user)
			throws Exception;

	
	
	/**
	 * ʵ��������
	 * @throws java.lang.Exception
	 */
	public UnikMap getInstVar(String wfInstId, boolean reload) throws Exception;

	/**
	 * ·�ɱ�
	 * @throws java.lang.Exception
	 */
	public LinkedList getNodeRoute(String wfDefId, String nodeId)
			throws Exception;

	/**
	 * ���ý���,��Ҫ���ṩ�������ڵ�
	 * @throws java.lang.Exception
	 */
	public AppResponse doService(UnikMap vars) throws Exception;

	/**
	 * ����Ƿ�����һ���ڵ���
	 * @throws java.lang.Exception
	 */
	boolean isContinue(UnikMap node,UnikMap var,boolean blStart)throws Exception;
	
	/**
	 * ����Ƿ����������
	 * @param node  ��ǰ�ڵ�
	 * @param var   ���̱���
	 * @param blStart �Ƿ�ʼ�ڵ�
	 * @return
	 * @throws Exception
	 */
	boolean isLastestHandler(UnikMap node,UnikMap var,boolean blStart) throws Exception;
	/**
	 * ����������˭����������,�������ٵ��û�
	 * @throws java.lang.Exception
	 */
	public String[] getTaskLeastUser(String[] user,int num)throws Exception;
	
	/**
	 * �������״̬,�Ƿ���״̬�������ύҵ��
	 * @param flowid ���̱��
	 * @throws java.lang.Exception
	 */
	void checkFlowState(String flowid) throws Exception;
	
	/**
	 * ���������е����ݱ�����������ʷ����ձ�����
	 * @param wfid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void delDwtaskVars(String wfid) throws Exception;
}