package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.UnikMap;
/**
* Title: 用户基本信息
* Description: 用户基本信息修改
* Copyright: Copyright (c) 2009
* Company: 北京天桥北大青鸟科技股份公司
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
		
		//modified by liuwt 添加流程角色互斥的校验。
		/*
		 ServiceOperatorUpdate_GetMutex.tpl
		 
			 select
				mutexpostid  
			from
				dwapprpost
			where
				postid = @postid
			
		*/
		/*String[] postIdSet = request.getField("postidset").split(",");//获取当前流程角色数组
		boolean flag = false;//是否存在互斥角色标识。false为无互斥角色，true为存在互斥角色。
		for(int i=0; i<postIdSet.length; i++){
			UnikMap um = new UnikMap();
			um.put("postid", postIdSet[i]);
			SingleResult sr = querySingle(getTpl("GetMutex"), um);//遍历取出当前分配角色postIdSet[i]对应的互斥角色
			if(sr==null){
				continue;
			}
			String[] mutexPostIdSet = {};
			 //若当前分配角色postIdSet[i]存在互斥角色mutexPostIdSet[]，则遍历比较互斥角色中是否包含当前分配角色中的角色。
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
		Assert(flag==false, "存在互斥角色，请确认后再进行操作");*/
		performAction(UPDATE, "cmuser"); 
		return SUCCESS();
	}	
}