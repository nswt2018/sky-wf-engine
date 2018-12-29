package com.sky.workflow.engine;

import java.util.LinkedList;

import com.sky.workflow.util.UnikMap;

public interface WorkflowRoute {
	// return nodeid or nodeid-list
	@SuppressWarnings("rawtypes")
	public UnikMap getNext(LinkedList route, UnikMap wfVars) throws Exception;

	public String getNext(UnikMap wfVars) throws Exception;
}