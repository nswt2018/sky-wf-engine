package com.sky.workflow.engine;

import javax.servlet.http.HttpSession;

import com.sky.core.util.SpringUtil;

public class AppUser {

	public HttpSession getSession() {
		return SpringUtil.getSession();
	}

}
