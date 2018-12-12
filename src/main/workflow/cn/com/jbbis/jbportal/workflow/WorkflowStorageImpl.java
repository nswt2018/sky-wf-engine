package cn.com.jbbis.jbportal.workflow;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;

import cn.com.jbbis.afx.AppContext;
import cn.com.jbbis.afx.AppModule;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.ehcahe.EhcacheTools;
import cn.com.jbbis.common.function.SerialNum;
import cn.com.jbbis.common.util.JBDate;
import cn.com.jbbis.jbportal.BizLogic;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.sql.BatchParams;
import cn.com.jbbis.sql.ColumnUpdator;
import cn.com.jbbis.sql.Columns;
import cn.com.jbbis.sql.DataContext;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.Errors;
import cn.com.jbbis.util.JBMath;
import cn.com.jbbis.util.NamedException;
import cn.com.jbbis.util.StringUtil;
import cn.com.jbbis.util.Strings;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>Title: �������������ݿ⡢Ӧ��������صĴ�����̡�</p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: ������������������Ϣϵͳ���޹�˾ </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.2 ƽ̨Ǩ�� , 2008-2-22 ����11:12:21
 */
public class WorkflowStorageImpl extends BizLogic implements WorkflowStorage {

	/**
	 * �������̶�����Ϣ,���dwflowmain��Ϣ
	 */
	private UnikMap wfDefs = null;
	
	/**
	 * ��������ʵ����Ϣ,���dwflowinst��Ϣ
	 */
	private UnikMap wfInsts = null;
	
	/**
	 * ������ת����������Ĳ�����Ϣ
	 */
	private UnikMap vars = new UnikMap();
	
	/**
	 * ��¼������Ա�Ƿ�������ʷ�б�,���dwtaskhis�в����¼�����ֵΪ'1',û�в��Ϊ'2'
	 */
	protected char hisisempty = '0';

	public WorkflowStorageImpl(AppContext ctx) {
		set(ctx);
	}
	
	/**
	 * @deprecated Method WorkflowStorageImpl is deprecated
	 */
	public WorkflowStorageImpl(Service service, AppContext context){
		this(context);
	}
	
	/**
	 * ��ȡ�������̶�����Ϣ,�����ڱ�dwflowmain
	 * @param wfDefId �������̶�����
	 * @throws java.lang.Exception
	 */
	public UnikMap getWorkflowDefinition(String wfDefId) throws Exception {
		
		wfDefs = wfDefs == null ? new UnikMap() : wfDefs;
		
		Object obj = wfDefs.get(wfDefId);
		if(obj != null){
			return (UnikMap)obj;
		}
		
		//����ȡ��ͬ����,���Դ˴���SingleResult
		/*
		 <sql name="getAllDwflowmain">
		 	select 
		 		* 
		 	from 
		 		dwflowmain 
		 	where 
		 		flowid = @wfDefId
		 </sql>
		 */

		UnikMap dp = new UnikMap();
		dp.put("wfdefid", wfDefId);
		
		//�ӻ�����ȡ��
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		UnikMap dwflowmainVo = cacheTools.queryDwflowmain(wfDefId);
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryDwflowmain cost:"+(endTime_-beginTime_));
		//�������̶���δ�ҵ�������ϵά����Ա��
		Errors.Assert(dwflowmainVo != null, getMsgInfo("WF_FlowDefNotFound"));
		
		//���̶�����Ϣд��wfDef
		wfDefs.put(wfDefId, dwflowmainVo);
		
		return (UnikMap)dwflowmainVo;
	}

	/**
	 * ��ȡ��������ʵ����Ϣ,�ӱ�dwflowinst��ȡ��,�������һ��,������ȥ��ȡ������,ֻ�Ե�ǰ������Ч��
	 * @param wfInstId ����ʵ�����
	 * @return UnikMap wfInst 
	 * @throws java.lang.Exception û���ҵ�����ʵ���׳�WF_FlowInstNotFound
	 */
	public UnikMap getWorkflowInstance(String wfInstId) throws Exception {
		
		wfInsts = wfInsts == null ? new UnikMap() : wfInsts;
		
		Object obj = wfInsts.get(wfInstId);
		if (obj != null)
			return (UnikMap) obj;
		/*
		 <sql name="getAllDwFlowInst">
		 	select 
		 		* 
		 	from 
		 		dwFlowInst 
		 	where 
		 		wfid = @wfInstId
		 </sql>
		 */
		
		UnikMap dp = new UnikMap();
		dp.put("wfinstid", wfInstId);
		
		SingleResult sr = querySingle(getTpl("getAllDwFlowInst"), dp,
				null);

		Errors.Assert(sr != null, getMsgInfo("WF_FlowInstNotFound"));
		
		wfInsts.put(wfInstId, sr);
		
		return (UnikMap)sr;
	}

	/**
	 * ��ȡ�ڵ���Ϣ,�ɸ��ݽڵ����ͺͽڵ�����ȡ��Ϣ
	 * 
	 * @param wfDefId
	 *            ���̱��
	 * @param nodeId
	 *            �ڵ���
	 * @param nodetype
	 *            �ڵ�����
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public UnikMap getNode(String wfDefId, String nodeId,String nodetype)throws Exception {
		
		Errors.Assert(wfDefId != null && wfDefId.length() != 0,
				getMsgInfo("WF_NeedwfDefId"));		
		/*
		 <sql name="getAllDwFlowNode">
		 	select 
		 		* 
		 	from 
		 		dwFlowNode 
		 	where 
		 		1 = 1
		 		#AND =@flowid
		 		#AND =@nodetype
		 		#AND =@nodeid
		 </sql>
		 */
		UnikMap wfNodeDef = new UnikMap();
		
		UnikMap dp = new UnikMap();
		dp.put("flowid", wfDefId);
		
		//�ӻ�����ȡ��
		EhcacheTools cacheTools = new EhcacheTools();
		UnikMap dwflowmainVo = null;
		
		String abcdef = null;
		
		
		if(nodeId != null && nodeId.length() != 0) {
			//ͨ��dwDefId �� nodeId ȡֵ
			long beginTime_ = System.currentTimeMillis();
			dwflowmainVo = cacheTools.queryDwflownodeByKey(wfDefId, nodeId);
			long endTime_ = System.currentTimeMillis();
			log(Service.INFO, "---------get cache queryDwflownodeByKey cost:"+(endTime_-beginTime_));
		}else if(nodetype!=null && nodetype.length() != 0 ){
			//ͨ��wfdefid��nodetypeȡֵ
			long beginTime_ = System.currentTimeMillis();
			List<UnikMap> listVo = cacheTools.queryDwflownodeByType(wfDefId, nodetype);
			long endTime_ = System.currentTimeMillis();
			log(Service.INFO, "---------get cache cmfieldinfo cost:"+(endTime_-beginTime_));
			if(listVo.size()<=0)
				log(Service.INFO, "����["+wfDefId+"]�ڻ���δ�ҵ��Ľڵ�!");
			Errors.Assert(listVo != null && listVo.size()>0, getMsgInfo("WF_FlowNodeNotFound"));
			dwflowmainVo = listVo.get(0);
		}
		/*else {
			//ͨ��dwdefidȡֵ
			List<UnikMap> listVo = cacheTools.queryDwflownodeByType(wfDefId);
			 
			dwflowmainVo = listVo.get(0);


		}*/
		Errors.Assert(dwflowmainVo != null, getMsgInfo("WF_FlowNodeNotFound"));
		long beginTime_ = System.currentTimeMillis();
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache cmfieldinfo cost:"+(endTime_-beginTime_));

		//���ڵ���Ϣ�������
		wfNodeDef.putAll(dwflowmainVo);

		//ֻ�ǻ�ȡ�������̽�ɫ
		//wfNodeDef.put("postauthority","1");
		//�ڽڵ���Ϣ�����Ӹ�λ��Ϣ
		wfNodeDef.putAll(getNodePost(wfNodeDef));

