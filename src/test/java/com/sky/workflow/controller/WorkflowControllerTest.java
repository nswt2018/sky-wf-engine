package com.sky.workflow.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sky.workflow.boot.WorkflowApplication;
import com.sky.workflow.controller.WorkflowController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorkflowApplication.class)
@WebAppConfiguration
public class WorkflowControllerTest {
	
	private MockMvc mvc;
    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new WorkflowController()).build();
    }
    
    @Test
    public void getHello() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/workflow/wf0001s?flowid=0000000001").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

	/*public static void main(String[] args) throws Exception {
		
		String wfDefId = "0000000001";
		String user = "3400008888/000001";
		UnikMap bizData = new UnikMap();
		String[] users = {};
		String[] viewUsers = {};
		UnikMap umWorkflow = new UnikMap();
		UnikMap outTaskNode = new UnikMap();
		Queue<UnikMap> tranQueue = null;
		
		WorkflowStorageImpl wsi = new WorkflowStorageImpl();
		Workflow wf = new Workflow(wsi);
		wf.start(wfDefId, user, bizData, users, viewUsers, umWorkflow, outTaskNode, tranQueue);
	}*/
}
