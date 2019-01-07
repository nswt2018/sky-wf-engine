package com.sky.workflow.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sky.core.base.controller.BaseController;
import com.sky.core.exception.BusinessException;
import com.sky.core.message.Message;
import com.sky.core.page.Page;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.service.IDwFlowMainTableService;

import reactor.core.publisher.Mono;

@RestController
public class DwFlowMainTableController extends BaseController{
	@Resource(name = "DwFlowMainTableService")
	private IDwFlowMainTableService DwFlowMainTableService;
	
	@RequestMapping(value = "/Lcdy01s801L.do")
	@ResponseBody
	public Mono<Page<DwFlowMainTable>> getDwFlowMainTablePageList(@RequestBody Page<DwFlowMainTable> page, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		page.setRequest(request);
		DwFlowMainTableService.findForPageList("com.sky.app.mapper.DwFlowMainTableMapper.findForPageList", page);
		page.setRequest(null);
		return Mono.justOrEmpty(page);
	}

	@PutMapping(value = "/Lcdy01s801I.do")
	@ResponseBody
	public Mono<Message> insertDwFlowMainTable(@RequestBody DwFlowMainTable Workflow01, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			if (Workflow01!= null) {
				int insertrows = DwFlowMainTableService.save(Workflow01);
				if (insertrows > 0) {
					return Mono.justOrEmpty(new Message("000001"));
				} else {
					return Mono.justOrEmpty(new Message("000004"));
				}
			} else {
				return Mono.justOrEmpty(new Message("000004"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("-000000", e.getMessage());
		}
	}
	
	@PutMapping(value = "/Lcdy01s801U.do")
	@ResponseBody
	public Mono<Message> updateDwFlowMainTable(@RequestBody DwFlowMainTable Workflow01, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			if (Workflow01 != null) {
				int updaterows = DwFlowMainTableService.update(Workflow01);
				if (updaterows > 0) {
					return Mono.justOrEmpty(new Message("000003"));
				} else {
					return Mono.justOrEmpty(new Message("000006"));
				}
			} else {
				return Mono.justOrEmpty(new Message("000006"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("-000000", e.getMessage());
		}
	}
	
	@DeleteMapping("/Lcdy01s801D.do")
	@ResponseBody
	public Mono<Message> deleteDwFlowMainTable(@RequestParam String[] delKeys, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (delKeys == null || delKeys.length == 0)
				throw new BusinessException("000005");
			for (String delKey : delKeys) {
				DwFlowMainTableService.delete(delKey);
			}
			return Mono.justOrEmpty(new Message("000002"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("-000000", e.getMessage());
		}
	}

}
