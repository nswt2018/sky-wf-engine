package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.common.util.ModifyLog;
import cn.com.jbbis.sql.DataContext;

public class ServiceRetPositionUpdate extends BaseService{

	protected AppResponse process() throws Exception { 	
        
       /* if("true".equalsIgnoreCase(context.getString("writelog"))){
         	DataContext exectx = new DataContext();	
			exectx.set(SELECT_FOR_UPDATE);
			exectx.set(RETURN_PK);
			SingleResult sr1 = performAction(UPDATE, "dbAppPeaLoanServInfo",request,exectx);
			
			//¼ÇÂ¼ÈÕÖ¾
			ModifyLog modify = new ModifyLog(this,context);
			modify.WriteModifyLog(sr1, exectx, ModifyLog.LOG_KIND_UPDATE);
        } else {
        	performAction(UPDATE, "dbapp");
        }  */      
        performAction(UPDATE, "dwopinion",request);
        return SUCCESS();
	}
}
