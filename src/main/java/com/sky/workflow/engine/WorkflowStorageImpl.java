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
 * Title: 流程引擎与数据库、应用数据相关的处理过程。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: 北京北大青鸟商用信息系统有限公司
 * </p>
 * 
 * @author kangsj@jbbis.com.cn
 * @version 1.2 平台迁移 , 2008-2-22 上午11:12:21
 */
@CacheConfig(cacheNames = "skyWorkflowCache")
@Service("WorkflowStorageImpl")
public class WorkflowStorageImpl implements IWorkflowStorage {

	/** 流程定义表 **/
	@Resource(name = "DwFlowMainTableService")
	private IDwFlowMainTableService DwFlowMainTableService;
	@Resource(name = "DwFlowNodeTableService")
	private IDwFlowNodeTableService DwFlowNodeTableService;
	/** 流程实例表 **/
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
	 * 流程流转过程中所需的参数信息
	 */
	private UnikMap vars = new UnikMap();

	/**
	 * 记录处理人员是否来自历史列表,如果dwtaskhis中查出记录，则此值为'1',没有查出为'2'
	 */
	protected char hisisempty = '0';

	/**
	 * <p>
	 * 读取工作流程定义信息,来自于表dwflowmain
	 * </p>
	 * 
	 * @param flowid 工作流程定义编号
	 * @throws java.lang.Exception
	 */
	public DwFlowMainTable getWorkflowDefinition(String flowid) {

		// 从缓存中取数
		DwFlowMainTable dwflowmainVo = DwFlowMainTableService.getById(flowid);

		// 工作流程定义未找到，请联系维护人员！
		Errors.Assert(dwflowmainVo != null, getMsgInfo("WF_FlowDefNotFound"));

		return dwflowmainVo;
	}

	/**
	 * 读取工作流程实例信息,从表dwflowinst中取数,如果读过一次,不会再去读取数据了,只对当前对象有效。
	 * 
	 * @param wfid 流程实例编号
	 * @return UnikMap wfInst
	 * @throws java.lang.Exception 没有找到流程实例抛出WF_FlowInstNotFound
	 */
	public DwFlowInstTable getWorkflowInstance(String wfid) throws Exception {

		DwFlowInstTable dwFlowInstVo = DwFlowInstTableService.getById(wfid);
		Errors.Assert(dwFlowInstVo != null, getMsgInfo("WF_FlowInstNotFound"));

		return dwFlowInstVo;
	}

	/**
	 * 读取节点信息,可根据节点类型和节点编号来取信息
	 * 
	 * @param flowid   流程编号
	 * @param nodeid   节点编号
	 * @param nodetype 节点类型
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
	 * 得到节点的岗位定义信息
	 * 
	 * @param var 中必须有flowid和nodeid字段信息,否则返回null
	 * @return UnikMap 内有值bankid,postidset
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

		// 从缓存中取数
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
	 * 根据流程节点类型读取节点信息；
	 * 
	 * @param wfDefId 流程编号
	 * @param type    节点类型
	 * @return
	 * @throws java.lang.Exception
	 */
	public DwFlowNodeTable getNodeByType(String wfDefId, String type) throws Exception {
		return getNode(wfDefId, null, type);
	}

	/**
	 * 根据流程节点编号读取节点信息
	 * 
	 * @param flowid 流程编号
	 * @param nodeid 节点编号
	 * @throws java.lang.Exception
	 */
	public DwFlowNodeTable getNodeById(String flowid, String nodeid) throws Exception {
		return getNode(flowid, nodeid, null);
	}

	/**
	 * <p>
	 * 创建流程实例,向dwflowinst表中插入新流程实例数据
	 * </p>
	 * 
	 * @param flowid           应用的flowid
	 * @param wfid             流程实例编号
	 * @param user             用户标识(bankid/operid),
	 * @param wfState          流程状态
	 * @param bizData          流程变量
	 * @param busiOperateStyle 业务处理方式
	 * @throws java.lang.Exception
	 */
	public void createInstance(String flowid, String wfid, String user, String wfState, UnikMap bizData, String busiOperateStyle) throws Exception {

		String users[] = user.split("/");
		DwFlowMainTable dfmt = getWorkflowDefinition(flowid);

		// 将流程描述信息里的变量用参数值替换flowdesc
		String wfdesc = replaceFlowDesc(dfmt.getFlowdesc(), bizData);

		// 组织数据插入dwflowinst表中
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

		// 流程紧急程度
		String instancyLevel = bizData.getString("dwflowinst.instancyLevel");
		instancyLevel = instancyLevel == null ? "1" : instancyLevel;
		dfit.setInstancylevel(instancyLevel);
		dfit.setExecsql(dfmt.getExecsql());
		dfit.setManusql(dfmt.getManusql());
		dfit.setUnitwfid(bizData.getString("unitwfid"));
		dfit.setBusioperatestyle(busiOperateStyle);
		// 将数据插入表dwflowinst中
		DwFlowInstTableService.save(dfit);
	}

