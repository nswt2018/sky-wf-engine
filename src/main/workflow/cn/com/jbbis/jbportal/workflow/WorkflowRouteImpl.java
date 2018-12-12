package cn.com.jbbis.jbportal.workflow;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import cn.com.jbbis.util.StringUtil;
import cn.com.jbbis.util.UnikMap;

public class WorkflowRouteImpl implements WorkflowRoute {
	/**
	 * 根据路由规则,取下一节点编号.路由规则支持变量与变量比较,被比较的变量使用大括号{A.B}.
	 * 由于此处使用范围较单纯，所以就没有考虑大括号包括的字符串问题
	 * @param route 路由规则
	 * @param wfVars 判断所需参数
	 * @return String 节点编号,取不到节点返回null
	 * @throws java.lang.Exception
	 */
	public UnikMap getNext(LinkedList route, UnikMap wfVars) throws Exception {
		Hashtable t = new Hashtable();
		UnikMap routeResult = new UnikMap();
//		t.putAll(wfVars);
		Iterator it = wfVars.keySet().iterator();
		while(it.hasNext()){
			
			String key = String.valueOf(it.next());             // 取出参数的key
			if(key!=null && key.indexOf(".") > 0)
				t.put(key.toUpperCase(), wfVars.getString(key));
		}
		
		synchronized(WorkflowRouteImpl.class) {
			for(int i = 0; i < route.size();){
				String  exp= route.get(i++).toString();
				String  tid = route.get(i++).toString();
				String  expConstType = route.get(i++).toString();
				String  taskassignstyle = route.get(i)==null ? "N" :route.get(i).toString();
				i++;
				String  routetype = route.get(i)==null ? "0" :route.get(i).toString();
				i++;
				
				exp = replaceExp(exp, t);                           // 替换变量
				//System.out.println("exp=" +exp + ";t=" +t);
				
				/////////////simon modified on 20180908
				Object obj = cn.com.jbbis.common.eval.Evaluation.eval(exp,t);
//				Object obj = null;
//				try{
//					obj = cn.com.jbbis.common.eval.Evaluation.eval("EQ(10,同意)",t);
//				}catch(Exception e) {
//					e.printStackTrace();
//				}
				//////////////////////////////////////////
				
				boolean flag = false;
				if(obj instanceof Boolean){
					flag = ((Boolean)obj).booleanValue();
				}else if(obj instanceof String){
					if("true".equalsIgnoreCase(obj.toString()))
						flag = true;
				}
				if(flag){
					routeResult.put("nextNodeId", tid);
					routeResult.put("taskAssignStyle", taskassignstyle);
					routeResult.put("routeType", routetype);
					return routeResult;
				}	
			}
		}
		return null;
	}

	public String getNext(UnikMap wfVars) throws Exception {
		return null;
	}

	/**
	 * 将变量{A.B}转换成A.B,参与计算,目前只支持一个变量替换,如果是表达式计算则还需要改进.
	 * 目前为了解决苏州公积金问题,临时做此简单支持
	 */
	private static String replaceExp(String exp, Map var)
	{
		Iterator it = var.keySet().iterator();
		while(it.hasNext())
		{
			String key = String.valueOf(it.next());
			exp = StringUtil.replace(exp, "{" + key + "}", String.valueOf(var.get(key)));
		}
		if(exp.indexOf("{") > -1)
			exp = StringUtil.replace(exp, "\\{", "{");
		if(exp.indexOf("}") > -1)
			exp = StringUtil.replace(exp, "\\}", "}");
		return exp;
	}
}