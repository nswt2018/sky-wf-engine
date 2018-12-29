package com.sky.workflow.controller;

import java.util.Queue;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sky.core.message.Message;
import com.sky.workflow.engine.Workflow;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.service.IDwFlowMainTableService;
import com.sky.workflow.util.UnikMap;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/workflow")
public class WorkflowController {

	@Resource(name = "DwFlowMainTableService")
	private IDwFlowMainTableService DwFlowMainTableService;
	
	@Resource(name = "Workflow")
	private Workflow Workflow;

	@RequestMapping(value = "/wf0001s")
	public Mono<DwFlowMainTable> getDwFlowMainById(@RequestParam String flowid) {
		DwFlowMainTable dfmt = DwFlowMainTableService.getById(flowid);
		return Mono.justOrEmpty(dfmt);
	}
	
	/**
	 * 
	 * @return
	 */
	@GetMapping("/start") 
	public String start() {
		return "workflow/start";
	}
	
	@GetMapping("/resume") 
	public String resume() {
		return "workflow/resume";
	}
	
	@GetMapping("/main") 
	public Mono<Message> main() throws Exception {
		String wfDefId = "0000000001";
		String user = "3400008888/000001";
		UnikMap bizData = new UnikMap();
		String[] users = {"3400008888/000002"};
		String[] viewUsers = {};
		UnikMap umWorkflow = new UnikMap();
		UnikMap outTaskNode = new UnikMap();
		Queue<UnikMap> tranQueue = null;
		
		Workflow.start(wfDefId, user, bizData, users, viewUsers, umWorkflow, outTaskNode, tranQueue);
		return Mono.justOrEmpty(new Message("000000"));
	}

}
