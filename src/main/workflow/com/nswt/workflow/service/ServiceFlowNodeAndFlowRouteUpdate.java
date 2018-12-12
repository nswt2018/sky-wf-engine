package com.nswt.workflow.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.sql.BatchParams;
import cn.com.jbbis.sql.Columns;
import cn.com.jbbis.util.UnikMap;
import net.sf.json.JSONObject;

public class ServiceFlowNodeAndFlowRouteUpdate extends BaseService {

	@Override
	protected AppResponse process() throws Exception {
	System.out.println("===========================================");
		// TODO Auto-generated method stub
		UnikMap params = new UnikMap();
		String data = request.getField("data");
		String rectNum = request.getField("rectNum");
		String pathNum = request.getField("pathNum");
		String flowid = request.getField("flowid");
		JSONObject JSONdata = JSONObject.fromObject(data);
		JSONObject states = JSONdata.getJSONObject("states");
		JSONObject paths = JSONdata.getJSONObject("paths");
		// nodes存储所有的flownode信息
		// UnikMap nodes = new UnikMap();
		for (int i = 0; i < Integer.parseInt(rectNum); i++) {
			UnikMap var = new UnikMap();
			String rectName = "rect" + (i + 1);
			JSONObject rect = states.getJSONObject(rectName);
			var.put("attribute", rect.getString("attr"));
			var.put("nodeid", rect.getJSONObject("props").getJSONObject("nodeid").getString("value"));
			var.put("nodetype", rect.getJSONObject("props").getJSONObject("nodetype").getString("value"));
			var.put("nodephase", rect.getJSONObject("props").getJSONObject("nodephase").getString("value"));
			var.put("nodename", rect.getJSONObject("props").getJSONObject("nodename").getString("value"));
			var.put("nodedesc", rect.getJSONObject("props").getJSONObject("nodedesc").getString("value"));
			var.put("isprimaryauditnode",
					rect.getJSONObject("props").getJSONObject("isprimaryauditnode").getString("value"));
			var.put("isallowget", rect.getJSONObject("props").getJSONObject("isallowget").getString("value"));
			var.put("isallowreturn", rect.getJSONObject("props").getJSONObject("isallowreturn").getString("value"));
			var.put("mindealnum", rect.getJSONObject("props").getJSONObject("mindealnum").getString("value"));
			var.put("autodisuserflag", rect.getJSONObject("props").getJSONObject("autodisuserflag").getString("value"));
			var.put("processmode", rect.getJSONObject("props").getJSONObject("processmode").getString("value"));
			var.put("assignmindealnumstyle",
					rect.getJSONObject("props").getJSONObject("assignmindealnumstyle").getString("value"));
			var.put("taskoverpolicy", rect.getJSONObject("props").getJSONObject("taskoverpolicy").getString("value"));
			var.put("hisflag", rect.getJSONObject("props").getJSONObject("hisflag").getString("value"));
			var.put("assignminnum", rect.getJSONObject("props").getJSONObject("assignminnum").getString("value"));
			var.put("overpercent", rect.getJSONObject("props").getJSONObject("overpercent").getString("value"));
			var.put("exectrancode", rect.getJSONObject("props").getJSONObject("exectrancode").getString("value"));
			var.put("submtrancode", rect.getJSONObject("props").getJSONObject("submtrancode").getString("value"));
			var.put("looktrancode", rect.getJSONObject("props").getJSONObject("looktrancode").getString("value"));
			var.put("isunit", rect.getJSONObject("props").getJSONObject("isunit").getString("value"));
			var.put("unitflowid", rect.getJSONObject("props").getJSONObject("unitflowid").getString("value"));
			var.put("nextnodemode", rect.getJSONObject("props").getJSONObject("nextnodemode").getString("value"));
			var.put("nextnode", rect.getJSONObject("props").getJSONObject("nextnode").getString("value"));
			if(rect.getJSONObject("props").getJSONObject("version").getString("value")==null||"".equals(rect.getJSONObject("props").getJSONObject("version").getString("value"))){
				var.put("version","1.0");
			}else{
				var.put("version",Float.parseFloat(rect.getJSONObject("props").getJSONObject("version").getString("value"))+0.1);
			}
			
			var.put("flowid", flowid);
			performAction(INSERT, "dwflownode", var);
			// nodes.put(rectName,var);
		}
		for (int i = 0; i < Integer.valueOf(pathNum); i++) {
			String pathName = "path" + (Integer.valueOf(rectNum) +i + 1);
			JSONObject path = paths.getJSONObject(pathName);
			UnikMap var = new UnikMap();
			var.put("textpos", path.getJSONObject("text").getString("textPos"));
			var.put("routeid", path.getJSONObject("props").getJSONObject("routeid").getString("value"));
			var.put("routedesc", path.getJSONObject("props").getJSONObject("routedesc").getString("value"));
			var.put("nodeid", path.getJSONObject("props").getJSONObject("nodeid").getString("value"));
			var.put("routecond", path.getJSONObject("props").getJSONObject("routecond").getString("value"));
			var.put("nextnodeid", path.getJSONObject("props").getJSONObject("nextnodeid").getString("value"));
			var.put("expfirst1", path.getJSONObject("props").getJSONObject("expfirst1").getString("value"));
			var.put("expval1", path.getJSONObject("props").getJSONObject("expval1").getString("value"));
			var.put("expsecond1", path.getJSONObject("props").getJSONObject("expsecond1").getString("value"));
			var.put("expconsttype1", path.getJSONObject("props").getJSONObject("expconsttype1").getString("value"));
			var.put("expconst1", path.getJSONObject("props").getJSONObject("expconst1").getString("value"));
			var.put("expfirst2", path.getJSONObject("props").getJSONObject("expfirst2").getString("value"));
			var.put("expval2", path.getJSONObject("props").getJSONObject("expval2").getString("value"));
			var.put("expsecond2", path.getJSONObject("props").getJSONObject("expsecond2").getString("value"));
			var.put("expconsttype2", path.getJSONObject("props").getJSONObject("expconsttype2").getString("value"));
			var.put("expconst2", path.getJSONObject("props").getJSONObject("expconst2").getString("value"));
			var.put("expfirst3", path.getJSONObject("props").getJSONObject("expfirst3").getString("value"));
			var.put("expval3", path.getJSONObject("props").getJSONObject("expval3").getString("value"));
			var.put("expsecond3", path.getJSONObject("props").getJSONObject("expsecond3").getString("value"));
			var.put("expconsttype3", path.getJSONObject("props").getJSONObject("expconsttype3").getString("value"));
			var.put("expconst3", path.getJSONObject("props").getJSONObject("expconst3").getString("value"));
			var.put("expfirst4", path.getJSONObject("props").getJSONObject("expfirst4").getString("value"));
			var.put("expval4", path.getJSONObject("props").getJSONObject("expval4").getString("value"));
			var.put("expsecond4", path.getJSONObject("props").getJSONObject("expsecond4").getString("value"));
			var.put("expconsttype4", path.getJSONObject("props").getJSONObject("expconsttype4").getString("value"));
			var.put("expconst4", path.getJSONObject("props").getJSONObject("expconst4").getString("value"));
			if(path.getJSONObject("props").getJSONObject("version").getString("value")==null||"".equals(path.getJSONObject("props").getJSONObject("version").getString("value"))){
				var.put("version","1.0");
			}else{
				var.put("version",Float.parseFloat(path.getJSONObject("props").getJSONObject("version").getString("value"))+0.1);
			}
			/*var.put("version",Float.parseFloat(path.getJSONObject("props").getJSONObject("version").getString("value"))+0.1);*/
			var.put("flowid", flowid);
			performAction(INSERT, "dwflowroute", var);
		}
		// UnikMap var=(UnikMap)nodes.get("rect1");
		// System.out.println("------------------"+var.getString("nodeid"));
		// this.executeProcedure("wf.wf0002U", new NodeBatch(nodes));
		return SUCCESS();
	}
	/**
	 * 批处理构建sql来完成批量新增记录，需要写tpl文件，如:
	 * <p>
	 * <sql>insert into Dwtaskvars(wfid,taskser,nodeid,varname,varvalue)
	 * values(@wfid,@taskser,@nodeid,@varname,@varvalue)</sql>
	 * <p>
	 * params存入的是不变的字段，而vars中存入的每个变量都将是一条记录
	 */

