/*
 * @(#)IParticipant.java
 * 
 * Beijing Beida Jade Bird Business Information System Co.,Ltd
 *
 * Copyright (c) 2009 JBBIS. All Rights Reserved.
 *
 * http://www.jbbis.com.cn
 */

package cn.com.jbbis.jbportal.workflow;

import cn.com.jbbis.afx.AppUser;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

/**
 * 工作流参与者接口.
 * <P>
 *
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-12 下午01:33:34
 * @since   JBPortal3.0
 */

public interface IParticipant {
	public DataList getUserId(String flowid, String wfInstId, AppUser user, UnikMap node) throws Exception;
}
