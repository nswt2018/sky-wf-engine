package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.UnikMap;
/**
* Title: �û�������Ϣ
* Description: �û�������Ϣ�޸�
* Copyright: Copyright (c) 2009
* Company: �������ű�������Ƽ��ɷݹ�˾
* @author Fangys@jbbis.com.cn
* @version 1.0
* Service S103U
*/
public class ServiceOperatorUpdate extends BaseService
{
	protected AppResponse process() throws Exception
	{	
		String userState = request.getField("userstate");
		if (userState.equals("9")){
			request.setField("userLoginState", "0");
		}else{
			request.setField("userLoginState", "1");
		}
		
		//modified by liuwt ������̽�ɫ�����У�顣
		/*
		 ServiceOperatorUpdate_GetMutex.tpl
		 
			 select
				mutexpostid  
			from
				dwapprpost
			where
				postid = @postid
			
		*/
		/*String[] postIdSet = request.getField("postidset").split(",");//��ȡ��ǰ���̽�ɫ����
		boolean flag = false;//�Ƿ���ڻ����ɫ��ʶ��falseΪ�޻����ɫ��trueΪ���ڻ����ɫ��
		for(int i=0; i<postIdSet.length; i++){
			UnikMap um = new UnikMap();
			um.put("postid", postIdSet[i]);
			SingleResult sr = querySingle(getTpl("GetMutex"), um);//����ȡ����ǰ�����ɫpostIdSet[i]��Ӧ�Ļ����ɫ
			if(sr==null){
				continue;
			}
			String[] mutexPostIdSet = {};
			 //����ǰ�����ɫpostIdSet[i]���ڻ����ɫmutexPostIdSet[]��������Ƚϻ����ɫ���Ƿ������ǰ�����ɫ�еĽ�ɫ��
			if(sr.getString("mutexpostid")!=null){
				mutexPostIdSet = sr.getString("mutexpostid").split(",");
				for(int j=0; j<mutexPostIdSet.length; j++){
					for(int k=0; k<postIdSet.length; k++){
						if(mutexPostIdSet[j].equals(postIdSet[k])){
							flag = true;
						}
					}
				}
			}
		}
		Assert(flag==false, "���ڻ����ɫ����ȷ�Ϻ��ٽ��в���");*/
		performAction(UPDATE, "cmuser"); 
		return SUCCESS();
	}	
}