package com.sky.workflow.engine;

import java.util.ArrayList;
import java.util.LinkedList;

import com.sky.workflow.model.DbBusiWfMapTable;
import com.sky.workflow.model.DwFlowInstTable;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.model.DwFlowNodeTable;
import com.sky.workflow.model.DwTaskTable;
import com.sky.workflow.util.UnikMap;

/**
 * <p>
 * Title: 流程引擎与数据库、应用数据相关的处理过程接口
 * </p>
 */
public interface IWorkflowStorage {

	public DwFlowMainTable getWorkflowDefinition(String flowid);

	public DwFlowInstTable getWorkflowInstance(String wfid) throws Exception;

	public DwFlowNodeTable getNode(String flowid, String nodeid, String nodetype) throws Exception;

	public UnikMap getNodePost(UnikMap var) throws Exception;

	public DwFlowNodeTable getNodeByType(String wfDefId, String type) throws Exception;

	public DwFlowNodeTable getNodeById(String flowid, String nodeid) throws Exception;

	public void createInstance(String flowid, String wfid, String user, String wfState, UnikMap bizData, String busiOperateStyle) throws Exception;

	public String getWorkdateCurrTime() throws Exception;

	public String replaceFlowDesc(String wfdesc, UnikMap vars) throws Exception;

	public void updateWorkflowVariables(String wfInstId, UnikMap wfVars, int taskSer, String wfDefId, String nodeId, String nodeDesc) throws Exception;

	public String[] selectHistoryUsers(UnikMap map, boolean flag, boolean f) throws Exception;

	public String[] selectUsers(UnikMap map, boolean flag, boolean f) throws Exception;

	public String[] selectUsers(UnikMap map, boolean flag) throws Exception;

	public String[] selectUsers(String org, String post, String local, String bindprodid, String wfid, String nodeId) throws Exception;

	public String[] selectUsers(String org, String post, String local, String bindprodid, String wfid, String nodeId, boolean flag) throws Exception;

	public String[] selectTaskUsers(String wfInstId, String nodeId) throws Exception;

	public String[] selectTaskUsers(String wfInstId, String nodeId, int tasktype) throws Exception;

	public String[] selectTaskViewUsers(String wfInstId, String nodeId) throws Exception;

	public String[] selectTaskUsers(String wfInstId, String nodeId, String userloginstate, boolean flag, int tasktype) throws Exception;

	public String[] selectTaskUsers(String wfInstId, String nodeId, String userloginstate, boolean flag) throws Exception;

	public String[] selectHistoryUser(String wfid, String nodeid, String hisFlag, boolean flag) throws Exception;

	public DataList getTask(String wfInstId) throws Exception;

	public DwTaskTable getTask(String wfid, String user) throws Exception;

	public UnikMap getAnyOneTask(String wfInstId) throws Exception;

	@SuppressWarnings("rawtypes")
	public int newTask(DwFlowNodeTable currentNode, String flowid, int taskSerial, String[] users, String[] viewUsers, UnikMap bizData, ArrayList taskList) throws Exception;

	public int getTaskMaxTaskRound(String wfid) throws Exception;

	public void updateTaskRound(String wfid, int taskRound) throws Exception;

	public void deleteTaskRound(String wfid) throws Exception;

	public void insertTaskRound(String wfid, int taskRound) throws Exception;

	public void writeBusiWFMap(String flowid, String wfid, UnikMap busiWorkflow) throws Exception;

	public boolean checkBusiWFMapIsExist(String wfid) throws Exception;

	public int getBusiMaxNo(String loanid) throws Exception;

	public SingleResult getBankInfo(String bankid) throws Exception;

	public SingleResult getUserInfo(String bankid, String operid) throws Exception;

	public boolean checkTempAuthStart(String flowId) throws Exception;

	public SingleResult getBusiRange(String flowid) throws Exception;

	public DataList getTempAuth(UnikMap um) throws Exception;

	public DbBusiWfMapTable getBusiWFMap(String wfid);

	public boolean updateReturnBusiWFMap(UnikMap umBusiWFMap) throws Exception;

	public boolean updateBusiWFMap(UnikMap umBusiWFMap) throws Exception;

	public boolean updateBusiWFMap(UnikMap node, String wfInstId, String curOperUser, String[] users) throws Exception;

	public boolean updateBusiWFMap(DwFlowNodeTable nextNode, String wfInstId, String curOperUser, String[] users, String isPrimaryAuditNode) throws Exception;

	public boolean updateNormalBusiWFMap(UnikMap node, String wfInstId, String curOperUser) throws Exception;

	public boolean completeBusiWFMap(String wfid, String isFinish);

	public boolean deleteBusiWFMap(String wfid);

	public int getAllUserFromHistoryTask(UnikMap var) throws Exception;

	public int getTaskNum(DwTaskTable dwTaskVo) throws Exception;

	public int getTaskNum(UnikMap var) throws Exception;

	public int getViewTaskNum(UnikMap var) throws Exception;

	public void checkFlowState(String flowid) throws Exception;

	public void completeAllTask(String wfInstId) throws Exception;

	public void completeTask(String wfid, DwTaskTable dwTaskVo, UnikMap bizData, DwFlowNodeTable currentNode) throws Exception;

	public boolean isLastestHandler(UnikMap node, UnikMap bizData, boolean blStart) throws Exception;

	public boolean isContinue(DwFlowNodeTable currentNode, DwTaskTable dwTaskVo, boolean blStart) throws Exception;

	public void updateTaskSerial(String wfInstId, int nextSerial) throws Exception;

	public void updateWorkflowStatus(String wfInstId, int status) throws Exception;

	public void updateTaskSerial(String wfInstId, int nextSerial, String instancylevel) throws Exception;

	public String getGlobalNum(String name, int len) throws Exception;

	public String[] getVarNames(String desc, String wfDefId, String nodeid) throws Exception;

	@SuppressWarnings("rawtypes")
	public LinkedList getNodeRoute(String wfDefId, String nodeId) throws Exception;

	public String getSelectText(String select, String value) throws Exception;

	public UnikMap getInstVar(String wfInstId, boolean reload) throws Exception;

	public void completeInstance(String wfInstId) throws Exception;

	public void doWithDraw(String flowid, String wfid) throws Exception;

	public AppResponse doReturnService(UnikMap node, UnikMap varsUm) throws Exception;

	public AppResponse doEndService(UnikMap node, UnikMap varsUm) throws Exception;

	public AppResponse doService(UnikMap v) throws Exception;

	public String[] getTaskLeastUser(String[] user, int num) throws Exception;

	public void deleteWFDef(String flowid) throws Exception;

	public void copyWFDef(String flowid, UnikMap props) throws Exception;

	public Object getTaskList(Object msg, String org, String user) throws Exception;

	public void delDwtaskVars(String wfid) throws Exception;

	public void updateDealNum(String flowid, String nodeid, long mindealnum) throws Exception;

	public String getForeNodeId(String wfInstId, String nodeId) throws Exception;

	public String formatMessage(String s, Object obj);

	public void saveDwFlowInstTable(DwFlowInstTable dwFlowInstVo);

}