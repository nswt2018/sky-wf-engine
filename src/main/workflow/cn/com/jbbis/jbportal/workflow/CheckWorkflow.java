/*
 * @(#)CheckWorkflow.java
 * 
 * Beijing Beida Jade Bird Business Information System Co.,Ltd
 *
 * Copyright (c) 2009 JBBIS. All Rights Reserved.
 *
 * http://www.jbbis.com.cn
 */

package cn.com.jbbis.jbportal.workflow;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import cn.com.jbbis.afx.AppContext;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.Application;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.ehcahe.EhcacheTools;
import cn.com.jbbis.common.ehcahe.vo.CmbankVo;
import cn.com.jbbis.common.ehcahe.vo.InterfaceVo;
import cn.com.jbbis.jbportal.BizLogic;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.Errors;
import cn.com.jbbis.util.UnikMap;

/**
 * 流程启动检查器.
 * <P>
 *
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-7 下午05:22:54
 * @since   JBPortal3.0
 */

public class CheckWorkflow extends BizLogic{
	private AppContext ctx;
	private Application app;
	private static Object obj;
	public CheckWorkflow(AppContext context){
		set(context);
		ctx = context;
		this.app = context.getApplication();
	}
	
	/**
	 * 判断当前交易是否走流程.
	 * @param tranCode 交易号
	 * @return 如果是则返回流程编号,否则返回null
	 * @throws Exception
	 */
	public String checkStartCondition(String tranCode) throws Exception{
		String flowid = null;
		Properties prop = loadConf(ctx, tranCode);
		if(prop != null){
			flowid = prop.getProperty(tranCode);
		}
		return flowid;
	}
	
	/**
	 * 判断是否是电子审批流程,主要是读取CmBankElecScope表中的配置信息
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
    private Properties loadConf(AppContext ctx, String tranCode) throws Exception {
    	//判断此交易是否走流程 begin add by kangsj	
    	//  prop.put(htb.get("submtrancode"), htb.get("flowid"));
		
    	Properties pt = new Properties();
    	//tranCode  提交交易
    	// request.getField("s_bankid") 机构号
    	//request.getField("prodid")  产品号
    	
    	String prodid = request.getField("prodid");    	
    	/////////////debug ////////////////
    	if(prodid==null || "".equals(prodid)) 
    	{
    		log(ERROR, "传入参数Prodid不能为空！");
    	}	
    	///////////////////////////////////
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		List<InterfaceVo>list = EhcacheTools.queryBankInfo(request.getField("s_operid"), "1");
		
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryBankInfo cost:"+(endTime_-beginTime_));
		
		
		String[] logbankinfoArray = null;
		if(list!=null && list.size()>0) {
			logbankinfoArray = cacheTools.transBankid(list);
		}else{
			logbankinfoArray = new String[]{request.getField("s_bankid")};
		}
		
		beginTime_ = System.currentTimeMillis();
		UnikMap u = cacheTools.queryCmbankelecscopeVo(logbankinfoArray, prodid, tranCode);
		endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryCmbankelecscopeVo cost:"+(endTime_-beginTime_));
		
		if(u==null) {
			return null;
		}
		
    	pt.put(tranCode, u.getString("flowid"));
    	
		ctx.log(Service.INFO, "[loadConf NO:2] tranCode: " + tranCode+", value is " + pt.toString());
		
		//flowdi
		return pt;
	}
    
    /**
     * table 按照序号存放了电子审批范围每条记录的所有字段的值。table是一个记录集
     * 
	 * 检查cmBankElecScope表中此交易走哪个流程,
	 * 如果支行或分行没有找到相应的记录，则查找上级行,递归查找
	 * 如果是贷款申请流程,则会根据产品去查找电子审批范围表,其它
	 * 业务种类的流程暂时只根据机构号去查找,如果同一产品本级机构
	 * 和上级机构都配置了流程，则以本级机构为主
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
    private void iselecappr(Hashtable table, AppContext ctx,
			Properties prop, String bankid, boolean flag) throws Exception {
		if (flag == true)
			return;
		Enumeration en = table.elements();
		AppRequest request = ctx.getRequest();
		int lever = 0;
		
		while (en.hasMoreElements()) 
		{
			Hashtable htb = (Hashtable) en.nextElement();
			if (bankid.equals(htb.get("bankid"))) {
				//flowtype值等1,说明是贷款申请流程,贷款申请流程需要匹配产品编号    流程业务种类
				Object flowType = htb.get("flowtype");

				//流程是否绑定到业务种类上  LIUXJ
			
					if (CommonConst.WF_TYPE_LOANAPPLY.equals(flowType) 							
							//|| CommonConst.WF_TYPE_LOANAFTER.equals(flowType)
							|| CommonConst.WF_TYPE_PLOANAPPLY.equals(flowType)) {   //业务种类为贷款申请或对公贷款申请
						//通过loanid获取产品编号
						String proId = request.getField("prodid");  //getProdId(request);
						
						
						//如果此产品ID和数据表中的产品ID不相同,则继续
						if (!proId.equals(htb.get("prodid"))) {
							continue;
						}
					} else
					
					//2012-09-25贷后管理流程中，对于账户变更，需要特殊处理，根据产品的不同去走不同的流程
					if (CommonConst.WF_TYPE_LOANAFTER.equals(flowType)){
						String proId = request.getField("prodid");
						//判断是否是绑定产品，主要是针对账户变更处理，目前除账户变更外的其它贷后流程都是绑定业务的
						String flowbindstyle = htb.get("flowbindstyle").toString();
						
						if ("1".equals(flowbindstyle)) {
							//如果此产品ID和数据表中的产品ID不相同,则继续
							if (!proId.equals(htb.get("prodid"))) {
								continue;
							}
						}
					}
					//2012-09-25 end.
					//2014-01-14  展期终止共用同一个提交交易.同一机构对不同业务条线的展期终止用产品来区分
					else if(CommonConst.WF_TYPE_CONTAPPR.equals(flowType)){
						String proId = request.getField("prodid");
						//判断是否是绑定产品
						String flowbindstyle = htb.get("flowbindstyle").toString();
						
						if ("1".equals(flowbindstyle)) {
							//如果此产品ID和数据表中的产品ID不相同,则继续
							if (!proId.equals(htb.get("prodid"))) {
								continue;
							}
						}
					}
					//2014-01-14 end
					String flowtype = request.getField("flowtype");
					
					if(!"".equals(flowtype)){
						if(!flowtype.equals(htb.get("flowtype"))){
							continue;
						}
					}
					if ("1".equals(htb.get("iselecapprflag")))
						prop.put(htb.get("submtrancode"), htb.get("flowid"));
					flag = true;
					break;

		    } 
			else 
			{
				continue;
			}
			
		}
		//如果没有s_banklevel可能会死循环,超过5级自动退出,但正常情况下是所有报文中都应该有此字段的
		if (flag == false && lever < 5
				&& !CommonConst.BANK_LEVEL_FIRST.equals(request.getField("s_banklevel"))) {
			lever++;
			String superbankid = getsuperbankid(ctx, bankid);
			flag = superbankid.equals(bankid) ? true : false;
			iselecappr(table, ctx, prop, superbankid, flag);
		}
    }
    
    /*
     * 从交易中获取产品编号
    */
    private String getProdId(UnikMap request) throws Exception{
    	//第一次查找的时候是没有产品ID的
		String wfProdid = request.getString("wf_prodid");
		if(wfProdid == null){
			wfProdid = request.getString("prodid");
			if(wfProdid == null){
				// 找出产品编号放入上传报文对象中
				/*
				 * <sql name="getProdidFromDbapp"> 
				 *     select 
				 *     		prodid 
				 *     from
				 *          dbapp 
				 *     where 
				 *     		loanid = @ loanid 
				 * </sql>
				 */
				UnikMap params = new UnikMap();
				params.put("loanid", request.getString("loanid"));
				SingleResult sr = null;
				//对公和个贷调用不同的TPL   liuxj
                if(CommonConst.WF_TYPE_PLOANAPPLY.equals(request.getString("flowtype"))){  //对公流程
                  // sr = querySingle(getTpl("getProdidFromCorpDbapp"), params, null);
                    sr = querySingle(getTpl("getProdidFromdbAppApprScheme"), params, null);  // dbAppApprScheme
                }
                else 
                	if(CommonConst.WF_TYPE_LOANAPPLY.equals(request.getString("flowtype"))){  //个贷流程
                      
                		//sr = querySingle(getTpl("getProdidFromDbapp"), params, null);
                		 sr = querySingle(getTpl("getProdidFromdbAppApprScheme"), params, null);  // dbAppApprScheme
                    } 
				
                //在dbapp表中没有找到产品编号
				Errors.Assert(sr != null, app.formatMessage("workflow/NotFoundProdid", request.getString("loanid")));
                
				wfProdid = sr.getString("prodid");
			}
			
			request.put("wf_prodid", wfProdid);
		}
		return wfProdid;
    }
    
    
    /**
	 * 根据机构代码查询上级机构号
	 * 
	 * @param bankid
	 *            当前机构代码
	 * @return String 上级机构代码
	 * @throws java.lang.Exception
	 */
    public String getsuperbankid(AppContext ctx, String bankid) throws Exception {
    	UnikMap dp = new UnikMap();
		dp.put("bankid", bankid);
		SingleResult sr = querySingle("jar:cn.com.jbbis.jbportal.workflow.procedure.WorkflowStorageImpl_getAllCmbank",dp, null);

		Errors.Assert(sr != null, app.formatMessage("workflow/SuperbankidNotFound", bankid));

		return sr.getField("superbankid");
	}
    
