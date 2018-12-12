package com.nswt.workflow.service;

// JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>
 * Title:用户基本信息
 * </p>
 * <p>
 * Description:新增用户基本信息
 * </p>
 * <p>
 * Copyright: Copyright(c) 2008
 * </p>
 * <p>
 * Compny: 北京北大青鸟商用信息系统有限公司
 * </p>
 * Service S103I
 * 
 * @author Li Xiaochao
 * @version 1.0
 * @Date 2008-2-25 上午10:08:26
 */

public class ServiceOperatorInsert extends BaseService {
	/** 是否要检查机构权限 */
	public boolean isBankAware() {
		return false;
	}

	protected AppResponse process() throws Exception {

		/**
		 * 业务处理过程 A-“机构代码+柜员号“不能与数据库中已经存在的记录相同； E- F-
		 */
		// 修改记录20080513 xuecheng将机构代码加用户代码唯一条件改为
		// 用户代码为全系统唯一条件
		/*
		 * <sql name="getCount"> select count(*) as userIdNo from cmUser where
		 * userid = @userid </sql>
		 */
		// canceled by wangj : 2011-08-04 19:58:28
		// SingleResult srNo = querySingle(getTpl("getCount"));
		// int num = srNo.getInt("userIdNo");
		// Assert(num == 0, "common/sysman/UserInfoAlreadyExist");

		String strPwd = new String("000000");
		strPwd = digestPassword(strPwd);
		// 对新增总行系统管理员角色的权限不再在程序中进行控制
		// String roleidset=request.getField("roleidset");
		// String targetBankId = request.getField("bankid");
		// String strBankId=user.getCache("bankid");
		// request.setField("strbankid", strBankId);
		// String strSuperBankId=request.getField("superbankid");
		/*
		 * <sql name="getSuperBankid"> select superbankid from cmbank where
		 * bankid=@strbankid </sql>
		 */
		// SingleResult sr1 = querySingle(getTpl("getSuperBankid"));
		// String strSuperBankId = sr1.getField("superbankid");

		/*
		 * <sql name="getBankid"> select bankid from cmbank where banklevel='1'
		 * </sql>
		 */

		// SingleResult sr2 = querySingle(getTpl("getBankid"));
		// String strZBankid = sr2.getField("bankid");

		// StringTokenizer st=new StringTokenizer(roleidset,",");
		/*
		 * while(st.hasMoreTokens()){ String roleid=st.nextToken();
		 * if("Z".equalsIgnoreCase
		 * (roleid)&&!targetBankId.equalsIgnoreCase(strZBankid)){
		 * Assert(false,"common/sysman/MustBeLvlTop"); }
		 * if("Z".equalsIgnoreCase(
		 * roleid)&&!strBankId.equalsIgnoreCase(strSuperBankId)){
		 * Assert(false,"common/sysman/branchcantinsert"); } }
		 */
		request.setField("password", strPwd);

		// modified by liuwt 添加流程角色互斥的校验。
		/*
		 * ServiceOperatorUpdate_GetMutex.tpl select mutexpostid from dwapprpost
		 * where postid = @postid
		 */

		/*if (!"".equals(request.getField("postidset"))) {
			String[] postIdSet = request.getField("postidset").split(",");// 获取当前流程角色数组
			boolean flag = false;// 是否存在互斥角色标识。false为无互斥角色，true为存在互斥角色。
			for (int i = 0; i < postIdSet.length; i++) {
				UnikMap um = new UnikMap();
				um.put("postid", postIdSet[i]);
				SingleResult sr = querySingle(
						"jbbis.common.sysman.ServiceOperatorUpdate_GetMutex",
						um);// 遍历取出当前分配角色postIdSet[i]对应的互斥角色
				String[] mutexPostIdSet = {};
				// 若当前分配角色postIdSet[i]存在互斥角色mutexPostIdSet[]，则遍历比较互斥角色中是否包含当前分配角色中的角色。
				if (sr.getString("mutexpostid") != null) {
					mutexPostIdSet = sr.getString("mutexpostid").split(",");
					for (int j = 0; j < mutexPostIdSet.length; j++) {
						for (int k = 0; k < postIdSet.length; k++) {
							if (mutexPostIdSet[j].equals(postIdSet[k])) {
								flag = true;
								break;
							}
						}
					}
				}
			}
			Assert(flag == false, "loan/common/sysman/ExistMutexPost");
		}
		
		*/
		
		//=====add by zhjl 2012-02-29
		String userid = request.getString("userid");
		request.setField("userid", userid);
		//=====end by zhjl
		
		
		performAction(INSERT, "cmuser"); // 向数据库中添加记录
		return SUCCESS();
	}
}