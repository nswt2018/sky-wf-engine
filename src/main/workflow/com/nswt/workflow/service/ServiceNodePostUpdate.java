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
			//ɾ��dwflownodepost���й��ڸ����̺ͽڵ��ȫ����λ��Ϣ
			UnikMap params = new UnikMap();
			params.put("flowid",flowid);
			params.put("nodeid",nodeid);
			this.executeProcedure("wf.DeleteNodePost", params);
			
			//��װ����bu�仯�Ĳ���
			UnikMap params1 = new UnikMap();
			params1.put("flowid",flowid);
			params1.put("nodeid",nodeid);
			//Ĭ�ϼ��뱾����@
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
			int flowid,nodeid,bankid,postid;  //������������/*,postbankid,lastchgdate,lastchgtime,postauthority,superbankidscope,bindprodid*/
			String sflowid,snodeid,sbankid;			   //���ж�Ӧ��ֵ
			private String[] values;                   //���е�ֵ
			
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
					/*System.out.println("��ʼ");
					for(int j=0;j<values.length;j++){
						
						System.out.println("values====="+values[j]);
						
					}
					System.out.println("����");*/
					return values;
				}
				return null;
			}
		}

}
