package com.nswt.workflow.service;

// JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>
 * Title:�û�������Ϣ
 * </p>
 * <p>
 * Description:�����û�������Ϣ
 * </p>
 * <p>
 * Copyright: Copyright(c) 2008
 * </p>
 * <p>
 * Compny: ������������������Ϣϵͳ���޹�˾
 * </p>
 * Service S103I
 * 
 * @author Li Xiaochao
 * @version 1.0
 * @Date 2008-2-25 ����10:08:26
 */

public class ServiceOperatorInsert extends BaseService {
	/** �Ƿ�Ҫ������Ȩ�� */
	public boolean isBankAware() {
		return false;
	}

	protected AppResponse process() throws Exception {

		/**
		 * ҵ������� A-����������+��Ա�š����������ݿ����Ѿ����ڵļ�¼��ͬ�� E- F-
		 */
		// �޸ļ�¼20080513 xuecheng������������û�����Ψһ������Ϊ
		// �û�����ΪȫϵͳΨһ����
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
		// ����������ϵͳ����Ա��ɫ��Ȩ�޲����ڳ����н��п���
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

		// modified by liuwt ������̽�ɫ�����У�顣
		/*
		 * ServiceOperatorUpdate_GetMutex.tpl select mutexpostid from dwapprpost
		 * where postid = @postid
		 */

		/*if (!"".equals(request.getField("postidset"))) {
			String[] postIdSet = request.getField("postidset").split(",");// ��ȡ��ǰ���̽�ɫ����
			boolean flag = false;// �Ƿ���ڻ����ɫ��ʶ��falseΪ�޻����ɫ��trueΪ���ڻ����ɫ��
			for (int i = 0; i < postIdSet.length; i++) {
				UnikMap um = new UnikMap();
				um.put("postid", postIdSet[i]);
				SingleResult sr = querySingle(
						"jbbis.common.sysman.ServiceOperatorUpdate_GetMutex",
						um);// ����ȡ����ǰ�����ɫpostIdSet[i]��Ӧ�Ļ����ɫ
				String[] mutexPostIdSet = {};
				// ����ǰ�����ɫpostIdSet[i]���ڻ����ɫmutexPostIdSet[]��������Ƚϻ����ɫ���Ƿ������ǰ�����ɫ�еĽ�ɫ��
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
		
		
		performAction(INSERT, "cmuser"); // �����ݿ�����Ӽ�¼
		return SUCCESS();
	}
}