package com.nswt.workflow.service;
import cn.com.jbbis.common.check.CheckService;
import cn.com.jbbis.afx.SingleResult;
//import cn.com.jbbis.loan.constant.CommonConst;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;
/**
 * <p>
 * Title:
 * </p>
 * Description:���̶�������
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: �������ű�������Ƽ��ɷ����޹�˾
 * </p>
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-1-25 ����04:07:30 Service
 */
public class WorkFlowDef extends CheckService {
	
	private String dealType = "";
	
	public void process() throws Exception {
		dealType = serviceConfig.getString("DealType");
		String tran = serviceConfig.getString("trancode");
		//���̶��崦��  WF0001D
		if("lcdys".equalsIgnoreCase(tran)){
			checkMain();
			return;
		}
		//ɾ����������WF0001D1
		if("delAll".equalsIgnoreCase(tran)){
			checkWorkFlow();
			return;
		}
		//���̸�λ���� S802
		if("lcgwdy".equalsIgnoreCase(tran)){
			checkNode();
			return;
		}
		//��λ��ת���� S804
		if("gwlzgz".equalsIgnoreCase(tran)){
			checkRoute();
			return;
		}
		//��λ��Ա���� S805
		if("gwrydy".equalsIgnoreCase(tran)){
			checkRole();
			return;
		}
		//Ԥ�������� S820 
		if("yjgzdy".equalsIgnoreCase(tran)){
			checkAlert();
			return;
		}
		//���̽ڵ��������
		if("lcjdyjdy".equalsIgnoreCase(tran)){
			checkNode();
			return;
		}
	}
	/**
	 * ���̶��� ����S820U
	 * @throws Exception
	 */
	private void checkAlert() throws Exception {
		SingleResult rs = querySingle(getWorkFlowTpl("getNum"));
		int ret = rs.getInt("flowstate");
		Assert(ret == 1, "loan/check/WF_WorkFlowIsUsed");//�������ѷ�������ͣʹ�û����
		
	}
	/**
	 * ���̶��� ����S801D
	 * @throws Exception
	 */
	private void checkMain() throws Exception {
		/*
		 	<sql name="getFlowNum">
		 		select 
		 			count(flowid) flownum, 
		 		from 
		 			dwFlowNode 
		 		where 
		 			flowid=@flowid
		 			#if @nodetype
		 			    and nodetype in (@list:nodetype)
		 			#end
		 	</sql>
		*/
		UnikMap paras = new UnikMap();
		paras.put("flowid", request.getField("flowid"));
		if(CommonConst.DEAL_TYPE_DELETE.equals(dealType)){
			SingleResult rs = querySingle(getWorkFlowTpl("getFlowNum"),paras,null);
			int ret = rs.getInt("flownum");
			Assert(ret <= 0, "loan/check/WF_NeedDeleteFlowNode");//�����̰����ڵ㶨�壬����ɾ��������ɾ�����̽ڵ㶨�壡
		}else if(CommonConst.DEAL_TYPE_UPDATE.equals(dealType)){
			String flowState = request.getField("flowstate");
			String flowStateOld = request.getField("flowstate.old");
			if(CommonConst.FLOW_STATE_PAUSE.equals(flowState) || CommonConst.FLOW_STATE_STOP.equals(flowState)){
				if(CommonConst.FLOW_STATE_INIT.equals(flowStateOld)){
					Assert(false, "loan/check/WF_WorkFlowIsInit");   //����״̬Ϊ��ʼ��,��ֹ����
				}
			}
			if(CommonConst.FLOW_STATE_INIT.equals(flowState) && !"".equals(flowStateOld)){
				checkFlowInst();//��������Ƿ���ʹ��
			}
			//�����̽��з���ʱҪ�����̽��������Լ��
			if(CommonConst.FLOW_STATE_RELEASE.equals(flowState)){
				paras.put("nodetype", "1");
				SingleResult rs = querySingle(getWorkFlowTpl("getFlowNum"),paras,null);
				int ret = rs.getInt("flownum");
				Assert(ret > 0, "common/workflow/WF_NotFoundFlowNode");   //������û�п�ʼ�ڵ������ڵ�
				paras.put("nodetype", "3");
				rs.clear();
				rs = querySingle(getWorkFlowTpl("getFlowNum"),paras,null);
				ret = rs.getInt("flownum");
				Assert(ret > 0, "loan/check/WF_NotFoundFlowNode");   //������û�п�ʼ�ڵ������ڵ�
				/*
				 	<sql name="getNodeId">
				 		select 
				 			flowid,nodeid,nodetype,NextNodeMode,NextNode 
				 		from 
				 			dwflownode where flowid=@flowid
				 	</sql>
				*/
				DataList dl = service.executeProcedure(getWorkFlowTpl("getNodeId"),paras);
				StringBuffer tmp = new StringBuffer();
				while(dl.next()){
					tmp.append(dl.getString("nodeid")+",");
				}
				String nodeIds = tmp.toString();//�õ����н����Ϣ
				dl.resetIterator();
				while(dl.next()){
					String nodeType = dl.getString("nodetype");
					String nodeId = dl.getString("nodeid");
					String nextNodeMode = dl.getString("nextnodemode");	//2ָ����λ1�����ж�
					String nextNode = dl.getString("nextnode");	//2ָ����λ1�����ж�
					nextNode = "".equals(nextNode)?null:nextNode;
					paras.put("nodeid", nodeId);
					//���ڵ�ĸ�λ��Ϣ
					/*
					 	<sql name="getPostNum">
					 		select 
					 			count(nodeid) num
					 		from
					 			dwflownodepost
					 		where
					 			nodeid=@nodeid
					 			and flowid=@flowid
					 	</sql>
					*/
					if(CommonConst.NODE_TYPE_MID.equals(nodeType)){
						SingleResult srPost = querySingle(getWorkFlowTpl("getPostNum"),paras,null);
						//�ڵ�û�и�λ��Ϣ
						Assert(srPost.getInt("num") > 0, context.getApplication().formatMessage("workflow/PostIdSetNotFound",nodeId));
					}
					//����ǿ�ʼ�ڵ���м�ڵ���ָ����λģʽ
					if("2".equals(nextNodeMode) || CommonConst.NODE_TYPE_END.equals(nodeType)){
						//��ʼ�ͽ����ڵ�ֻ�����û�и�λ��Ա��Ϣ
						if(!CommonConst.NODE_TYPE_END.equals(nodeType)){
							String[] info ={nodeId,nextNode};
							//ָ���ڵ���Ϣ������.
							Assert(nodeIds.indexOf(nextNode+",") != -1,
									context.getApplication().formatMessage("common/workflow/NextNodeNotFounds",info));
						}
						continue;
					}else if("1".equals(nextNodeMode)){	
						/*
						 	<sql name="getNextNodeId">
						 		select 
						 			nextnodeid
						 		from 
						 			dwFlowroute 
						 		where 
						 			flowid =@flowid
						 			and nodeid = @nodeid
						 	</sql>
						*/
						DataList dlNext = service.executeProcedure(getWorkFlowTpl("getNextNodeId"),paras);
						tmp.delete(0, tmp.length());
						while(dlNext.next()){
							String routeNode = dlNext.getString("nextnodeid");
							routeNode = "".equals(routeNode)?null:routeNode;
							//���̽ڵ�a�ĸ�λ��ת������ָ����λb������.
							String[] info ={nodeId,routeNode};
							Assert(nodeIds.indexOf(routeNode+",") != -1,
									context.getApplication().formatMessage("common/workflow/RouteNextNodeNotFound",info));
							tmp.append(routeNode).append(",");
						}
						Assert(tmp.length() > 0, context.getApplication().formatMessage("common/workflow/WF_NotFoundFlowRoute",nodeId));
					}
				}
			}
		}
	}
	/**
	 * @discription:ɾ���������� S801D1
	 * @throws Exception
	 */
	private void checkWorkFlow() throws Exception{
		/*
		 	<sql name="getSum">
		 		select
		 			count(flowid) countnum
		 		from
		 			cmBankElecScope
		 		where
		 			flowid=@flowid
		 	</sql>
		*/
		SingleResult rs = querySingle(getWorkFlowTpl("getSum"));
		int ret = rs.getInt("countnum");
		Assert(ret <= 0, "loan/check/WF_WorkFlowIsUsed");//����������ʹ�ã�����ɾ����
	}
	/**
	 * @discription:���̸�λ���� S802   S��L��I�������  U��D���
	 * @throws Exception
	 */
	private void checkNode() throws Exception {
		/*
			<sql name="getNum">
				select 
					flowstate 
				from 
					dwflowmain
				where 
					flowid=@flowid
			</sql>
		*/
		SingleResult rs = querySingle(getWorkFlowTpl("getNum"));
		int ret = rs.getInt("flowstate");
		Assert(ret == 1, "loan/check/WF_WorkFlowIsUsed");//�������ѷ�������ͣʹ�û����
		//����ɾ������ʱ
		if(CommonConst.DEAL_TYPE_DELETE.equalsIgnoreCase(dealType)){
			/*
				<sql name="getFlow">
					select 
						count(flowid) as flownum 
					from 
						@asis:table
					where 
						flowid=@flowid
						and nodeid=@nodeid
				</sql>
			*/
			request.put("table", "dwflownodepost");
			SingleResult rss = querySingle(getWorkFlowTpl("getFlow"));
			ret = rss.getInt("flownum");
			Assert(ret <= 0, "loan/check/WF_WorkFlowHasPost");//����������ʹ�ã�����ɾ����
			request.put("table", "dwFlowRoute");
			SingleResult rsf = querySingle(getWorkFlowTpl("getFlow"));
			ret = rsf.getInt("flownum");
			Assert(ret <= 0, "loan/check/WF_WorkFlowHasRoute");//����������ʹ�ã�����ɾ����	
		}
	}
	/**
	 * @discription:��λ��ת���� S804  L���׼��
	 * @throws Exception
	 */
	private void checkRoute() throws Exception {
		String nextNodeMode = request.getField("nextnodemode");
		String nodeType = request.getField("nodetype");
		//��һ�ڵ�����ģʽ 1=�����ж�
		//ֻ��ָ����һ�ڵ����÷�ʽΪ������·�ɣ��������˽��ף�
		Assert("1".equalsIgnoreCase(nextNodeMode), "loan/check/WF_NotNeedSetRouteMode");
		// �����ڵ㲻��Ҫ����·�ɹ���   ����
		Assert(!CommonConst.NODE_TYPE_END.equalsIgnoreCase(nodeType), "loan/check/WF_EndNodeNotNeedRoute");
		
	}

	/**
	 * @discription: ��λ��Ա����  S805L
	 * @throws Exception
	 */
	private void checkRole() throws Exception {
		String nodeType = request.getField("nodetype");
		Assert(!CommonConst.NODE_TYPE_OUT.equalsIgnoreCase(nodeType)
				&& !CommonConst.NODE_TYPE_UNITE.equalsIgnoreCase(nodeType),
				"loan/check/WF_NotNeedSetPost"); //����ڵ�Ϊ�ַ����ϲ��ڵ㲻��Ҫ���ø�λ
	}

	/**
	 * @discription:���ʵ����
	 * @throws Exception
	 */
	public void checkFlowInst() throws Exception{
		/*
		 	<sql name="getInstNum">
		 		select
		 			count(flowid) instNum
		 		from
		 			dwflowinst
		 		where
		 			flowid=@flowid
		 	</sql>
		*/
		SingleResult rs = querySingle(getWorkFlowTpl("getInstNum"));
		Assert(rs.getInt("instNum") == 0,"loan/check/WF_WorkFlowIsUsed");//����������ʹ�ã�
	}
}
