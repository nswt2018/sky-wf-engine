package com.nswt.workflow.service;

import java.util.Iterator;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.sql.BatchParams;
import cn.com.jbbis.sql.Columns;
import cn.com.jbbis.util.UnikMap;


public class ServiceNodePostUpdate extends BaseService{

	
		@Override
		protected AppResponse process() throws Exception {
			String flowid=request.getField("flowid");
			String nodeid=request.getField("nodeid");
			String bankid=request.getField("s_bankid");
			//删除dwflownodepost表中关于该流程和节点的全部岗位信息
			UnikMap params = new UnikMap();
			params.put("flowid",flowid);
			params.put("nodeid",nodeid);
			this.executeProcedure("wf.DeleteNodePost", params);
			
			//封装的是bu变化的参数
			UnikMap params1 = new UnikMap();
			params1.put("flowid",flowid);
			params1.put("nodeid",nodeid);
			//默认加入本银行@
			params1.put("bankid", "@");
			
			UnikMap vars=new UnikMap();
			String[] postids = request.getArray("postid"); 
			for(int i=0;i<postids.length;i++){
				/*System.out.println("======================="+postids[i]);*/
				vars.put("postid"+i,postids[i]);
			}
			
			this.executeProcedure("wf.InsertNodePost", new MyBatch(params1,vars));
			return SUCCESS();
		}
		
		private class MyBatch implements BatchParams {
			private Iterator it;
			private UnikMap vars = null;
			int flowid,nodeid,bankid,postid;  //各列名的索引/*,postbankid,lastchgdate,lastchgtime,postauthority,superbankidscope,bindprodid*/
			String sflowid,snodeid,sbankid;			   //各列对应的值
			private String[] values;                   //各列的值
			
			private MyBatch(UnikMap m,UnikMap var) {
				this.it = var.keySet().iterator();
				this.vars = var;
				this.sflowid = m.getString("flowid");
				this.snodeid = m.getString("nodeid");
				this.sbankid = m.getString("bankid");
			}
			@Override
			public void init(Columns names) {
				values = new String[names.size()];
				flowid = names.indexOf("flowid");
				nodeid = names.indexOf("nodeid");
				bankid = names.indexOf("bankid");
				postid = names.indexOf("postid");
			}

			public String[] next() {
				if(it.hasNext()){
					values[flowid] = sflowid;
					values[nodeid] = snodeid;
					values[bankid] = sbankid;
					String name = it.next().toString();
					values[postid] =(String) vars.get(name);
					/*System.out.println("开始");
					for(int j=0;j<values.length;j++){
						
						System.out.println("values====="+values[j]);
						
					}
					System.out.println("结束");*/
					return values;
				}
				return null;
			}
		}

}
