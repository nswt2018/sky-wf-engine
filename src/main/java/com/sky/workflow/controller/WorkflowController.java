package com.sky.workflow.controller;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.core.message.Message;
import com.sky.core.util.SpringUtil;
import com.sky.workflow.engine.Workflow;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.service.IDwFlowMainTableService;
import com.sky.workflow.util.Errors;
import com.sky.workflow.util.NamedException;
import com.sky.workflow.util.UnikMap;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/workflow")
public class WorkflowController {

	@Resource(name = "DwFlowMainTableService")
	private IDwFlowMainTableService dwFlowMainTableService;
	
	@Resource(name = "Workflow")
	private Workflow Workflow;

	@RequestMapping(value = "/wf0001s")
	@CrossOrigin
	public Mono<DwFlowMainTable> getDwFlowMainById(@RequestBody DwFlowMainTable dwFlowMainVo) {
		DwFlowMainTable dfmt = dwFlowMainTableService.getById(dwFlowMainVo.getFlowid());
		return Mono.justOrEmpty(dfmt);
	}

	@GetMapping("/start") 
	public Mono<Message> start() throws Exception {
		String wfDefId = "0000000001";
		String user = "3400008888/000001";
		UnikMap bizData = new UnikMap();
		String[] users = {"3400008888/000002"};
		String[] viewUsers = {};
		UnikMap umWorkflow = new UnikMap();
		UnikMap outTaskNode = new UnikMap();
		Queue<UnikMap> tranQueue = new ArrayDeque<UnikMap>();
		
		SpringUtil.getSession();
		
		Workflow.start(wfDefId, user, bizData, users, viewUsers, umWorkflow, outTaskNode, tranQueue);
		return Mono.justOrEmpty(new Message("000000"));
	}
	
	@GetMapping("/resume") 
	public Mono<Message> resume(String wfid, String cuser, String nuser) throws Exception {
		
		String user = "3400008888/"+cuser;
		UnikMap bizData = new UnikMap();
		String[] users = {"3400008888/"+nuser};
		String[] viewUsers = {};
		boolean blStart = false;
		boolean isDirectForeNode = false;
		UnikMap outTaskNode = new UnikMap();
		Queue<UnikMap> tranQueue = new ArrayDeque<UnikMap>();;
		
		Workflow.resume(wfid, user, bizData, users, viewUsers, blStart, isDirectForeNode, outTaskNode, tranQueue);
		return Mono.justOrEmpty(new Message("000000"));
	}
	
	@GetMapping("/trans") 
	@Transactional
	public Mono<Message> TransTest(String flowid) throws NamedException {
		DwFlowMainTable  dwFlowMainVo = new DwFlowMainTable();
		dwFlowMainVo.setFlowid("aaa");
		dwFlowMainTableService.save(dwFlowMainVo);
		if(flowid==null) Errors.Assert(false, "flowid==null");
		dwFlowMainVo.setFlowid("bbb");
		dwFlowMainTableService.save(dwFlowMainVo);
		return Mono.justOrEmpty(new Message("000000"));
	}

}
