package cn.com.jbbis.jbportal.workflow;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import cn.com.jbbis.util.StringUtil;
import cn.com.jbbis.util.UnikMap;

public class WorkflowRouteImpl implements WorkflowRoute {
	/**
	 * ����·�ɹ���,ȡ��һ�ڵ���.·�ɹ���֧�ֱ���������Ƚ�,���Ƚϵı���ʹ�ô�����{A.B}.
	 * ���ڴ˴�ʹ�÷�Χ�ϵ��������Ծ�û�п��Ǵ����Ű������ַ�������
	 * @param route ·�ɹ���
	 * @param wfVars �ж��������
	 * @return String �ڵ���,ȡ�����ڵ㷵��null
	 * @throws java.lang.Exception
	 */
	public UnikMap getNext(LinkedList route, UnikMap wfVars) throws Exception {
		Hashtable t = new Hashtable();
		UnikMap routeResult = new UnikMap();
//		t.putAll(wfVars);
		Iterator it = wfVars.keySet().iterator();
		while(it.hasNext()){
			
			String key = String.valueOf(it.next());             // ȡ��������key
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
				
				exp = replaceExp(exp, t);                           // �滻����
				//System.out.println("exp=" +exp + ";t=" +t);
				
				/////////////simon modified on 20180908
				Object obj = cn.com.jbbis.common.eval.Evaluation.eval(exp,t);
//				Object obj = null;
//				try{
//					obj = cn.com.jbbis.common.eval.Evaluation.eval("EQ(10,ͬ��)",t);
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
	 * ������{A.B}ת����A.B,�������,Ŀǰֻ֧��һ�������滻,����Ǳ��ʽ��������Ҫ�Ľ�.
	 * ĿǰΪ�˽�����ݹ���������,��ʱ���˼�֧��
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