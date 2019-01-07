package com.sky.workflow.engine;

import java.util.LinkedList;

import com.sky.workflow.util.UnikMap;

public interface WorkflowRoute {
	@SuppressWarnings("rawtypes")
	public UnikMap getNext(LinkedList route, UnikMap wfVars);
}