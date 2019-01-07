package com.sky.workflow.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.sky.workflow.model.DbBusiWfMapTable;
import com.sky.workflow.model.DwFlowHisTable;
import com.sky.workflow.model.DwFlowInstTable;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.model.DwFlowNodeTable;
import com.sky.workflow.model.DwTaskHisTable;
import com.sky.workflow.model.DwTaskRoundTable;
import com.sky.workflow.model.DwTaskTable;
import com.sky.workflow.model.DwTaskTableKey;
import com.sky.workflow.service.IDbBusiWfMapTableService;
import com.sky.workflow.service.IDwFlowHisTableService;
import com.sky.workflow.service.IDwFlowInstTableService;
import com.sky.workflow.service.IDwFlowMainTableService;
import com.sky.workflow.service.IDwFlowNodeTableService;
import com.sky.workflow.service.IDwTaskHisTableService;
import com.sky.workflow.service.IDwTaskRoundTableService;
import com.sky.workflow.service.IDwTaskTableService;
import com.sky.workflow.service.IDwTaskVarsTableService;
import com.sky.workflow.util.Errors;
import com.sky.workflow.util.JBDate;
import com.sky.workflow.util.NamedException;
import com.sky.workflow.util.StringUtils;
import com.sky.workflow.util.Strings;
import com.sky.workflow.util.UnikMap;

/**
 * <p>
 * Title: �������������ݿ⡢Ӧ��������صĴ�����̡�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: ������������������Ϣϵͳ���޹�˾
 * </p>
 * 
 * @author kangsj@jbbis.com.cn
 * @version 1.2 ƽ̨Ǩ�� , 2008-2-22 ����11:12:21
 */
@CacheConfig(cacheNames = "skyWorkflowCache")
@Service("WorkflowStorageImpl")
public class WorkflowStorageImpl implements IWorkflowStorage {

	/** ���̶���� **/
	@Resource(name = "DwFlowMainTableService")
	private IDwFlowMainTableService DwFlowMainTableService;
	@Resource(name = "DwFlowNodeTableService")
	private IDwFlowNodeTableService DwFlowNodeTableService;
	/** ����ʵ���� **/
	@Resource(name = "DwFlowInstTableService")
	private IDwFlowInstTableService DwFlowInstTableService;
	@Resource(name = "DwFlowHisTableService")
	private IDwFlowHisTableService DwFlowHisTableService;
	@Resource(name = "DbBusiWfMapTableService")
	private IDbBusiWfMapTableService DbBusiWfMapTableService;
	@Resource(name = "DwTaskTableService")
	private IDwTaskTableService DwTaskTableService;
	@Resource(name = "DwTaskHisTableService")
	private IDwTaskHisTableService DwTaskHisTableService;
	@Resource(name = "DwTaskRoundTableService")
	private IDwTaskRoundTableService DwTaskRoundTableService;
	@Resource(name = "DwTaskVarsTableService")
	private IDwTaskVarsTableService DwTaskVarsTableService;

	/**
	 * ������ת����������Ĳ�����Ϣ
	 */
	private UnikMap vars = new UnikMap();

	/**
	 * ��¼������Ա�Ƿ�������ʷ�б�,���dwtaskhis�в����¼�����ֵΪ'1',û�в��Ϊ'2'
	 */
	protected char hisisempty = '0';

	/**
	 * <p>
	 * ��ȡ�������̶�����Ϣ,�����ڱ�dwflowmain
	 * </p>
	 * 
	 * @param flowid �������̶�����
	 * @throws java.lang.Exception
	 */
	public DwFlowMainTable getWorkflowDefinition(String flowid) {

		// �ӻ�����ȡ��
		DwFlowMainTable dwflowmainVo = DwFlowMainTableService.getById(flowid);

		// �������̶���δ�ҵ�������ϵά����Ա��
		Errors.Assert(dwflowmainVo != null, getMsgInfo("WF_FlowDefNotFound"));

		return dwflowmainVo;
	}

	/**
	 * ��ȡ��������ʵ����Ϣ,�ӱ�dwflowinst��ȡ��,�������һ��,������ȥ��ȡ������,ֻ�Ե�ǰ������Ч��
	 * 
	 * @param wfid ����ʵ�����
	 * @return UnikMap wfInst
	 * @throws java.lang.Exception û���ҵ�����ʵ���׳�WF_FlowInstNotFound
	 */
	public DwFlowInstTable getWorkflowInstance(String wfid) throws Exception {

		DwFlowInstTable dwFlowInstVo = DwFlowInstTableService.getById(wfid);
		Errors.Assert(dwFlowInstVo != null, getMsgInfo("WF_FlowInstNotFound"));

		return dwFlowInstVo;
	}

	/**
	 * ��ȡ�ڵ���Ϣ,�ɸ��ݽڵ����ͺͽڵ�����ȡ��Ϣ
	 * 
	 * @param flowid   ���̱��
	 * @param nodeid   �ڵ���
	 * @param nodetype �ڵ�����
	 * @return
	 * @throws Exception
	 */
	public DwFlowNodeTable getNode(String flowid, String nodeid, String nodetype) throws Exception {

		Errors.Assert(flowid != null && flowid.length() != 0, getMsgInfo("WF_NeedwfDefId"));

		DwFlowNodeTable dfnt = null;
		if (nodeid != null && nodeid.length() != 0) {
			dfnt = DwFlowNodeTableService.getByKey(flowid, nodeid);
		} else if (nodetype != null && nodetype.length() != 0) {
			List<DwFlowNodeTable> listVo = DwFlowNodeTableService.selectByNodeType(flowid, nodetype);
			Errors.Assert(listVo != null && listVo.size() > 0, getMsgInfo("WF_FlowNodeNotFound"));
			dfnt = listVo.get(0);
		}
		Errors.Assert(dfnt != null, getMsgInfo("WF_FlowNodeNotFound"));

		return dfnt;
	}

	/**
	 * �õ��ڵ�ĸ�λ������Ϣ
	 * 
	 * @param var �б�����flowid��nodeid�ֶ���Ϣ,���򷵻�null
	 * @return UnikMap ����ֵbankid,postidset
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodePost(UnikMap var) throws Exception {
		/*
		 * <sql name="getAllDwFlowNodePost"> select bankid, postid, postbankid from
		 * dwFlowNodePost where flowid = @flowid and nodeid = @nodeid </sql>
		 */
		// if(var.getString("flowid").length()==0 || var.getString("nodeid").length() ==
		// 0)
		// return null;

		// �ӻ�����ȡ��
		String postAuthority = (String) var.get("postauthority");

		List<UnikMap> list = new ArrayList<>();

		if (postAuthority == null || postAuthority.equals("") || postAuthority.equalsIgnoreCase("null")) {
			// list = cacheTools.queryDwflownodepost(var.getString("flowid"),
			// var.getString("nodeid"));
		} else {
			// list = cacheTools.queryDwflownodepost(var.getString("flowid"),
			// var.getString("nodeid"), postAuthority);
		}

		StringBuffer bankid = new StringBuffer();
		StringBuffer postidset = new StringBuffer();
		StringBuffer postbankid = new StringBuffer();
		StringBuffer superbankscope = new StringBuffer();
		StringBuffer bindprodid = new StringBuffer();

		for (int i = 0; i < list.size(); i++) {
			UnikMap dl = list.get(i);
			bankid.append(dl.getString("bankid")).append("|");
			postidset.append(dl.getString("postid")).append("|");
			postbankid.append(dl.getString("postbankid")).append("|");
			superbankscope.append(dl.getString("superbankscope")).append("|");
			bindprodid.append(dl.getString("bindprodid")).append("|");
		}

		int bankidlen = bankid.length();
		int postidlen = postidset.length();
		int postbankidlen = postbankid.length();
		int superbankscopelen = superbankscope.length();
		int bindprodidlen = bindprodid.length();