    /**
	 * 读取cmBankElecScope表中的配置存储至Application,缓存中存放的是Properties,名字为iselecappr_config
	 * 主要是将提交交易存至应用缓存,这样减少频繁查询给系统带来的压力,如
	 * 果设置有变更,则应调用此方法更新缓存
	 * @throws java.lang.Exception
	 */
    public Properties setBankElecScope(AppRequest request) throws Exception{
    	/*
		 <sql name="getAllCmBankElecScope">
		 	select 
				@asis:SelectField 
			from 
				cmBankElecScope 
			where
				1=1
				#AND =@submtrancode
			group by submtrancode
		 </sql>
		 */

		DataList dlist = executeProcedure(getTpl("getSubmtrancodeFromCmBankElecScope"), request);

		Properties p = new Properties();
		
		while (dlist.next()) {
			String submtrancode = dlist.getString("submtrancode");
			p.put(submtrancode, submtrancode);
		}
		
		//app.setCache("iselecappr_config", p);
		obj=p;
		return p;
    }
    
    private String fmt(String s) {
		return " [" + s + "]";
	}
    
    /**
	 * 取TPL名字,返回的是jar:cn.com.jbbis.jbportal.workflow.ClassName_procname
	 * @param procname TPL名字
	 * @throws java.lang.Exception
	 */
	private String getTpl(String procname) throws Exception{
		StringBuffer className = new StringBuffer(this.getClass().getName());
		int index = className.indexOf("cn.com.jbbis.");
		if (index != -1){
			className.insert(className.lastIndexOf(".")+1, "procedure.");
			return "jar:" + className + "_" + procname;
		}else{
			return "jbbis" + procname;
		}
	}
}