	/**
	 * 取当前工作时间加系统时分秒
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
	 * 将流程描述内容用变量进行替换
	 * 
	 * @param wfdesc 流程描述
	 * @param vars   参数
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String replaceFlowDesc(String wfdesc, UnikMap vars) throws Exception {
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			if (value.length() > 200)
				value = value.substring(0, 200) + "..."; // 个别情况需要截取

			wfdesc = StringUtils.replace(wfdesc, "{" + name.toLowerCase() + "}", value);
		}
		return wfdesc;
	}

	/**
	 * 修改流程环境变量,如果更新不成功则进行新增操作,采用的是merge方式。 nodeDesc为null或长度为零，则不更新变量直接返回。
	 * 
	 * @param wfInstId 工作流顺序号，也就是应用中的wfid
	 * @param wfVars   工作流变量
	 * @param taskSer  流程序号
	 * @param wfDefId  工作流编号
	 * @param nodeId   节点编号
	 * @param nodeDesc 节点描述信息
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

		// 删除当前工作流的变量表中的变量
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
	 * System.out.println("================得到工作流变量＝＝＝＝＝＝＝＝＝＝＝"); //删除当前工作流的变量表中的变量
	 * if(buf.length() > 1){ buf.delete(buf.length()-1, buf.length());
	 * 
	 * <sql name="deleteDwtaskvars"> delete from dwtaskvars where wfid=@wfid and
	 * taskser = @number:taskser and varname in (@list:varname) </sql>
	 * 
	 * UnikMap map = new UnikMap(); map.putAll(m); map.put("varname", buf);
	 * executeProcedure(getTpl("deleteDwtaskVars"), map); buf.delete(0,
	 * buf.length()); } System.out.println("================删除工作流变量＝＝＝＝＝＝＝＝＝＝＝");
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
		// 检查是否有这些字段信息

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

		// 流程发起人,去历史列表里找出流程启动人返回就可以了
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
		 * =========================如果配置允许，则第一步先看此节点的用户是否需要从历史列表中进行查找==============================
		 **/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag"), flag);
			// 查到用户不为空则为历史用户 2009-03-27 kangshangjun
			if (user != null) {
				map.put("usrhisflag", "true");
				return user;
			} else {
				map.put("usrhisflag", "false");
			}
		}

		UnikMap dp = new UnikMap();
		/*
		 * 第一步检查此节点是否被处理过,如果处理过没有必要去岗位信息里去查找机构信息了, 如果是开始节点,则直接返回机构/用户信息
		 */
		/*
		 * <sql name="getUserAndNodeType"> select a.bankid, a.operid, b.nodetype from
		 * dwtaskhis a, dwflownode b, dwflowinst c where a.wfid = c.wfid and a.nodeid =
		 * b.nodeid and b.flowid = c.flowid and a.wfid= @wfid and a.nodeid= @nodeid
		 * <sql/>
		 */

		// 此查询实际是多笔的,只所以用SingleResult那是因为所有记录的bankid和nodetype是一样的
		SingleResult sr = new SingleResult();// querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null) {
			// org = sr.getField("bankid"); //liuxj 0715
			// 如果是开始节点可以直接返回查询出来的用户信息,因为开始节点只有一个人处理
			if (CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))) {
				org = (String) sr.getField("bankid"); // liuxj

				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				// log(DEBUG,"WF_NODETYPE_START user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		// 第二步,根据org查找机构信息,如果没有org或post就说明没有配置用户信息
		Errors.Assert(org.length() != 0 || post.length() != 0, formatMessage("PostIdSetNotFound", map.getString("nodeid")));

		// 根据取出的机构信息去查找用户信息
		// log(INFO,"根据取出的机构信息去查找用户信息====post=" + post);
		LinkedList list = new LinkedList();
		// String postid[] = post.split(",");

		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");

		for (int j = 0; j < postid.length; j++) {
			// ====================================================================开始判断机构================================
			String bankid = null;
			String superbankids = "";

			String submitbankid = localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				// 本机构
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

				// 判断只是查找直接上级管理机构还是全部上级管理机构

				// 上级机构
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
				 * Boolean bankdyprod=!"99".equals(bindprodids[j]); if(bankdyprod){//节点中配置了业务范围
				 * dp.put("bindprodid",bindprodids[j]); sr =
				 * querySingle(getTpl("getAllCmbankbyprod"),dp, null); }else{ sr =
				 * querySingle(getTpl("getAllCmbank"),dp, null); }
				 */
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				// 判断是取上级审批机构还是上级管理机构
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
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {// 流程发起机构

				if (bankids[j].length() == 1) {

					// 退回到开始节点所在机构，应该不会出现此种情况
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
						// liuxj 20141216 查找流程发起机构下制定角色的所有用户

						dp.clear();
						dp.put("bankid", sr.getString("bankid")); // 流程发起机构
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

			// ===========================================================结束判断机构===========================

			// 分割流程角色，并组织成多个条件

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
			// 这里修改成允许给自己分配任务，原来不允许获取登录用户自己的信息 liuxj
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
	 * 查找用户信息,返回用户数组,如果是开始节点就直接返回第一次进行处理的业务人员。
	 * 目前处理机构可以查询本机构、上级管理机构、上级审批机构、流程发起机构、指定机构。
	 * 
	 * @param map  map中需要用org,post,local,wfid,nodeid,postbankid这些字段信息
	 *             这些参数可以通过调用getNodePost方法得到,参数字段不可缺的,hisflag是可 选的，决定是否从历史列表中查询处理人员。
	 * @param flag 如果为true则会把username写到返回值中,报文内容bankid/operid|username,false则只会写bankid
	 *             /operid
	 * @param f    决定hisflag的配置是否生效,true:生效,false:失效
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String[] selectUsers(UnikMap map, boolean flag, boolean f) throws Exception {
		String user[] = null;
		// 检查是否有这些字段信息

		String fields[] = { "org", "post", "local", "wfid", "nodeid", "postbankid" };
		Errors.Assert(!WorkFlowFunc.checkField(map, fields), "workflow/MissFields");
		String org = map.getString("org"); // * 表示流程发起人
		String post = map.getString("post");
		String SuperBankScope = map.getString("SuperBankScope");
		String local[] = map.getString("local").split("/");
		String localBankid = local[0];
		String localOperid = local.length == 2 ? local[1] : null;
		String wfid = map.getString("wfid");
		String nodeid = map.getString("nodeid");
		String bindprodid = map.getString("bindprodid");

		// 流程发起人,去历史列表里找出流程启动人返回就可以了
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
		 * =========================如果配置允许，则第一步先看此节点的用户是否需要从历史列表中进行查找==============================
		 **/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag"), flag);
			// 查到用户不为空则为历史用户 2009-03-27 kangshangjun
			if (user != null) {
				map.put("usrhisflag", "true");
				return user;
			} else {
				map.put("usrhisflag", "false");
			}
		}

		UnikMap dp = new UnikMap();
		/*
		 * 第一步检查此节点是否被处理过,如果处理过没有必要去岗位信息里去查找机构信息了, 如果是开始节点,则直接返回机构/用户信息
		 */
		/*
		 * <sql name="getUserAndNodeType"> select a.bankid, a.operid, b.nodetype from
		 * dwtaskhis a, dwflownode b, dwflowinst c where a.wfid = c.wfid and a.nodeid =
		 * b.nodeid and b.flowid = c.flowid and a.wfid= @wfid and a.nodeid= @nodeid
		 * <sql/>
		 */

		// 此查询实际是多笔的,只所以用SingleResult那是因为所有记录的bankid和nodetype是一样的
		SingleResult sr = new SingleResult();// querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null) {
			// org = sr.getField("bankid"); //liuxj 0715
			// 如果是开始节点可以直接返回查询出来的用户信息,因为开始节点只有一个人处理
			if (CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))) {
				org = (String) sr.getField("bankid"); // liuxj

				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				// log(DEBUG,"WF_NODETYPE_START user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		// 第二步,根据org查找机构信息,如果没有org或post就说明没有配置用户信息
		Errors.Assert(org.length() != 0 || post.length() != 0, formatMessage("PostIdSetNotFound", map.getString("nodeid")));

		// 根据取出的机构信息去查找用户信息
		// log(INFO,"根据取出的机构信息去查找用户信息====post=" + post);
		LinkedList list = new LinkedList();
		// String postid[] = post.split(",");

		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");

		for (int j = 0; j < postid.length; j++) {
			// ====================================================================开始判断机构================================
			String bankid = null;
			String superbankids = "";

			String submitbankid = localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				// 本机构
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {

				// 判断只是查找直接上级管理机构还是全部上级管理机构

				// 上级机构
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
				 * Boolean bankdyprod=!"99".equals(bindprodids[j]); if(bankdyprod){//节点中配置了业务范围
				 * dp.put("bindprodid",bindprodids[j]); sr =
				 * querySingle(getTpl("getAllCmbankbyprod"),dp, null); }else{ sr =
				 * querySingle(getTpl("getAllCmbank"),dp, null); }
				 */
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				// 判断是取上级审批机构还是上级管理机构
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
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {// 流程发起机构

				// Errors.Assert(bankids[j].length() == 1, getMsgInfo("WF_BankIdError"));

				// 退回到开始节点所在机构，应该不会出现此种情况
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
				 * // 退回到开始节点所在机构，应该不会出现此种情况 user = new String[1]; dp.clear(); dp.put("wfid",
				 * wfid);
				 * 
				 * <sql name="getAllDwtaskhis"> select
				 *
				 * from dwtaskhis where wfid = @wfid #AND = @nodeid order by taskser </sql>
				 * 
				 * sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				 * 
				 * if (sr != null) { //liuxj 20141216 查找流程发起机构下制定角色的所有用户
				 * 
				 * dp.clear(); dp.put("bankid", sr.getString("bankid")); //流程发起机构
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
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)) {// 流程发起机构及其上级审批机构 20141227

				if (bankids[j].length() == 1) {
					// 退回到开始节点所在机构，应该不会出现此种情况
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 * <sql name="getAllDwtaskhis"> select * from dwtaskhis where wfid = @wfid #AND
					 * = @nodeid order by taskser </sql>
					 */
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);

					if (sr != null) {
						// 判断获取直接上级审批机构还是所有上级审批机构
						String scope = SuperBankScopes[j];
						if (scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL)) // 所有上级审批机构
						{
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); // 流程发起机构
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
						} else // 上级审批机构
						{
							// 得到流程发起机构及其上级机构
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); // 流程发起机构
							DataList superBankList;
							superBankList = executeProcedure(getTpl("getsuperapprbank"), superUM);

							while (superBankList.next()) {
								// superbankids += superbankids.equals("") ? "'" +
								// superBankList.getString("bankid") + "'" : ",'" +
								// superBankList.getString("bankid") + "'";
								superbankids += superbankids.equals("") ? superBankList.getString("superapprbankid") : "," + superBankList.getString("superapprbankid");
							}
							if (!superbankids.equals("")) // 并入发起机构
							{
								superbankids += "," + sr.getString("bankid");
							}
						}

						bankid = superbankids;

						// 查找流程发起机构下指定角色的所有用户 liuxj 20141216
						/*
						 * dp.clear(); dp.put("bankid", superbankids); //流程发起机构及其上级审批机构 dp.put("operid",
						 * localOperid); //dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
						 * dp.put("postidset", postid[j]); String tpl = getTpl("getuserininitsuperorg");
						 * //得到流程发起机构及其上级审批机构的所有用户 DataList dl = executeProcedure(tpl, dp); while
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

			// ===========================================================结束判断机构===========================

			// 分割流程角色，并组织成多个条件

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
			// 这里修改成允许给自己分配任务，原来不允许获取登录用户自己的信息 liuxj
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
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)) {// 流程发起机构及其上级审批机构 20141227
				dp.put("bankid", bankid); // 流程发起机构及其上级审批机构
				tpl = getTpl("getuserininitsuperorg"); // 得到流程发起机构及其上级审批机构的所有用户
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
	 * 检查是否用重复用户
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
	 * 根据配置去查找,如果配置的是原路走下去,则去dwtaskhis表中找出已经处理过的人员进行分配。
	 * 如果此节点不是第一次处理，则先查找此节点是否需要分配给上次处理过的人员进行处理，如果是
	 * 则从历史表中查出上次处理人员，进行任务分配，如果不是则按分配策略进行分配。
	 * 
	 * @see #selectUsers
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap map, boolean flag) throws Exception {
		return selectUsers(map, flag, true);
	}

	/**
	 * 根据机构号和岗位编号查询用户列表,目前只处理一个岗位 此处的上级机构是指:取上级审批机构信息,并非上级管理机构
	 * 
	 * @param org    当前节点机构号
	 * @param post   岗位
	 * @param local  机构/用户
	 * @param wfid   流程编号,
	 * @param nodeId 节点编号
	 * @return String[] 用户信息如下bankid/userid|username
	 * @deprecated As of JDK version 1.1,
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(String org, String post, String local, String bindprodid, String wfid, String nodeId) throws Exception {
		return selectUsers(org, post, local, bindprodid, wfid, nodeId, false);
	}

	/**
	 * map中需要有org,post,local,wfid,nodeid,postbankid等参数
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
	 * 从历史列表中查找符合岗位的用户。
	 * 
	 * @param wfInstId 流程编号
	 * @param nodeId   节点编号
	 * @param flag     如果为true则会把username写到返回值中,报文内容bankid/operid|username,false则只会写bankid
	 *                 /operid
	 * @param tasktype 任务类型，1：查看任务用户 2：处理任务用户
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
		// 没有找到符合该岗位的用户，请检查流程定义中的'流程节点岗位定义'!
		if (tasktype == 2) // 只对处理用户进行限制
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
	 * 从历史列表中查找符合岗位的用户。
	 * 
	 * @param wfInstId   流程编号
	 * @param nodeId     节点编号
	 * @param flag       如果为true则会把username写到返回值中,报文内容bankid/operid|username,false则只会写bankid
	 *                   /operid
	 * @param intHistory -1 不按照本字段查询，0：当前用户 1：历史任务用户
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
		// 没有找到符合该岗位的用户，请检查流程定义中的'流程节点岗位定义'!
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
	 * 根据任务分配策略中的hisFlag字段来判断，是否从历史列表中查找任务处理人员。
	 * 
	 * @param wfid    流程编号
	 * @param nodeid  节点编号
	 * @param hisFlag 是否进行历史人员查询
	 * @param flag    是否显示用户姓名
	 * @return 任务处理人员列表,如果hisFlag=0或hisFlag='1'但是处理过程中出异常了，都会返回null
	 * @throws Exception
	 */
	public String[] selectHistoryUser(String wfid, String nodeid, String hisFlag, boolean flag) throws Exception {
		String[] users = null;
		if (CommonConst.WF_HISUSER_ON.equals(hisFlag)) { // 从历史列表中查询处理人员
			try {
				users = selectTaskUsers(wfid, nodeid, "1", flag);
				hisisempty = '1';
			} catch (Exception e) { // 此异常99%是没有查询出记录，所以不需要处理
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
	 * 读取某个流程下的所有未处理任务
	 * 
	 * @param wfInstId 流程编号
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

		// System.out.println(dl!=null ? dl.countRows() + "行" : "0行");
		return dl;
	}

	/**
	 * 读取任务列表,把dwtask中的未处理数据读取出来
	 * 
	 * @param wfid 流程编号
	 * @param user 用户
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
	 * 读取任务列表,把dwtask中的未处理任一数据读取出来
	 * 
	 * @param wfInstId 流程编号
	 * @param user     用户
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
	 * 创建新的任务列表,将需要处理的任务放到dwtask表中
	 * 
	 * @param currentNode 节点信息
	 * @param wfid        实例编号
	 * @param taskSerial  序号
	 * @param users       处理用户
	 * @param viewUsers   查看用户
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int newTask(DwFlowNodeTable currentNode, String wfid, int taskSerial, String[] users, String[] viewUsers, UnikMap bizData, ArrayList taskList) throws Exception {
		// 获取轮次
		int taskRound = getTaskMaxTaskRound(wfid);
		DwFlowInstTable dwFlowInstVo = getWorkflowInstance(wfid);
		String busiOperateStyle = dwFlowInstVo.getBusioperatestyle();
		String assignmindealnumstyle = currentNode.getAssignmindealnumstyle();

		// 任务提交时间
		Date receDate = new Date();
		String receTime = new SimpleDateFormat("yyyyMMddHHmmss").format(receDate);
		StringBuffer buf = new StringBuffer();

		// 分配处理用户
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

			// 将数据插入dwtask表中
			DwTaskTableService.save(dwTaskVo);

			// 添加任务输出
			taskList.add(taskSerial);
			taskSerial++;
		}

		// 分配查看用户
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

			// 将数据插入dwtask表中
			DwTaskTableService.save(dwTaskVo);
			taskSerial++;
		}

		return taskSerial - 1;
	}

	/**
	 * 查找dwtask表中的最大轮次记录
	 * 
	 * @param var var中需要有 wfid
	 * @return int 最大轮次
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
	 * 更新任务的最大轮次
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
	 * 删除任务的最大轮次 任务结束的时候
	 */
	public void deleteTaskRound(String wfid) throws Exception {
		DwTaskRoundTable dwTaskRoundVo = new DwTaskRoundTable();
		dwTaskRoundVo.setWfid(wfid);
		DwTaskRoundTableService.delete(dwTaskRoundVo);
	}

	/**
	 * 更新任务的最大轮次
	 */
	public void insertTaskRound(String wfid, int taskRound) throws Exception {
		DwTaskRoundTable dwTaskRoundVo = new DwTaskRoundTable();
		dwTaskRoundVo.setWfid(wfid);
		dwTaskRoundVo.setTaskround(taskRound);
		dwTaskRoundVo.setLastchangetime(new Date());
		DwTaskRoundTableService.save(dwTaskRoundVo);
	}

	/**
	 * ???暂未实现
	 * <p>
	 * 写流程实例对照表
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
	 * 查找dbBusiWFMap表中的记录
	 * 
	 * @param var var中需要有 wfid
	 * @return boolean 是否存在
	 * @throws java.lang.Exception
	 */
	public boolean checkBusiWFMapIsExist(String wfid) throws Exception {
		DbBusiWfMapTable record = DbBusiWfMapTableService.getById(wfid);
		return record != null ? true : false;
	}

	/**
	 * 查找dbBusiWFMap表中的最大序号
	 * 
	 * @param var var中需要有 wfid
	 * @return int 最大序号
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
	 * 查找cmbank表中的机构信息
	 * 
	 * @param bankid
	 * @return SingleResult 一条记录
	 * @throws java.lang.Exception
	 */
	public SingleResult getBankInfo(String bankid) throws Exception {

		UnikMap map = new UnikMap();
		map.put("bankid", bankid);

		SingleResult sr = performAction("", "cmbank", map);

		return sr;
	}

	/**
	 * 查找cmuser表中的用户信息
	 * 
	 * @param userid
	 * @return SingleResult 一条记录
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
		// 从缓存里面取数
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
	 * 查找dbBusiWFMap表中的信息
	 * 
	 * @param var var中需要有 wfid
	 * @return SingleResult 一条记录
	 * @throws java.lang.Exception
	 */
	public DbBusiWfMapTable getBusiWFMap(String wfid) {
		DbBusiWfMapTable dbBusiWfMapVo = DbBusiWfMapTableService.getById(wfid);
		return dbBusiWfMapVo;
	}

	/**
	 * 重新调查进行映射表更改处理
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
	 * 更改流程实例业务对照表
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
	 * ???暂未实现 更改流程实例业务对照表
	 */
	public boolean updateBusiWFMap(UnikMap node, String wfInstId, String curOperUser, String[] users) throws Exception {
		boolean blSuccess = false;
		try {
			// 任务提交时间
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
			// 查找用户名称和机构名称
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
			// 查找用户名称和机构名称
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
			// 任务提交时间
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

				// 查找用户名称和机构名称
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
	 * 更改流程实例业务对照表
	 */
	public boolean updateNormalBusiWFMap(UnikMap node, String wfInstId, String curOperUser) throws Exception {
		boolean blSuccess = false;
		try {
			String curUser[] = curOperUser.split("/");
			String curOperId = curUser[1];
			String curBankId = curUser[0];
			String curOperIdName = "";
			String curBankName = "";

			// 查找用户名称和机构名称
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
	 * 更改流程实例业务对照表的完成标识
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
	 * 删除流程实例业务对照表
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
	 * 查找dwtask表中的记录
	 * 
	 * @param dwTaskVo 中需要有 wfid ,nodeid
	 * @return int 记录总条数
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
	 * 查找dwtask表中的浏览权限任务记录
	 * 
	 * @param var var中需要有 wfid ,nodeid
	 * @return int 记录总条数
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
	 * 检查流程状态是否发布,非发布状态不允许提交业务
	 * 
	 * @param flowid 流程编号
	 * @throws java.lang.Exception 初始化抛出WFStateIsInit，流程被暂停使用抛出 WFStateIsPause
	 *         流程被禁用抛出WFStateIsStop
	 */
	public void checkFlowState(String flowid) throws Exception {
		DwFlowMainTable dfmt = getWorkflowDefinition(flowid);
		String flowname = dfmt.getFlowname();
		String flowstate = dfmt.getFlowstate();

		// 是否还处于初始化
		Errors.Assert(!CommonConst.WF_STATE_INIT.equals(flowstate), formatMessage("WFStateIsInit", flowname));

		// 是否被暂停使用
		Errors.Assert(!CommonConst.WF_STATE_PAUSE.equals(flowstate), formatMessage("WFStateIsPause", flowname));

		// 是否被禁用,主要是新提交的业务,因为正在处理的业务已被转手工了
		Errors.Assert(!CommonConst.WF_STATE_STOP.equals(flowstate), formatMessage("WFStateIsStop", flowname));
	}

	/**
	 * 删除所有任务
	 * 
	 * @param wfInstId
	 * @throws Exception
	 */
	public void completeAllTask(String wfInstId) throws Exception {

		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);

		// 删除dwTask中的数据
		// DataContext exec = new DataContext();
		executeProcedure(getTpl("deletealltask"), dp);

	}

	/**
	 * 完成当前工作任务。 2007.5.16 增加了设置节点描述字段的更新，原来是在newTas方法中进行，因为要求将
	 * 审批意见（提交交易时做），放入描述字段所以只能在完成任务时更新了。
	 * 
	 * @param wfid    流程实例编号
	 * @param taskser 任务序号
	 * @param bizData 参数
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("rawtypes")
	public void completeTask(String wfid, DwTaskTable dwTaskVo, UnikMap bizData, DwFlowNodeTable currentNode) throws Exception {
		String taskdesc = currentNode.getNodedesc();

		// 此算法保证读取的变量是最新保存的,重新从dwtaskvars中读取变量信息
		getInstVar(wfid, true);

		// 从最新的变量信息中取值去替换taskdesc中的变量字段
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			taskdesc = StringUtils.replace(taskdesc, "{" + name.toLowerCase() + "}", value);
		}

		Date dealdate = new Date();
		String dealtime = getWorkdateCurrTime();
		String dealsystime = JBDate.getSysCurrTime(CommonConst.DATE_TYPE_TIMESTAMP);
		// 将dwtask表中的记录存入dwtaskhis中
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

		// 删除dwTask中的数据
		DwTaskTableKey key = new DwTaskTableKey();
		key.setWfid(wfid);
		key.setTaskser(dwTaskVo.getTaskser());
		DwTaskTableService.deleteByPrimaryKey(key);
	}

	/**
	 * 最后处理人
	 * 
	 * @return
	 */
	public boolean isLastestHandler(UnikMap node, UnikMap bizData, boolean blStart) throws Exception {
		String taskoverpolicy = node.getString("taskoverpolicy"); // 任务完成策略
		int num = 1; // 任务需要完成的人数
		int mindealnum = node.getInt("mindealnum"); // 任务分配最少处理人数;
		String assignmindealnumstyle = bizData.getString("assignmindealnumstyle"); // 是否分配最少处理人数，最少处理人数为手动选择的用户数
		// log(DEBUG, "assignmindealnumstyle=" + assignmindealnumstyle);
		if (assignmindealnumstyle != null && assignmindealnumstyle.equals("1")) // =================这里是任务分配人数
		{
			String assignMinDealNum = bizData.getString("assignMinDealNum");
			mindealnum = Integer.valueOf(assignMinDealNum);
		}
		// log(DEBUG, "assignMinDealNum=" + mindealnum);
		// 得到某个流程下某个结点下的任务数
		int tasknum = getTaskNum(bizData); // dwtask表中还剩下的任务数
		// 如果是分配所有人员,那么它的任务分配最少处理人数为所有人员
		if (CommonConst.WF_TASKASSIGN_ALL.equals(node.getString("autodisuserflag"))) {
			mindealnum = tasknum + num; // 处理人数为任务数加上已处理的一条
		}
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) {
			num = node.getInt("assignminnum"); // 取配置的处理人数
		} else if (CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)) {
			double percent = node.getDouble("overpercent") / 100; // 取出百分比
			num = WorkFlowFunc.ceil(mindealnum * percent); // 最少处理人数乘以百分比取整
		} else if (CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)) {
			num = mindealnum; // 必须所有都完成
		}
		// 分配人数减去最少完成人数等于未完成的任务数或任务列表中没有任务就认为当前任务已全部完成
		boolean flag = (mindealnum - num) == tasknum || (blStart && (mindealnum - num) == 0 && tasknum == 1) || tasknum == 0 ? true : false;
		return flag;

	}

	/**
	 * 判断是否进行任务分配,根据下一节点的任务分配策略进行处理
	 * 
	 * @throws java.lang.Exception
	 */
	public boolean isContinue(DwFlowNodeTable currentNode, DwTaskTable dwTaskVo, boolean blStart) throws Exception {
		String taskoverpolicy = currentNode.getTaskoverpolicy();// 任务完成策略
		Long num = 1L;// 任务需要完成的人数
		Long mindealnum = currentNode.getMindealnum();// 任务分配最少处理人数;
		String assignmindealnumstyle = dwTaskVo.getAssignmindealnumstyle();// 是否分配最少处理人数，最少处理人数为手动选择的用户数
		if (assignmindealnumstyle != null && assignmindealnumstyle.equals("1")) // =================这里是任务分配人数
		{
			Long assignMinDealNum = dwTaskVo.getAssignmindealnum();
			mindealnum = assignMinDealNum;
		}
		// 得到某个流程下某个结点下的任务数
		int tasknum = getTaskNum(dwTaskVo); // dwtask表中还剩下的任务数
		// 如果是分配所有人员,那么它的任务分配最少处理人数为所有人员
		if (CommonConst.WF_TASKASSIGN_ALL.equals(currentNode.getAutodisuserflag())) {
			mindealnum = tasknum + num; // 处理人数为任务数加上已处理的一条
		}
		if (CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)) {
			num = currentNode.getAssignminnum(); // 取配置的处理人数
		} else if (CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)) {
			double percent = currentNode.getOverpercent() / 100; // 取出百分比
			num = WorkFlowFunc.ceil(mindealnum * percent) + 0L; // 最少处理人数乘以百分比取整
		} else if (CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)) {
			num = mindealnum; // 必须所有都完成
		}
		// 分配人数减去最少完成人数等于未完成的任务数或任务列表中没有任务就认为当前任务已全部完成
		long restTaskCount = mindealnum - num;
		boolean flag = (restTaskCount == tasknum || (blStart && restTaskCount == 0 && tasknum == 1) || tasknum == 0) ? true : false;
		// 如果任务处理完毕且任务表中还存在未处理任务,则清空任务表
		if (flag && tasknum > 0) {
			/*
			 * <sql name="deleteFromDwtask"> DELETE FROM DWTASK WHERE WFID=@wfid AND NODEID
			 * = @nodeid </sql>
			 */
			// executeProcedure(getTpl("deleteFromDwtask"), dwTaskVo);
		}
		// 清理当前浏览任务
		// 得到某个流程下某个结点下的任务数
		/*
		 * int viewtasknum = getViewTaskNum(dwTaskVo); //dwtask表中还剩下的任务数 if(flag &&
		 * viewtasknum>0) { executeProcedure(getTpl("deleteFromDwtask"), dwTaskVo); }
		 */
		return flag;
	}

	/**
	 * 更新流程实例中的任务编号
	 */
	public void updateTaskSerial(String wfid, int taskser) throws Exception {
		updateTaskSerial(wfid, taskser, null);
	}

	/**
	 * 更新流程实例的流程状态
	 */
	public void updateWorkflowStatus(String wfid, int status) throws Exception {
		DwFlowInstTable dwFlowInstVo = new DwFlowInstTable();
		dwFlowInstVo.setWfid(wfid);
		dwFlowInstVo.setWfstate(status + "");
		DwFlowInstTableService.updateByPrimaryKeySelective(dwFlowInstVo);
	}

	/**
	 * 更新流程实例中的任务编号和紧急程度
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
	 * 返回编号
	 * 
	 * @param name 编号名称
	 * @param len  编号的长度
	 * @throws RunTimeException
	 */
	public String getGlobalNum(String name, int len) throws Exception {
		String str = getSerialNumByAbbrv("global", name);
		return calFillString(str, len, "0") + str;
	}

	/**
	 * 根据机构号和拼音缩写生成流水号
	 * 
	 * @param sBankId 机构号
	 * @param sAbbrv  拼音缩写
	 * @return 流水号
	 * @throws RunTimeException
	 */
	private String getSerialNumByAbbrv(String sBankId, String sAbbrv) throws Exception {
		String abbrv = sAbbrv.trim();
		String bankId = sBankId.trim();

		// 修改用参数
		UnikMap nvalues = new UnikMap();
		// nvalues.put("amtnum", new SerNoUpdator());
		nvalues.put("bankid", bankId);
		nvalues.put("serid", abbrv);

		// DataContext exectx = new DataContext();
		// exectx.set(NEWTRANSACTION);
		// exectx.table = "cmserno";
		// 新增用参数
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
	 * 从节点定义描述字段根中读取那些变量需要保存
	 * 
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] getVarNames(String desc, String wfDefId, String nodeid) throws Exception {
		StringBuffer buf = new StringBuffer();
		String arrs[] = StringUtils.split(desc, "{");
		boolean flag = false;
		// 读取节点定义中的变量
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
		// 读取节点路由中用到的变量

		/*
		 * <sql name="getAllFromDwFlowRoute"> Select
		 *
		 * From dwFlowRoute Where flowid=@flowid and nodeid=@nodeid </sql>
		 */

		// 以下代码写了没有意义，不知道为什么要这样，不过还是给译过来了！！！！ 没有意义还不屏蔽，那不是浪费资源
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
	 * 取节点路由信息
	 * 
	 * @param wfDefId flowid
	 * @return nodeId nodeid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList getNodeRoute(String wfDefId, String nodeId) throws Exception {
		LinkedList list = new LinkedList();
		// 从缓存中取数
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
	 * 得到参数列表,主要是给路由器作参数使用
	 * 
	 * @param wfid   工作流实例编号
	 * @param reload 是否重载参数,reload=false并且已有参数时不会再去查询数据库
	 * @return UnikMap vars参数列表
	 * @throws java.lang.Exception
	 */
	public UnikMap getInstVar(String wfid, boolean reload) throws Exception {
		if (!reload && vars.size() != 0)
			return vars;

		/*
		 * //DwTaskVarsTableService. UnikMap dp = new UnikMap(); dp.put("wfid", wfid);
		 * dp.put("order", "asc"); DataList dl =
		 * executeProcedure(getTpl("getAllDwTaskVars"), dp); while (dl.next()) { //
		 * 此算法保证读取的变量是最新保存的 String name = dl.getString("varname"); String value =
		 * dl.getString("varvalue"); vars.put(name, value); }
		 */

		return vars;
	}

	/**
	 * 结束流程
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
	 * 回退到开始节点时需要处理的一些事情。
	 */
	public void doWithDraw(String flowid, String wfid) throws Exception {
		String execsql = getWorkflowDefinition(flowid).getExecsql();
		;
		if (execsql.length() == 0)
			return;
		String esql[] = StringUtils.split(execsql, ";");
		// 得到对应流程实例的变量信息
		UnikMap m = getInstVar(wfid, false);
		for (int i = 0; i < esql.length; i++) {
			String sql = esql[i];

			// 变量替换；
			while (true) {
				int pos1 = sql.indexOf("{");
				int pos2 = sql.indexOf("}");
				if (pos1 > 0 && pos2 > 2) {
					String name = sql.substring(pos1 + 1, pos2);
					String s = m.getString(name.toLowerCase());
					if (s.length() > 0) {
						sql = StringUtils.replace(sql, "{" + name.toLowerCase() + "}", s);
					} else {
						// 如果s没有取到值,此处不提示的话,就是一个死循环
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
	 * 调用应用服务，注意应用服务中"s_"开头的字段都没有
	 * 
	 * @param v 执行交易所以参数,一般为request
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
		 * <option value="10" text="同意"/> <option value="11" text="同意并上报"/> <option
		 * value="20" text="不同意"/> <option value="30" text="再议"/> <option value="40"
		 * text="打回"/> <option value="50" text="重新整理会议记录"/> <option value="60"
		 * text="同意会议记录"/> String dealopin = req.getString("dealopin");
		 * 
		 * if("同意".equals(dealopin)) { req.put("dealopin", "10"); } else
		 * if("同意并上报".equals(dealopin)) { req.put("dealopin", "11"); } else
		 * if("不同意".equals(dealopin)) { req.put("dealopin", "20"); } else
		 * if("再议".equals(dealopin)) { req.put("dealopin", "30"); } else
		 * if("打回".equals(dealopin)) { req.put("dealopin", "40"); } else
		 * if("重新整理会议记录".equals(dealopin)) { req.put("dealopin", "50"); } else
		 * if("同意会议记录".equals(dealopin)) { req.put("dealopin", "60"); }
		 */

		return new AppResponse();// invoke(tid, req);
	}

	/**
	 * 调用应用服务，注意应用服务中"s_"开头的字段都没有
	 * 
	 * @param v 执行交易所以参数,一般为request
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
	 * 在一个服务中调用另一个服务. invoke(code, request.withparam("a:b,c,d"));
	 * 
	 * @param code 服务代码
	 */
	/*
	 * public AppResponse invoke(String code, AppRequest req) throws Exception{
	 * context.save(); context.setRequest(req); req.setHeader("TranCode", code); try
	 * { AppModule s = application.prepareModule(code, context); return s.invoke();
	 * } finally { context.restore(); } }
	 */

	/**
	 * 返回处理任务最少的用户
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
					list.add(user[j]);// 加载没有任务处理的人员名称,任务条数为0
				}
			}
			String[] strbusyusrs = strbuf.toString().split(",");
			for (int k = 0; k < strbusyusrs.length; k++) {
				list.add(strbusyusrs[k]);// 按升序加载待处理人员
			}
			// list里存放的是按升序排列的人员
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
	 * 删除整个流程 add by kangsj on 071109 from WorkflowStorageImpl.classs
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
	 * 复制流程 add by kangsj on 071109 from WorkflowStorageImpl.class
	 * 
	 * @throws java.lang.Exception
	 */
	public void copyWFDef(String flowid, UnikMap props) throws Exception {
	}

	/**
	 * 得到TPL名字
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
	 * 返回消息提示
	 * 
	 * @param title 消息名字
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
	 * 将变量表中的数据保存至变量历史表并清空变量表
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
	 * 如果是分配所有人员,则在任务分配的时候,去更新处理人员
	 * 
	 * @param flowid     工作流编号
	 * @param nodeid     任务编号即节点编号
	 * @param mindealnum 任务分配处理人数
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
	 * 返回历史列表中的上一环节编号.如果有多条记录会返回最后一条不为null的数据.
	 * 
	 * @param wfInstId 工作流序号
	 * @param nodeId   环节编号
	 * @return 如果没有查询出结果返回null
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