		return wfNodeDef;
	}
	
	/**
	 * �õ��ڵ�ĸ�λ������Ϣ
	 * 
	 * @param var
	 *            �б�����flowid��nodeid�ֶ���Ϣ,���򷵻�null
	 * @return UnikMap ����ֵbankid,postidset
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodePost(UnikMap var) throws Exception{
		/*
		 <sql name="getAllDwFlowNodePost">
		 	select 
		 		bankid,
		 		postid,
		 		postbankid
		 	from 
		 		dwFlowNodePost 
		 	where 
		 		flowid = @flowid
		 		and nodeid = @nodeid
		 </sql>
		 */
		//if(var.getString("flowid").length()==0 || var.getString("nodeid").length() == 0)
		//	return null;
		
		//�ӻ�����ȡ��
		String postAuthority = (String)var.get("postauthority");
		
		EhcacheTools cacheTools = new EhcacheTools();
		
		List<UnikMap> list ;
		
		if(postAuthority==null || postAuthority.equals("") || postAuthority.equalsIgnoreCase("null")){
			long beginTime_ = System.currentTimeMillis();
			list = cacheTools.queryDwflownodepost(var.getString("flowid"), var.getString("nodeid"));
			long endTime_ = System.currentTimeMillis();
			log(Service.INFO, "---------get cache queryDwflownodepost cost:"+(endTime_-beginTime_));
		}
		else{
			long beginTime_ = System.currentTimeMillis();
			list = cacheTools.queryDwflownodepost(var.getString("flowid"), var.getString("nodeid"),postAuthority);
			long endTime_ = System.currentTimeMillis();
			log(Service.INFO, "---------get cache queryDwflownodepost cost:"+(endTime_-beginTime_));
		}
		
		
		StringBuffer bankid = new StringBuffer();
		StringBuffer postidset = new StringBuffer();
		StringBuffer postbankid = new StringBuffer();
		StringBuffer superbankscope = new StringBuffer();
		StringBuffer bindprodid = new StringBuffer();
		
		for(int i=0;i<list.size();i++){
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
		
		if (bankidlen > 1){
			bankid = bankid.delete(bankidlen-1, bankidlen);
		}
		if(postidlen > 1){
			postidset = postidset.delete(postidlen - 1, postidlen);
		}
		if(postbankidlen > 1){
			postbankid = postbankid.delete(postbankidlen - 1, postbankidlen);
		}
		if(superbankscopelen > 1){
			superbankscope = superbankscope.delete(superbankscopelen - 1, superbankscopelen);
		}
		if(bindprodidlen > 1){
			bindprodid = bindprodid.delete(bindprodidlen - 1, bindprodidlen);
		}
		log(INFO,"bankid=" + bankid.toString());
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
	 * @param wfDefId ���̱��
	 * @param type �ڵ�����
	 * @return UnikMap
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodeByType(String wfDefId, String type) throws Exception {
		return getNode(wfDefId, null, type);
	}

	/**
	 * �������̽ڵ��Ŷ�ȡ�ڵ���Ϣ
	 * @param wfDefId ���̱��
	 * @param nodeId �ڵ���
	 * @throws java.lang.Exception
	 */
	public UnikMap getNodeById(String wfDefId, String nodeId) throws Exception {
		return getNode(wfDefId, nodeId, null);
	}

	/**
	 * ��������ʵ��,��dwflowinst���в���������ʵ������
	 * @param wfDefId Ӧ�õ�flowid
	 * @param wfInstId ����ʵ�����
	 * @param user �û���ʶ(bankid+operid),
	 * @param wfState ����״̬
	 * @throws java.lang.Exception
	 */
	public void createInstance(String wfDefId, String wfInstId, String user,
			String wfState, UnikMap var,String busiOperateStyle) throws Exception {
		String users[] = user.split("/");

		UnikMap wfDef = getWorkflowDefinition(wfDefId);
		//������������Ϣ��ı����ò���ֵ�滻
		String wfdesc = replaceFlowDesc(wfDef.getString("flowdesc"), var);
		/*
		 <sql name="getFlowType">
		 	select 
				FlowType
			from 
				cmBankElecScope 
			where 
				flowid=@flowid
		 </sql>
		 */
		//��֯���ݲ���dwflowinst����
		UnikMap dp = new UnikMap();
		dp.put("flowid", wfDefId);
		//�ӻ�����ȡ��
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		//UnikMap sr = cacheTools.queryOneDwflownodepost(wfDefId);
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryOneDwflownodepost cost:"+(endTime_-beginTime_));
		dp.put("flowtype", wfDef.getString("flowtype"));
		dp.put("wfid", wfInstId);
		dp.put("flowname", wfDef.get("flowname"));
		dp.put("flowdesc",wfdesc);
		this.log(DEBUG,"debug======================================wfDef.get(\"flowname\")=" + wfDef.get("flowname"));
		this.log(DEBUG,"debug======================================wfdesc=" + wfdesc);
		dp.put("bankid", users[0]);
		dp.put("operid", users[1]);
		dp.put("lastchgdate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		dp.put("creattime", getWorkdateCurrTime());
		dp.put("lastchgtime", getWorkdateCurrTime());
		dp.put("wfstate", wfState);
		dp.setInt("taskser", 1);
		//���̽����̶�
		String instancyLevel = var.getString("dwflowinst.instancyLevel");
		instancyLevel = instancyLevel == null ? "1" : instancyLevel;
		dp.put("instancylevel", instancyLevel);
		
		dp.put("execsql", wfDef.getString("execsql"));
		dp.put("manusql", wfDef.getString("manusql"));
		dp.put("unitwfid", var.getString("unitwfid"));
		
		dp.put("busiOperateStyle", busiOperateStyle);
		
		//�����ݲ����dwflowinst��
		performAction(Service.INSERT, "dwflowinst", dp);
	}
	
	/**
	 * ȡ��ǰ����ʱ���ϵͳʱ����
	 * @throws Exception
	 */
	public String getWorkdateCurrTime() throws Exception{
		return JBDate.getWorkDateCurrTime(getSysdate());
	}
	
	private String getSysdate(){
		return user.getCache("sysdate");
	}
	
	
	/**
	 * ���������������ñ��������滻
	 * @param wfdesc ��������
	 * @param vars ����
	 * @return
	 */
	public String replaceFlowDesc(String wfdesc,UnikMap vars) throws Exception{
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			if(value.length()>200)
				value = value.substring(0, 200) + "...";  //���������Ҫ��ȡ
			
			wfdesc = StringUtil.replace(wfdesc, "{" + name.toLowerCase() + "}", value);
		}
		return wfdesc;
	}

	/**
	 * �޸����̻�������,������²��ɹ��������������,���õ���merge��ʽ�� nodeDescΪnull�򳤶�Ϊ�㣬�򲻸��±���ֱ�ӷ��ء�
	 * 
	 * @param wfInstId
	 *            ������˳��ţ�Ҳ����Ӧ���е�wfid
	 * @param wfVars
	 *            ����������
	 * @param taskSer
	 *            �������
	 * @param wfDefId
	 *            ���������
	 * @param nodeId
	 *            �ڵ���
	 * @param nodeDesc
	 *            �ڵ�������Ϣ
	 * 
	 * @throws Exception
	 *             :java.lang.Exception
	 */
	public void updateWorkflowVariables(String wfInstId, UnikMap wfVars,
			int taskSer, String wfDefId, String nodeId, String nodeDesc)
			throws Exception {
		if (nodeDesc == null || nodeDesc.length() == 0){
			log(DEBUG,"WF_IN_updateWorkflowVariables: nodeDesc value is null so return");
			return;
		}
		UnikMap m = new UnikMap();
		m.put("wfid",wfInstId);
		//m.put("taskser", 1);
		m.put("nodeid", nodeId);
		m.put("lastchgdate", getSysdate());
		m.put("lastchgtime", getWorkdateCurrTime());
		
		DataList dl = executeProcedure(getTpl("getAllDwTaskVars"), m);
		StringBuffer buf = new StringBuffer();
		
		/*Iterator its = wfVars.keySet().iterator();
		while(its.hasNext())
		{	
			String key = (String)its.next();
			String varValue = (String)wfVars.get(key);
				
		}
		*/
		while(dl.next()){
		
			String varName = dl.getString("varname");		
			Iterator it = wfVars.keySet().iterator();
			
			while(it.hasNext())
			{	
				String key = (String)it.next();
				String varValue = (String)wfVars.get(key);
				if(varName.equalsIgnoreCase(key))
				{

					buf.append(varName).append(",");
				}
			}
			
		}
		

		//ɾ����ǰ�������ı������еı���  
		if(buf.length() > 1){
			buf.delete(buf.length()-1, buf.length());
						
			UnikMap map = new UnikMap();
			map.putAll(m);
			map.put("varname", buf);
			executeProcedure(getTpl("deleteDwtaskVars"), map);
			buf.delete(0, buf.length());
		}


		m.put("taskser", 1);
		String tpl = getTpl("insertDwtaskvars");
		//System.out.println("tpl=" + tpl);
		AcParams param =  new AcParams(m,wfVars);
		
		/*log(DEBUG,"++++++++++++++++++++++++++++++++++++++tpl=" + tpl);		
		for(Iterator it = wfVars.keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			String value = wfVars.getField(key);
			log(DEBUG,"++++++++++++++++++++++++++++++++++++++key="+value+"; value="+value);
		}*/
		
		executeProcedure(tpl,param);

	}
	
	/*public void updateWorkflowVariables(String wfInstId, UnikMap wfVars,
			int taskSer, String wfDefId, String nodeId, String nodeDesc)
			throws Exception {
		if (nodeDesc == null || nodeDesc.length() == 0){
			log(DEBUG,"WF_IN_updateWorkflowVariables: nodeDesc value is null so return");
			return;
		}
		UnikMap m = new UnikMap();
		m.put("wfid",wfInstId);
		//m.put("taskser", taskSer);
		m.put("taskser", 1);
		m.put("nodeid", nodeId);
		System.out.println("------------------------------------"+wfInstId+"\t"+taskSer+"\t"+nodeId+"\t"+wfVars);
		
		 <sql name="getAllDwtaskVars">
		 	select 
				wfid,
				taskser,
				nodeid,
				varname,
				varvalue
			from 
				 dwTaskVars 
			where 
				 wfid = @wfid
				 #AND = @taskser
				 #AND = @varname
			#if @order
				 order by taskser asc
			#end 
		 </sql>
		 
		
		DataList dl = executeProcedure(getTpl("getAllDwTaskVars"), m);
		StringBuffer buf = new StringBuffer();
		while(dl.next()){
			buf.append(dl.getString("varname")).append(",");
		}
		System.out.println("================�õ���������������������������������");
		//ɾ����ǰ�������ı������еı���  
		if(buf.length() > 1){
			buf.delete(buf.length()-1, buf.length());
			
			 <sql name="deleteDwtaskvars">
			 	delete from 
			 		dwtaskvars
			 	where
			 		wfid=@wfid
			 		and taskser = @number:taskser
			 		and varname in (@list:varname)
			 </sql>
			 
			UnikMap map = new UnikMap();
			map.putAll(m);
			map.put("varname", buf);
			executeProcedure(getTpl("deleteDwtaskVars"), map);
			buf.delete(0, buf.length());
		}
		System.out.println("================ɾ����������������������������������");
		
		 <sql name="insertDwtaskvars">
		 	insert into dwtaskvars(
		 		wfid,
		 		taskser,
		 		nodeid,
		 		varname,
		 		varvalue
		 	) values(
		 		@wfid,
		 		@number:taskser,
		 		@nodeid,
		 		@varname,
		 		@varvalue
		 	)
		 <sql>
		 

//		DataContext exectx = new DataContext();
//		exectx.set(NEWTRANSACTION);
//		exectx.table = "Dwtaskvars";
		
		//wfVars.getString("")
		String tpl = getTpl("insertDwtaskvars");
		System.out.println("tpl=" + tpl);
		AcParams param =  new AcParams(m,wfVars);
		
		executeProcedure(tpl,param);

	}
*/
	
	public String[] selectHistoryUsers(UnikMap map,boolean flag,boolean f) throws Exception{
		String user[] = null;
		//����Ƿ�����Щ�ֶ���Ϣ

		String fields[] = {"org","post","local","wfid","nodeid","postbankid"};
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
		if(CommonConst.WF_POSTID_WFINITUSER.equals(org)){

			UnikMap params = new UnikMap();
			params.put("wfid", wfid);
			params.put("nodetype", CommonConst.WF_NODETYPE_START);
			/*
			 //getTaskInitUser
			 	select 
					a.*,
					d.username 
				from 
					dwtaskhis a,
					dwflownode b,
					dwflowinst c,
					cmuser d
				where 
					a.wfid = c.wfid
					and c.flowid = b.flowid
					and a.operid = d.userid
					and a.wfid = @wfid
					and b.nodetype= @nodetype
			 */
			SingleResult sr = querySingle(getTpl("getTaskInitUser"), params, null);
			Errors.Assert(sr != null, "workflow/StartNodeTaskNotFound");
			StringBuffer buf = new StringBuffer();
			buf.append(sr.getField("bankid")).append("/").append(sr.getField("operid"));
			if(flag)
				buf.append("|").append(sr.getField("username"));
			String[] s = {buf.toString()};
					
			return s;
		}
		
		/**=========================��������������һ���ȿ��˽ڵ���û��Ƿ���Ҫ����ʷ�б��н��в���==============================**/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag") , flag);
			//�鵽�û���Ϊ����Ϊ��ʷ�û� 2009-03-27 kangshangjun
			if (user != null){
				map.put("usrhisflag", "true");
				return user;
			}else{
				map.put("usrhisflag", "false");
			}
		}
		
		UnikMap dp = new UnikMap();
		/*��һ�����˽ڵ��Ƿ񱻴����,��������û�б�Ҫȥ��λ��Ϣ��ȥ���һ�����Ϣ��,
		����ǿ�ʼ�ڵ�,��ֱ�ӷ��ػ���/�û���Ϣ
		*/
		/*
		 <sql name="getUserAndNodeType">
		 	select 
				a.bankid,
				a.operid,
				b.nodetype 
			from 
				dwtaskhis a,
				dwflownode b,
				dwflowinst c 
			where 
				a.wfid = c.wfid 
				and a.nodeid = b.nodeid 
				and b.flowid = c.flowid
				and a.wfid= @wfid
				and a.nodeid= @nodeid
		 <sql/>
		 */
		
		//�˲�ѯʵ���Ƕ�ʵ�,ֻ������SingleResult������Ϊ���м�¼��bankid��nodetype��һ����
		SingleResult sr = querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null){
			//org = sr.getField("bankid");  //liuxj 0715
			//����ǿ�ʼ�ڵ����ֱ�ӷ��ز�ѯ�������û���Ϣ,��Ϊ��ʼ�ڵ�ֻ��һ���˴���
			if(CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))){
				org = sr.getField("bankid");  //liuxj
				
				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				log(DEBUG,"WF_NODETYPE_START  user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		//�ڶ���,����org���һ�����Ϣ,���û��org��post��˵��û�������û���Ϣ
		Errors.Assert(org.length()!=0 || post.length() !=0, formatMessage("PostIdSetNotFound", map.getField("nodeid")));

		//����ȡ���Ļ�����Ϣȥ�����û���Ϣ
		log(INFO,"����ȡ���Ļ�����Ϣȥ�����û���Ϣ====post=" + post);
		LinkedList list = new LinkedList();
		//String postid[] = post.split(",");
		
		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");
		
		
		for (int j = 0; j < postid.length; j++) {
			//====================================================================��ʼ�жϻ���================================
			String bankid = null;
			String superbankids = "";
			
			String submitbankid=localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				//������
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) 
					|| CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {
				
				//�ж�ֻ�ǲ���ֱ���ϼ������������ȫ���ϼ��������

				// �ϼ�����
				/*
				 <sql name="getAllCmbank">
				 	select 
				 		*
				 	from 
				 		cmbank 
				 	where 
				 		bankid = @bankid
				 		#AND = @bankcode
				 </sql>
				 */				

				dp.clear();
				dp.put("bankid", submitbankid);
				
				//liuxj
				sr = querySingle(getTpl("getAllCmbank"),dp, null);
								
				/*
				Boolean bankdyprod=!"99".equals(bindprodids[j]);
				if(bankdyprod){//�ڵ���������ҵ��Χ
					dp.put("bindprodid",bindprodids[j]);
					sr = querySingle(getTpl("getAllCmbankbyprod"),dp, null);
				}else{
					sr = querySingle(getTpl("getAllCmbank"),dp, null);
				}
				*/
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				//�ж���ȡ�ϼ��������������ϼ��������
				String scope = SuperBankScopes[j];
				if(CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])){
					
					bankid = sr.getString("superbankid");
					//String superbankids = "";
					if(scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
					{
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList ;
						
						//liuxj
						superBankList=executeProcedure(getTpl("getallsuperbank"), superUM);
					/*	if(bankdyprod){
							superBankList=executeProcedure(getTpl("getallsuperbankbyprod"), superUM);
						}else{
							superBankList=executeProcedure(getTpl("getallsuperbank"), superUM);
						}
						*/
						while(superBankList.next())
						{
							superbankids+= superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					//bankid = superbankids;
				}else{
					bankid = sr.getString("superapprbankid");
					if(scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
					{						
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList ;
						
						//liuxj
						superBankList=executeProcedure(getTpl("getallsuperapprbank"), superUM);
						/*
						if(bankdyprod){
							superBankList=executeProcedure(getTpl("getallsuperapprbankbyprod"), superUM);
						}else{
							superBankList=executeProcedure(getTpl("getallsuperapprbank"), superUM);
						}
						*/
						while(superBankList.next())
						{
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					//bankid = superbankids;
				}
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {//���̷������
				
				if (bankids[j].length() == 1) {

					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 <sql name="getAllDwtaskhis">
					 	select 
					 		*
					 	from 
					 		dwtaskhis 
					 	where 
					 		wfid = @wfid
					 		#AND = @nodeid
					 		order by taskser
					 </sql>
					 */
					
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
					if (sr != null) {
						//liuxj  20141216  �������̷���������ƶ���ɫ�������û�
						
						dp.clear();
						dp.put("bankid", sr.getString("bankid"));  //���̷������
						dp.put("operid", localOperid);
						//dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
						dp.put("postidset", postid[j]);
						String tpl = getTpl("getUseridFromCmuser");
						DataList dl = executeProcedure(tpl, dp);
						
						while (dl.next()) 
						{
							String value = dl.getString("bankid") + "/" + dl.getString("userid");
							//String value =  dl.getString("userid");
							if (flag) {
								value += "|" + dl.getString("username");
							}
							list.add(list.size(),value);
						}
						Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser"));
						if(list.size() > 0){
							user = (String[])list.toArray(new String[0]);
						}
						/*user[0] = sr.getString("bankid") + "/"
								+ sr.getString("operid");*/
						
					}
					/*if (sr != null) {
						user[0] = sr.getString("bankid") + "/"
								+ sr.getString("operid");
					}
*/
					return user;
				} else{
					Errors.Assert(bankids[j].length() == 2, getMsgInfo("WF_BankIdError"));
				}

				int level = JBMath.getInt(bankids[j].substring(1));
				String banks[] = null;
				
				dp.clear();
				dp.put("wfid", wfid);

				sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				
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
				
				sr = querySingle(getTpl("getAllCmbank"), dp, null);
				
				//System.out.println("============================sr = querySingle(getTpl(");
				
				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				
				bankid = sr.getString("bankid");
			} else if(CommonConst.WF_POSTID_ASSIGN.equals(bankids[j])){
				bankid = map.getString("postbankid");
			} else{
				bankid = bankids[j];
			}
			
			//===========================================================�����жϻ���===========================
			
			//�ָ����̽�ɫ������֯�ɶ������
			
			/*
			 <sql name="getUseridFromCmuser">
			 	select 
			 		userid,
			 		username
			 	from 
			 		cmuser 
			 	where 
			 		bankid = @bankid
			 		#AND = @userLoginstate
			 		and userid != @operid
			 		and (PostIdSet like @'%,{PostIdSet},%'
					or PostIdSet like @'{PostIdSet},%'
					or PostIdSet like @'%,{PostIdSet}' 
					or PostIdSet = @PostIdSet)
			 </sql>
			 */
			
			dp.clear();
			
			dp.put("operid", localOperid);
			//dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
			dp.put("postidset", postid[j]);
			
			//System.out.println("============================executeProcedure(getTpl");
			//�����޸ĳ�������Լ���������ԭ���������ȡ��¼�û��Լ�����Ϣ liuxj
			DataList dl = new DataList();
			String tpl = "";
			if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) 	
			{
				if( SuperBankScopes[j].equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
				{
					dp.put("bankid", superbankids);
					tpl = getTpl("getallsuperuserid");	
				}
				else
				{
					dp.put("bankid", bankid);
					tpl = getTpl("getUseridFromCmuser");
				}	
			}
			else	
			{
				dp.put("bankid", bankid);
				tpl = getTpl("getUseridFromCmuser");
			}
			
			dl = executeProcedure(tpl, dp);
			
			while (dl.next()) 
			{
				String value = dl.getString("bankid") + "/" + dl.getString("userid");
				//String value =  dl.getString("userid");
				if (flag) {
					value += "|" + dl.getString("username");
				}
				list.add(list.size(),value);
			}
		}
		Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser"));
		if(list.size() > 0){
			user = (String[])list.toArray(new String[0]);
		}
		
		/*for(int i=0;i<user.length;i++)
		{
			System.out.println("============================user=" + user[i]);
		}*/
		return user;
	}
	
	/**
	 * �����û���Ϣ,�����û�����,����ǿ�ʼ�ڵ��ֱ�ӷ��ص�һ�ν��д����ҵ����Ա��
	 * Ŀǰ����������Բ�ѯ���������ϼ�����������ϼ��������������̷��������ָ��������
	 * 
	 * @param map
	 *            map����Ҫ��org,post,local,wfid,nodeid,postbankid��Щ�ֶ���Ϣ
	 *            ��Щ��������ͨ������getNodePost�����õ�,�����ֶβ���ȱ��,hisflag�ǿ�
	 *            ѡ�ģ������Ƿ����ʷ�б��в�ѯ������Ա��
	 * @param flag
	 *            ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *            /operid
	 * @param f ����hisflag�������Ƿ���Ч,true:��Ч,false:ʧЧ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap map,boolean flag,boolean f) throws Exception{
		String user[] = null;
		//����Ƿ�����Щ�ֶ���Ϣ

		String fields[] = {"org","post","local","wfid","nodeid","postbankid"};
		Errors.Assert(!WorkFlowFunc.checkField(map, fields), "workflow/MissFields");
		String org = map.getString("org"); //* ��ʾ���̷�����
		String post = map.getString("post");
		String SuperBankScope = map.getString("SuperBankScope");
		String local[] = map.getString("local").split("/");
		String localBankid = local[0];
		String localOperid = local.length == 2 ? local[1] : null;
		String wfid = map.getString("wfid");
		String nodeid = map.getString("nodeid");
		String bindprodid = map.getString("bindprodid");
		
		
		// ���̷�����,ȥ��ʷ�б����ҳ����������˷��ؾͿ�����
		if(CommonConst.WF_POSTID_WFINITUSER.equals(org)){

			UnikMap params = new UnikMap();
			params.put("wfid", wfid);
			params.put("nodetype", CommonConst.WF_NODETYPE_START);
			/*
			 //getTaskInitUser
			 	select 
					a.*,
					d.username 
				from 
					dwtaskhis a,
					dwflownode b,
					dwflowinst c,
					cmuser d
				where 
					a.wfid = c.wfid
					and c.flowid = b.flowid
					and a.operid = d.userid
					and a.wfid = @wfid
					and b.nodetype= @nodetype
			 */
			SingleResult sr = querySingle(getTpl("getTaskInitUser"), params, null);
			Errors.Assert(sr != null, "workflow/StartNodeTaskNotFound");
			StringBuffer buf = new StringBuffer();
			buf.append(sr.getField("bankid")).append("/").append(sr.getField("operid"));
			if(flag)
				buf.append("|").append(sr.getField("username"));
			String[] s = {buf.toString()};
					
			return s;
		}
		
		/**=========================��������������һ���ȿ��˽ڵ���û��Ƿ���Ҫ����ʷ�б��н��в���==============================**/
		if (f) {

			user = selectHistoryUser(wfid, nodeid, map.getString("hisflag") , flag);
			//�鵽�û���Ϊ����Ϊ��ʷ�û� 2009-03-27 kangshangjun
			if (user != null){
				map.put("usrhisflag", "true");
				return user;
			}else{
				map.put("usrhisflag", "false");
			}
		}
		
		UnikMap dp = new UnikMap();
		/*��һ�����˽ڵ��Ƿ񱻴����,��������û�б�Ҫȥ��λ��Ϣ��ȥ���һ�����Ϣ��,
		����ǿ�ʼ�ڵ�,��ֱ�ӷ��ػ���/�û���Ϣ
		*/
		/*
		 <sql name="getUserAndNodeType">
		 	select 
				a.bankid,
				a.operid,
				b.nodetype 
			from 
				dwtaskhis a,
				dwflownode b,
				dwflowinst c 
			where 
				a.wfid = c.wfid 
				and a.nodeid = b.nodeid 
				and b.flowid = c.flowid
				and a.wfid= @wfid
				and a.nodeid= @nodeid
		 <sql/>
		 */
		
		//�˲�ѯʵ���Ƕ�ʵ�,ֻ������SingleResult������Ϊ���м�¼��bankid��nodetype��һ����
		SingleResult sr = querySingle(getTpl("getUserAndNodeType"), map,null);

		if (sr != null){
			//org = sr.getField("bankid");  //liuxj 0715
			//����ǿ�ʼ�ڵ����ֱ�ӷ��ز�ѯ�������û���Ϣ,��Ϊ��ʼ�ڵ�ֻ��һ���˴���
			if(CommonConst.WF_NODETYPE_START.equals(sr.getField("nodetype"))){
				org = sr.getField("bankid");  //liuxj
				
				user = new String[1];
				user[0] = org + "/" + sr.getField("operid");
				log(DEBUG,"WF_NODETYPE_START  user[0]=" + user[0]);
				return user;
			}
		}
//		System.out.println("==================================sr == null");
		//�ڶ���,����org���һ�����Ϣ,���û��org��post��˵��û�������û���Ϣ
		Errors.Assert(org.length()!=0 || post.length() !=0, formatMessage("PostIdSetNotFound", map.getField("nodeid")));

		//����ȡ���Ļ�����Ϣȥ�����û���Ϣ
		log(INFO,"����ȡ���Ļ�����Ϣȥ�����û���Ϣ====post=" + post);
		LinkedList list = new LinkedList();
		//String postid[] = post.split(",");
		
		String postid[] = post.split("\\|");
		String bankids[] = org.split("\\|");
		String SuperBankScopes[] = SuperBankScope.split("\\|");
		String bindprodids[] = bindprodid.split("\\|");
		
		
		for (int j = 0; j < postid.length; j++) {
			//====================================================================��ʼ�жϻ���================================
			String bankid = null;
			String superbankids = "";
			
			String submitbankid=localBankid;
			if (CommonConst.WF_POSTID_LOCAL.equalsIgnoreCase(bankids[j])) {
				//������
				bankid = submitbankid;
			} else if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) 
					|| CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) {
				
				//�ж�ֻ�ǲ���ֱ���ϼ������������ȫ���ϼ��������

				// �ϼ�����
				/*
				 <sql name="getAllCmbank">
				 	select 
				 		*
				 	from 
				 		cmbank 
				 	where 
				 		bankid = @bankid
				 		#AND = @bankcode
				 </sql>
				 */				

				dp.clear();
				dp.put("bankid", submitbankid);
				
				//liuxj
				sr = querySingle(getTpl("getAllCmbank"),dp, null);
								
				/*
				Boolean bankdyprod=!"99".equals(bindprodids[j]);
				if(bankdyprod){//�ڵ���������ҵ��Χ
					dp.put("bindprodid",bindprodids[j]);
					sr = querySingle(getTpl("getAllCmbankbyprod"),dp, null);
				}else{
					sr = querySingle(getTpl("getAllCmbank"),dp, null);
				}
				*/
				Errors.Assert(sr != null, formatMessage("BankIdNotFound", localBankid));
				//�ж���ȡ�ϼ��������������ϼ��������
				String scope = SuperBankScopes[j];
				if(CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])){
					
					bankid = sr.getString("superbankid");
					//String superbankids = "";
					if(scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
					{
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList ;
						
						//liuxj
						superBankList=executeProcedure(getTpl("getallsuperbank"), superUM);
					/*	if(bankdyprod){
							superBankList=executeProcedure(getTpl("getallsuperbankbyprod"), superUM);
						}else{
							superBankList=executeProcedure(getTpl("getallsuperbank"), superUM);
						}
						*/
						while(superBankList.next())
						{
							superbankids+= superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					//bankid = superbankids;
				}else{
					bankid = sr.getString("superapprbankid");
					if(scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
					{						
						UnikMap superUM = new UnikMap();
						superUM.put("bankid", bankid);
						superUM.put("bindprodid", bindprodids[j]);
						DataList superBankList ;
						
						//liuxj
						superBankList=executeProcedure(getTpl("getallsuperapprbank"), superUM);
						/*
						if(bankdyprod){
							superBankList=executeProcedure(getTpl("getallsuperapprbankbyprod"), superUM);
						}else{
							superBankList=executeProcedure(getTpl("getallsuperapprbank"), superUM);
						}
						*/
						while(superBankList.next())
						{
							superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
						}
					}
					//bankid = superbankids;
				}
			} else if (bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT)) {//���̷������
				
				   // Errors.Assert(bankids[j].length() == 1, getMsgInfo("WF_BankIdError"));

					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 <sql name="getAllDwtaskhis">
					 	select 
					 		*
					 	from 
					 		dwtaskhis 
					 	where 
					 		wfid = @wfid
					 		#AND = @nodeid
					 		order by taskser
					 </sql>
					 */
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
					
					Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
					bankid = sr.getString("bankid");
			

				
				/*if (bankids[j].length() == 1) {

					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					
					 <sql name="getAllDwtaskhis">
					 	select 
					 		*
					 	from 
					 		dwtaskhis 
					 	where 
					 		wfid = @wfid
					 		#AND = @nodeid
					 		order by taskser
					 </sql>
					 
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
					
					if (sr != null) {
						//liuxj  20141216  �������̷���������ƶ���ɫ�������û�
						
						dp.clear();
						dp.put("bankid", sr.getString("bankid"));  //���̷������
						dp.put("operid", localOperid);
						//dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
						dp.put("postidset", postid[j]);
						String tpl = getTpl("getUseridFromCmuser");
						DataList dl = executeProcedure(tpl, dp);
						
						while (dl.next()) 
						{
							String value = dl.getString("bankid") + "/" + dl.getString("userid");
							//String value =  dl.getString("userid");
							if (flag) {
								value += "|" + dl.getString("username");
							}
							list.add(list.size(),value);
						}
						Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser"));
						if(list.size() > 0){
							user = (String[])list.toArray(new String[0]);
						}
						user[0] = sr.getString("bankid") + "/"
								+ sr.getString("operid");
						
					}

					return user;
				} else{
					Errors.Assert(bankids[j].length() == 2, getMsgInfo("WF_BankIdError"));
				}
*/
				/*int level = JBMath.getInt(bankids[j].substring(1));
				String banks[] = null;
				
				dp.clear();
				dp.put("wfid", wfid);

				sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				
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
				
				sr = querySingle(getTpl("getAllCmbank"), dp, null);
				
				//System.out.println("============================sr = querySingle(getTpl(");
				
				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				
				bankid = sr.getString("bankid");*/
			}else if(bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)){//���̷�����������ϼ���������   20141227
				
				if (bankids[j].length() == 1) {
					// �˻ص���ʼ�ڵ����ڻ�����Ӧ�ò�����ִ������
					user = new String[1];
					dp.clear();
					dp.put("wfid", wfid);
					/*
					 <sql name="getAllDwtaskhis">
					 	select * 
					 	from 
					 		dwtaskhis 
					 	where 
					 		wfid = @wfid
					 		#AND = @nodeid
					 		order by taskser
					 </sql>
					 */
					sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
					
					if (sr != null){
						//�жϻ�ȡֱ���ϼ������������������ϼ���������
						String scope = SuperBankScopes[j];
						if(scope.equals(CommonConst.WF_SUPERBANKSCOPE_ALL))  //�����ϼ��������� 
						{
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); //���̷������
							DataList superBankList ;
							superBankList=executeProcedure(getTpl("getallsuperapprbank"), superUM);
							
							while(superBankList.next())
							{
								//superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
								superbankids += superbankids.equals("") ?  superBankList.getString("bankid") : "," + superBankList.getString("bankid") ;
							}
							if(!superbankids.equals(""))
							{
								superbankids += "," + sr.getString("bankid");
							}
						}
						else  //�ϼ��������� 
						{
							//�õ����̷�����������ϼ�����
							UnikMap superUM = new UnikMap();
							superUM.put("bankid", sr.getString("bankid")); //���̷������
							DataList superBankList ;
							superBankList=executeProcedure(getTpl("getsuperapprbank"), superUM);
							
							while(superBankList.next())
							{
								//superbankids += superbankids.equals("") ? "'" + superBankList.getString("bankid") + "'" : ",'" + superBankList.getString("bankid") + "'";
								superbankids += superbankids.equals("") ?  superBankList.getString("superapprbankid") : "," + superBankList.getString("superapprbankid") ;
							}
							if(!superbankids.equals("")) //���뷢�����
							{
								superbankids += "," + sr.getString("bankid");
							}
						}
						
						bankid = superbankids;
						
						//�������̷��������ָ����ɫ�������û�  liuxj 20141216 
					/*	dp.clear();
						dp.put("bankid", superbankids);  //���̷�����������ϼ���������
						dp.put("operid", localOperid);
						//dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
						dp.put("postidset", postid[j]);
						String tpl = getTpl("getuserininitsuperorg");  //�õ����̷�����������ϼ����������������û�
						DataList dl = executeProcedure(tpl, dp);						
						while (dl.next()){
							String value = dl.getString("bankid") + "/" + dl.getString("userid");
							if (flag){
								value += "|" + dl.getString("username");
							}
							list.add(list.size(),value);
						}
						Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser"));
						if(list.size() > 0){
							user = (String[])list.toArray(new String[0]);
						}*/
					}

					//return user;
				} else{
					Errors.Assert(bankids[j].length() == 2, getMsgInfo("WF_BankIdError"));
				}

			/*	int level = JBMath.getInt(bankids[j].substring(1));
				String banks[] = null;
				
				dp.clear();
				dp.put("wfid", wfid);

				sr = querySingle(getTpl("getAllDwtaskhis"), dp, null);
				
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
				
				sr = querySingle(getTpl("getAllCmbank"), dp, null);
				
				//System.out.println("============================sr = querySingle(getTpl(");
				
				Errors.Assert(sr != null, getMsgInfo("WF_NotFoundBank"));
				
				bankid = sr.getString("bankid");*/
			
				
			}else if(CommonConst.WF_POSTID_ASSIGN.equals(bankids[j])){
				bankid = map.getString("postbankid");
			} else{
				bankid = bankids[j];
			}
			
			//===========================================================�����жϻ���===========================
			
			//�ָ����̽�ɫ������֯�ɶ������
			
			/*
			 <sql name="getUseridFromCmuser">
			 	select 
			 		userid,
			 		username
			 	from 
			 		cmuser 
			 	where 
			 		bankid = @bankid
			 		#AND = @userLoginstate
			 		and userid != @operid
			 		and (PostIdSet like @'%,{PostIdSet},%'
					or PostIdSet like @'{PostIdSet},%'
					or PostIdSet like @'%,{PostIdSet}' 
					or PostIdSet = @PostIdSet)
			 </sql>
			 */
			
			dp.clear();
			
			dp.put("operid", localOperid);
			//dp.put("userLoginstate", CommonConst.WF_USERLOGON_IN);
			dp.put("postidset", postid[j]);
			
			//System.out.println("============================executeProcedure(getTpl");
			//�����޸ĳ�������Լ���������ԭ���������ȡ��¼�û��Լ�����Ϣ liuxj
			DataList dl = new DataList();
			String tpl = "";
			if (CommonConst.WF_POSTID_SUPERAPPR.equalsIgnoreCase(bankids[j]) || CommonConst.WF_POSTID_SUPERMGR.equals(bankids[j])) 	
			{
				if( SuperBankScopes[j].equals(CommonConst.WF_SUPERBANKSCOPE_ALL))
				{
					dp.put("bankid", superbankids);
					tpl = getTpl("getallsuperuserid");	
				}
				else
				{
					dp.put("bankid", bankid);
					tpl = getTpl("getUseridFromCmuser");
				}	
			}
			else if(bankids[j].startsWith(CommonConst.WF_POSTID_WFINIT_SUPERAPPR)){//���̷�����������ϼ���������   20141227
				dp.put("bankid", bankid);  //���̷�����������ϼ���������
				tpl = getTpl("getuserininitsuperorg");  //�õ����̷�����������ϼ����������������û�
			}else	
			{
				dp.put("bankid", bankid);
				tpl = getTpl("getUseridFromCmuser");
			}
			
			dl = executeProcedure(tpl, dp);
			
			while (dl.next()) 
			{
				String value = dl.getString("bankid") + "/" + dl.getString("userid");
				//String value =  dl.getString("userid");
				if (flag) {
					value += "|" + dl.getString("username");
				}
				boolean isExist = checkExist(list,value);
				if(isExist==false)
					list.add(list.size(),value);
			}
		}
		Errors.Assert(list.size() >0 , getMsgInfo("WF_NotFoundUser"));
		if(list.size() > 0){
			user = (String[])list.toArray(new String[0]);
		}
		
		/*for(int i=0;i<user.length;i++)
		{
			System.out.println("============================user=" + user[i]);
		}*/
		return user;
	}
	
	/**
	 * ����Ƿ����ظ��û�
	 * @param list
	 * @param value
	 * @return
	 */
    private boolean checkExist(LinkedList list,String value)
    {
    	boolean exist = false;
    	for(int i=0;i<list.size();i++)
    	{
    		String v = (String)list.get(i);
    		if(v.equals(value))
    		{
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
	 * @see #selectUsers
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(UnikMap map,boolean flag) throws Exception{
		return selectUsers(map, flag, true);
	}
	
	/**
	 * ���ݻ����ź͸�λ��Ų�ѯ�û��б�,Ŀǰֻ����һ����λ �˴����ϼ�������ָ:ȡ�ϼ�����������Ϣ,�����ϼ��������
	 * 
	 * @param org
	 *            ��ǰ�ڵ������
	 * @param post
	 *            ��λ
	 * @param local
	 *            ����/�û�
	 * @param wfid
	 *            ���̱��,
	 * @param nodeId
	 *            �ڵ���
	 * @return String[] �û���Ϣ����bankid/userid|username
	 * @deprecated As of JDK version 1.1,
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(String org, String post, String local,String bindprodid,
			String wfid, String nodeId) throws Exception {
		return selectUsers(org,post,local,bindprodid,wfid,nodeId,false);
	}
	
	/**
	 * map����Ҫ��org,post,local,wfid,nodeid,postbankid�Ȳ���
	 * 
	 * @deprecated As of JBPortal3.0
	 * @throws java.lang.Exception
	 */
	public String[] selectUsers(String org, String post, String local,String bindprodid,
			String wfid, String nodeId,boolean flag) throws Exception {
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
	public String[] selectTaskUsers(String wfInstId, String nodeId)
			throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false);
	}
	
	public String[] selectTaskUsers(String wfInstId, String nodeId,int tasktype)
	throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false,tasktype);
}
	
	public String[] selectTaskViewUsers(String wfInstId, String nodeId)
	throws Exception {
		return selectTaskUsers(wfInstId, nodeId, null, false,1);
	}
	
	/**
	 * ����ʷ�б��в��ҷ��ϸ�λ���û���
	 * 
	 * @param wfInstId
	 *            ���̱��
	 * @param nodeId
	 *            �ڵ���
	 * @param flag
	 *            ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *            /operid
	 * @param tasktype �������ͣ�1���鿴�����û�  2�����������û�           
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId,String userloginstate ,boolean flag,int tasktype)
			throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("nodeid", nodeId);
		dp.put("userloginstate", userloginstate);
		dp.put("tasktype", tasktype);
		
		
		/*
		<sql name="getDistinctDwtaskhis">
		select
			distinct 
			a.bankid,
			a.operid,
	        b.username
		from 
			dwtaskhis a,
			cmuser b 
		where 
			a.operid = b.userid 
			and wfid = @wfid
			#AND = @nodeid
			#AND = @userloginstate
			#AND = @tasktype
		</sql>
		*/
		
		DataList dl = executeProcedure(getTpl("getDistinctDwtaskhis"), dp);
		
		int count = dl.countRows();
		// û���ҵ����ϸø�λ���û����������̶����е�'���̽ڵ��λ����'!
		if(tasktype==2) //ֻ�Դ����û���������
			Errors.Assert(count > 0, getMsgInfo("WF_NotFoundUser"));
		
		String user[] = new String[count];
		StringBuffer buf = new StringBuffer();
		int index = 0;
		while (dl.next()) {
			buf.append(dl.getString("bankid")).append("/");
			buf.append(dl.getString("operid"));
			if(flag){
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
	 * @param wfInstId
	 *            ���̱��
	 * @param nodeId
	 *            �ڵ���
	 * @param flag
	 *            ���Ϊtrue����usernameд������ֵ��,��������bankid/operid|username,false��ֻ��дbankid
	 *            /operid
	 * @param intHistory -1 �����ձ��ֶβ�ѯ��0����ǰ�û�  1����ʷ�����û�           
	 * @throws java.lang.Exception
	 */
	public String[] selectTaskUsers(String wfInstId, String nodeId,String userloginstate ,boolean flag)
			throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("nodeid", nodeId);
		dp.put("userloginstate", userloginstate);
		
		
		/*
		<sql name="getDistinctDwtaskhis">
			select
		distinct 
		a.bankid,
		a.operid,
        b.username
	from 
		dwtaskhis a,
		cmuser b 
	where 
		a.operid = b.userid 
		and wfid = @wfid
		#AND = @nodeid
		#AND = @userloginstate
		#AND = @tasktype
		</sql>
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
			if(flag){
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
	 * @param wfid ���̱��
	 * @param nodeid �ڵ���
	 * @param hisFlag �Ƿ������ʷ��Ա��ѯ
	 * @param flag �Ƿ���ʾ�û�����
	 * @return ��������Ա�б�,���hisFlag=0��hisFlag='1'���Ǵ�������г��쳣�ˣ����᷵��null
	 * @throws Exception
	 */
	public String [] selectHistoryUser(String wfid,String nodeid,String hisFlag,boolean flag) throws Exception{
		String[] users = null;
		if(CommonConst.WF_HISUSER_ON.equals(hisFlag)){   //����ʷ�б��в�ѯ������Ա
			try{
				users = selectTaskUsers(wfid, nodeid, "1", flag);
				hisisempty = '1';
			}catch(Exception e){                         //���쳣99%��û�в�ѯ����¼�����Բ���Ҫ����
				hisisempty = '2';
				log(DEBUG, "cn.com.jbbis.jbportal.WrokflowStorageImpl.selectHistoryUser(): " + e);
			}
		}else{
			hisisempty = '3';
		}
		return users;
	}
	
	/**
	 * ��ȡĳ�������µ�����δ��������
	 * @param wfInstId ���̱��
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public DataList getTask(String wfInstId) throws Exception {
		/*
		<sql name="getAllDwTask">
			select 
				wfid,
				taskser,
				bankid,
				operid,
				recetime,
				dealtime,
				nodeid,
				nodename,
				exectrancode,
				submtrancode,
				looktrancode,
				taskdesc 
			from 
				dwTask
			where
			 	wfid = @wfid
			 	# AND = @bankid
			 	# AND = @operid
		</sql>
		*/
		
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		
		DataList dl = executeProcedure(getTpl("getAllDwTask"), dp);			

		//System.out.println(dl!=null ? dl.countRows() + "��" : "0��");
		return dl;
	}
	
	/**
	 * ��ȡ�����б�,��dwtask�е�δ�������ݶ�ȡ����
	 * @param wfInstId ���̱��
	 * @param user �û�
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public UnikMap getTask(String wfInstId, String user) throws Exception {
		/*
		<sql name="getAllDwTask">
			select 
				wfid,
				taskser,
				bankid,
				operid,
				recetime,
				dealtime,
				nodeid,
				nodename,
				exectrancode,
				submtrancode,
				looktrancode,
				taskdesc 
			from 
				dwTask
			where
			 	wfid = @wfid
			 	# AND = @bankid
			 	# AND = @operid
		</sql>
		*/
		
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("bankid", user.substring(0, user.indexOf("/")));
		dp.put("operid", user.substring(user.indexOf("/") + 1));
		
		SingleResult sr = querySingle(getTpl("getAllDwTask"), dp,null);
		
		String msg[] = new String[2];
		msg[0] = dp.getString("bankid");
		msg[1] = dp.getString("operid");
		
		Errors.Assert(sr != null, formatMessage("WF_TaskNotFound",msg));

		return (UnikMap)sr;
	}

	/**
	 * ��ȡ�����б�,��dwtask�е�δ������һ���ݶ�ȡ����
	 * @param wfInstId ���̱��
	 * @param user �û�
	 * @return UnikMap wfNodeDef
	 * @throws java.lang.Exception
	 */
	public UnikMap getAnyOneTask(String wfInstId) throws Exception {
		/*
		<sql name="getAllDwTask">
			select 
				wfid,
				taskser,
				bankid,
				operid,
				recetime,
				dealtime,
				nodeid,
				nodename,
				exectrancode,
				submtrancode,
				looktrancode,
				taskdesc 
			from 
				dwTask
			where
			 	wfid = @wfid
			 	# AND = @bankid
			 	# AND = @operid
		</sql>
		*/
		
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		
		SingleResult sr = querySingle(getTpl("getAllDwTask"), dp,null);
		
		return (UnikMap)sr;
	}
	/**
	 * �����µ������б�,����Ҫ���������ŵ�dwtask����
	 * @param node �ڵ���Ϣ
	 * @param wfInstId ʵ�����
	 * @param taskSerial ���
	 * @param users �����û�
	 * @param users �鿴�û�
	 * @throws java.lang.Exception
	 */
	public int newTask(UnikMap node,String wfInstId, int taskSerial,String[] users,String[] viewUsers,UnikMap var,ArrayList taskList) throws Exception {
		//��ȡ�ִ�  
		int taskRound = getTaskMaxTaskRound(wfInstId);
		
		UnikMap umInstance = getWorkflowInstance(wfInstId);
		String busiOperateStyle = umInstance.getString("busioperatestyle");
		String assignmindealnumstyle = node.getString("assignmindealnumstyle"); 
		//�����ύʱ��
		String recetime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String lastchgdate = recetime;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < users.length; i++) {
			String user[] = users[i].split("/");
			buf.append(users[i]).append(" ");
			UnikMap param = new UnikMap();
			param.putAll(node);
			param.put("wfid", wfInstId);
			param.setInt("taskser", taskSerial);
			param.put("bankid", user[0]);
			param.put("operid", user[1]);
			param.put("recetime", recetime);
			param.put("lastchgtime", recetime);
			param.put("lastchgdate", lastchgdate);
			param.put("tasktype", CommonConst.WF_TASKTYPE_DEAL);  //��������
			param.put("taskround", taskRound);  //�����ִ�
			param.put("busiOperateStyle", busiOperateStyle);			
			param.put("assignmindealnumstyle", assignmindealnumstyle);
			//if(assignmindealnumstyle.equals("1"))
				param.put("assignMinDealNum", users.length); //������û���
			//else
			//	param.put("assignMinDealNum", 0); //������û���
			

			/**
			 * ��ʱע�͵������������ʱ���������ֶε�ֵ��ʧȥ�˹�������ɫ���ɱ���
			 */
			//������������Ϣ��ı����ò���ֵ�滻
			//String wfdesc = replaceFlowDesc(wfDef.getString("flowdesc"), var);
			param.put("taskdesc", "");

			// �����ݲ���dwtask����
			performAction(Service.INSERT, "dwtask", param);
			
			//����������
			taskList.add(taskSerial);
			
			taskSerial ++;
			//System.out.println("newTask userid=" +users[i] );
		}
		
		//����鿴�û�
		
		for (int i = 0; viewUsers!=null && i < viewUsers.length; i++) {
			String user[] = viewUsers[i].split("/");			
			UnikMap param = new UnikMap();
			param.putAll(node);
			param.put("wfid", wfInstId);
			param.setInt("taskser", taskSerial);
			param.put("bankid", user[0]);
			param.put("operid", user[1]);
			param.put("recetime", recetime);
			param.put("lastchgtime", recetime);
			param.put("lastchgdate", lastchgdate);
			param.put("tasktype", CommonConst.WF_TASKTYPE_VIEW);  //�鿴����
			param.put("taskround", taskRound);  //�����ִ�
			param.put("busiOperateStyle", busiOperateStyle);
			param.put("assignmindealnumstyle", assignmindealnumstyle);
			//if(assignmindealnumstyle.equals("1"))
				param.put("assignMinDealNum", users.length); //������û���
		//	else
			//	param.put("assignMinDealNum", 0); //������û���
			
			/**
			 * ��ʱע�͵������������ʱ���������ֶε�ֵ��ʧȥ�˹�������ɫ���ɱ���
			 */
			param.put("taskdesc", "");
			

			// �����ݲ���dwtask����
			performAction(Service.INSERT, "dwtask", param);
			
			taskSerial ++;
		}
		
		log(DEBUG, "[WF_assignTask] NewTask OK, wfid=" + wfInstId + ", nodeid=" + node.getString("nodeid") + ", users=[" + buf + "]");
		return taskSerial - 1;
	}
	
	/**
	 * ����dwtask���е�����ִμ�¼
	 * @param var var����Ҫ�� wfid 
	 * @return int ����ִ�
	 * @throws java.lang.Exception
	 */
	public int getTaskMaxTaskRound(String wfid) throws Exception {
		int n = 0;
		
		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		//map.put("nodeid", nodeid);

		SingleResult sr = querySingle(getTpl("getdwtaskmaxround"), map,null);

		if (sr != null) {
			n = sr.getInt("taskRound");
		}
		return n;
	}
	
	/**
	 * �������������ִ�
	 */
	public void updateTaskRound(String wfid,int taskRound)throws Exception 
	{
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfid);
		dp.put("taskround", taskRound);
		SingleResult sr = performAction(Service.SELECT, "dwTaskRound", dp);
		if(sr==null)
			performAction(Service.INSERT, "dwTaskRound", dp);	
		else
			performAction(Service.UPDATE, "dwTaskRound", dp);	
		
	}
	/**
	 * ɾ�����������ִ�  ���������ʱ��
	 */
	public void deleteTaskRound(String wfid)throws Exception 
	{
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfid);
		performAction(Service.DELETE, "dwTaskRound", dp);
				
	}
	
	/**
	 * �������������ִ�
	 */
	public void insertTaskRound(String wfid,int taskRound)throws Exception 
	{
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfid);
		dp.put("taskround", taskRound);
		
		performAction(Service.INSERT, "dwTaskRound", dp);	
	}
	
	/**
	 * ����dbBusiWFMap���еļ�¼
	 * @param var var����Ҫ�� wfid 
	 * @return boolean �Ƿ����
	 * @throws java.lang.Exception
	 */
	public boolean checkBusiWFMapIsExist(String wfid) throws Exception {
		boolean blExist = false;
		
		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		
		SingleResult sr = querySingle(getTpl("getbusiwfmap"), map,null);

		if (sr != null) {
			blExist = true;
		}
		return blExist;
	}
	
	/**
	 * ����dbBusiWFMap���е�������
	 * @param var var����Ҫ�� wfid 
	 * @return int ������
	 * @throws java.lang.Exception
	 */
	public int getBusiMaxNo(String loanid) throws Exception {
		int n = 0;
		
		UnikMap map = new UnikMap();
		map.put("loanid", loanid);
		
		SingleResult sr = querySingle(getTpl("getbusiwfmapmaxno"),map);

		if (sr != null) {
			n = sr.getInt("transeq");
		}
		return n;
	}
	/**
	 * ����cmbank���еĻ�����Ϣ
	 * @param bankid
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public SingleResult getBankInfo(String bankid) throws Exception {
		
		UnikMap map = new UnikMap();
		map.put("bankid", bankid);
		
		SingleResult sr = performAction(Service.SELECT, "cmbank", map);	
		
		return sr;
	}
	/**
	 * ����cmuser���е��û���Ϣ
	 * @param userid
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public SingleResult getUserInfo(String bankid,String operid) throws Exception {
		
		UnikMap map = new UnikMap();
		map.put("bankid", bankid);
		map.put("userid", operid);
				
		SingleResult sr = performAction(Service.SELECT, "cmuser", map);	
		
		return sr;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public boolean checkTempAuthStart(String flowId) throws Exception
	{
		boolean blSuccess = false;
		UnikMap map = new UnikMap();
		map.put("flowid", flowId);
		//�ӻ�������ȡ��	
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		UnikMap sr = cacheTools.queryDwflowmain(flowId);
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryDwflowmain cost:"+(endTime_-beginTime_));
		if(sr!=null)
		{
			String isStartTempAuth = sr.getString("isStartTempAuth");
			if(isStartTempAuth!=null && isStartTempAuth.equalsIgnoreCase("1"))
			{
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
	public SingleResult getBusiRange(String flowid) throws Exception 
	{
		UnikMap map = new UnikMap();
		map.put("flowid", flowid);
				
		SingleResult sr = querySingle(getTpl("getbusirange"),map);
		
		return sr;
		
	}
	/**
	 * 
	 * @param um
	 * @return
	 * @throws Exception
	 */
	public DataList getTempAuth(UnikMap um) throws Exception
	{
		DataList dlist = executeProcedure(getTpl("gettempauth"), um);
		return dlist;
	}
	
	/**
	 * ����dbBusiWFMap���е���Ϣ
	 * @param var var����Ҫ�� wfid 
	 * @return SingleResult һ����¼
	 * @throws java.lang.Exception
	 */
	public SingleResult getBusiWFMap(String wfid) throws Exception {
		
		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		
		SingleResult sr = performAction(Service.SELECT, "dbBusiWFMap", map);	
		
		return sr;
	}
	
	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean insertBusiWFMap(UnikMap umBusiWFMap)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));
	
			dp.put("loanid", umBusiWFMap.get("loanid"));
			dp.put("transeq", umBusiWFMap.get("transeq"));
			dp.put("custid" , umBusiWFMap.get("custid"));
			dp.put("custname", umBusiWFMap.get("custname"));
			dp.put("prodid", umBusiWFMap.get("prodid"));
			dp.put("prodname", umBusiWFMap.get("prodname"));
			dp.put("busitype", umBusiWFMap.get("busitype"));
			dp.put("flowid" , umBusiWFMap.get("flowid"));
			dp.put("nodename", umBusiWFMap.get("nodename"));
			dp.put("curoperid", umBusiWFMap.get("curoperid"));
			dp.put("curoperidname", umBusiWFMap.get("curoperidname"));
			dp.put("curbankid", umBusiWFMap.get("curbankid"));
			dp.put("curbankname", umBusiWFMap.get("curbankname"));
			dp.put("recetime", getWorkdateCurrTime());
			dp.put("approperid", umBusiWFMap.get("approperid"));
			dp.put("appropername", umBusiWFMap.get("appropername"));
			dp.put("apprbankid", umBusiWFMap.get("apprbankid"));
			dp.put("apprbankname", umBusiWFMap.get("apprbankname"));
			dp.put("apprdate", umBusiWFMap.get("apprdate"));
			dp.put("approperLev", umBusiWFMap.get("approperLev"));
			dp.put("isfinish", umBusiWFMap.get("isfinish"));
			this.log(DEBUG,"debug(insertBusiWFMap)======================================umBusiWFMap.get(\"nodename\")=" +umBusiWFMap.get("nodename"));
	
			performAction(Service.INSERT, "dbBusiWFMap", dp);	
			blSuccess = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	/**
	 * ���µ������ӳ�����Ĵ���
	 */
	public boolean updateReturnBusiWFMap(UnikMap umBusiWFMap)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));
	
			dp.put("loanid", umBusiWFMap.get("loanid"));
			//dp.put("transeq", umBusiWFMap.get("transeq"));
			dp.put("custid" , umBusiWFMap.get("custid"));
			dp.put("custname", umBusiWFMap.get("custname"));
			dp.put("prodid", umBusiWFMap.get("prodid"));
			dp.put("prodname", umBusiWFMap.get("prodname"));
			dp.put("busitype", umBusiWFMap.get("busitype"));
			dp.put("flowid" , umBusiWFMap.get("flowid"));
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
			this.log(DEBUG,"debug(updateReturnBusiWFMap)======================================umBusiWFMap.get(\"nodename\")=" +umBusiWFMap.get("nodename"));

			performAction(Service.INSERT, "dbBusiWFMap", dp);	
			blSuccess = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean updateBusiWFMap(UnikMap umBusiWFMap)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
		
			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));
	
			dp.put("flowid" , umBusiWFMap.get("flowid"));
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
			this.log(DEBUG,"debug(updateBusiWFMap)======================================umBusiWFMap.get(\"nodename\")=" +umBusiWFMap.get("nodename"));

			
			performAction(Service.UPDATE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean updateBusiWFMap(UnikMap node,String wfInstId,String curOperUser, String[] users)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			//�����ύʱ��
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
			//�����û����ƺͻ�������
			SingleResult operSr = getUserInfo(curBankId,curOperId);
			if(operSr!=null)
			{
				curOperIdName = operSr.getString("username");
			}
			SingleResult bankSr = getBankInfo(curBankId);
			if(bankSr!=null)
			{
				curBankName = bankSr.getString("bankname");
			}
			
			for (int i = 0; i < users.length; i++) {
				String user[] = users[i].split("/");				
				bankids = user[0];
				operids = user[1];
			}
			//�����û����ƺͻ�������
			SingleResult operApprSr = getUserInfo(bankids,operids);
			if(operApprSr!=null)
			{
				appropername = operApprSr.getString("username");
			}
			SingleResult bankApprSr = getBankInfo(bankids);
			if(bankApprSr!=null)
			{
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
			dp.put("apprbankid",bankids);
			dp.put("apprbankname", apprbankname);
			dp.put("apprdate", apprdate);
			dp.put("approperLev", approperLev);
						
			
			performAction(Service.UPDATE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	public boolean updateBusiWFMap(UnikMap node,String wfInstId,String curOperUser, String[] users,String isPrimaryAuditNode)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			//�����ύʱ��
			String apprdate = getWorkdateCurrTime();
			String curUser[] = curOperUser.split("/");			
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
				
				//�����û����ƺͻ�������
				SingleResult operApprSr = getUserInfo(user[0],user[1]);
				if(operApprSr!=null)
				{
					curOperIdName += (curOperIdName.equals("") ?  operApprSr.getString("username") : "," +  operApprSr.getString("username"));
					appropername = operApprSr.getString("username");
				}
				SingleResult bankApprSr = getBankInfo(user[0]);
				if(bankApprSr!=null)
				{
					if(curBankName.indexOf((String)bankApprSr.getString("bankname"))<0)
						curBankName += (curBankName.equals("")) ? bankApprSr.getString("bankname") : "," + bankApprSr.getString("bankname");
					apprbankname = bankApprSr.getString("bankname");
					approperLev = bankApprSr.getString("banklevel");
				}
				
				curBankId += (curBankId==null || curBankId.equals("")) ?  user[0] : "," + user[0];
				curOperId += (curOperId==null || curOperId.equals("")) ? user[1] : "," + user[1];
			}
			
			appropername = curOperIdName;
			apprbankname= curBankName;
			
/*			for (int i = 0; i < users.length; i++) {
				String user[] = users[i].split("/");				
				bankids = user[0];
				operids = user[1];
			}
			//�����û����ƺͻ�������
			SingleResult operApprSr = getUserInfo(bankids,operids);
			if(operApprSr!=null)
			{
				appropername = operApprSr.getString("username");
			}
			SingleResult bankApprSr = getBankInfo(bankids);
			if(bankApprSr!=null)
			{
				apprbankname = bankApprSr.getString("bankname");
				approperLev = bankApprSr.getString("banklevel");
			}*/
			
			String nodeName = node.getString("nodename");
			
			UnikMap dp = new UnikMap();
			dp.put("wfid", wfInstId);
			dp.put("nodename", nodeName);
            dp.put("isPrimaryAuditNode", isPrimaryAuditNode==null ? "2" : isPrimaryAuditNode);
			if(isPrimaryAuditNode!=null && isPrimaryAuditNode.equals("1"))
			{
				dp.put("curoperid", curOperId);
				dp.put("curoperidname", curOperIdName);
				dp.put("curbankid", curBankId);
				dp.put("curbankname", curBankName);
				dp.put("approperid", operids);
				dp.put("appropername", appropername);
				dp.put("apprbankid",bankids);
				dp.put("apprbankname", apprbankname);
				dp.put("apprdate", apprdate);
				dp.put("approperLev", approperLev);
			}
			else
			{
				dp.put("curoperid", curOperId);
				dp.put("curoperidname", curOperIdName);
				dp.put("curbankid", curBankId);
				dp.put("curbankname", curBankName);
				
			}	
			
			performAction(Service.UPDATE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	
	/**
	 * ��������ʵ��ҵ����ձ�
	 */
	public boolean updateNormalBusiWFMap(UnikMap node,String wfInstId, String curOperUser)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			String curUser[] = curOperUser.split("/");			
			String curOperId = curUser[1];
			String curBankId = curUser[0];
			String curOperIdName = "";
			String curBankName = "";
			
			
			//�����û����ƺͻ�������
			SingleResult operSr = getUserInfo(curBankId,curOperId);
			if(operSr!=null)
			{
				curOperIdName = operSr.getString("username");
			}
			SingleResult bankSr = getBankInfo(curBankId);
			if(bankSr!=null)
			{
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
			
			performAction(Service.UPDATE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	
	
	
	/**
	 * ��������ʵ��ҵ����ձ����ɱ�ʶ
	 */
	public boolean completeBusiWFMap(String wfid,String isFinish)throws Exception 
	{
		boolean blSuccess = false;
		try
		{
			
			UnikMap dp = new UnikMap();
			dp.put("wfid", wfid);
			dp.put("isfinish", isFinish);
			
			
			performAction(Service.UPDATE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	/**
	 * ɾ������ʵ��ҵ����ձ�
	 */
	public boolean deleteBusiWFMap(UnikMap umBusiWFMap)throws Exception 
	{
		boolean blSuccess = false;
		try
		{			
			UnikMap dp = new UnikMap();
			dp.put("wfid", umBusiWFMap.get("wfid"));
			
			performAction(Service.DELETE, "dbBusiWFMap", dp);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			blSuccess = false;
		}
		return blSuccess;
	}
	
	public int getAllUserFromHistoryTask(UnikMap var) throws Exception {
		int n = 0;
		/*
		  <sql name="getCountDwtask"> 
		  		select 
		  			count(*) as num 
		  		from 
		  			dwTask 
		  		where
		  			wfid = @wfid 
		  			#AND = @nodeid 
		  </sql>
		 */

		SingleResult sr = querySingle(getTpl("gethistoryuser"), var,
				null);

		if (sr != null) {
			n = sr.getInt("num");
		}
		return n;
	}
	/**
	 * ����dwtask���еļ�¼
	 * @param var var����Ҫ�� wfid ,nodeid
	 * @return int ��¼������
	 * @throws java.lang.Exception
	 */
	public int getTaskNum(UnikMap var) throws Exception {
		int n = 0;
		/*
		  <sql name="getCountDwtask"> 
		  		select 
		  			count(*) as num 
		  		from 
		  			dwTask 
		  		where
		  			wfid = @wfid 
		  			#AND = @nodeid 
		  </sql>
		 */

		SingleResult sr = querySingle(getTpl("getCountDwtask"), var,
				null);

		if (sr != null) {
			n = sr.getInt("num");
		}
		return n;
	}
	/**
	 * ����dwtask���е����Ȩ�������¼
	 * @param var var����Ҫ�� wfid ,nodeid
	 * @return int ��¼������
	 * @throws java.lang.Exception
	 */
	public int getViewTaskNum(UnikMap var) throws Exception {
		int n = 0;
		/*
		  <sql name="getCountDwtask"> 
		  		select 
		  			count(*) as num 
		  		from 
		  			dwTask 
		  		where
		  			wfid = @wfid 
		  			#AND = @nodeid 
		  </sql>
		 */

		SingleResult sr = querySingle(getTpl("getviewcountdwtask"), var,null);

		if (sr != null) {
			n = sr.getInt("num");
		}
		return n;
	}
	
	/**
	 * �������״̬�Ƿ񷢲�,�Ƿ���״̬�������ύҵ��
	 * @param flowid ���̱��
	 * @throws java.lang.Exception ��ʼ���׳�WFStateIsInit�����̱���ͣʹ���׳�
	 * WFStateIsPause ���̱������׳�WFStateIsStop
	 */
	public void checkFlowState(String flowid) throws Exception{
		UnikMap m = getWorkflowDefinition(flowid);
		String flowname = m.getString("flowname");
		String flowstate = m.getString("flowstate");
		
		//�Ƿ񻹴��ڳ�ʼ��
		Errors.Assert(!CommonConst.WF_STATE_INIT.equals(flowstate),
				formatMessage("WFStateIsInit", flowname));

		// �Ƿ���ͣʹ��
		Errors.Assert(!CommonConst.WF_STATE_PAUSE.equals(flowstate),
				formatMessage("WFStateIsPause", flowname));

		// �Ƿ񱻽���,��Ҫ�����ύ��ҵ��,��Ϊ���ڴ����ҵ���ѱ�ת�ֹ���
		Errors.Assert(!CommonConst.WF_STATE_STOP.equals(flowstate),
				formatMessage("WFStateIsStop", flowname));
	}

	/**
	 * ɾ����������
	 * @param wfInstId	 
	 * @throws Exception
	 */
	public void completeAllTask(String wfInstId) throws Exception {
				
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		
		//ɾ��dwTask�е�����			
		DataContext exec = new DataContext();
		executeProcedure(getTpl("deletealltask"), dp, exec);
		
		
	}
	
	
	/**
	 * ��ɵ�ǰ��������
	 * 2007.5.16 ���������ýڵ������ֶεĸ��£�ԭ������newTas�����н��У���ΪҪ��
	 * ����������ύ����ʱ���������������ֶ�����ֻ�����������ʱ�����ˡ�
	 * 
	 * @param wfInstId
	 *            ����ʵ�����
	 * @param taskser
	 *            �������
	 * @param var
	 *            ����
	 * @throws java.lang.Exception
	 */
	public void completeTask(String wfInstId, String taskser,String nodephase, UnikMap var,UnikMap node) throws Exception {
		String taskdesc = node.getString("nodedesc");
		// ���㷨��֤��ȡ�ı��������±����,���´�dwtaskvars�ж�ȡ������Ϣ
		getInstVar(wfInstId, true);
		
		//�����µı�����Ϣ��ȡֵȥ�滻taskdesc�еı����ֶ�
		
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);

			//System.out.println("�滻���������" + taskdesc + "==============================" + name.toLowerCase() + "=================================" + value);
			taskdesc = StringUtil.replace(taskdesc, "{" + name.toLowerCase() + "}", value);
			
			this.log(DEBUG,"debug(completeTask)======================================taskdesc=" +taskdesc);

		}
		
		/*
		<sql name="insertDwtaskhis">
			insert into 
				dwTaskHis (
				wfid,
				taskser,
				bankid,
				operid,
				recetime,
				dealtime,
				dealsystime,
				nodeid,
				nodename,
				exectrancode,
				submtrancode,
				looktrancode,
				taskdesc,
				forenodeid,
				nodephase,
				isAllowGet,
				isAllowReturn,
				taskRound,
				isPrimaryAuditNode,
				busiOperateStyle
				)
			select 
				wfid,
				taskser,
				bankid,
				operid,
				#if @recetime
				@recetime recetime,
				#else
				recetime,
				#end
				@dealtime as dealtime,
				@dealsystime as dealsystime,
				nodeid,
				nodename,
				exectrancode,
				submtrancode,
				looktrancode,
				@taskdesc as taskdesc,
				forenodeid��
				nodephase,
				isAllowGet,
				isAllowReturn,
				taskRound,
				isPrimaryAuditNode,
				busiOperateStyle
			from 
				dwTask 
			where 
				wfid = @wfid
				#AND = @taskser
		</sql>
		*/
		
		UnikMap dp = new UnikMap();

		//��¼����ʱ����ԭ���Ĳ���ϵͳʱ���Ϊ���ڵ�workdate+ϵͳʱ����
		dp.put("dealtime", getWorkdateCurrTime());
		dp.put("lastchgtime", getWorkdateCurrTime());
		dp.put("lastchgdate", getSysdate());
		//dealsystime��Ҫ�Ǵ���ʱ�Ĳ���ϵͳʱ��
		dp.put("dealsystime", JBDate.getSysCurrTime(CommonConst.DATE_TYPE_TIMESTAMP));
		dp.put("taskdesc", taskdesc);
		dp.put("wfid", wfInstId);
		dp.put("taskser", taskser);
		dp.put("nodephase", nodephase);
		//dp.put("tasktype", node.get("nodetype"));
		
		//��dwtask���еļ�¼����dwtaskhis��
		DataContext exec = new DataContext();
		executeProcedure(getTpl("insertDwtaskhis"), dp, exec);
		
		Errors.Assert(exec.affectedRows != 0, getMsgInfo("workflow/TaskMaybeSubmit"));
		
		//ɾ��dwTask�е�����
			
		performAction(Service.DELETE, "dwtask", dp);		
		
		
	}
	
	/**
	 * �������
	 * @return
	 */
	public boolean isLastestHandler(UnikMap node,UnikMap var,boolean blStart) throws Exception{
		String taskoverpolicy = node.getString("taskoverpolicy");       //������ɲ���
		int num = 1;                                                   //������Ҫ��ɵ�����
		int mindealnum = node.getInt("mindealnum");                    //����������ٴ�������;
		String assignmindealnumstyle = var.getString("assignmindealnumstyle"); //�Ƿ�������ٴ������������ٴ�������Ϊ�ֶ�ѡ����û���
		String wfid = var.getString("wfid");
		log(DEBUG, "assignmindealnumstyle=" + assignmindealnumstyle);
		if(assignmindealnumstyle!=null && assignmindealnumstyle.equals("1"))   //=================�����������������
		{
			String assignMinDealNum = var.getString("assignMinDealNum");
			mindealnum = Integer.valueOf(assignMinDealNum);
		}
		log(DEBUG, "assignMinDealNum=" + mindealnum);
		//�õ�ĳ��������ĳ������µ�������
		int tasknum = getTaskNum(var);                                 //dwtask���л�ʣ�µ�������		
		//����Ƿ���������Ա,��ô��������������ٴ�������Ϊ������Ա
		if(CommonConst.WF_TASKASSIGN_ALL.equals(node.getString("autodisuserflag"))){
			mindealnum = tasknum + num;                                //��������Ϊ�����������Ѵ����һ��
		}	
		if(CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)){
			num = node.getInt("assignminnum");                         //ȡ���õĴ�������
		}else if(CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)){
			double percent = node.getDouble("overpercent")/100;        //ȡ���ٷֱ�
			num = WorkFlowFunc.ceil(mindealnum * percent);             //���ٴ����������԰ٷֱ�ȡ��
		}else if(CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)){
			num = mindealnum;                                          //�������ж����
		}
		//����������ȥ���������������δ��ɵ��������������б���û���������Ϊ��ǰ������ȫ�����
		boolean flag = (mindealnum - num) == tasknum || (blStart && (mindealnum - num) == 0 && tasknum==1)|| tasknum ==0 ? true : false;
		return flag;

	}
	
	/**
	 * �ж��Ƿ�����������,������һ�ڵ�����������Խ��д���
	 * @throws java.lang.Exception
	 */
	public boolean isContinue(UnikMap node,UnikMap var,boolean blStart) throws Exception{
		String taskoverpolicy = node.getString("taskoverpolicy");       //������ɲ���
		int num = 1;                                                   //������Ҫ��ɵ�����
		int mindealnum = node.getInt("mindealnum");                    //����������ٴ�������;
		String assignmindealnumstyle = var.getString("assignmindealnumstyle"); //�Ƿ�������ٴ������������ٴ�������Ϊ�ֶ�ѡ����û���
		String wfid = var.getString("wfid");
		log(DEBUG, "assignmindealnumstyle=" + assignmindealnumstyle);
		if(assignmindealnumstyle!=null && assignmindealnumstyle.equals("1"))   //=================�����������������
		{
			String assignMinDealNum = var.getString("assignMinDealNum");
			mindealnum = Integer.valueOf(assignMinDealNum);
		}
		log(DEBUG, "assignMinDealNum=" + mindealnum);
		//�õ�ĳ��������ĳ������µ�������
		int tasknum = getTaskNum(var);                                 //dwtask���л�ʣ�µ�������		
		//����Ƿ���������Ա,��ô��������������ٴ�������Ϊ������Ա
		if(CommonConst.WF_TASKASSIGN_ALL.equals(node.getString("autodisuserflag"))){
			mindealnum = tasknum + num;                                //��������Ϊ�����������Ѵ����һ��
		}	
		if(CommonConst.WF_TASKCOMP_AMT.equals(taskoverpolicy)){
			num = node.getInt("assignminnum");                         //ȡ���õĴ�������
		}else if(CommonConst.WF_TASKCOMP_PER.equals(taskoverpolicy)){
			double percent = node.getDouble("overpercent")/100;        //ȡ���ٷֱ�
			num = WorkFlowFunc.ceil(mindealnum * percent);             //���ٴ����������԰ٷֱ�ȡ��
		}else if(CommonConst.WF_TASKCOMP_ALL.equals(taskoverpolicy)){
			num = mindealnum;                                          //�������ж����
		}
		//����������ȥ���������������δ��ɵ��������������б���û���������Ϊ��ǰ������ȫ�����
		int restTaskCount = mindealnum - num;
		boolean flag = (restTaskCount == tasknum || (blStart && restTaskCount == 0 && tasknum==1)|| tasknum ==0) ? true : false;
		//��������������������л�����δ��������,����������
		if(flag && tasknum > 0){
			/*
			 <sql name="deleteFromDwtask">
			 	DELETE FROM
			 		DWTASK
			 	WHERE
			 		WFID=@wfid
			 		AND NODEID = @nodeid
			 </sql>
			 */
			executeProcedure(getTpl("deleteFromDwtask"), var);
		}
		//����ǰ�������
		//�õ�ĳ��������ĳ������µ�������	
		int viewtasknum = getViewTaskNum(var);                                 //dwtask���л�ʣ�µ�������
		if(flag && viewtasknum>0)
		{
			executeProcedure(getTpl("deleteFromDwtask"), var);
		}
		return flag;
	}

	/**
	 * ��������ʵ���е�������
	 */
	public void updateTaskSerial(String wfInstId, int nextSerial)
			throws Exception {

		UnikMap dp = new UnikMap();
		//dp.enableModifyFlag();
		dp.setInt("taskser", nextSerial);
		dp.put("wfid", wfInstId);

		performAction(Service.UPDATE, "dwflowinst", dp);
	}

	/**
	 * ��������ʵ��������״̬
	 */
	public void updateWorkflowStatus(String wfInstId, int status)
			throws Exception {
		UnikMap dp = new UnikMap();
		dp.setInt("wfstate", status);
		dp.put("wfid", wfInstId);

		performAction(Service.UPDATE, "dwflowinst", dp);
	}
	
	/**
	 * ��������ʵ���е������źͽ����̶�
	 */
	public void updateTaskSerial(String wfInstId, int nextSerial, String instancylevel)
	throws Exception {
		
		UnikMap dp = new UnikMap();
		//dp.enableModifyFlag();
		dp.setInt("taskser", nextSerial);
		if(null!=instancylevel&&!"".equals(instancylevel)){
			dp.put("instancylevel", instancylevel);
		}
		dp.put("wfid", wfInstId);
		
		performAction(Service.UPDATE, "dwflowinst", dp);
	}


	/**
	 * ���ر��
	 * 
	 * @param name
	 *            �������
	 * @param len
	 *            ��ŵĳ���
	 * @throws RunTimeException
	 */
	public String getGlobalNum(String name, int len) throws Exception {
		String str = getSerialNumByAbbrv("global", name);
		return calFillString(str, len, "0") + str;
	}

	/**
	 * ���ݻ����ź�ƴ����д������ˮ��
	 * 
	 * @param sBankId
	 *            ������
	 * @param sAbbrv
	 *            ƴ����д
	 * @return ��ˮ��
	 * @throws RunTimeException
	 */
	private String getSerialNumByAbbrv(String sBankId, String sAbbrv)
			throws Exception {
		String abbrv = sAbbrv.trim();
		String bankId = sBankId.trim();
		
		//�޸��ò���
		UnikMap nvalues = new UnikMap();
		nvalues.put("amtnum", new SerNoUpdator());
		nvalues.put("bankid", bankId);
		nvalues.put("serid", abbrv);
		
		DataContext exectx = new DataContext();
		exectx.set(NEWTRANSACTION);
		exectx.table = "cmserno";
		//�����ò���
		UnikMap dp = new UnikMap();
		dp.put("bankid", bankId);
		dp.put("serid", abbrv);
		dp.put("sername", abbrv);
		dp.setInt("serno", 1);
		
		/*
			<sql name="getCmSerNo">
				Select
					bankid,
					serid, 
					sername,
					serno
				From
					cmserno
				Where
					bankid=@bankid 
					and serid=@serid
			</sql>
		 */
		SingleResult result = merge(getTpl("getCmSerNo"), nvalues, dp, exectx);
		
		String n;

		if (result != null) {
			String s = (String) result.get("serno");
			n = String.valueOf(Long.parseLong(s) + 1);
		}
		else {
			n = "1"; 
		}

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
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public String[] getVarNames(String desc, String wfDefId, String nodeid)
			throws Exception {
		StringBuffer buf = new StringBuffer();
		String arrs[] = StringUtil.split(desc, "{");
		boolean flag = false;
		// ��ȡ�ڵ㶨���еı���
		if (desc.charAt(0) == '{')
			flag = true;
		for (int i = 0; i < arrs.length; i++) {
			if (flag)
				buf.append(StringUtil.split(arrs[i], "}")[0]);
			else {
				if (i < arrs.length - 1)
					buf.append(StringUtil.split(arrs[i + 1], "}")[0]);
			}
			buf.append(",");
		}
		// ��ȡ�ڵ�·�����õ��ı���
		
		/*
			<sql name="getAllFromDwFlowRoute">
				Select
					*
				From
					dwFlowRoute
				Where
					flowid=@flowid 
					and nodeid=@nodeid
			</sql>
		 */
		
		//���´���д��û�����壬��֪��ΪʲôҪ�������������Ǹ�������ˣ�������          û�����廹�����Σ��ǲ����˷���Դ		
/*		UnikMap dp = new UnikMap();
		dp.put("flowid", wfDefId);
		dp.put("nodeid", nodeid);
		
		DataList dl = executeProcedure(getTpl("getAllFromDwFlowRoute"), dp);

		while (dl.next()) {
			dl.getString("reoutecond");
		}*/

		return StringUtil.split(buf.toString());
	}

	/** 
	 * ȡ�ڵ�·����Ϣ
	 * @param wfDefId flowid
	 * @return nodeId nodeid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public LinkedList getNodeRoute(String wfDefId, String nodeId)
			throws Exception {
		LinkedList list = new LinkedList();
		//�ӻ�����ȡ��
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		List<UnikMap> dlList = cacheTools.queryDwflowrouteVo(wfDefId, nodeId);
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryDwflowrouteVo cost:"+(endTime_-beginTime_));
		
		for(int i=0;i<dlList.size();i++){
			UnikMap dl = dlList.get(i);
			String cond = dl.getString("routecond");
			String nnid = dl.getString("nextnodeid");
			String taskassignstyle = dl.getString("taskassignstyle");
			String routetype = dl.getString("routetype");
			String retutrancode = dl.getString("retutrancode");
			String expConstType = dl.getString("expconsttype1") + 
			                   "," + dl.getString("expconsttype2") + 
			                   "," + dl.getString("expconsttype3") + 
			                   "," + dl.getString("expconsttype4");

			if (cond == null || cond.length() == 0 || nnid == null
					|| nnid.length() == 0)
				throw new NamedException(getMsgInfo("WF_NotFoundRouteDef"));
			list.add(list.size(), cond);
			list.add(list.size(), nnid);
			list.add(list.size(),expConstType);
			list.add(list.size(), taskassignstyle);
			list.add(list.size(),routetype);
		}

		return list;
	}
	/**
	 * @discription:�õ������б��������Ϣ
	 * @param:�����б��ֶ���
	 * @return:�����ֵ����XmlConfig,����null
	 * @throws Exception:java.lang.Exception
	 */
	public Document getSelectConfig(String select) throws Exception{
		InputStream in = application.getResource("select/" + select + ".xml");
		Document doc = null;
		if(in != null){
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = b.parse(in);
			in.close();
		}
		return doc;
	}
	/**
	 * @discription:���������б��name��valueȡ��text
	 * @param:select:�����б��name,value�����б��value
	 * @return:�����б��text,���û���ҵ���Ӧ��text,�򷵻�value
	 * @throws Exception:java.lang.Exception
	 */
	/*public String getSelectText(String select,String value) throws Exception{
		String text = value;
		Document doc = getSelectConfig(select);
		if(doc == null)
			return text;
		Element root = doc.getDocumentElement(); 
		NodeList list = root.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE){
				NamedNodeMap m = n.getAttributes();
				if(m.getNamedItem("value").getNodeValue().equals(value)){
					text = m.getNamedItem("text").getNodeValue();
					break;
				}else{
					continue;
				}
			}
		}
		return text;
	}*/
	
	public String getSelectText(String select, String value) throws Exception {
		
		HashMap<String,UnikMap> map = EhcacheTools.getHashMap("selectall");
		if(map==null) return value;
		
		UnikMap s = map.get(select);
		String text = value;
		if(s != null) {
			text = (String) s.get(value);
			if(Strings.isEmpty(text)) {
				return value;
			}
		} 
		
		return text;
	}
	/**
	 * �õ������б�,��Ҫ�Ǹ�·����������ʹ��
	 * 
	 * @param wfInstId
	 *            ������ʵ�����
	 * @param reload
	 *            �Ƿ����ز���,reload=false�������в���ʱ������ȥ��ѯ���ݿ�
	 * @return UnikMap vars�����б�
	 * @throws java.lang.Exception
	 */
	public UnikMap getInstVar(String wfInstId, boolean reload) throws Exception {
		if (!reload && vars.size() != 0)
			return vars;
		
		/*
		<sql>
		select 
			wfid,
			taskser,
			nodeid,
			varname,
			varvalue
		from 
			 dwtaskvars 
		where 
			 wfid = @wfid
			 #AND = @number:taskser
			 #AND = @varname
		#if @order
			 order by taskser asc
		#end 
	   </sql>
	   */
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("order", "asc");			
		DataList dl = executeProcedure(getTpl("getAllDwTaskVars"), dp);
		while (dl.next()) {
			// ���㷨��֤��ȡ�ı��������±����
			String name = dl.getString("varname");
			String value = dl.getString("varvalue");		
			vars.put(name, value);
		}

		return vars;
	}

	/**
	 * ��������
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void completeInstance(String wfInstId) throws Exception {
		/*
			<sql name="insertDwFlowHisFromdwFlowInst">
				insert into 
					dwFlowHis 
				select 
					* 
				from 
					dwFlowInst 
				where 
					wfid = @wfid
			</sql>
		 */
		
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);
		dp.put("lastchgdate", getSysdate());
		dp.put("lastchgtime", getWorkdateCurrTime());

		executeProcedure(getTpl("insertDwFlowHisFromdwFlowInst"), dp);
		
		//ɾ����dwFlowInst������
		performAction(Service.DELETE, "dwflowinst", dp);
		
		
		DataList dlist = executeProcedure(getTpl("getAllDwtaskhis"), dp);
		if (dlist.next()) {
			//String flowdesc = q.getString("taskdesc");
			String looktrancode = dlist.getString("looktrancode");

			dp.put("looktrancode", looktrancode);

			performAction(Service.UPDATE, "dwflowhis", dp);
		}
	}

	/**
	 * ���˵���ʼ�ڵ�ʱ��Ҫ�����һЩ���顣
	 */
	public void doWithDraw(String flowid, String wfid) throws Exception {
		String execsql = getWorkflowDefinition(flowid).getString("execsql");
		if(execsql.length() == 0)
			return;
		execsql = execsql; //��SQL���ת����Сд
		String esql[] = StringUtil.split(execsql,";");
		//�õ���Ӧ����ʵ���ı�����Ϣ
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
						sql = StringUtil.replace(sql, "{" + name.toLowerCase() + "}", s);  
					}else{
						//���sû��ȡ��ֵ,�˴�����ʾ�Ļ�,����һ����ѭ��
						throw new NamedException(formatMessage("FieldNotInRequest", name));
					}
				} else {
					break;
				}
			}
			UnikMap dp = new UnikMap();
			dp.put("sql", sql);
			
			executeProcedure(getTpl("execProcedure"),dp);
		}
	}
	
	/**
	 * ����Ӧ�÷���ע��Ӧ�÷�����"s_"��ͷ���ֶζ�û��
	 * @param v ִ�н������Բ���,һ��Ϊrequest
	 * @throws java.lang.Exception
	 */
	public AppResponse doReturnService(UnikMap node,UnikMap varsUm) throws Exception {
		String tid = node.getString("retutrancode");
		//getInstVar(node.getString("wfid"), false);
		AppRequest req = new AppRequest();
		
		req.putAll(node);
		Iterator it = varsUm.keySet().iterator();
		while(it.hasNext()){
			String name = it.next().toString();
			String val = varsUm.getString(name);
			String n[]= StringUtil.split(name, ".");
			if(n.length==2){
				req.put(n[1], val);
			}else{
				req.put(name, val);
			}
		}
	
		return invoke(tid, req);
	}
	
	public AppResponse doEndService(UnikMap node,UnikMap varsUm) throws Exception {
		String tid = node.getString("submtrancode");
		//getInstVar(node.getString("wfid"), false);
		AppRequest req = new AppRequest();
		
		req.putAll(node);
		varsUm.remove("dealopin");//add by mahong 
		Iterator it = varsUm.keySet().iterator();
		while(it.hasNext()){
			String name = it.next().toString();
			String val = varsUm.getString(name);
			String n[]= StringUtil.split(name, ".");
			if(n.length==2){
				req.put(n[1], val);
			}else{
				req.put(name, val);
			}
		}
		
		/*<option value="10" text="ͬ��"/>
		<option value="11" text="ͬ�Ⲣ�ϱ�"/>
		<option value="20" text="��ͬ��"/>
		<option value="30" text="����"/>
		<option value="40" text="���"/>
		<option value="50" text="������������¼"/>
		<option value="60" text="ͬ������¼"/>*/
		String dealopin = req.getString("dealopin");
		
		if("ͬ��".equals(dealopin)) {
			req.put("dealopin", "10");
		} else if("ͬ�Ⲣ�ϱ�".equals(dealopin)) {
			req.put("dealopin", "11");
		} else if("��ͬ��".equals(dealopin)) {
			req.put("dealopin", "20");
		} else if("����".equals(dealopin)) {
			req.put("dealopin", "30");
		} else if("���".equals(dealopin)) {
			req.put("dealopin", "40");
		} else if("������������¼".equals(dealopin)) {
			req.put("dealopin", "50");
		} else if("ͬ������¼".equals(dealopin)) {
			req.put("dealopin", "60");
		}
		
		return invoke(tid, req);
	}
	/**
	 * ����Ӧ�÷���ע��Ӧ�÷�����"s_"��ͷ���ֶζ�û��
	 * @param v ִ�н������Բ���,һ��Ϊrequest
	 * @throws java.lang.Exception
	 */
	public AppResponse doService(UnikMap v) throws Exception {
		String tid = v.getString("submtrancode");
		getInstVar(v.getString("wfid"), false);
		AppRequest req = new AppRequest();
		
		req.putAll(v);
		Iterator it = vars.keySet().iterator();
		while(it.hasNext()){
			String name = it.next().toString();
			String val = vars.getString(name);
			String n[]= StringUtil.split(name, ".");
			if(n.length==2){
				req.put(n[1], val);
			}else{
				req.put(name, val);
			}
		}
	
		return invoke(tid, req);
	}
	
	/**
	 * ��һ�������е�����һ������.
	 * invoke(code, request.withparam("a:b,c,d"));
	 * @param code �������
	 */
	public AppResponse invoke(String code, AppRequest req) throws Exception{
		context.save();
		context.setRequest(req);
		req.setHeader("TranCode", code);
		try {
			AppModule s = application.prepareModule(code, context);
			return s.invoke();
		}
		finally {
			context.restore();
		}
	}

	/**
	 * ���ش����������ٵ��û�
	 * @throws Exception
	 */
	public String[] getTaskLeastUser(String[] user, int num) throws Exception {
		List list = new ArrayList();
		UnikMap map = new UnikMap();
		String [] retusr = new String[num];
		for(int i=0;i<user.length;i++){
			map.put(user[i], user[i]);
		}
		
		/*
			<sql name="getBankidFromDwtaskGroupbybankidAndOperid">
				select
					{fn concat({fn concat({fn rtrim(bankid)}, '/')}, operid)} userid,
					count(bankid) num
				from 
					dwtask 
				group by 
					bankid,operid 
				order by 2
			</sql>
		 */
		
		
		DataList dl = executeProcedure(getTpl("getBankidFromDwtaskGroupbybankidAndOperid"));
		String userid = null;
		StringBuffer strbuf = new StringBuffer();
		while(dl.next()){
			userid = dl.getString("userid");
			String s = map.getString(userid);
			if (s.length() > 0) {
				strbuf.append(userid);
				strbuf.append(",");
			}
		}

		if(strbuf.length()>2){
			strbuf.delete(strbuf.length()-1, strbuf.length());
			for(int j=0;j<user.length;j++){
				if(strbuf.toString().indexOf(user[j])<0){
					list.add(user[j]);//����û�����������Ա����,��������Ϊ0
				}
			}
			String [] strbusyusrs = strbuf.toString().split(",");
			for(int k=0;k<strbusyusrs.length;k++){
				list.add(strbusyusrs[k]);//��������ش�������Ա
			}
			//list���ŵ��ǰ��������е���Ա
			for(int m=0;m<num;m++){
				retusr[m]=(String)list.get(m);
			}
		}else{
			for(int m = 0;m<num;m++){
				retusr[m]=user[m];
			}
		}
		return retusr;
	}
	
	/**
	 * ɾ����������
	 * add by kangsj on 071109 from WorkflowStorageImpl.classs
	 * @throws java.lang.Exception
	 */
	public void deleteWFDef(String flowid) throws Exception {
		UnikMap dp = new UnikMap();
		dp.put("wherestr", " flowid='" + flowid + "'");
		/*
		 * <sql>
				delete from 
				@asis:tablename
				where
					@asis:wherestr
			</sql>
		 */
		deletewf(dp,"dwflowroute");
		deletewf(dp,"dwflownodepost");
		deletewf(dp,"dwopiniondefine");
		deletewf(dp,"dwflownode");
		deletewf(dp,"dwflowmain");
	}
	
	private void deletewf(UnikMap dp,String tabname) throws Exception{
		dp.put("tablename", tabname);
		executeProcedure(getTpl("DeletaProcedure"), dp);
	}
	
	private Service createService(String tid, AppContext context) throws Exception{
		return (Service)context.getApplication().prepareModule(tid, context);
	}
	
	/**
	 * ��������
	 * add by kangsj on 071109 from WorkflowStorageImpl.class
	 * @throws java.lang.Exception
	 */
	public void copyWFDef(String flowid, UnikMap props) throws Exception {
		String trancode = context.getRequest().getHeader("trancode");
		SerialNum sn = new SerialNum(createService(trancode, context), request);
		String keyno = sn.getGlobalNum("dwflowmain", 10);
		
		String flowName = props.get("flowname").toString();
		
		/*
			<sql name="insertDwFlowMainFromdwFlowMain">
				insert into dwFlowMain 
					(FlowId,
					FlowName,
					FlowDesc,
					FlowState,
					EffDate,
					EndDate,
					ManuSql,
					ExecSql)
				select 
					@keyno as FlowId,
					#if @flowName
						@flowName as 
					#end
					FlowName,
					FlowDesc,
					FlowState,
					EffDate,
					EndDate,
					ManuSql,
					ExecSql
				from 
					dwFlowMain 
				where 
					FlowId = @FlowId
			</sql>
		 */
		
		UnikMap dp = new UnikMap();
		dp.put("keyno", keyno);
		dp.put("flowname", flowName);
		dp.put("flowid", flowid);
		
		executeProcedure(getTpl("insertDwFlowMainFromdwFlowMain"), dp);
		
		/*
			<sql name="insertDwFlowNodeFromDwFlowNode">
				insert into dwFlowNode 
					(FLOWID, 
					NODEID, 
					NODENAME, 
					NODEDESC, 
					NODETYPE, 
					ISUNIT, 
					UNITFLOWID, 
					MINDEALNUM, 
					AUTODISUSERFLAG, 
					TASKOVERPOLICY, 
					ASSIGNMINNUM, 
					OVERPERCENT, 
					SELECTOPERFLAG, 
					EXECTRANCODE, 
					SUBMTRANCODE, 
					LOOKTRANCODE, 
					TRANTERM, 
					NEXTNODEMODE,
					NEXTNODE, 
					PROCESSMODE,
					HISFLAG) 
				select 
					@keyno as FlowId,
					NODEID, 
					NODENAME, 
					NODEDESC, 
					NODETYPE, 
					ISUNIT, 
					UNITFLOWID, 
					MINDEALNUM, 
					AUTODISUSERFLAG, 
					TASKOVERPOLICY, 
					ASSIGNMINNUM, 
					OVERPERCENT, 
					SELECTOPERFLAG, 
					EXECTRANCODE, 
					SUBMTRANCODE, 
					LOOKTRANCODE, 
					TRANTERM, 
					NEXTNODEMODE, 
					NEXTNODE, 
					PROCESSMODE,
					HISFLAG
				from 
					dwFlowNode 
				where 
					FlowId = @FlowId
			</sql>
		 */
		
		executeProcedure(getTpl("insertDwFlowNodeFromDwFlowNode"), dp);
		
		/*
			<sql name="insertDwFlowNodePostFromDwFlowNodePost">
				insert into dwFlowNodePost
					(FLOWID, NODEID, BANKID, POSTID, POSTBANKID)
				select 
					@keyno as FlowId,
					NODEID, BANKID, POSTID, POSTBANKID
				from
					dwFlowNodePost 
				where 
					FlowId = @FlowId
			</sql>
		 */
		
		executeProcedure(getTpl("insertDwFlowNodePostFromDwFlowNodePost"), dp);
		
		/*
			<sql name="insertDwFlowRouteFromDwFlowRoute">
				insert into dwFlowRoute
					(FlowId,
					NodeId,
					RouteId,
					RouteDesc,
					RouteCond,
					NextNodeId,
					expfirst1,
					expval1,
					expsecond1,
					expfield1,
					expconst1,
					expfirst2,
					expval2,
					expsecond2,
					expfield2,
					expconst2,
					expfirst3,
					expval3,
					expsecond3,
					expfield3,
					expconst3,
					expfirst4,
					expval4,
					expsecond4,
					expfield4,
					expconst4)
				select 
					@keyno as FlowId,
					NodeId,
					RouteId,
					RouteDesc,
					RouteCond,
					NextNodeId,
					expfirst1,
					expval1,
					expsecond1,
					expfield1,
					expconst1,
					expfirst2,
					expval2,
					expsecond2,
					expfield2,
					expconst2,
					expfirst3,
					expval3,
					expsecond3,
					expfield3,
					expconst3,
					expfirst4,
					expval4,
					expsecond4,
					expfield4,
					expconst4
				from 
					dwFlowRoute 
				where 
					FlowId = @FlowId
			</sql>
		 */
		executeProcedure(getTpl("insertDwFlowRouteFromDwFlowRoute"), dp);	
		
		//���������Ϣ
		UnikMap param = new UnikMap();
		
		param.put("flowid", flowid);	
		DataList dl = executeProcedure(getTpl("getdwopiniondefine"), param);
		while(dl.next()){
			String tmpnodeid = dl.getString("nodeid");
			String opincode = dl.getString("opincode");
			String opindesc = dl.getString("opindesc");
			
			//��ȡ���������
			long opinid = 0;
			DataList maxdl = executeProcedure(getTpl("getmaxopinidfromdwopiniondefine"));
			while(maxdl.next()){
				opinid = maxdl.getLong("opin");
				opinid = opinid + 1;
			}

			UnikMap m = new UnikMap();
			m.put("opinid", opinid);
			m.put("flowid", keyno);
			m.put("nodeid", tmpnodeid);
			m.put("opincode", opincode);
			m.put("opindesc", opindesc);
			performAction(INSERT,"dwOpinionDefine",m);
			
		}
		
			//����ҵ�������̶���       
			/*
			<sql name="insertDwBusiScopeWFMapFromDwBusiScopeWFMap">
				insert into dwBusiScopeWFMap
					(busiscope,
					flowid,
					operid,
					bankid,
					createdate,
					lastchanperson,
					lastchanbankid,
					lastchandate,
					busiscopename,
					lastchangetime)
				select
					busiscope,
					@keyno AS flowid,
					operid,
					bankid,
					createdate,
					lastchanperson,
					lastchanbankid,
					lastchandate,
					busiscopename,
					lastchangetime
				from 
					dwBusiScopeWFMap 
				where 
					flowid = @flowid
			</sql>
		 */
		//������Ŀû��dwbusiscopewfmap������Ҫ�˲���
		//executeProcedure(getTpl("insertdwbusiscopewfmapfromdwbusiscopewfmap"), dp);
	}
	
	/**
	 * �õ�TPL����
	 */
	private String getTpl(String procname){
		StringBuffer  className = new StringBuffer(this.getClass().getName());
		int index = className.indexOf("cn.com.jbbis.");
		
		if (index != -1){
			className.insert(className.lastIndexOf(".")+1, "procedure.");
			return "jar:" + className + "_" + procname;
		}else{
			return "jbbis" + procname;
		}
	}
	
	/**
	 * ������Ϣ��ʾ
	 * @param title ��Ϣ����
	 * @return "workflow/"+title
	 */
	private String getMsgInfo(String title){
		if (title == null || title.length() == 0)
			return null;
		return "workflow/"+title;
	}
	
	private class SerNoUpdator implements ColumnUpdator {
		public Object update(String name, String old) throws Exception {
			long n = Long.parseLong(old);
			String s = String.valueOf(n + 1);
			return s;
		}
	}

	public Object getTaskList(Object msg, String org, String user)
			throws Exception {
		return null;
	}

	/**
	 * ���������е����ݱ�����������ʷ����ձ�����
	 * @param wfid
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void delDwtaskVars(String wfid) throws Exception {
		UnikMap map = new UnikMap();
		map.put("wfid", wfid);
		/*
		 <sql name="insertDwtaskVarshisFromDwtaskvars">
			insert into 
				dwTaskVarshis
			select 
				* 
			from 
				dwTaskVars 
			where 
				wfid = @wfid
		</sql>
		 */
		executeProcedure(getTpl("insertDwtaskVarshisFromDwtaskvars"), map);
		
		/*
		 <sql name="deleteDwtaskVars">
			DELETE FROM
				dwTaskVars
			WHERE 
				wfid = @wfid
				and taskser = @number:taskser
				and varname in (@list:varname)
		 </sql>
		 */
		executeProcedure(getTpl("deleteDwtaskVars"), map);		
	}
	
	/**
	 * ����Ƿ���������Ա,������������ʱ��,ȥ���´�����Ա
	 * 
	 * @param flowid
	 *            ���������
	 * @param nodeid
	 *            �����ż��ڵ���
	 * @param num
	 *            ������䴦������
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
	public void updateDealNum(String flowid,String nodeid,int num) throws Exception{
		/*
		 <sql name="updateDealNumFromDwflowNode">
		 	update 
		 		dwflownode
		 	set
		 		mindealnum=@mindealnum
		 	where
		 		flowid=@flowid
		 		and nodeid=@nodeid
		 </sql>
		 */
		UnikMap m = new UnikMap();
		m.put("flowid", flowid);
		m.put("nodeid", nodeid);
		m.setInt("mindealnum", num);
		
		executeProcedure(getTpl("updateDealNumFromDwflowNode"), m);
	}
	
	/**
	 * ������ʷ�б��е���һ���ڱ��.����ж�����¼�᷵�����һ����Ϊnull������.
	 * @param wfInstId ���������
	 * @param nodeId ���ڱ��
	 * @return ���û�в�ѯ���������null
	 * @throws Exception
	 */
	public String getForeNodeId(String wfInstId, String nodeId) throws Exception{
		UnikMap param = new UnikMap();
		param.put("wfid", wfInstId);
		param.put("nodeid", nodeId);
		/*
		 <sql>
				select 
					* 
				from 
					dwtaskhis
				where 
					nodeid in (
		      			select 
		             		forenodeid 
		      			from 
		             		dwtaskhis 
		      			where 
		      				wfid=@wfid 
		      				and nodeid &lt;> forenodeid ORDER by dealsystime desc)
					and wfid=@wfid
					and nodeid=@nodeid
			</sql>
		 */
		String nodeid = null;
		DataList dl = executeProcedure(getTpl("getForeNodeId"), param);
		while(dl.next()){
			String s = dl.getString("forenodeid");
			nodeid = s == null ? nodeid : s;
		}
		return nodeid;
	}
	
	public String formatMessage(String s, Object obj)
    {
        return super.formatMessage("workflow/" + s, obj);
    }
	
	/**
	 * �������ݱ�����dwtaskvars
	 *
	 */
	private class AcParams implements BatchParams {
		private String strWfid,strTaskser,strNodeid,strLastChgDate,strLastChgTime;
		private Iterator it;
		private UnikMap vars = null;
		int wfid,taskser,nodeid,varname,varvalue,lastchgdate, lastchgtime;
		private String[] values;
		private AcParams(UnikMap m,UnikMap var) {
			
			Iterator it = var.keySet().iterator();
			while(it.hasNext())
			{
				String key = (String)it.next();
			}
			
			
			this.it = var.keySet().iterator();
			this.vars = var;
			this.strWfid = m.getString("wfid");
			this.strTaskser = m.getString("taskser");
			this.strNodeid = m.getString("nodeid");
			this.strLastChgDate = m.getString("lastchgdate");
			this.strLastChgTime = m.getString("lastchgtime");
		}
		
		public void init(Columns names) {
			values = new String[names.size()];
			wfid = names.indexOf("wfid");
			taskser = names.indexOf("taskser");
			nodeid = names.indexOf("nodeid");
			varname = names.indexOf("varname");
			varvalue = names.indexOf("varvalue");
			lastchgdate = names.indexOf("lastchgdate");
			lastchgtime = names.indexOf("lastchgtime");
		}

		public String[] next() {
			if(it.hasNext()){
				values[wfid] = strWfid;
				values[taskser] = strTaskser;
				values[nodeid] = strNodeid;
				values[lastchgdate] = strLastChgDate;
				values[lastchgtime] = strLastChgTime;
				String name = it.next().toString();
				values[varname] = name;
				values[varvalue] = vars.getString(name);
				
				return values;
			}
			return null;
		}		
	}
}