	/*
	 * private class NodeBatch implements BatchParams { private UnikMap vars =
	 * null; private Iterator it; int attribute, nodeid, nodetype, nodephase,
	 * nodename, nodedesc, isprimaryauditnode, isallowget, isallowreturn,
	 * mindealnum, autodisuserflag, processmode, assignmindealnumstyle,
	 * taskoverpolicy, hisflag, assignminnum, overpercent, exectrancode,
	 * submtrancode, looktrancode, isunit, unitflowid, nextnodemode, nextnode;
	 * //各列名的索引 private String[] values; //各列的值 private NodeBatch(UnikMap nodes)
	 * { it=nodes.keySet().iterator(); this.vars = nodes; }
	 * 
	 * public void init(Columns names) { values = new String[names.size()];
	 * System.out.println("------------values初始化--names.size="+names.size()+
	 * names.toString()); //attribute= names.indexOf("attribute"); nodeid=
	 * names.indexOf("nodeid");
	 * System.out.println("0-----------nodeid:"+nodeid); nodetype=
	 * names.indexOf("nodetype"); nodephase= names.indexOf("nodephase");
	 * nodename= names.indexOf("nodename"); nodedesc= names.indexOf("nodedesc");
	 * isprimaryauditnode= names.indexOf("isprimaryauditnode"); isallowget=
	 * names.indexOf("isallowget"); isallowreturn=
	 * names.indexOf("isallowreturn"); mindealnum= names.indexOf("mindealnum");
	 * autodisuserflag= names.indexOf("autodisuserflag"); processmode=
	 * names.indexOf("processmode"); assignmindealnumstyle=
	 * names.indexOf("assignmindealnumstyle"); taskoverpolicy=
	 * names.indexOf("taskoverpolicy"); hisflag= names.indexOf("hisflag");
	 * assignminnum= names.indexOf("assignminnum"); overpercent=
	 * names.indexOf("overpercent"); exectrancode=
	 * names.indexOf("exectrancode"); submtrancode=
	 * names.indexOf("submtrancode"); looktrancode=
	 * names.indexOf("looktrancode"); isunit= names.indexOf("isunit");
	 * unitflowid= names.indexOf("unitflowid"); nextnodemode=
	 * names.indexOf("nextnodemode"); nextnode= names.indexOf("nextnode"); }
	 * 
	 * public String[] next() { if(it.hasNext()){ UnikMap var = (UnikMap)
	 * vars.get(it.next().toString());
	 * //values[attribute]=var.getString("attribute");
	 * values[nodeid]=var.getString("nodeid");
	 * values[nodetype]=var.getString("nodetype");
	 * values[nodephase]=var.getString("nodephase");
	 * values[nodename]=var.getString("nodename");
	 * values[nodedesc]=var.getString("nodedesc");
	 * values[isprimaryauditnode]=var.getString("isprimaryauditnode");
	 * values[isallowget]=var.getString("isallowget");
	 * values[isallowreturn]=var.getString("isallowreturn");
	 * values[mindealnum]=var.getString("mindealnum");
	 * values[autodisuserflag]=var.getString("autodisuserflag");
	 * values[processmode]=var.getString("processmode");
	 * values[assignmindealnumstyle]=var.getString("assignmindealnumstyle");
	 * values[taskoverpolicy]=var.getString("taskoverpolicy");
	 * values[hisflag]=var.getString("hisflag");
	 * values[assignminnum]=var.getString("assignminnum");
	 * values[overpercent]=var.getString("overpercent");
	 * values[exectrancode]=var.getString("exectrancode");
	 * values[submtrancode]=var.getString("submtrancode");
	 * values[looktrancode]=var.getString("looktrancode");
	 * values[isunit]=var.getString("isunit");
	 * values[unitflowid]=var.getString("unitflowid");
	 * values[nextnodemode]=var.getString("nextnodemode");
	 * values[nextnode]=var.getString("nextnode");
	 * System.out.println("next()-----------"+values[nodeid]); return values; }
	 * return null; } }
	 */
}
