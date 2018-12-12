package cn.com.jbbis.jbportal.workflow;

import java.util.LinkedList;
import cn.com.jbbis.util.UnikMap;

public interface WorkflowRoute
{
	// return nodeid or nodeid-list
	public UnikMap getNext(LinkedList route,UnikMap wfVars) throws Exception;
	public String getNext(UnikMap wfVars) throws Exception;
}