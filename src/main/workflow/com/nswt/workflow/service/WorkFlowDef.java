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
 * Description:流程定义检查器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: 北京天桥北大青鸟科技股份有限公司
 * </p>
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-1-25 下午04:07:30 Service
 */
public class WorkFlowDef extends CheckService {
	
	private String dealType = "";
	
	public void process() throws Exception {
		dealType = serviceConfig.getString("DealType");
		String tran = serviceConfig.getString("trancode");
		//流程定义处理  WF0001D
		if("lcdys".equalsIgnoreCase(tran)){
			checkMain();
			return;
		}
		//删除整个流程WF0001D1
		if("delAll".equalsIgnoreCase(tran)){
			checkWorkFlow();
			return;
		}
		//流程岗位定义 S802
		if("lcgwdy".equalsIgnoreCase(tran)){
			checkNode();
			return;
		}
		//岗位流转规则 S804
		if("gwlzgz".equalsIgnoreCase(tran)){
			checkRoute();
			return;
		}
		//岗位人员定义 S805
		if("gwrydy".equalsIgnoreCase(tran)){
			checkRole();
			return;
		}
		//预警规则定义 S820 
		if("yjgzdy".equalsIgnoreCase(tran)){
			checkAlert();
			return;
		}
		//流程节点意见定义
		if("lcjdyjdy".equalsIgnoreCase(tran)){
			checkNode();
			return;
		}
	}
	/**
	 * 流程定义 处理S820U
	 * @throws Exception
	 */
	private void checkAlert() throws Exception {
		SingleResult rs = querySingle(getWorkFlowTpl("getNum"));
		int ret = rs.getInt("flowstate");
		Assert(ret == 1, "loan/check/WF_WorkFlowIsUsed");//此流程已发布、暂停使用或禁用
		
	}
	/**
	 * 流程定义 处理S801D
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
			Assert(ret <= 0, "loan/check/WF_NeedDeleteFlowNode");//该流程包含节点定义，不能删除，请先删除流程节点定义！
		}else if(CommonConst.DEAL_TYPE_UPDATE.equals(dealType)){
			String flowState = request.getField("flowstate");
			String flowStateOld = request.getField("flowstate.old");
			if(CommonConst.FLOW_STATE_PAUSE.equals(flowState) || CommonConst.FLOW_STATE_STOP.equals(flowState)){
				if(CommonConst.FLOW_STATE_INIT.equals(flowStateOld)){
					Assert(false, "loan/check/WF_WorkFlowIsInit");   //流程状态为初始化,禁止操作
				}
			}
			if(CommonConst.FLOW_STATE_INIT.equals(flowState) && !"".equals(flowStateOld)){
				checkFlowInst();//检查流程是否在使用
			}
			//当流程进行发布时要对流程进行完整性检查
			if(CommonConst.FLOW_STATE_RELEASE.equals(flowState)){
				paras.put("nodetype", "1");
				SingleResult rs = querySingle(getWorkFlowTpl("getFlowNum"),paras,null);
				int ret = rs.getInt("flownum");
				Assert(ret > 0, "common/workflow/WF_NotFoundFlowNode");   //此流程没有开始节点或结束节点
				paras.put("nodetype", "3");
				rs.clear();
				rs = querySingle(getWorkFlowTpl("getFlowNum"),paras,null);
				ret = rs.getInt("flownum");
				Assert(ret > 0, "loan/check/WF_NotFoundFlowNode");   //此流程没有开始节点或结束节点
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
				String nodeIds = tmp.toString();//得到所有结点信息
				dl.resetIterator();
				while(dl.next()){
					String nodeType = dl.getString("nodetype");
					String nodeId = dl.getString("nodeid");
					String nextNodeMode = dl.getString("nextnodemode");	//2指定岗位1条件判断
					String nextNode = dl.getString("nextnode");	//2指定岗位1条件判断
					nextNode = "".equals(nextNode)?null:nextNode;
					paras.put("nodeid", nodeId);
					//检查节点的岗位信息
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
						//节点没有岗位信息
						Assert(srPost.getInt("num") > 0, context.getApplication().formatMessage("workflow/PostIdSetNotFound",nodeId));
					}
					//如果是开始节点或中间节点检查指定岗位模式
					if("2".equals(nextNodeMode) || CommonConst.NODE_TYPE_END.equals(nodeType)){
						//开始和结束节点只检查有没有岗位人员信息
						if(!CommonConst.NODE_TYPE_END.equals(nodeType)){
							String[] info ={nodeId,nextNode};
							//指定节点信息不存在.
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
							//流程节点a的岗位流转规则中指定岗位b不存在.
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
	 * @discription:删除整个流程 S801D1
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
		Assert(ret <= 0, "loan/check/WF_WorkFlowIsUsed");//该流程正在使用，不能删除！
	}
	/**
	 * @discription:流程岗位定义 S802   S、L、I不做检查  U、D检查
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
		Assert(ret == 1, "loan/check/WF_WorkFlowIsUsed");//此流程已发布、暂停使用或禁用
		//当做删除交易时
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
			Assert(ret <= 0, "loan/check/WF_WorkFlowHasPost");//该流程正在使用，不能删除！
			request.put("table", "dwFlowRoute");
			SingleResult rsf = querySingle(getWorkFlowTpl("getFlow"));
			ret = rsf.getInt("flownum");
			Assert(ret <= 0, "loan/check/WF_WorkFlowHasRoute");//该流程正在使用，不能删除！	
		}
	}
	/**
	 * @discription:岗位流转规则 S804  L交易检查
	 * @throws Exception
	 */
	private void checkRoute() throws Exception {
		String nextNodeMode = request.getField("nextnodemode");
		String nodeType = request.getField("nodetype");
		//下一节点设置模式 1=条件判断
		//只有指定下一节点设置方式为：计算路由，才能做此交易！
		Assert("1".equalsIgnoreCase(nextNodeMode), "loan/check/WF_NotNeedSetRouteMode");
		// 结束节点不需要设置路由规则   结束
		Assert(!CommonConst.NODE_TYPE_END.equalsIgnoreCase(nodeType), "loan/check/WF_EndNodeNotNeedRoute");
		
	}

	/**
	 * @discription: 岗位人员定义  S805L
	 * @throws Exception
	 */
	private void checkRole() throws Exception {
		String nodeType = request.getField("nodetype");
		Assert(!CommonConst.NODE_TYPE_OUT.equalsIgnoreCase(nodeType)
				&& !CommonConst.NODE_TYPE_UNITE.equalsIgnoreCase(nodeType),
				"loan/check/WF_NotNeedSetPost"); //如果节点为分发、合并节点不需要设置岗位
	}

	/**
	 * @discription:检查实例表
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
		Assert(rs.getInt("instNum") == 0,"loan/check/WF_WorkFlowIsUsed");//该流程正在使用，
	}
}