		if (bankidlen > 1) {
			bankid = bankid.delete(bankidlen - 1, bankidlen);
		}
		if (postidlen > 1) {
			postidset = postidset.delete(postidlen - 1, postidlen);
		}
		if (postbankidlen > 1) {
			postbankid = postbankid.delete(postbankidlen - 1, postbankidlen);
		}
		if (superbankscopelen > 1) {
			superbankscope = superbankscope.delete(superbankscopelen - 1, superbankscopelen);
		}
		if (bindprodidlen > 1) {
			bindprodid = bindprodid.delete(bindprodidlen - 1, bindprodidlen);
		}
		UnikMap m = new UnikMap();
		m.put("bankid", bankid);
		m.put("postidset", postidset);
		m.put("postbankid", postbankid);
		m.put("superbankscope", superbankscope);
		m.put("bindprodid", bindprodid);
		return m;
	}

	/**
	 * �������̽ڵ����Ͷ�ȡ�ڵ���Ϣ��
	 * 
	 * @param wfDefId ���̱��
	 * @param type    �ڵ�����
	 * @return
	 * @throws java.lang.Exception
	 */
	public DwFlowNodeTable getNodeByType(String wfDefId, String type) throws Exception {
		return getNode(wfDefId, null, type);
	}

	/**
	 * �������̽ڵ��Ŷ�ȡ�ڵ���Ϣ
	 * 
	 * @param flowid ���̱��
	 * @param nodeid �ڵ���
	 * @throws java.lang.Exception
	 */
	public DwFlowNodeTable getNodeById(String flowid, String nodeid) throws Exception {
		return getNode(flowid, nodeid, null);
	}

	/**
	 * <p>
	 * ��������ʵ��,��dwflowinst���в���������ʵ������
	 * </p>
	 * 
	 * @param flowid           Ӧ�õ�flowid
	 * @param wfid             ����ʵ�����
	 * @param user             �û���ʶ(bankid/operid),
	 * @param wfState          ����״̬
	 * @param bizData          ���̱���
	 * @param busiOperateStyle ҵ����ʽ
	 * @throws java.lang.Exception
	 */
	public void createInstance(String flowid, String wfid, String user, String wfState, UnikMap bizData, String busiOperateStyle) throws Exception {

		String users[] = user.split("/");
		DwFlowMainTable dfmt = getWorkflowDefinition(flowid);

		// ������������Ϣ��ı����ò���ֵ�滻flowdesc
		String wfdesc = replaceFlowDesc(dfmt.getFlowdesc(), bizData);

		// ��֯���ݲ���dwflowinst����
		DwFlowInstTable dfit = new DwFlowInstTable();
		dfit.setFlowid(flowid);
		dfit.setFlowtype("");
		dfit.setWfid(wfid);
		dfit.setFlowname(dfmt.getFlowname());
		dfit.setFlowdesc(wfdesc);
		dfit.setBankid(users[0]);
		dfit.setOperid(users[1]);
		dfit.setLastchgdate(new Date());
		dfit.setCreattime(getWorkdateCurrTime());
		dfit.setLastchgtime(getWorkdateCurrTime());
		dfit.setWfstate(wfState);
		dfit.setTaskser(1L);

		// ���̽����̶�
		String instancyLevel = bizData.getString("dwflowinst.instancyLevel");
		instancyLevel = instancyLevel == null ? "1" : instancyLevel;
		dfit.setInstancylevel(instancyLevel);
		dfit.setExecsql(dfmt.getExecsql());
		dfit.setManusql(dfmt.getManusql());
		dfit.setUnitwfid(bizData.getString("unitwfid"));
		dfit.setBusioperatestyle(busiOperateStyle);
		// �����ݲ����dwflowinst��
		DwFlowInstTableService.save(dfit);
	}

	/**
	 * ȡ��ǰ����ʱ���ϵͳʱ����
	 * 
	 * @throws Exception
	 */
	public String getWorkdateCurrTime() throws Exception {
		return JBDate.getWorkDateCurrTime(getSysdate());
	}

	private String getSysdate() {
		try {
			return JBDate.formatDate(new Date(), "yyyyMMdd");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ���������������ñ��������滻
	 * 
	 * @param wfdesc ��������
	 * @param vars   ����
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String replaceFlowDesc(String wfdesc, UnikMap vars) throws Exception {
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			if (value.length() > 200)
				value = value.substring(0, 200) + "..."; // ���������Ҫ��ȡ

			wfdesc = StringUtils.replace(wfdesc, "{" + name.toLowerCase() + "}", value);
		}
		return wfdesc;
	}

	/**
	 * �޸����̻�������,������²��ɹ��������������,���õ���merge��ʽ�� nodeDescΪnull�򳤶�Ϊ�㣬�򲻸��±���ֱ�ӷ��ء�
	 * 
	 * @param wfInstId ������˳��ţ�Ҳ����Ӧ���е�wfid
	 * @param wfVars   ����������
	 * @param taskSer  �������
	 * @param wfDefId  ���������
	 * @param nodeId   �ڵ���
	 * @param nodeDesc �ڵ�������Ϣ
	 * 
	 * @throws Exception :java.lang.Exception
	 */
	@SuppressWarnings("rawtypes")
	public void updateWorkflowVariables(String wfInstId, UnikMap wfVars, int taskSer, String wfDefId, String nodeId, String nodeDesc) throws Exception {
		if (nodeDesc == null || nodeDesc.length() == 0) {
			return;
		}
		UnikMap m = new UnikMap();
		m.put("wfid", wfInstId);
		// m.put("taskser", 1);
		m.put("nodeid", nodeId);
		m.put("lastchgdate", getSysdate());
		m.put("lastchgtime", getWorkdateCurrTime());

		DataList dl = new DataList();// executeProcedure(getTpl("getAllDwTaskVars"), m);
		StringBuffer buf = new StringBuffer();

		/*
		 * Iterator its = wfVars.keySet().iterator(); while(its.hasNext()) { String key
		 * = (String)its.next(); String varValue = (String)wfVars.get(key);
		 * 
		 * }
		 */
		while (dl.next()) {

			String varName = dl.getString("varname");
			Iterator it = wfVars.keySet().iterator();

			while (it.hasNext()) {
				String key = (String) it.next();
				if (varName.equalsIgnoreCase(key)) {

					buf.append(varName).append(",");
				}
			}

		}

		// ɾ����ǰ�������ı������еı���
		if (buf.length() > 1) {
			buf.delete(buf.length() - 1, buf.length());

			UnikMap map = new UnikMap();
			map.putAll(m);
			map.put("varname", buf);
			// executeProcedure(getTpl("deleteDwtaskVars"), map);
			buf.delete(0, buf.length());
		}

		m.put("taskser", 1);

		/*
		 * log(DEBUG,"++++++++++++++++++++++++++++++++++++++tpl=" + tpl); for(Iterator
		 * it = wfVars.keySet().iterator(); it.hasNext();) { String key =
		 * String.valueOf(it.next()); String value = wfVars.getField(key);
		 * log(DEBUG,"++++++++++++++++++++++++++++++++++++++key="+value+"; value="+value
		 * ); }
		 */

		// executeProcedure(tpl,param);

	}

	/*
	 * public void updateWorkflowVariables(String wfInstId, UnikMap wfVars, int
	 * taskSer, String wfDefId, String nodeId, String nodeDesc) throws Exception {
	 * if (nodeDesc == null || nodeDesc.length() == 0){
	 * log(DEBUG,"WF_IN_updateWorkflowVariables: nodeDesc value is null so return");
	 * return; } UnikMap m = new UnikMap(); m.put("wfid",wfInstId);
	 * //m.put("taskser", taskSer); m.put("taskser", 1); m.put("nodeid", nodeId);
	 * System.out.println("------------------------------------"+wfInstId+"\t"+
	 * taskSer+"\t"+nodeId+"\t"+wfVars);
	 * 
	 * <sql name="getAllDwtaskVars"> select wfid, taskser, nodeid, varname, varvalue
	 * from dwTaskVars where wfid = @wfid #AND = @taskser #AND = @varname #if @order
	 * order by taskser asc #end </sql>
	 * 
	 * 
	 * DataList dl = executeProcedure(getTpl("getAllDwTaskVars"), m); StringBuffer
	 * buf = new StringBuffer(); while(dl.next()){
	 * buf.append(dl.getString("varname")).append(","); }
	 * System.out.println("================�õ���������������������������������"); //ɾ����ǰ�������ı������еı���
	 * if(buf.length() > 1){ buf.delete(buf.length()-1, buf.length());
	 * 
	 * <sql name="deleteDwtaskvars"> delete from dwtaskvars where wfid=@wfid and
	 * taskser = @number:taskser and varname in (@list:varname) </sql>
	 * 
	 * UnikMap map = new UnikMap(); map.putAll(m); map.put("varname", buf);
	 * executeProcedure(getTpl("deleteDwtaskVars"), map); buf.delete(0,
	 * buf.length()); } System.out.println("================ɾ����������������������������������");
	 * 
	 * <sql name="insertDwtaskvars"> insert into dwtaskvars( wfid, taskser, nodeid,
	 * varname, varvalue ) values(
	 * 
	 * @wfid,
	 * 
	 * @number:taskser,
	 * 
	 * @nodeid,
	 * 
	 * @varname,
	 * 
	 * @varvalue ) <sql>
	 * 
	 * 
	 * // DataContext exectx = new DataContext(); // exectx.set(NEWTRANSACTION); //
	 * exectx.table = "Dwtaskvars";
	 * 
	 * //wfVars.getString("") String tpl = getTpl("insertDwtaskvars");
	 * System.out.println("tpl=" + tpl); AcParams param = new AcParams(m,wfVars);
	 * 
	 * executeProcedure(tpl,param);
	 * 
	 * }
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String[] selectHistoryUsers(UnikMap map, boolean flag, boolean f) throws Exception {
		String user[] = null;
		// ����Ƿ�����Щ�ֶ���Ϣ

		String fields[] = { "org", "post", "local", "wfid", "nodeid", "postbankid" };
		Errors.Assert(!WorkFlowFunc.checkField(map, fields), "workflow/MissFields");
		String org = map.getString("org");
		String post = map.getString("post");
		String SuperBankScope = map.getString("SuperBankScope");
		String local[] = map.getString("local").split("/");
		String localBankid = local[0];
		String localOperid = local.length == 2 ? local[1] : null;
		String wfid = map.getString("wfid");
		String nodeid = map.getString("nodeid");
		String bindprodid = map.getString("bindprodid");

		// ���̷�����,ȥ��ʷ�б����ҳ����������˷��ؾͿ�����
		if (CommonConst.WF_POSTID_WFINITUSER.equals(org)) {

			UnikMap params = new UnikMap();
			params.put("wfid", wfid);
			params.put("nodetype", CommonConst.WF_NODETYPE_START);
			/*
			 * //getTaskInitUser select a.*, d.username from dwtaskhis a, dwflownode b,
			 * dwflowinst c, cmuser d where a.wfid = c.wfid and c.flowid = b.flowid and
			 * a.operid = d.userid and a.wfid = @wfid and b.nodetype= @nodetype
			 */
			SingleResult sr = new SingleResult();// querySingle(getTpl("getTaskInitUser"), params, null);
			Errors.Assert(sr != null, "workflow/StartNodeTaskNotFound");
			StringBuffer buf = new StringBuffer();
			buf.append(sr.getField("bankid")).append("/").append(sr.getField("operid"));
			if (flag)
				buf.append("|").append(sr.getField("username"));
			String[] s = { buf.toString() };

			return s;
		}

		/**
		 * =========================��������������һ���ȿ��˽ڵ���û��Ƿ���Ҫ����ʷ�б��н��в���==============================
		 **/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag"), flag);
			// �鵽�û���Ϊ����Ϊ��ʷ�û� 2009-03-27 kangshangjun
			if (user != null) {
				map.put("usrhisflag", "true");
				return user;
			} else {
				map.put("usrhisflag", "false");
			}
		}

		UnikMap dp = new UnikMap();
		/*
		 * ��һ�����˽ڵ��Ƿ񱻴����,��������û�б�Ҫȥ��λ��Ϣ��ȥ���һ�����Ϣ��, ����ǿ�ʼ�ڵ�,��ֱ�ӷ��ػ���/�û���Ϣ
		 */
		/*
		 * <sql name="getUserAndNodeType"> select a.bankid, a.operid, b.nodetype from
		 * dwtaskhis a, dwflownode b, dwflowinst c where a.wfid = c.wfid and a.nodeid =
		 * b.nodeid and b.flowid = c.flowid and a.wfid= @wfid and a.nodeid= @nodeid
		 * <sql/>
		 */

		// �˲�ѯʵ���Ƕ�ʵ�,ֻ������SingleResult������Ϊ���м�¼��bankid��nodetype��һ����
		SingleResult sr = new SingleResult();// querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null) {
			// org = sr.getField("bankid"); //liuxj 0715
			// ����ǿ�ʼ�ڵ����ֱ�ӷ��ز�ѯ�������û���Ϣ,��Ϊ��ʼ�ڵ�ֻ��һ���˴���
			if (CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))) {
				org = (String) sr.getField("bankid"); // liuxj

				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				// log(DEBUG,"WF_NODETYPE_START user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		// �ڶ���,����org���һ�����Ϣ,���û��org��post��˵��û�������û���Ϣ
		Errors.Assert(org.length() != 0 || post.length() != 0, formatMessage("PostIdSetNotFound", map.getString("nodeid")));

		// ����ȡ���Ļ�����Ϣȥ�����û���Ϣ
		// log(INFO,"����ȡ���Ļ�����Ϣȥ�����û���Ϣ====post=" + post);
		LinkedList list = new LinkedList();
		// String postid[] = post.split(",");

		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");

		for (int j = 0; j < postid.length; j++) {
			// ====================================================================��ʼ�жϻ���================================
			String bankid = null;
			String superbankids = "";

			String submitbankid = localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				// ������
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

				// �ж�ֻ�ǲ���ֱ���ϼ������������ȫ���ϼ��������

				// �ϼ�����
				/*
				 * <sql name="getAllCmbank"> select
				 *
				 * from cmbank where bankid = @bankid #AND = @bankcode </sql>
				 */

				dp.clear();
				dp.put("bankid", submitbankid);

				// liuxj
				sr = new SingleResult();// querySingle(getTpl("getAllCmbank"),dp, null);

				/*
				 * Boolean bankdyprod=!"99".equals(bindprodids[j]); if(bankdyprod){//�ڵ���������ҵ��Χ
				 * dp.put("bindprodid",bindprodids[j]); sr =
				 * querySingle(getTpl("getAllCmbankbyprod"),dp, null); }else{ sr =
				 * querySingle(getTpl("getAllCmbank"),dp, null); }
				 */
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				// �ж���ȡ�ϼ��������������ϼ��������
				String scope = SuperBankScopes[j];
				if (CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

					bankid = sr.getString("superbankid");
					// String superbankids = "";
					if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList;

						// liuxj
						superBankList = new DataList();// executeProcedure(getTpl("getallsuperbank"), superUM);
						/*
						 * if(bankdyprod){
						 * superBankList=executeProcedure(getTpl("getallsuperbankbyprod"), superUM);
						 * }else{ superBankList=executeProcedure(getTpl("getallsuperbank"), superUM); }
						 */
						while (superBankList.next()) {
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					// bankid = superbankids;
				} else {
					bankid = sr.getString("superapprbankid");
					if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList;

						// liuxj
						superBankList = new DataList();// executeProcedure(getTpl("getallsuperapprbank"), superUM);
						/*
						 * if(bankdyprod){
						 * superBankList=executeProcedure(getTpl("getallsuperapprbankbyprod"), superUM);
						 * }else{ superBankList=executeProcedure(getTpl("getallsuperapprbank"),
						 * superUM); }
						 */
						while (superBankList.next()) {
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					// bankid = superbankids;
				}
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {// ���̷������

				if (bankids[j].length() == 1) {

					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 * <sql name="getAllDwtaskhis"> select
					 *
					 * from dwtaskhis where wfid = @wfid #AND = @nodeid order by taskser </sql>
					 */

					sr = new SingleResult();// querySingle(getTpl("getAllDwtaskhis"), dp, null);
					if (sr != null) {
						// liuxj 20141216 �������̷���������ƶ���ɫ�������û�

						dp.clear();
						dp.put("bankid", sr.getString("bankid")); // ���̷������
						dp.put("operid", localOperid);
						dp.put("postidset", postid[j]);
						DataList dl = new DataList();// executeProcedure(tpl, dp);

						while (dl.next()) {
							String value = dl.getString("bankid") + "/" + dl.getString("userid");
							// String value = dl.getString("userid");
							if (flag) {
								value += "|" + dl.getString("username");
							}
							list.add(list.size(), value);
						}
						Errors.Assert(list.size() > 0, getMsgInfo("WF_NotFoundUser"));
						if (list.size() > 0) {
							user = (String[]) list.toArray(new String[0]);
						}
						/*
						 * user[0] = sr.getString("bankid") + "/" + sr.getString("operid");
						 */

					}
					/*
					 * if (sr != null) { user[0] = sr.getString("bankid") + "/" +
					 * sr.getString("operid"); }
					 */
					return user;
				} else {
					Errors.Assert(bankids[j].length() == 2, getMsgInfo("WF_BankIdError"));
				}

				int level = 0;// JBMath.getInt(bankids[j].substring(1));
				String banks[] = null;

				dp.clear();
				dp.put("wfid", wfid);

				sr = new SingleResult();// (getTpl("getAllDwtaskhis"), dp, null);

				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));

				String bankcode = sr.getString("bankcode");

				banks = new String[bankcode.length() / 3];
				banks[bankcode.length() / 3 - 1] = bankcode;
				for (int i = 3; i < bankcode.length(); i += 3) {
					banks[i / 3] = bankcode.substring(0, i);
				}

				if (level >= banks.length)
					throw new NamedException(getMsgInfo("WF_NotFoundBank"));

				dp.clear();
				dp.put("bankcode", banks[level]);

				sr = new SingleResult();// querySingle(getTpl("getAllCmbank"), dp, null);

				// System.out.println("============================sr = querySingle(getTpl(");

				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));

				bankid = sr.getString("bankid");
			} else if (CommonConst.WF_POSTID_ASSIGN.equals(bankids[j])) {
				bankid = map.getString("postbankid");
			} else {
				bankid = bankids[j];
			}

			// ===========================================================�����жϻ���===========================

			// �ָ����̽�ɫ������֯�ɶ������

			/*
			 * <sql name="getUseridFromCmuser"> select userid, username from cmuser where
			 * bankid = @bankid #AND = @userLoginstate and userid != @operid and (PostIdSet
			 * like @'%,{PostIdSet},%' or PostIdSet like @'{PostIdSet},%' or PostIdSet
			 * like @'%,{PostIdSet}' or PostIdSet = @PostIdSet) </sql>
			 */

			dp.clear();

			dp.put("operid", localOperid);
			// dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
			dp.put("postidset", postid[j]);

			// System.out.println("============================executeProcedure(getTpl");
			// �����޸ĳ�������Լ���������ԭ���������ȡ��¼�û��Լ�����Ϣ liuxj
			DataList dl = new DataList();
			if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {
				if (SuperBankScopes[j].equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
					dp.put("bankid", superbankids);
				} else {
					dp.put("bankid", bankid);
				}
			} else {
				dp.put("bankid", bankid);
			}

			dl = new DataList();// executeProcedure(tpl, dp);

			while (dl.next()) {
				String value = dl.getString("bankid") + "/" + dl.getString("userid");
				// String value = dl.getString("userid");
				if (flag) {
					value += "|" + dl.getString("username");
				}
				list.add(list.size(), value);
			}
		}
		Errors.Assert(list.size() > 0, getMsgInfo("WF_NotFoundUser"));
		if (list.size() > 0) {
			user = (String[]) list.toArray(new String[0]);
		}

		/*
		 * for(int i=0;i<user.length;i++) {
		 * System.out.println("============================user=" + user[i]); }
		 */
		return user;
	}

	/**
	 * �����û���Ϣ,�����û�����,����ǿ�ʼ�ڵ��ֱ�ӷ��ص�һ�ν��д����ҵ����Ա��
	 * Ŀǰ����������Բ�ѯ���������ϼ�����������ϼ��������������̷��������ָ��������
	 * 
	 * @param map  map����Ҫ��org,post,local,wfid,nodeid,postbankid��Щ�ֶ���Ϣ
	 *             ��Щ��������ͨ������getNodePost�����õ�,�����ֶβ���ȱ��,hisflag�ǿ� ѡ�ģ������Ƿ����ʷ�б��в�ѯ������Ա��
	 * @param flag ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *             /operid
	 * @param f    ����hisflag�������Ƿ���Ч,true:��Ч,false:ʧЧ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String[] selectUsers(UnikMap map, boolean flag, boolean f) throws Exception {
		String user[] = null;
		// ����Ƿ�����Щ�ֶ���Ϣ

		String fields[] = { "org", "post", "local", "wfid", "nodeid", "postbankid" };
		Errors.Assert(!WorkFlowFunc.checkField(map, fields), "workflow/MissFields");
		String org = map.getString("org"); // * ��ʾ���̷�����
		String post = map.getString("post");
		String SuperBankScope = map.getString("SuperBankScope");
		String local[] = map.getString("local").split("/");
		String localBankid = local[0];
		String localOperid = local.length == 2 ? local[1] : null;
		String wfid = map.getString("wfid");
		String nodeid = map.getString("nodeid");
		String bindprodid = map.getString("bindprodid");

		// ���̷�����,ȥ��ʷ�б����ҳ����������˷��ؾͿ�����
		if (CommonConst.WF_POSTID_WFINITUSER.equals(org)) {

			UnikMap params = new UnikMap();
			params.put("wfid", wfid);
			params.put("nodetype", CommonConst.WF_NODETYPE_START);
			/*
			 * //getTaskInitUser select a.*, d.username from dwtaskhis a, dwflownode b,
			 * dwflowinst c, cmuser d where a.wfid = c.wfid and c.flowid = b.flowid and
			 * a.operid = d.userid and a.wfid = @wfid and b.nodetype= @nodetype
			 */
			SingleResult sr = new SingleResult();// querySingle(getTpl("getTaskInitUser"), params, null);
			Errors.Assert(sr != null, "workflow/StartNodeTaskNotFound");
			StringBuffer buf = new StringBuffer();
			buf.append(sr.getField("bankid")).append("/").append(sr.getField("operid"));
			if (flag)
				buf.append("|").append(sr.getField("username"));
			String[] s = { buf.toString() };

			return s;
		}

		/**
		 * =========================��������������һ���ȿ��˽ڵ���û��Ƿ���Ҫ����ʷ�б��н��в���==============================
		 **/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag"), flag);
			// �鵽�û���Ϊ����Ϊ��ʷ�û� 2009-03-27 kangshangjun
			if (user != null) {
				map.put("usrhisflag", "true");
				return user;
			} else {
				map.put("usrhisflag", "false");
			}
		}

		UnikMap dp = new UnikMap();
		/*
		 * ��һ�����˽ڵ��Ƿ񱻴����,��������û�б�Ҫȥ��λ��Ϣ��ȥ���һ�����Ϣ��, ����ǿ�ʼ�ڵ�,��ֱ�ӷ��ػ���/�û���Ϣ
		 */
		/*
		 * <sql name="getUserAndNodeType"> select a.bankid, a.operid, b.nodetype from
		 * dwtaskhis a, dwflownode b, dwflowinst c where a.wfid = c.wfid and a.nodeid =
		 * b.nodeid and b.flowid = c.flowid and a.wfid= @wfid and a.nodeid= @nodeid
		 * <sql/>
		 */

		// �˲�ѯʵ���Ƕ�ʵ�,ֻ������SingleResult������Ϊ���м�¼��bankid��nodetype��һ����
		SingleResult sr = new SingleResult();// querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null) {
			// org = sr.getField("bankid"); //liuxj 0715
			// ����ǿ�ʼ�ڵ����ֱ�ӷ��ز�ѯ�������û���Ϣ,��Ϊ��ʼ�ڵ�ֻ��һ���˴���
			if (CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))) {
				org = (String) sr.getField("bankid"); // liuxj

				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				// log(DEBUG,"WF_NODETYPE_START user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		// �ڶ���,����org���һ�����Ϣ,���û��org��post��˵��û�������û���Ϣ
		Errors.Assert(org.length() != 0 || post.length() != 0, formatMessage("PostIdSetNotFound", map.getString("nodeid")));

		// ����ȡ���Ļ�����Ϣȥ�����û���Ϣ
		// log(INFO,"����ȡ���Ļ�����Ϣȥ�����û���Ϣ====post=" + post);
		LinkedList list = new LinkedList();
		// String postid[] = post.split(",");

		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");

		for (int j = 0; j < postid.length; j++) {
			// ====================================================================��ʼ�жϻ���================================
			String bankid = null;
			String superbankids = "";

			String submitbankid = localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				// ������
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

				// �ж�ֻ�ǲ���ֱ���ϼ������������ȫ���ϼ��������

				// �ϼ�����
				/*
				 * <sql name="getAllCmbank"> select
				 *
				 * from cmbank where bankid = @bankid #AND = @bankcode </sql>
				 */

				dp.clear();
				dp.put("bankid", submitbankid);

				// liuxj
				sr = new SingleResult();// querySingle(getTpl("getAllCmbank"),dp, null);

				/*
				 * Boolean bankdyprod=!"99".equals(bindprodids[j]); if(bankdyprod){//�ڵ���������ҵ��Χ
				 * dp.put("bindprodid",bindprodids[j]); sr =
				 * querySingle(getTpl("getAllCmbankbyprod"),dp, null); }else{ sr =
				 * querySingle(getTpl("getAllCmbank"),dp, null); }
				 */
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				// �ж���ȡ�ϼ��������������ϼ��������
				String scope = SuperBankScopes[j];
				if (CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

					bankid = sr.getString("superbankid");
					// String superbankids = "";
					if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList;

						// liuxj
						superBankList = executeProcedure(getTpl("getallsuperbank"), superUM);
						/*
						 * if(bankdyprod){
						 * superBankList=executeProcedure(getTpl("getallsuperbankbyprod"), superUM);
						 * }else{ superBankList=executeProcedure(getTpl("getallsuperbank"), superUM); }
						 */
						while (superBankList.next()) {
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					// bankid = superbankids;
				} else {
					bankid = sr.getString("superapprbankid");
					if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList;

						// liuxj
						superBankList = executeProcedure(getTpl("getallsuperapprbank"), superUM);
						/*
						 * if(bankdyprod){
						 * superBankList=executeProcedure(getTpl("getallsuperapprbankbyprod"), superUM);
						 * }else{ superBankList=executeProcedure(getTpl("getallsuperapprbank"),
						 * superUM); }
						 */
						while (superBankList.next()) {
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					// bankid = superbankids;
				}
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {// ���̷������

				// Errors.Assert(bankids[j].length() == 1, getMsgInfo("WF_BankIdError"));

				// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
				user = new String[1];
				dp.clear();
				dp.put("wfid", wfid);
				/*
				 * <sql name="getAllDwtaskhis"> select
				 *
				 * from dwtaskhis where wfid = @wfid #AND = @nodeid order by taskser </sql>
				 */
				sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);

				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				bankid = sr.getString("bankid");

				/*
				 * if (bankids[j].length() == 1) {
				 * 
				 * // �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������ user = new String[1]; dp.clear(); dp.put("wfid",
				 * wfid);
				 * 
				 * <sql name="getAllDwtaskhis"> select
				 *
				 * from dwtaskhis where wfid = @wfid #AND = @nodeid order by taskser </sql>
				 * 
				 * sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				 * 
				 * if (sr != null) { //liuxj 20141216 �������̷���������ƶ���ɫ�������û�
				 * 
				 * dp.clear(); dp.put("bankid", sr.getString("bankid")); //���̷������
				 * dp.put("operid", localOperid); //dp.put("userLoginstate",
				 * CommonConst.WF_USERLOGON_IN); dp.put("postidset", postid[j]); String tpl =
				 * getTpl("getUseridFromCmuser"); DataList dl = executeProcedure(tpl, dp);
				 * 
				 * while (dl.next()) { String value = dl.getString("bankid") + "/" +
				 * dl.getString("userid"); //String value = dl.getString("userid"); if (flag) {
				 * value += "|" + dl.getString("username"); } list.add(list.size(),value); }
				 * Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser")); if(list.size()
				 * > 0){ user = (String[])list.toArray(new String[0]); } user[0] =
				 * sr.getString("bankid") + "/" + sr.getString("operid");
				 * 
				 * }
				 * 
				 * return user; } else{ Errors.Assert(bankids[j].length() == 2,
				 * getMsgInfo("WF_BankIdError")); }
				 */
				/*
				 * int level = JBMath.getInt(bankids[j].substring(1)); String banks[] = null;
				 * 
				 * dp.clear(); dp.put("wfid", wfid);
				 * 
				 * sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				 * 
				 * Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				 * 
				 * String bankcode = sr.getString("bankcode");
				 * 
				 * banks = new String[bankcode.length() / 3]; banks[bankcode.length() / 3 - 1] =
				 * bankcode; for (int i = 3; i < bankcode.length(); i += 3) { banks[i / 3] =
				 * bankcode.substring(0, i); }
				 * 
				 * if (level >= banks.length) throw new
				 * NamedException(getMsgInfo("WF_NotFoundBank"));
				 * 
				 * dp.clear(); dp.put("bankcode", banks[level]);
				 * 
				 * sr = querySingle(getTpl("getAllCmbank"), dp, null);
				 * 
				 * //System.out.println("============================sr = querySingle(getTpl(");
				 * 
				 * Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				 * 
				 * bankid = sr.getString("bankid");
				 */
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)) {// ���̷�����������ϼ��������� 20141227

				if (bankids[j].length() == 1) {
					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 * <sql name="getAllDwtaskhis"> select * from dwtaskhis where wfid = @wfid #AND
					 * = @nodeid order by taskser </sql>
					 */
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);

					if (sr != null) {
						// �жϻ�ȡֱ���ϼ������������������ϼ���������
						String scope = SuperBankScopes[j];
						if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) // �����ϼ���������
						{
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); // ���̷������
							DataList superBankList;
							superBankList = executeProcedure(getTpl("getallsuperapprbank"), superUM);

							while (superBankList.next()) {
								// superbankids += superbankids.equals("") ? "'" +
								// superBankList.getString("bankid") + "'" : ",'" +
								// superBankList.getString("bankid") + "'";
								superbankids += superbankids.equals("") ? superBankList.getString("bankid") : "," + superBankList.getString("bankid");
							}
							if (!superbankids.equals("")) {
								superbankids += "," + sr.getString("bankid");
							}
						} else // �ϼ���������
						{
							// �õ����̷�����������ϼ�����
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); // ���̷������
							DataList superBankList;
							superBankList = executeProcedure(getTpl("getsuperapprbank"), superUM);

							while (superBankList.next()) {
								// superbankids += superbankids.equals("") ? "'" +
								// superBankList.getString("bankid") + "'" : ",'" +
								// superBankList.getString("bankid") + "'";
								superbankids += superbankids.equals("") ? superBankList.getString("superapprbankid") : "," + superBankList.getString("superapprbankid");
							}
							if (!superbankids.equals("")) // ���뷢�����
							{
								superbankids += "," + sr.getString("bankid");
							}
						}

						bankid = superbankids;

						// �������̷��������ָ����ɫ�������û� liuxj 20141216
						/*
						 * dp.clear(); dp.put("bankid", superbankids); //���̷�����������ϼ��������� dp.put("operid",
						 * localOperid); //dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
						 * dp.put("postidset", postid[j]); String tpl = getTpl("getuserininitsuperorg");
						 * //�õ����̷�����������ϼ����������������û� DataList dl = executeProcedure(tpl, dp); while
						 * (dl.next()){ String value = dl.getString("bankid") + "/" +
						 * dl.getString("userid"); if (flag){ value += "|" + dl.getString("username"); }
						 * list.add(list.size(),value); } Errors.Assert(list.size() >0 ,
						 * getMsgInfo("WF_NotFoundUser")); if(list.size() > 0){ user =
						 * (String[])list.toArray(new String[0]); }
						 */
					}

					// return user;
				} else {
					Errors.Assert(bankids[j].length() == 2, getMsgInfo("WF_BankIdError"));
				}

				/*
				 * int level = JBMath.getInt(bankids[j].substring(1)); String banks[] = null;
				 * 
				 * dp.clear(); dp.put("wfid", wfid);
				 * 
				 * sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				 * 
				 * Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				 * 
				 * String bankcode = sr.getString("bankcode");
				 * 
				 * banks = new String[bankcode.length() / 3]; banks[bankcode.length() / 3 - 1] =
				 * bankcode; for (int i = 3; i < bankcode.length(); i += 3) { banks[i / 3] =
				 * bankcode.substring(0, i); }
				 * 
				 * if (level >= banks.length) throw new
				 * NamedException(getMsgInfo("WF_NotFoundBank"));
				 * 
				 * dp.clear(); dp.put("bankcode", banks[level]);
				 * 
				 * sr = querySingle(getTpl("getAllCmbank"), dp, null);
				 * 
				 * //System.out.println("============================sr = querySingle(getTpl(");
				 * 
				 * Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				 * 
				 * bankid = sr.getString("bankid");
				 */

			} else if (CommonConst.WF_POSTID_ASSIGN.equals(bankids[j])) {
				bankid = map.getString("postbankid");
			} else {
				bankid = bankids[j];
			}

			// ===========================================================�����жϻ���===========================

			// �ָ����̽�ɫ������֯�ɶ������

			/*
			 * <sql name="getUseridFromCmuser"> select userid, username from cmuser where
			 * bankid = @bankid #AND = @userLoginstate and userid != @operid and (PostIdSet
			 * like @'%,{PostIdSet},%' or PostIdSet like @'{PostIdSet},%' or PostIdSet
			 * like @'%,{PostIdSet}' or PostIdSet = @PostIdSet) </sql>
			 */

			dp.clear();

			dp.put("operid", localOperid);
			// dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
			dp.put("postidset", postid[j]);

			// System.out.println("============================executeProcedure(getTpl");
			// �����޸ĳ�������Լ���������ԭ���������ȡ��¼�û��Լ�����Ϣ liuxj
			DataList dl = new DataList();
			String tpl = "";
			if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {
				if (SuperBankScopes[j].equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) {
					dp.put("bankid", superbankids);
					tpl = getTpl("getallsuperuserid");
				} else {
					dp.put("bankid", bankid);
					tpl = getTpl("getUseridFromCmuser");
				}
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)) {// ���̷�����������ϼ��������� 20141227
				dp.put("bankid", bankid); // ���̷�����������ϼ���������
				tpl = getTpl("getuserininitsuperorg"); // �õ����̷�����������ϼ����������������û�
			} else {
				dp.put("bankid", bankid);
				tpl = getTpl("getUseridFromCmuser");
			}

			dl = executeProcedure(tpl, dp);

			while (dl.next()) {
				String value = dl.getString("bankid") + "/" + dl.getString("userid");
				// String value = dl.getString("userid");
				if (flag) {
					value += "|" + dl.getString("username");
				}
				boolean isExist = checkExist(list, value);
				if (isExist == false)
					list.add(list.size(), value);
			}
		}
		Errors.Assert(list.size() > 0, getMsgInfo("WF_NotFoundUser"));
		if (list.size() > 0) {
			user = (String[]) list.toArray(new String[0]);
		}

		/*
		 * for(int i=0;i<user.length;i++) {
		 * System.out.println("============================user=" + user[i]); }
		 */
		return user;
	}

	private SingleResult querySingle(String tpl, UnikMap dp, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	private DataList executeProcedure(String tpl, UnikMap superUM) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ����Ƿ����ظ��û�
	 * 
	 * @param list
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean checkExist(LinkedList list, String value) {
		boolean exist = false;
		for (int i = 0; i < list.size(); i++) {
			String v = (String) list.get(i);
			if (v.equals(value)) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	/**
	 * ��������ȥ����,������õ���ԭ·����ȥ,��ȥdwtaskhis�����ҳ��Ѿ����������Ա���з��䡣
	 * ����˽ڵ㲻�ǵ�һ�δ������Ȳ��Ҵ˽ڵ��Ƿ���Ҫ������ϴδ��������Ա���д��������
	 * �����ʷ���в���ϴδ�����Ա������������䣬��������򰴷�����Խ��з��䡣
	 * 
	 * @see #selectUsers
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap map, boolean flag) throws Exception {
		return selectUsers(map, flag, true);
	}

	/**
	 * ���ݻ����ź͸�λ��Ų�ѯ�û��б�,Ŀǰֻ����һ����λ �˴����ϼ�������ָ:ȡ�ϼ�����������Ϣ,�����ϼ��������
	 * 
	 * @param org    ��ǰ�ڵ������
	 * @param post   ��λ
	 * @param local  ����/�û�
	 * @param wfid   ���̱��,
	 * @param nodeId �ڵ���
	 * @return String[] �û���Ϣ����bankid/userid|username
	 * @deprecated As of JDK version 1.1,
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(String org, String post, String local, String bindprodid, String wfid, String nodeId) throws Exception {
		return selectUsers(org, post, local, bindprodid, wfid, nodeId, false);
	}

	/**
	 * map����Ҫ��org,post,local,wfid,nodeid,postbankid�Ȳ���
	 * 
	 * @deprecated As of JBPortal3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(String org, String post, String local, String bindprodid, String wfid, String nodeId, boolean flag) throws Exception {
		UnikMap m = new UnikMap();
		m.put("org", org);
		m.put("post", post);
		m.put("local", local);
		m.put("wfid", wfid);
		m.put("nodeid", nodeId);
		m.put("bindprodid", bindprodid);
		return selectUsers(m, flag);
	}

	/**
	 * @see #selectTaskUsers(String, String, boolean)
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId) throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false);
	}

	public String[] selectTaskUsers(String wfInstId, String nodeId, int tasktype) throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false, tasktype);
	}

	public String[] selectTaskViewUsers(String wfInstId, String nodeId) throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false, 1);
	}

	/**
	 * ����ʷ�б��в��ҷ��ϸ�λ���û���
	 * 
	 * @param wfInstId ���̱��
	 * @param nodeId   �ڵ���
	 * @param flag     ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *                 /operid
	 * @param tasktype �������ͣ�1���鿴�����û� 2�����������û�
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId, String userloginstate, boolean flag, int tasktype) throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("nodeid", nodeId);
		dp.put("userloginstate", userloginstate);
		dp.put("tasktype", tasktype);

		/*
		 * <sql name="getDistinctDwtaskhis"> select distinct a.bankid, a.operid,
		 * b.username from dwtaskhis a, cmuser b where a.operid = b.userid and wfid
		 * = @wfid #AND = @nodeid #AND = @userloginstate #AND = @tasktype </sql>
		 */

		DataList dl = executeProcedure(getTpl("getDistinctDwtaskhis"), dp);

		int count = dl.countRows();
		// û���ҵ����ϸø�λ���û����������̶����е�'���̽ڵ��λ����'!
		if (tasktype == 2) // ֻ�Դ����û���������
			Errors.Assert(count > 0, getMsgInfo("WF_NotFoundUser"));

		String user[] = new String[count];
		StringBuffer buf = new StringBuffer();
		int index = 0;
		while (dl.next()) {
			buf.append(dl.getString("bankid")).append("/");
			buf.append(dl.getString("operid"));
			if (flag) {
				buf.append("|").append(dl.getString("username"));
			}

			user[index] = buf.toString();
			buf.delete(0, buf.length());
			index += 1;
		}
		return user;
	}

	/**
	 * ����ʷ�б��в��ҷ��ϸ�λ���û���
	 * 
	 * @param wfInstId   ���̱��
	 * @param nodeId     �ڵ���
	 * @param flag       ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *                   /operid
	 * @param intHistory -1 �����ձ��ֶβ�ѯ��0����ǰ�û� 1����ʷ�����û�
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId, String userloginstate, boolean flag) throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("nodeid", nodeId);
		dp.put("userloginstate", userloginstate);

		/*
		 * <sql name="getDistinctDwtaskhis"> select distinct a.bankid, a.operid,
		 * b.username from dwtaskhis a, cmuser b where a.operid = b.userid and wfid
		 * = @wfid #AND = @nodeid #AND = @userloginstate #AND = @tasktype </sql>
		 */

		DataList dl = executeProcedure(getTpl("getDistinctDwtaskhis"), dp);

		int count = dl.countRows();
		// û���ҵ����ϸø�λ���û����������̶����е�'���̽ڵ��λ����'!
		Errors.Assert(count > 0, getMsgInfo("WF_NotFoundUser"));

		String user[] = new String[count];
		StringBuffer buf = new StringBuffer();
		int index = 0;
		while (dl.next()) {
			buf.append(dl.getString("bankid")).append("/");
			buf.append(dl.getString("operid"));
			if (flag) {
				buf.append("|").append(dl.getString("username"));
			}
			user[index] = buf.toString();
			buf.delete(0, buf.length());
			index += 1;
		}
		return user;
	}

	/**
	 * ���������������е�hisFlag�ֶ����жϣ��Ƿ����ʷ�б��в�����������Ա��
	 * 
	 * @param wfid    ���̱��
	 * @param nodeid  �ڵ���
	 * @param hisFlag �Ƿ������ʷ��Ա��ѯ
	 * @param flag    �Ƿ���ʾ�û�����
	 * @return ��������Ա�б�,���hisFlag=0��hisFlag='1'���Ǵ�������г��쳣�ˣ����᷵��null
	 * @throws Exception
	 */
	public String[] selectHistoryUser(String wfid, String nodeid, String hisFlag, boolean flag) throws Exception {
		String[] users = null;
		if (CommonConst.WF_HISUSER_ON.equals(hisFlag)) { // ����ʷ�б��в�ѯ������Ա
			try {
				users = selectTaskUsers(wfid, nodeid, "1", flag);
				hisisempty = '1';
			} catch (Exception e) { // ���쳣99%��û�в�ѯ����¼�����Բ���Ҫ����
				hisisempty = '2';
				// log(DEBUG, "cn.com.jbbis.jbportal.WrokflowStorageImpl.selectHistoryUser(): "
				// + e);
			}
		} else {
			hisisempty = '3';
		}
		return users;
	}

	/**
	 * ��ȡĳ�������µ�����δ��������
	 * 
	 * @param wfInstId ���̱��
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public DataList getTask(String wfInstId) throws Exception {
		/*
		 * <sql name="getAllDwTask"> select wfid, taskser, bankid, operid, recetime,
		 * dealtime, nodeid, nodename, exectrancode, submtrancode, looktrancode,
		 * taskdesc from dwTask where wfid = @wfid # AND = @bankid # AND = @operid
		 * </sql>
		 */

		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);

		DataList dl = executeProcedure(getTpl("getAllDwTask"), dp);

		// System.out.println(dl!=null ? dl.countRows() + "��" : "0��");
		return dl;
	}

	/**
	 * ��ȡ�����б�,��dwtask�е�δ�������ݶ�ȡ����
	 * 
	 * @param wfid ���̱��
	 * @param user �û�
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public DwTaskTable getTask(String wfid, String user) throws Exception {

		String bankid = user.substring(0, user.indexOf("/"));
		String operid = user.substring(user.indexOf("/") + 1);

		DwTaskTable dwTaskVo = DwTaskTableService.getByWfidUser(wfid, bankid, operid);

		String msg[] = new String[2];
		msg[0] = bankid;
		msg[1] = operid;

		Errors.Assert(dwTaskVo != null, formatMessage("WF_TaskNotFound", msg));

		return dwTaskVo;
	}

	/**
	 * ��ȡ�����б�,��dwtask�е�δ������һ���ݶ�ȡ����
	 * 
	 * @param wfInstId ���̱��
	 * @param user     �û�
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public UnikMap getAnyOneTask(String wfInstId) throws Exception {
		/*
		 * <sql name="getAllDwTask"> select wfid, taskser, bankid, operid, recetime,
		 * dealtime, nodeid, nodename, exectrancode, submtrancode, looktrancode,
		 * taskdesc from dwTask where wfid = @wfid # AND = @bankid # AND = @operid
		 * </sql>
		 */

		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);

		// SingleResult sr = querySingle(getTpl("getAllDwTask"), dp,null);

		return null;// (UnikMap)sr;
	}

	/**
	 * �����µ������б�,����Ҫ���������ŵ�dwtask����
	 * 
	 * @param currentNode �ڵ���Ϣ
	 * @param wfid        ʵ�����
	 * @param taskSerial  ���
	 * @param users       �����û�
	 * @param viewUsers   �鿴�û�
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int newTask(DwFlowNodeTable currentNode, String wfid, int taskSerial, String[] users, String[] viewUsers, UnikMap bizData, ArrayList taskList) throws Exception {
		// ��ȡ�ִ�
		int taskRound = getTaskMaxTaskRound(wfid);
		DwFlowInstTable dwFlowInstVo = getWorkflowInstance(wfid);
		String busiOperateStyle = dwFlowInstVo.getBusioperatestyle();
		String assignmindealnumstyle = currentNode.getAssignmindealnumstyle();

		// �����ύʱ��
		Date receDate = new Date();
		String receTime = new SimpleDateFormat("yyyyMMddHHmmss").format(receDate);
		StringBuffer buf = new StringBuffer();

		// ���䴦���û�
		for (int i = 0; i < users.length; i++) {
			String user[] = users[i].split("/");
			buf.append(users[i]).append(" ");

			DwTaskTable dwTaskVo = new DwTaskTable();
			dwTaskVo.setWfid(wfid);
			dwTaskVo.setTaskser(taskSerial + 0L);
			dwTaskVo.setFlowid(currentNode.getFlowid());
			dwTaskVo.setBankid(user[0]);
			dwTaskVo.setOperid(user[1]);
			dwTaskVo.setRecetime(receTime);
			dwTaskVo.setDealtime(receTime);
			dwTaskVo.setNodeid(currentNode.getNodeid());
			dwTaskVo.setNodename(currentNode.getNodename());
			dwTaskVo.setExectrancode(currentNode.getExectrancode());
			dwTaskVo.setSubmtrancode(currentNode.getSubmtrancode());
			dwTaskVo.setLooktrancode(currentNode.getLooktrancode());
			dwTaskVo.setTaskdesc("");
			dwTaskVo.setForenodeid("");
			dwTaskVo.setLastchgdate(receDate);
			dwTaskVo.setLastchgtime(receTime);
			dwTaskVo.setTasktype(CommonConst.WF_TASKTYPE_DEAL + "");
			dwTaskVo.setNodephase(currentNode.getNodephase());
			dwTaskVo.setIsallowget(currentNode.getIsallowget());
			dwTaskVo.setIsallowreturn(currentNode.getIsallowreturn());
			dwTaskVo.setTaskround(taskRound);
			dwTaskVo.setIsprimaryauditnode(currentNode.getIsprimaryauditnode());
			dwTaskVo.setBusioperatestyle(busiOperateStyle);
			dwTaskVo.setAssignmindealnum(users.length + 0L);
			dwTaskVo.setAssignmindealnumstyle(assignmindealnumstyle);
			dwTaskVo.setLastchangetime(receDate);

			// �����ݲ���dwtask����
			DwTaskTableService.save(dwTaskVo);

			// ����������
			taskList.add(taskSerial);
			taskSerial++;
		}

		// ����鿴�û�
		for (int i = 0; viewUsers != null && i < viewUsers.length; i++) {
			String user[] = viewUsers[i].split("/");
			DwTaskTable dwTaskVo = new DwTaskTable();
			dwTaskVo.setFlowid(wfid);
			dwTaskVo.setBankid(user[0]);
			dwTaskVo.setOperid(user[1]);
			dwTaskVo.setRecetime(receTime);
			dwTaskVo.setDealtime("");
			dwTaskVo.setNodeid(currentNode.getNodeid());
			dwTaskVo.setNodename(currentNode.getNodename());
			dwTaskVo.setExectrancode(currentNode.getExectrancode());
			dwTaskVo.setSubmtrancode(currentNode.getSubmtrancode());
			dwTaskVo.setLooktrancode(currentNode.getLooktrancode());
			dwTaskVo.setTaskdesc("");
			dwTaskVo.setForenodeid("");
			dwTaskVo.setLastchgdate(receDate);
			dwTaskVo.setLastchgtime(receTime);
			dwTaskVo.setTasktype(CommonConst.WF_TASKTYPE_VIEW + "");
			dwTaskVo.setNodephase(currentNode.getNodephase());
			dwTaskVo.setIsallowget(currentNode.getIsallowget());
			dwTaskVo.setIsallowreturn(currentNode.getIsallowreturn());
			dwTaskVo.setTaskround(taskRound);
			dwTaskVo.setIsprimaryauditnode(currentNode.getIsprimaryauditnode());
			dwTaskVo.setBusioperatestyle(busiOperateStyle);
			dwTaskVo.setAssignmindealnum(users.length + 0L);
			dwTaskVo.setAssignmindealnumstyle(assignmindealnumstyle);
			dwTaskVo.setLastchangetime(receDate);

			// �����ݲ���dwtask����
			DwTaskTableService.save(dwTaskVo);
			taskSerial++;
		}

		return taskSerial - 1;
	}

	/**
	 * ����dwtask���е�����ִμ�¼
	 * 
	 * @param var var����Ҫ�� wfid
	 * @return int ����ִ�
	 * @throws java.lang.Exception
	 */
	public int getTaskMaxTaskRound(String wfid) throws Exception {
		int n = 0;

		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		// map.put("nodeid", nodeid);

		SingleResult sr = querySingle(getTpl("getdwtaskmaxround"), map, null);

		if (sr != null) {
			n = sr.getInt("taskRound");
		}
		return n;
	}

	/**
	 * �������������ִ�
	 */
	public void updateTaskRound(String wfid, int taskRound) throws Exception {

		DwTaskRoundTable dwTaskRoundVo = new DwTaskRoundTable();
		dwTaskRoundVo.setWfid(wfid);
		dwTaskRoundVo.setTaskround(taskRound);

		DwTaskRoundTable record = DwTaskRoundTableService.getById(wfid);
		if (record == null)
			DwTaskRoundTableService.save(dwTaskRoundVo);
		else
			DwTaskRoundTableService.update(dwTaskRoundVo);

	}

	SingleResult performAction(String string, String string2, UnikMap dp) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ɾ�����������ִ� ���������ʱ��
	 */
	public void deleteTaskRound(String wfid) throws Exception {
		DwTaskRoundTable dwTaskRoundVo = new DwTaskRoundTable();
		dwTaskRoundVo.setWfid(wfid);
		DwTaskRoundTableService.delete(dwTaskRoundVo);
	}

	/**
	 * �������������ִ�
	 */
	public void insertTaskRound(String wfid, int taskRound) throws Exception {
		DwTaskRoundTable dwTaskRoundVo = new DwTaskRoundTable();
		dwTaskRoundVo.setWfid(wfid);
		dwTaskRoundVo.setTaskround(taskRound);
		dwTaskRoundVo.setLastchangetime(new Date());
		DwTaskRoundTableService.save(dwTaskRoundVo);
	}

	/**
	 * ???��δʵ��
	 * <p>
	 * д����ʵ�����ձ�
	 * </p>
	 * 
	 * @param flowid
	 * @param wfid
	 * @param busiWorkflow
	 * @throws Exception
	 */
	public void writeBusiWFMap(String flowid, String wfid, UnikMap busiWorkflow) throws Exception {
		DwFlowMainTable dfmt = getWorkflowDefinition(flowid);
		String isWriteBusiWFMap = dfmt.getIswritebusiwfmap();
		if (busiWorkflow != null && isWriteBusiWFMap != null && isWriteBusiWFMap.equals("1")) {
			String loanid = busiWorkflow.getString("loanid");
			String custid = busiWorkflow.getString("custid");
			String custname = busiWorkflow.getString("custname");
			String prodid = busiWorkflow.getString("prodid");
			String prodname = busiWorkflow.getString("prodname");
			String busitype = busiWorkflow.getString("busitype");
			String busitypedetail = busiWorkflow.getString("busitypedetail");
			int transeq = getBusiMaxNo(loanid) + 1;
			busitype = Strings.notEmpty(busitypedetail) ? busitypedetail : busitype;

			DbBusiWfMapTable dbBusiWfMapVo = new DbBusiWfMapTable();
			dbBusiWfMapVo.setWfid(wfid);
			dbBusiWfMapVo.setLoanid(loanid);
			dbBusiWfMapVo.setTranseq(transeq + 0L);
			dbBusiWfMapVo.setCustid(custid);
			dbBusiWfMapVo.setCustname(custname);
			dbBusiWfMapVo.setProdid(prodid);
			dbBusiWfMapVo.setProdname(prodname);
			dbBusiWfMapVo.setBusitype(busitype);
			dbBusiWfMapVo.setFlowid(flowid);
			dbBusiWfMapVo.setNodename("");
			dbBusiWfMapVo.setCuroperid("");
			dbBusiWfMapVo.setCuroperidname("");
			dbBusiWfMapVo.setCurbankid("");
			dbBusiWfMapVo.setCurbankname("");
			dbBusiWfMapVo.setRecetime(getWorkdateCurrTime());
			dbBusiWfMapVo.setApproperid("");
			dbBusiWfMapVo.setAppropername("");
			dbBusiWfMapVo.setApprbankid("");
			dbBusiWfMapVo.setApprbankname("");
			dbBusiWfMapVo.setApprdate("");
			dbBusiWfMapVo.setApproperlev("");
			dbBusiWfMapVo.setIsfinish("1");
			dbBusiWfMapVo.setIsprimaryauditnode("");
			DbBusiWfMapTableService.save(dbBusiWfMapVo);
		}
	}

	/**
	 * ����dbBusiWFMap���еļ�¼
	 * 
	 * @param var var����Ҫ�� wfid
	 * @return boolean �Ƿ����
	 * @throws java.lang.Exception
	 */
	public boolean checkBusiWFMapIsExist(String wfid) throws Exception {
		DbBusiWfMapTable record = DbBusiWfMapTableService.getById(wfid);
		return record != null ? true : false;
	}

	/**
	 * ����dbBusiWFMap���е�������
	 * 
	 * @param var var����Ҫ�� wfid
	 * @return int ������
	 * @throws java.lang.Exception
	 */
	public int getBusiMaxNo(String loanid) throws Exception {
		int n = 0;

		UnikMap map = new UnikMap();
		map.put("loanid", loanid);

		SingleResult sr = querySingle(getTpl("getbusiwfmapmaxno"), map);

		if (sr != null) {
			n = sr.getInt("transeq");
		}
		return n;
	}

	private SingleResult querySingle(String tpl, UnikMap map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ����cmbank���еĻ�����Ϣ
	 * 
	 * @param bankid
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public SingleResult getBankInfo(String bankid) throws Exception {

		UnikMap map = new UnikMap();
		map.put("bankid", bankid);

		SingleResult sr = performAction("", "cmbank", map);

		return sr;
	}

	/**
	 * ����cmuser���е��û���Ϣ
	 * 
	 * @param userid
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public SingleResult getUserInfo(String bankid, String operid) throws Exception {

		UnikMap map = new UnikMap();
		map.put("bankid", bankid);
		map.put("userid", operid);

		SingleResult sr = performAction("", "cmuser", map);

		return sr;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkTempAuthStart(String flowId) throws Exception {
		boolean blSuccess = false;
		UnikMap map = new UnikMap();
		map.put("flowid", flowId);
		// �ӻ�������ȡ��
		DwFlowMainTable dfmt = this.getWorkflowDefinition(flowId);
		if (dfmt != null) {
			String isStartTempAuth = dfmt.getIsstarttempauth();
			;
			if (isStartTempAuth != null && isStartTempAuth.equalsIgnoreCase("1")) {
				blSuccess = true;
			}
		}
		return blSuccess;
	}

	/**
	 * 
	 * @param flowid
	 * @return
	 * @throws Exception
	 */
	public SingleResult getBusiRange(String flowid) throws Exception {
		UnikMap map = new UnikMap();
		map.put("flowid", flowid);

		SingleResult sr = querySingle(getTpl("getbusirange"), map);

		return sr;

	}

	/**
	 * 
	 * @param um
	 * @return
	 * @throws Exception
	 */
	public DataList getTempAuth(UnikMap um) throws Exception {
		DataList dlist = executeProcedure(getTpl("gettempauth"), um);
		return dlist;
	}

	/**
	 * ����dbBusiWFMap���е���Ϣ
	 * 
	 * @param var var����Ҫ�� wfid
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public DbBusiWfMapTable getBusiWFMap(String wfid) {
		DbBusiWfMapTable dbBusiWfMapVo = DbBusiWfMapTableService.getById(wfid);
		return dbBusiWfMapVo;
	}

	/**
	 * ���µ������ӳ�����Ĵ���
	 */
	public boolean updateReturnBusiWFMap(UnikMap umBusiWFMap) throws Exception {
		boolean blSuccess = false;
		try {
			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));

			dp.put("loanid", umBusiWFMap.get("loanid"));
			// dp.put("transeq", umBusiWFMap.get("transeq"));
			dp.put("custid", umBusiWFMap.get("custid"));
			dp.put("custname", umBusiWFMap.get("custname"));
			dp.put("prodid", umBusiWFMap.get("prodid"));
			dp.put("prodname", umBusiWFMap.get("prodname"));
			dp.put("busitype", umBusiWFMap.get("busitype"));
			dp.put("flowid", umBusiWFMap.get("flowid"));
			dp.put("nodename", umBusiWFMap.get("nodename"));
			dp.put("curoperid", umBusiWFMap.get("curoperid"));
			dp.put("curoperidname", umBusiWFMap.get("curoperidname"));
			dp.put("curbankid", umBusiWFMap.get("curbankid"));
			dp.put("curbankname", umBusiWFMap.get("curbankname"));
			dp.put("recetime", getWorkdateCurrTime());
			dp.put("approperid", "");
			dp.put("appropername", "");
			dp.put("apprbankid", "");
			dp.put("apprbankname", "");
			dp.put("apprdate", "");
			dp.put("approperLev", "");
			dp.put("isfinish", "1");
			// this.log(DEBUG,"debug(updateReturnBusiWFMap)======================================umBusiWFMap.get(\"nodename\")="
			// +umBusiWFMap.get("nodename"));

			performAction("", "dbBusiWFMap", dp);
			blSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean updateBusiWFMap(UnikMap umBusiWFMap) throws Exception {
		boolean blSuccess = false;
		try {

			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));

			dp.put("flowid", umBusiWFMap.get("flowid"));
			dp.put("nodename", umBusiWFMap.get("nodename"));
			dp.put("curoperid", umBusiWFMap.get("curoperid"));
			dp.put("curoperidname", umBusiWFMap.get("curoperidname"));
			dp.put("curbankid", umBusiWFMap.get("curbankid"));
			dp.put("curbankname", umBusiWFMap.get("curbankname"));
			dp.put("recetime", umBusiWFMap.get("recetime"));
			dp.put("approperid", umBusiWFMap.get("approperid"));
			dp.put("appropername", umBusiWFMap.get("appropername"));
			dp.put("apprbankid", umBusiWFMap.get("apprbankid"));
			dp.put("apprbankname", umBusiWFMap.get("apprbankname"));
			dp.put("apprdate", umBusiWFMap.get("apprdate"));
			dp.put("approperLev", umBusiWFMap.get("approperLev"));
			dp.put("isfinish", umBusiWFMap.get("isfinish"));
			// this.log(DEBUG,"debug(updateBusiWFMap)======================================umBusiWFMap.get(\"nodename\")="
			// +umBusiWFMap.get("nodename"));

			performAction("", "dbBusiWFMap", dp);
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	/**
	 * ???��δʵ�� ��������ʵ��ҵ����ձ�
	 */
	public boolean updateBusiWFMap(UnikMap node, String wfInstId, String curOperUser, String[] users) throws Exception {
		boolean blSuccess = false;
		try {
			// �����ύʱ��
			String apprdate = getWorkdateCurrTime();
			String curUser[] = curOperUser.split("/");
			String curOperId = curUser[1];
			String curBankId = curUser[0];
			String curOperIdName = "";
			String curBankName = "";

			String operids = "";
			String bankids = "";
			String appropername = "";
			String apprbankname = "";
			String approperLev = "";
			// �����û����ƺͻ�������
			SingleResult operSr = getUserInfo(curBankId, curOperId);
			if (operSr != null) {
				curOperIdName = operSr.getString("username");
			}
			SingleResult bankSr = getBankInfo(curBankId);
			if (bankSr != null) {
				curBankName = bankSr.getString("bankname");
			}

			for (int i = 0; i < users.length; i++) {
				String user[] = users[i].split("/");
				bankids = user[0];
				operids = user[1];
			}
			// �����û����ƺͻ�������
			SingleResult operApprSr = getUserInfo(bankids, operids);
			if (operApprSr != null) {
				appropername = operApprSr.getString("username");
			}
			SingleResult bankApprSr = getBankInfo(bankids);
			if (bankApprSr != null) {
				apprbankname = bankApprSr.getString("bankname");
				approperLev = bankApprSr.getString("banklevel");
			}
			String nodeName = node.getString("nodename");

			UnikMap dp = new UnikMap();
			dp.put("wfid", wfInstId);
			dp.put("nodename", nodeName);

			dp.put("curoperid", curOperId);
			dp.put("curoperidname", curOperIdName);
			dp.put("curbankid", curBankId);
			dp.put("curbankname", curBankName);
			dp.put("approperid", operids);
			dp.put("appropername", appropername);
			dp.put("apprbankid", bankids);
			dp.put("apprbankname", apprbankname);
			dp.put("apprdate", apprdate);
			dp.put("approperLev", approperLev);

			performAction("", "dbBusiWFMap", dp);
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	public boolean updateBusiWFMap(DwFlowNodeTable nextNode, String wfInstId, String curOperUser, String[] users, String isPrimaryAuditNode) throws Exception {
		boolean blSuccess = false;
		try {
			// �����ύʱ��
			String apprdate = getWorkdateCurrTime();
			String curOperId = "";
			String curBankId = "";
			String curOperIdName = "";
			String curBankName = "";

			String operids = "";
			String bankids = "";
			String appropername = "";
			String apprbankname = "";
			String approperLev = "";

			for (int i = 0; i < users.length; i++) {
				String user[] = users[i].split("/");

				// �����û����ƺͻ�������
				SingleResult operApprSr = getUserInfo(user[0], user[1]);
				if (operApprSr != null) {
					curOperIdName += (curOperIdName.equals("") ? operApprSr.getString("username") : "," + operApprSr.getString("username"));
					appropername = operApprSr.getString("username");
				}
				SingleResult bankApprSr = getBankInfo(user[0]);
				if (bankApprSr != null) {
					if (curBankName.indexOf((String) bankApprSr.getString("bankname")) < 0)
						curBankName += (curBankName.equals("")) ? bankApprSr.getString("bankname") : "," + bankApprSr.getString("bankname");
					apprbankname = bankApprSr.getString("bankname");
					approperLev = bankApprSr.getString("banklevel");
				}

				curBankId += (curBankId == null || curBankId.equals("")) ? user[0] : "," + user[0];
				curOperId += (curOperId == null || curOperId.equals("")) ? user[1] : "," + user[1];
			}

			appropername = curOperIdName;
			apprbankname = curBankName;

			String nodeName = nextNode.getNodename();

			UnikMap dp = new UnikMap();
			dp.put("wfid", wfInstId);
			dp.put("nodename", nodeName);
			dp.put("isPrimaryAuditNode", isPrimaryAuditNode == null ? "2" : isPrimaryAuditNode);
			if (isPrimaryAuditNode != null && isPrimaryAuditNode.equals("1")) {
				dp.put("curoperid", curOperId);
				dp.put("curoperidname", curOperIdName);
				dp.put("curbankid", curBankId);
				dp.put("curbankname", curBankName);
				dp.put("approperid", operids);
				dp.put("appropername", appropername);
				dp.put("apprbankid", bankids);
				dp.put("apprbankname", apprbankname);
				dp.put("apprdate", apprdate);
				dp.put("approperLev", approperLev);
			} else {
				dp.put("curoperid", curOperId);
				dp.put("curoperidname", curOperIdName);
				dp.put("curbankid", curBankId);
				dp.put("curbankname", curBankName);

			}

			performAction("", "dbBusiWFMap", dp);
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean updateNormalBusiWFMap(UnikMap node, String wfInstId, String curOperUser) throws Exception {
		boolean blSuccess = false;
		try {
			String curUser[] = curOperUser.split("/");
			String curOperId = curUser[1];
			String curBankId = curUser[0];
			String curOperIdName = "";
			String curBankName = "";

			// �����û����ƺͻ�������
			SingleResult operSr = getUserInfo(curBankId, curOperId);
			if (operSr != null) {
				curOperIdName = operSr.getString("username");
			}
			SingleResult bankSr = getBankInfo(curBankId);
			if (bankSr != null) {
				curBankName = bankSr.getString("bankname");
			}
			String nodeName = node.getString("nodename");

			UnikMap dp = new UnikMap();
			dp.put("wfid", wfInstId);
			dp.put("nodename", nodeName);

			dp.put("curoperid", curOperId);
			dp.put("curoperidname", curOperIdName);
			dp.put("curbankid", curBankId);
			dp.put("curbankname", curBankName);

			performAction("", "dbBusiWFMap", dp);
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	/**
	 * ��������ʵ��ҵ����ձ����ɱ�ʶ
	 */
	public boolean completeBusiWFMap(String wfid, String isFinish) {
		boolean blSuccess = false;
		try {
			DbBusiWfMapTable dbBusiWfMapVo = new DbBusiWfMapTable();
			dbBusiWfMapVo.setWfid(wfid);
			dbBusiWfMapVo.setIsfinish(isFinish);
			DbBusiWfMapTableService.update(dbBusiWfMapVo);
			blSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	/**
	 * ɾ������ʵ��ҵ����ձ�
	 */
	public boolean deleteBusiWFMap(String wfid) {
		boolean blSuccess = false;
		try {
			DbBusiWfMapTableService.delete(wfid);
			blSuccess = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}

	public int getAllUserFromHistoryTask(UnikMap var) throws Exception {
		int n = 0;
		/*
		 * <sql name="getCountDwtask"> select count(*) as num from dwTask where wfid
		 * = @wfid #AND = @nodeid </sql>
		 */

		SingleResult sr = querySingle(getTpl("gethistoryuser"), var, null);

		if (sr != null) {
			n = sr.getInt("num");
		}
		return n;
	}

	/**
	 * ����dwtask���еļ�¼
	 * 
	 * @param dwTaskVo ����Ҫ�� wfid ,nodeid
	 * @return int ��¼������
	 * @throws java.lang.Exception
	 */
	public int getTaskNum(DwTaskTable dwTaskVo) throws Exception {
		String wfid = dwTaskVo.getWfid();
		String nodeid = dwTaskVo.getNodeid();
		int n = DwTaskTableService.getNumByWfidNodeId(wfid, nodeid);
		return n;
	}

	public int getTaskNum(UnikMap var) throws Exception {
		String wfid = var.getString("wfid");
		String nodeid = var.getString("nodeid");
		int n = DwTaskTableService.getNumByWfidNodeId(wfid, nodeid);
		return n;
	}

	/**
	 * ����dwtask���е����Ȩ�������¼
	 * 
	 * @param var var����Ҫ�� wfid ,nodeid
	 * @return int ��¼������
	 * @throws java.lang.Exception
	 */
	public int getViewTaskNum(UnikMap var) throws Exception {
		int n = 0;
		/*
		 * <sql name="getCountDwtask"> select count(*) as num from dwTask where wfid
		 * = @wfid #AND = @nodeid </sql>
		 */

		SingleResult sr = querySingle(getTpl("getviewcountdwtask"), var, null);

		if (sr != null) {
			n = sr.getInt("num");
		}
		return n;
	}

	/**
	 * �������״̬�Ƿ񷢲�,�Ƿ���״̬�������ύҵ��
	 * 
	 * @param flowid ���̱��
	 * @throws java.lang.Exception ��ʼ���׳�WFStateIsInit�����̱���ͣʹ���׳� WFStateIsPause
	 *         ���̱������׳�WFStateIsStop
	 */
	public void checkFlowState(String flowid) throws Exception {
		DwFlowMainTable dfmt = getWorkflowDefinition(flowid);
		String flowname = dfmt.getFlowname();
		String flowstate = dfmt.getFlowstate();

		// �Ƿ񻹴��ڳ�ʼ��
		Errors.Assert(!CommonConst.WF_STATE_INIT.equals(flowstate), formatMessage("WFStateIsInit", flowname));

		// �Ƿ���ͣʹ��
		Errors.Assert(!CommonConst.WF_STATE_PAUSE.equals(flowstate), formatMessage("WFStateIsPause", flowname));

		// �Ƿ񱻽���,��Ҫ�����ύ��ҵ��,��Ϊ���ڴ����ҵ���ѱ�ת�ֹ���
		Errors.Assert(!CommonConst.WF_STATE_STOP.equals(flowstate), formatMessage("WFStateIsStop", flowname));
	}

	/**
	 * ɾ����������
	 * 
	 * @param wfInstId
	 * @throws Exception
	 */
	public void completeAllTask(String wfInstId) throws Exception {

		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);

		// ɾ��dwTask�е�����
		// DataContext exec = new DataContext();
		executeProcedure(getTpl("deletealltask"), dp);

	}

	/**
	 * ��ɵ�ǰ�������� 2007.5.16 ���������ýڵ������ֶεĸ��£�ԭ������newTas�����н��У���ΪҪ��
	 * ����������ύ����ʱ���������������ֶ�����ֻ�����������ʱ�����ˡ�
	 * 
	 * @param wfid    ����ʵ�����
	 * @param taskser �������
	 * @param bizData ����
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("rawtypes")
	public void completeTask(String wfid, DwTaskTable dwTaskVo, UnikMap bizData, DwFlowNodeTable currentNode) throws Exception {
		String taskdesc = currentNode.getNodedesc();

		// ���㷨��֤��ȡ�ı��������±����,���´�dwtaskvars�ж�ȡ������Ϣ
		getInstVar(wfid, true);

		// �����µı�����Ϣ��ȡֵȥ�滻taskdesc�еı����ֶ�
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			taskdesc = StringUtils.replace(taskdesc, "{" + name.toLowerCase() + "}", value);
		}

		Date dealdate = new Date();
		String dealtime = getWorkdateCurrTime();
		String dealsystime = JBDate.getSysCurrTime(CommonConst.DATE_TYPE_TIMESTAMP);
		// ��dwtask���еļ�¼����dwtaskhis��
		DwTaskHisTable dwTaskHisVo = new DwTaskHisTable();
		dwTaskHisVo.setWfid(dwTaskVo.getWfid());
		dwTaskHisVo.setTaskser(dwTaskVo.getTaskser());
		dwTaskHisVo.setBankid(dwTaskVo.getBankid());
		dwTaskHisVo.setOperid(dwTaskVo.getOperid());
		dwTaskHisVo.setRecetime(dwTaskVo.getRecetime());
		dwTaskHisVo.setDealtime(dealtime);
		dwTaskHisVo.setDealsystime(dealsystime);
		dwTaskHisVo.setNodeid(dwTaskVo.getNodeid());
		dwTaskHisVo.setNodename(dwTaskVo.getNodename());
		dwTaskHisVo.setExectrancode(dwTaskVo.getExectrancode());
		dwTaskHisVo.setSubmtrancode(dwTaskVo.getSubmtrancode());
		dwTaskHisVo.setLooktrancode(dwTaskVo.getLooktrancode());
		dwTaskHisVo.setTaskdesc(taskdesc);
		dwTaskHisVo.setForenodeid(dwTaskVo.getForenodeid());
		dwTaskHisVo.setLastchgdate(dealdate);
		dwTaskHisVo.setLastchgtime(dealtime);
		dwTaskHisVo.setTasktype(dwTaskVo.getTasktype());
		dwTaskHisVo.setNodephase(dwTaskVo.getNodephase());
		dwTaskHisVo.setIsallowget(dwTaskVo.getIsallowget());
		dwTaskHisVo.setIsallowreturn(dwTaskVo.getIsallowreturn());
		dwTaskHisVo.setTaskround(dwTaskVo.getTaskround());
		dwTaskHisVo.setBusioperatestyle(dwTaskVo.getBusioperatestyle());
		dwTaskHisVo.setIsprimaryauditnode(dwTaskVo.getIsprimaryauditnode());
		dwTaskHisVo.setAssignmindealnum(dwTaskVo.getAssignmindealnum());
		dwTaskHisVo.setAssignmindealnumstyle(dwTaskVo.getAssignmindealnumstyle());
		dwTaskHisVo.setLastchangetime(dealdate);
		DwTaskHisTableService.save(dwTaskHisVo);

		// ɾ��dwTask�е�����
		DwTaskTableKey key = new DwTaskTableKey();
		key.setWfid(wfid);
		key.setTaskser(dwTaskVo.getTaskser());
		DwTaskTableService.deleteByPrimaryKey(key);
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	public boolean isLastestHandler(UnikMap node, UnikMap bizData, boolean blStart) throws Exception {
		String taskoverpolicy = node.getString("taskoverpolicy"); // ������ɲ���
		int num = 1; // ������Ҫ��ɵ�����
		int mindealnum = node.getInt("mindealnum"); // ����������ٴ�������;
		String assignmindealnumstyle = bizData.getString("assignmindealnumstyle"); // �Ƿ�������ٴ������������ٴ�������Ϊ�ֶ�ѡ����û���
		// log(DEBUG, "assignmindealnumstyle=" + assignmindealnumstyle);
		if (assignmindealnumstyle != null && assignmindealnumstyle.equals("1")) // =================�����������������
		{
			String assignMinDealNum = bizData.getString("assignMinDealNum");
			mindealnum = Integer.valueOf(assignMinDealNum);
		}
		// log(DEBUG, "assignMinDealNum=" + mindealnum);
		// �õ�ĳ��������ĳ������µ�������
		int tasknum = getTaskNum(bizData); // dwtask���л�ʣ�µ�������
		// ����Ƿ���������Ա,��ô��������������ٴ�������Ϊ������Ա
		if (CommonConst.WF_TASKASSIGN_ALL.equals(node.getString("autodisuserflag"))) {
			mindealnum = tasknum + num; // ��������Ϊ�����������Ѵ����һ��
		}
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) {
			num = node.getInt("assignminnum"); // ȡ���õĴ�������
		} else if (CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)) {
			double percent = node.getDouble("overpercent") / 100; // ȡ���ٷֱ�
			num = WorkFlowFunc.ceil(mindealnum * percent); // ���ٴ����������԰ٷֱ�ȡ��
		} else if (CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)) {
			num = mindealnum; // �������ж����
		}
		// ����������ȥ���������������δ��ɵ��������������б���û���������Ϊ��ǰ������ȫ�����
		boolean flag = (mindealnum - num) == tasknum || (blStart && (mindealnum - num) == 0 && tasknum == 1) || tasknum == 0 ? true : false;
		return flag;

	}

	/**
	 * �ж��Ƿ�����������,������һ�ڵ�����������Խ��д���
	 * 
	 * @throws java.lang.Exception
	 */
	public boolean isContinue(DwFlowNodeTable currentNode, DwTaskTable dwTaskVo, boolean blStart) throws Exception {
		String taskoverpolicy = currentNode.getTaskoverpolicy();// ������ɲ���
		Long num = 1L;// ������Ҫ��ɵ�����
		Long mindealnum = currentNode.getMindealnum();// ����������ٴ�������;
		String assignmindealnumstyle = dwTaskVo.getAssignmindealnumstyle();// �Ƿ�������ٴ������������ٴ�������Ϊ�ֶ�ѡ����û���
		if (assignmindealnumstyle != null && assignmindealnumstyle.equals("1")) // =================�����������������
		{
			Long assignMinDealNum = dwTaskVo.getAssignmindealnum();
			mindealnum = assignMinDealNum;
		}
		// �õ�ĳ��������ĳ������µ�������
		int tasknum = getTaskNum(dwTaskVo); // dwtask���л�ʣ�µ�������
		// ����Ƿ���������Ա,��ô��������������ٴ�������Ϊ������Ա
		if (CommonConst.WF_TASKASSIGN_ALL.equals(currentNode.getAutodisuserflag())) {
			mindealnum = tasknum + num; // ��������Ϊ�����������Ѵ����һ��
		}
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) {
			num = currentNode.getAssignminnum(); // ȡ���õĴ�������
		} else if (CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)) {
			double percent = currentNode.getOverpercent() / 100; // ȡ���ٷֱ�
			num = WorkFlowFunc.ceil(mindealnum * percent) + 0L; // ���ٴ����������԰ٷֱ�ȡ��
		} else if (CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)) {
			num = mindealnum; // �������ж����
		}
		// ����������ȥ���������������δ��ɵ��������������б���û���������Ϊ��ǰ������ȫ�����
		long restTaskCount = mindealnum - num;
		boolean flag = (restTaskCount == tasknum || (blStart && restTaskCount == 0 && tasknum == 1) || tasknum == 0) ? true : false;
		// ��������������������л�����δ��������,����������
		if (flag && tasknum > 0) {
			/*
			 * <sql name="deleteFromDwtask"> DELETE FROM DWTASK WHERE WFID=@wfid AND NODEID
			 * = @nodeid </sql>
			 */
			// executeProcedure(getTpl("deleteFromDwtask"), dwTaskVo);
		}
		// ����ǰ�������
		// �õ�ĳ��������ĳ������µ�������
		/*
		 * int viewtasknum = getViewTaskNum(dwTaskVo); //dwtask���л�ʣ�µ������� if(flag &&
		 * viewtasknum>0) { executeProcedure(getTpl("deleteFromDwtask"), dwTaskVo); }
		 */
		return flag;
	}

	/**
	 * ��������ʵ���е�������
	 */
	public void updateTaskSerial(String wfid, int taskser) throws Exception {
		updateTaskSerial(wfid, taskser, null);
	}

	/**
	 * ��������ʵ��������״̬
	 */
	public void updateWorkflowStatus(String wfid, int status) throws Exception {
		DwFlowInstTable dwFlowInstVo = new DwFlowInstTable();
		dwFlowInstVo.setWfid(wfid);
		dwFlowInstVo.setWfstate(status + "");
		DwFlowInstTableService.updateByPrimaryKeySelective(dwFlowInstVo);
	}

	/**
	 * ��������ʵ���е������źͽ����̶�
	 */
	public void updateTaskSerial(String wfid, int taskser, String instancylevel) throws Exception {

		DwFlowInstTable dwFlowInstVo = new DwFlowInstTable();
		dwFlowInstVo.setWfid(wfid);
		dwFlowInstVo.setTaskser(taskser + 0L);

		if (null != instancylevel && !"".equals(instancylevel)) {
			dwFlowInstVo.setInstancylevel(instancylevel);
		}

		DwFlowInstTableService.updateByPrimaryKeySelective(dwFlowInstVo);
	}

	/**
	 * ���ر��
	 * 
	 * @param name �������
	 * @param len  ��ŵĳ���
	 * @throws RunTimeException
	 */
	public String getGlobalNum(String name, int len) throws Exception {
		String str = getSerialNumByAbbrv("global", name);
		return calFillString(str, len, "0") + str;
	}

	/**
	 * ���ݻ����ź�ƴ����д������ˮ��
	 * 
	 * @param sBankId ������
	 * @param sAbbrv  ƴ����д
	 * @return ��ˮ��
	 * @throws RunTimeException
	 */
	private String getSerialNumByAbbrv(String sBankId, String sAbbrv) throws Exception {
		String abbrv = sAbbrv.trim();
		String bankId = sBankId.trim();

		// �޸��ò���
		UnikMap nvalues = new UnikMap();
		// nvalues.put("amtnum", new SerNoUpdator());
		nvalues.put("bankid", bankId);
		nvalues.put("serid", abbrv);

		// DataContext exectx = new DataContext();
		// exectx.set(NEWTRANSACTION);
		// exectx.table = "cmserno";
		// �����ò���
		UnikMap dp = new UnikMap();
		dp.put("bankid", bankId);
		dp.put("serid", abbrv);
		dp.put("sername", abbrv);
		dp.setInt("serno", 1);

		/*
		 * <sql name="getCmSerNo"> Select bankid, serid, sername, serno From cmserno
		 * Where bankid=@bankid and serid=@serid </sql>
		 */
		SingleResult result = new SingleResult();// merge(getTpl("getCmSerNo"), nvalues, dp);

		String n = "";

		if (result != null) {
			String s = "";// (String) result.get("serno");
			n = String.valueOf(Long.parseLong(s) + 1);
		} /*
			 * else { n = "1"; }
			 */

		return n;
	}

	private String calFillString(String str, int iSerialLen, String sFillStuff) {
		String s = str.trim();
		StringBuffer buffer = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < iSerialLen - len; i++) {
			buffer.append(sFillStuff);
		}
		return buffer.toString();
	}

	/**
	 * �ӽڵ㶨�������ֶθ��ж�ȡ��Щ������Ҫ����
	 * 
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] getVarNames(String desc, String wfDefId, String nodeid) throws Exception {
		StringBuffer buf = new StringBuffer();
		String arrs[] = StringUtils.split(desc, "{");
		boolean flag = false;
		// ��ȡ�ڵ㶨���еı���
		if (desc.charAt(0) == '{')
			flag = true;
		for (int i = 0; i < arrs.length; i++) {
			if (flag)
				buf.append(StringUtils.split(arrs[i], "}")[0]);
			else {
				if (i < arrs.length - 1)
					buf.append(StringUtils.split(arrs[i + 1], "}")[0]);
			}
			buf.append(",");
		}
		// ��ȡ�ڵ�·�����õ��ı���

		/*
		 * <sql name="getAllFromDwFlowRoute"> Select
		 *
		 * From dwFlowRoute Where flowid=@flowid and nodeid=@nodeid </sql>
		 */

		// ���´���д��û�����壬��֪��ΪʲôҪ�������������Ǹ�������ˣ������� û�����廹�����Σ��ǲ����˷���Դ
		/*
		 * UnikMap dp = new UnikMap(); dp.put("flowid", wfDefId); dp.put("nodeid",
		 * nodeid);
		 * 
		 * DataList dl = executeProcedure(getTpl("getAllFromDwFlowRoute"), dp);
		 * 
		 * while (dl.next()) { dl.getString("reoutecond"); }
		 */

		return StringUtils.split(buf.toString());
	}

	/**
	 * ȡ�ڵ�·����Ϣ
	 * 
	 * @param wfDefId flowid
	 * @return nodeId nodeid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList getNodeRoute(String wfDefId, String nodeId) throws Exception {
		LinkedList list = new LinkedList();
		// �ӻ�����ȡ��
		List<UnikMap> dlList = new ArrayList<UnikMap>();// cacheTools.queryDwflowrouteVo(wfDefId, nodeId);

		for (int i = 0; i < dlList.size(); i++) {
			UnikMap dl = dlList.get(i);
			String cond = dl.getString("routecond");
			String nnid = dl.getString("nextnodeid");
			String taskassignstyle = dl.getString("taskassignstyle");
			String routetype = dl.getString("routetype");
			String expConstType = dl.getString("expconsttype1") + "," + dl.getString("expconsttype2") + "," + dl.getString("expconsttype3") + "," + dl.getString("expconsttype4");

			if (cond == null || cond.length() == 0 || nnid == null || nnid.length() == 0)
				throw new NamedException(getMsgInfo("WF_NotFoundRouteDef"));
			list.add(list.size(), cond);
			list.add(list.size(), nnid);
			list.add(list.size(), expConstType);
			list.add(list.size(), taskassignstyle);
			list.add(list.size(), routetype);
		}

		return list;
	}

	public String getSelectText(String select, String value) throws Exception {

		HashMap<String, UnikMap> map = new HashMap<String, UnikMap>();// EhcacheTools.getHashMap("selectall");
		/*
		 * if (map == null) return value;
		 */

		UnikMap s = map.get(select);
		String text = value;
		if (s != null) {
			text = (String) s.get(value);
			if (Strings.isEmpty(text)) {
				return value;
			}
		}

		return text;
	}

	/**
	 * �õ������б�,��Ҫ�Ǹ�·����������ʹ��
	 * 
	 * @param wfid   ������ʵ�����
	 * @param reload �Ƿ����ز���,reload=false�������в���ʱ������ȥ��ѯ���ݿ�
	 * @return UnikMap vars�����б�
	 * @throws java.lang.Exception
	 */
	public UnikMap getInstVar(String wfid, boolean reload) throws Exception {
		if (!reload && vars.size() != 0)
			return vars;

		/*
		 * //DwTaskVarsTableService. UnikMap dp = new UnikMap(); dp.put("wfid", wfid);
		 * dp.put("order", "asc"); DataList dl =
		 * executeProcedure(getTpl("getAllDwTaskVars"), dp); while (dl.next()) { //
		 * ���㷨��֤��ȡ�ı��������±���� String name = dl.getString("varname"); String value =
		 * dl.getString("varvalue"); vars.put(name, value); }
		 */

		return vars;
	}

	/**
	 * ��������
	 * 
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void completeInstance(String wfid) throws Exception {
		Date lastDate = new Date();
		DwFlowInstTable dwFlowInstVo = DwFlowInstTableService.getById(wfid);
		DwFlowHisTable dwFlowHisVo = new DwFlowHisTable();
		dwFlowHisVo.setWfid(dwFlowInstVo.getWfid());
		dwFlowHisVo.setFlowid(dwFlowInstVo.getFlowid());
		dwFlowHisVo.setFlowname(dwFlowInstVo.getFlowname());
		dwFlowHisVo.setFlowdesc(dwFlowInstVo.getFlowdesc());
		dwFlowHisVo.setFlowtype(dwFlowInstVo.getFlowtype());
		dwFlowHisVo.setBankid(dwFlowInstVo.getBankid());
		dwFlowHisVo.setOperid(dwFlowInstVo.getOperid());
		dwFlowHisVo.setOpername(dwFlowInstVo.getOpername());
		dwFlowHisVo.setCreattime(dwFlowInstVo.getCreattime());
		dwFlowHisVo.setNodename(dwFlowInstVo.getNodename());
		dwFlowHisVo.setWfstate(dwFlowInstVo.getWfstate());
		dwFlowHisVo.setExectrancode(dwFlowInstVo.getExectrancode());
		dwFlowHisVo.setSubmtrancode(dwFlowInstVo.getSubmtrancode());
		dwFlowHisVo.setLooktrancode(dwFlowInstVo.getLooktrancode());
		dwFlowHisVo.setTaskser(dwFlowInstVo.getTaskser());
		dwFlowHisVo.setManusql(dwFlowInstVo.getManusql());
		dwFlowHisVo.setExecsql(dwFlowInstVo.getExecsql());
		dwFlowHisVo.setUnitwfid(dwFlowInstVo.getUnitwfid());
		dwFlowHisVo.setInstancylevel(dwFlowInstVo.getInstancylevel());
		dwFlowHisVo.setLastchgdate(lastDate);
		dwFlowHisVo.setLastchgtime(getWorkdateCurrTime());
		dwFlowHisVo.setIswritebusiwfmap(dwFlowInstVo.getIswritebusiwfmap());
		dwFlowHisVo.setBusioperatestyle(dwFlowInstVo.getBusioperatestyle());
		dwFlowHisVo.setLastchangetime(lastDate);
		DwFlowHisTableService.save(dwFlowHisVo);
		DwFlowInstTableService.delete(dwFlowInstVo);
	}

	/**
	 * ���˵���ʼ�ڵ�ʱ��Ҫ�����һЩ���顣
	 */
	public void doWithDraw(String flowid, String wfid) throws Exception {
		String execsql = getWorkflowDefinition(flowid).getExecsql();
		;
		if (execsql.length() == 0)
			return;
		String esql[] = StringUtils.split(execsql, ";");
		// �õ���Ӧ����ʵ���ı�����Ϣ
		UnikMap m = getInstVar(wfid, false);
		for (int i = 0; i < esql.length; i++) {
			String sql = esql[i];

			// �����滻��
			while (true) {
				int pos1 = sql.indexOf("{");
				int pos2 = sql.indexOf("}");
				if (pos1 > 0 && pos2 > 2) {
					String name = sql.substring(pos1 + 1, pos2);
					String s = m.getString(name.toLowerCase());
					if (s.length() > 0) {
						sql = StringUtils.replace(sql, "{" + name.toLowerCase() + "}", s);
					} else {
						// ���sû��ȡ��ֵ,�˴�����ʾ�Ļ�,����һ����ѭ��
						throw new NamedException(formatMessage("FieldNotInRequest", name));
					}
				} else {
					break;
				}
			}
			UnikMap dp = new UnikMap();
			dp.put("sql", sql);

			executeProcedure(getTpl("execProcedure"), dp);
		}
	}

	/**
	 * ����Ӧ�÷���ע��Ӧ�÷�����"s_"��ͷ���ֶζ�û��
	 * 
	 * @param v ִ�н������Բ���,һ��Ϊrequest
	 * @throws java.lang.Exception
	 */
	public AppResponse doReturnService(UnikMap node, UnikMap varsUm) throws Exception {
		/*
		 * String tid = node.getString("retutrancode");
		 * //getInstVar(node.getString("wfid"), false); AppRequest req = new
		 * AppRequest();
		 * 
		 * req.putAll(node); Iterator it = varsUm.keySet().iterator();
		 * while(it.hasNext()){ String name = it.next().toString(); String val =
		 * varsUm.getString(name); String n[]= StringUtil.split(name, ".");
		 * if(n.length==2){ req.put(n[1], val); }else{ req.put(name, val); } }
		 */

		return new AppResponse();// invoke(tid, req);
	}

	public AppResponse doEndService(UnikMap node, UnikMap varsUm) throws Exception {
		/*
		 * String tid = node.getString("submtrancode");
		 * //getInstVar(node.getString("wfid"), false); AppRequest req = new
		 * AppRequest();
		 * 
		 * req.putAll(node); varsUm.remove("dealopin");//add by mahong Iterator it =
		 * varsUm.keySet().iterator(); while(it.hasNext()){ String name =
		 * it.next().toString(); String val = varsUm.getString(name); String n[]=
		 * StringUtil.split(name, "."); if(n.length==2){ req.put(n[1], val); }else{
		 * req.put(name, val); } }
		 * 
		 * <option value="10" text="ͬ��"/> <option value="11" text="ͬ�Ⲣ�ϱ�"/> <option
		 * value="20" text="��ͬ��"/> <option value="30" text="����"/> <option value="40"
		 * text="���"/> <option value="50" text="������������¼"/> <option value="60"
		 * text="ͬ������¼"/> String dealopin = req.getString("dealopin");
		 * 
		 * if("ͬ��".equals(dealopin)) { req.put("dealopin", "10"); } else
		 * if("ͬ�Ⲣ�ϱ�".equals(dealopin)) { req.put("dealopin", "11"); } else
		 * if("��ͬ��".equals(dealopin)) { req.put("dealopin", "20"); } else
		 * if("����".equals(dealopin)) { req.put("dealopin", "30"); } else
		 * if("���".equals(dealopin)) { req.put("dealopin", "40"); } else
		 * if("������������¼".equals(dealopin)) { req.put("dealopin", "50"); } else
		 * if("ͬ������¼".equals(dealopin)) { req.put("dealopin", "60"); }
		 */

		return new AppResponse();// invoke(tid, req);
	}

	/**
	 * ����Ӧ�÷���ע��Ӧ�÷�����"s_"��ͷ���ֶζ�û��
	 * 
	 * @param v ִ�н������Բ���,һ��Ϊrequest
	 * @throws java.lang.Exception
	 */
	public AppResponse doService(UnikMap v) throws Exception {
		/*
		 * String tid = v.getString("submtrancode"); getInstVar(v.getString("wfid"),
		 * false); AppRequest req = new AppRequest();
		 * 
		 * req.putAll(v); Iterator it = vars.keySet().iterator(); while(it.hasNext()){
		 * String name = it.next().toString(); String val = vars.getString(name); String
		 * n[]= StringUtil.split(name, "."); if(n.length==2){ req.put(n[1], val); }else{
		 * req.put(name, val); } }
		 */

		return new AppResponse();// invoke(tid, req);
	}

	/**
	 * ��һ�������е�����һ������. invoke(code, request.withparam("a:b,c,d"));
	 * 
	 * @param code �������
	 */
	/*
	 * public AppResponse invoke(String code, AppRequest req) throws Exception{
	 * context.save(); context.setRequest(req); req.setHeader("TranCode", code); try
	 * { AppModule s = application.prepareModule(code, context); return s.invoke();
	 * } finally { context.restore(); } }
	 */

	/**
	 * ���ش����������ٵ��û�
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String[] getTaskLeastUser(String[] user, int num) throws Exception {
		List list = new ArrayList();
		UnikMap map = new UnikMap();
		String[] retusr = new String[num];
		for (int i = 0; i < user.length; i++) {
			map.put(user[i], user[i]);
		}

		/*
		 * <sql name="getBankidFromDwtaskGroupbybankidAndOperid"> select {fn concat({fn
		 * concat({fn rtrim(bankid)}, '/')}, operid)} userid, count(bankid) num from
		 * dwtask group by bankid,operid order by 2 </sql>
		 */

		DataList dl = executeProcedure(getTpl("getBankidFromDwtaskGroupbybankidAndOperid"));
		String userid = null;
		StringBuffer strbuf = new StringBuffer();
		while (dl.next()) {
			userid = dl.getString("userid");
			String s = map.getString(userid);
			if (s.length() > 0) {
				strbuf.append(userid);
				strbuf.append(",");
			}
		}

		if (strbuf.length() > 2) {
			strbuf.delete(strbuf.length() - 1, strbuf.length());
			for (int j = 0; j < user.length; j++) {
				if (strbuf.toString().indexOf(user[j]) < 0) {
					list.add(user[j]);// ����û�����������Ա����,��������Ϊ0
				}
			}
			String[] strbusyusrs = strbuf.toString().split(",");
			for (int k = 0; k < strbusyusrs.length; k++) {
				list.add(strbusyusrs[k]);// ��������ش�������Ա
			}
			// list���ŵ��ǰ��������е���Ա
			for (int m = 0; m < num; m++) {
				retusr[m] = (String) list.get(m);
			}
		} else {
			for (int m = 0; m < num; m++) {
				retusr[m] = user[m];
			}
		}
		return retusr;
	}

	private DataList executeProcedure(String tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ɾ���������� add by kangsj on 071109 from WorkflowStorageImpl.classs
	 * 
	 * @throws java.lang.Exception
	 */
	public void deleteWFDef(String flowid) throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wherestr", " flowid='" + flowid + "'");
		/*
		 * <sql> delete from
		 * 
		 * @asis:tablename where
		 * 
		 * @asis:wherestr </sql>
		 */
		deletewf(dp, "dwflowroute");
		deletewf(dp, "dwflownodepost");
		deletewf(dp, "dwopiniondefine");
		deletewf(dp, "dwflownode");
		deletewf(dp, "dwflowmain");
	}

	private void deletewf(UnikMap dp, String tabname) throws Exception {
		dp.put("tablename", tabname);
		executeProcedure(getTpl("DeletaProcedure"), dp);
	}

	/**
	 * �������� add by kangsj on 071109 from WorkflowStorageImpl.class
	 * 
	 * @throws java.lang.Exception
	 */
	public void copyWFDef(String flowid, UnikMap props) throws Exception {
	}

	/**
	 * �õ�TPL����
	 */
	private String getTpl(String procname) {
		StringBuffer className = new StringBuffer(this.getClass().getName());
		int index = className.indexOf("cn.com.jbbis.");

		if (index != -1) {
			className.insert(className.lastIndexOf(".") + 1, "procedure.");
			return "jar:" + className + "_" + procname;
		} else {
			return "jbbis" + procname;
		}
	}

	/**
	 * ������Ϣ��ʾ
	 * 
	 * @param title ��Ϣ����
	 * @return "workflow/"+title
	 */
	private String getMsgInfo(String title) {
		if (title == null || title.length() == 0)
			return null;
		return "workflow/" + title;
	}

	public Object getTaskList(Object msg, String org, String user) throws Exception {
		return null;
	}

	/**
	 * ���������е����ݱ�����������ʷ����ձ�����
	 * 
	 * @param wfid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void delDwtaskVars(String wfid) throws Exception {
		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		/*
		 * <sql name="insertDwtaskVarshisFromDwtaskvars"> insert into dwTaskVarshis
		 * select
		 * 
		 * from dwTaskVars where wfid = @wfid </sql>
		 */
		executeProcedure(getTpl("insertDwtaskVarshisFromDwtaskvars"), map);

		/*
		 * <sql name="deleteDwtaskVars"> DELETE FROM dwTaskVars WHERE wfid = @wfid and
		 * taskser = @number:taskser and varname in (@list:varname) </sql>
		 */
		executeProcedure(getTpl("deleteDwtaskVars"), map);
	}

	/**
	 * ����Ƿ���������Ա,������������ʱ��,ȥ���´�����Ա
	 * 
	 * @param flowid     ���������
	 * @param nodeid     �����ż��ڵ���
	 * @param mindealnum ������䴦������
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void updateDealNum(String flowid, String nodeid, long mindealnum) throws Exception {
		/*
		 * <sql name="updateDealNumFromDwflowNode"> update dwflownode set
		 * mindealnum=@mindealnum where flowid=@flowid and nodeid=@nodeid </sql>
		 */
		UnikMap m = new UnikMap();
		m.put("flowid", flowid);
		m.put("nodeid", nodeid);
		m.setInt("mindealnum", (int) mindealnum);

		executeProcedure(getTpl("updateDealNumFromDwflowNode"), m);
	}

	/**
	 * ������ʷ�б��е���һ���ڱ��.����ж�����¼�᷵�����һ����Ϊnull������.
	 * 
	 * @param wfInstId ���������
	 * @param nodeId   ���ڱ��
	 * @return ���û�в�ѯ���������null
	 * @throws Exception
	 */
	public String getForeNodeId(String wfInstId, String nodeId) throws Exception {
		UnikMap param = new UnikMap();
		param.put("wfid", wfInstId);
		param.put("nodeid", nodeId);
		/*
		 * <sql> select
		 * 
		 * from dwtaskhis where nodeid in ( select forenodeid from dwtaskhis where
		 * wfid=@wfid and nodeid &lt;> forenodeid ORDER by dealsystime desc) and
		 * wfid=@wfid and nodeid=@nodeid </sql>
		 */
		String nodeid = null;
		DataList dl = executeProcedure(getTpl("getForeNodeId"), param);
		while (dl.next()) {
			String s = dl.getString("forenodeid");
			nodeid = s == null ? nodeid : s;
		}
		return nodeid;
	}

	public String formatMessage(String s, Object obj) {
		return "workflow/" + s;// super.formatMessage("workflow/" + s, obj);
	}

	public void saveDwFlowInstTable(DwFlowInstTable dwFlowInstVo) {
		DwFlowInstTableService.save(dwFlowInstVo);
	}
}