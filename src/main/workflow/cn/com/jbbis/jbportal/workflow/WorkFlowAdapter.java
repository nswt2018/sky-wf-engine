package cn.com.jbbis.jbportal.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.common.PubConstant;
import cn.com.jbbis.util.UnikMap;

/**
 * 
 * @ClassName: WorkFlowAdapter
 * @Description: 业务流程模板,该模板实现了流程引擎模板,并在此基础上提取了一些公共变量与方法,减少重复编码
 * @author rOOkiE
 * @date 2012-7-24 下午9:06:06
 * 
 */
public abstract class WorkFlowAdapter implements WorkFlowTemplate, Serializable {

	
	/**
	 * 业务流程初始化方法,对业务处理中经常用到的参数赋值
	 */
	public void bizInit(WFParams params) {

		if (params.getParam().containsKey(params.getPkey() + "_pk")) {
			pk = bizserno = (String) params.getParam().get(
					params.getPkey() + "_pk");
		} else {
			pk = bizserno = (String) params.getParam().get(
					params.getPkey() + "_bizserno");
		}
		busitype = (String) params.getParam().get(
				params.getPkey() + "_busitype");
		currentTime = (String) params.getParam().get(
				params.getPkey() + "_lastchgtime");
		bankid = params.getBankid();
		owner = params.getOwner();
		ownorg = params.getOwnorg();
		operid = params.getOperid();
		opername = params.getOpername();
		workdate = params.getCurdate();
//		service = BootstrapService.getInstance();
//		service = BaseService.getService();
		currentTask = params.getCurrentTask();
		result = currentTask != null ? (String)currentTask.get("result") : null;
		nextNodePhase = this.getNextNodePhase(params);
		currentNode = params.getCurrentNode();
		phase = currentNode != null ? (String)currentNode.get("phase") : null;
		
		//初始化的时候,将流程参数全部放入到para集中
		para.putAll(convertWorkFlowParam2Map(params));
		//end
		para.put("bizserno", bizserno);
		pkey = params.getPkey();
		desc = params.getDesc();
		//modefy by rOOkiE at 20130115 用于表外业务 
		prodid = (String) params.getParam().get(pkey+"_prodid");
		para.put("prodid", prodid);
		
		//modify by linjianqing 2013-1-29
		para.put("operuserid", operid);
		para.put("operbankid", bankid);
		para.put("opertime", workdate); 
		
	}
	
	/**
	 * 
	 * @Title: convertWorkFlowParam2Map
	 * @Description: 将流程参数去掉流程标识 ,转换成通用Map类型
	 * @param @param params
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
	public static Map<String, String> convertWorkFlowParam2Map(WFParams params) {

		Map<String, String> paramMap = new HashMap<String, String>();

		Map<String, Object> param = params.getParam();// 流程参数

		String pkey = params.getPkey();// 流程标识

		int len = pkey.length();

		Iterator<Entry<String, Object>> iterator = param.entrySet().iterator();

		while (iterator.hasNext()) {

			Entry<String, Object> map = iterator.next();
			String oldKey = map.getKey();
			// modefied by hfb at 20130701 添加空判断
			Object value = map.getValue();
			String val = (value == null ? "" : String.valueOf(value));
			if (oldKey.indexOf(pkey) > -1) {
				String newKey = oldKey.substring(len + 1);
				paramMap.put(newKey, val);
			} else {
				paramMap.put(oldKey, val);
			}

		}

		return paramMap;
	}

	@Override
	public void finish(WFParams params) {

		this.bizInit(params);// 初始化业务参数
		this.executeAtFinish(params);
	}

	@Override
	public void initialize(WFParams params) {
		this.bizInit(params);// 初始化业务参数
		try {
			this.setApprmode(params);// 设置审批模式参数值
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtInitialize(params);
	}

	@Override
	public void postApprove(WFParams params) {
		this.bizInit(params);// 初始化业务参数

		this.executeAtPostApprove(params);
		try {
			String apprResult = null;
			if (result != null) {
				if ("同意".equals(result.trim())) {
					apprResult = PubConstant.APPRRESULT_10;
				}
			} else {
				apprResult = "";// 由于合同审批的第一岗没有审批意见 ,所以直接把cp_log的审批审批意见字段置 为""
			}

			this.updateCPLog(apprResult);// 更新CP_LOG
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	public void postStarter(WFParams params) {
		this.bizInit(params);// 初始化业务参数
		this.executeAtPostStarter(params);
		try {
			String apprResult = null;
			if (result != null) {
				if ("同意".equals(result.trim())) {
					apprResult = PubConstant.APPRRESULT_10;
				}
			} else {
				apprResult = "";
			}

			this.updateCPLog(apprResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void preApprove(WFParams params) {
		this.bizInit(params);// 初始化业务参数
		try {
			this.setApprmode(params);// 设置审批模式参数值
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtPreApprove(params);
	}

	@Override
	public void preStarter(WFParams params) {
		this.bizInit(params);// 初始化业务参数
		try {
			this.setApprmode(params);// 设置审批模式参数值
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtPreStarter(params);
	}

	/**
	 * 
	 * @Title: getNextNodePhase
	 * @Description: 获取下一节点业务阶段
	 * @param @param params
	 * @param @return nextNodePhase
	 * @return String 返回类型
	 * @throws
	 */
	private String getNextNodePhase(WFParams params) {

		String nextNodePhase = null;

		Map<Map<String, Object>,List<Map<String, Object>>> nodeInfo = params
				.getNextNodesInfo();

		if (nodeInfo != null && nodeInfo.size() > 0) {
			Iterator<Entry<Map<String,Object>, List<Map<String, Object>>>> it = nodeInfo
					.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Map<String,Object>, List<Map<String, Object>>> entry = it.next();
				Map<String,Object> node = entry.getKey();
				nextNodePhase = (String)node.get("phase");// 下一节点所处业务阶段
				break;
			}
		}

		return nextNodePhase;
	}

	/**
	 * @Title: updateCPLog
	 * @Description: 更新业务流水表(CP_LOG)
	 * @param @设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateCPLog(String apprResult) {

//		UnikMap uMap = new UnikMap();
//		uMap.putAll(service.getRequest());
//		uMap.putAll(this.para);
//		uMap.put("bizserno", bizserno);
//		uMap.put("phase", nextNodePhase);
//		uMap.put("ApprResult", apprResult);
//
//		uMap.put("BusiDesc", desc);
//
//		service.executeProcedure(PubConstant.CP2609U, uMap);// 更新信贷业务流水（CP_LOG）
	}

	/**
	 * 
	 * @Title: getApprmode
	 * @Description: 根据节点的所属业务阶段获取审批模式
	 * @param @param params
	 * @param @return
	 * @param @设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String getApprmode(WFParams params) {

		String apprmode = "";
		/****************** step 1:查询流程参数 中 是否含有审批模式这个参数 ********************/
		Map<String, Object> paraMap = params.getParam();// 取得所有的流程参数
		String pkey = params.getPkey();// 流程标识
		if (paraMap.containsKey(pkey + "_apprmode")) {// 流程参数表中已定义了该参数

			apprmode = (String) paraMap.get(pkey + "_apprmode");
		}

		return apprmode;

	}

	/**
	 * 
	 * @Title: setApprmode
	 * @Description: 
	 *               通过节点所属业务阶段对审批模式进行赋值,并交由流程引擎进行保存,注意:这个方法只能放在流程初始化方法,或者流程提交前处理中才有用
	 *               ;
	 * @param @param params
	 * @param @设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setApprmode(WFParams params) {

		String apprmode = "";
		/****************** step 1:查询流程参数 中 是否含有审批模式这个参数 ********************/
		Map<String, Object> paraMap = params.getParam();// 取得所有的流程参数
		String pkey = params.getPkey();// 流程标识

		// 流程参数表中已定义了该参数
		if (PubConstant.PHASE_A22.equals(phase)) {
			apprmode = PubConstant.APPRMODE_A1;
		} else if (PubConstant.PHASE_A23.equals(phase)) {
			apprmode = PubConstant.APPRMODE_A2;
		} else if (PubConstant.PHASE_A13.equals(phase)) {
			apprmode = PubConstant.APPRMODE_A3;
		} else if (PubConstant.PHASE_A28.equals(phase)) {
			apprmode = PubConstant.APPRMODE_A4;
		} else if (PubConstant.PHASE_A27.equals(phase)) {
			apprmode = PubConstant.APPRMODE_A5;
		} else if (PubConstant.PHASE_A52.equals(phase)) {
			apprmode = PubConstant.APPRMODE_B1;
		} else if (PubConstant.PHASE_A53.equals(phase)) {
			apprmode = PubConstant.APPRMODE_B2;
		} else if (PubConstant.PHASE_A54.equals(phase)) {
			apprmode = PubConstant.APPRMODE_B3;
		} else if (PubConstant.PHASE_A58.equals(phase)) {
			apprmode = PubConstant.APPRMODE_B4;
		} else if (PubConstant.PHASE_A57.equals(phase)) {
			apprmode = PubConstant.APPRMODE_B5;
		} else {

			if (paraMap.containsKey(pkey + "_apprmode")) {
				apprmode = (String) paraMap.get(pkey + "_apprmode");
			}

		}

		/*********************** step 2 :将apprmode的值保存到流程参数对象 中,由流程引擎存到表中 ********************************/
		if (!"".equals(apprmode)) {
			params.getParam().put(pkey + "_apprmode", apprmode);
		}

	}

	/**
	 * 
	 * @Title: getTemplate
	 * @Description: 获取远程模板对象
	 * @param @param service
	 * @param @param templateName
	 * @param @param systemid
	 * @param @return
	 * @param @设定文件
	 * @return WorkFlowAdapter 返回类型
	 * @throws
	 */
//	public WorkFlowAdapter getTemplate(BaseService service, String templateName,
//			String systemid) {
//		this.service = service;
//		Application application = service.getApplication();
//		Config config = application.getConfig("RemoteTemplateUrl");
//		String url = (String) config.get(systemid);
//		HessianProxyFactory factory = new HessianProxyFactory();
//		WorkFlowAdapter adapter;
//		try{
//			RemoteCallWorkflowTemplateService templateService = (RemoteCallWorkflowTemplateService) factory.create(RemoteCallWorkflowTemplateService.class, url);
//			adapter = (WorkFlowAdapter) templateService.getRemoteObject(templateName);
//		}catch(Exception e) {
//			throw new RuntimeException(e);
//		}
//		return adapter;
//
//	}

	/**
	 * @Title: executeAtFinish
	 * @Description: 流程结束后执行的方法,表示该笔流程已完全结束
	 * @param @param params
	 * @param @设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtFinish(WFParams params) {

	}

	/**
	 * @Title: executeAtInitialize
	 * @Description: 流程初始化方法,只在流程启动的时候执行
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtInitialize(WFParams params) {

	}

	/**
	 * @Title: executeAtPostApprove
	 * @Description: 流程中提交后处理,不包含第一个节点
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPostApprove(WFParams params) {

	}

	/**
	 * @Title: executeAtPostStarter
	 * @Description: 流程第一个节点提交后处理,该 方法只针对第一个节点
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPostStarter(WFParams params) {

	}

	/**
	 * @Title: executeAtPreApprove
	 * @Description: 流程中提交前处理,不包含第一个节点
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPreApprove(WFParams params) {

	}

	/**
	 * @Title: executeAtPreStarter
	 * @Description: 流程第一个节点 提交前处理,只针对流程中的第一个节点
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPreStarter(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtPreReplevy
	 * @Description: 流程捞回前操作
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPreReplevy(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtPostReplevy
	 * @Description: 流程捞回后操作
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtPostReplevy(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtFinishReplevy
	 * @Description: 流程发起节点进行捞回操作,流程捞回结束操作
	 * @param @param params
	 * @param @throws RuntimeException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void executeAtFinishReplevy(WFParams params)
			throws RuntimeException {

	}

	protected SingleResult resultMerge(SingleResult src, SingleResult target) {

		if (src != null && target != null) {
			Iterator it = src.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) it.next();
				String key = entry.getKey();
				String val = entry.getValue();
				if (val != null && !"".equals(val)) {
					target.put(key, val);
				}

			}
		}

		return target;

	}
	

	private static final long serialVersionUID = 1L;

	/**
	 * 业务流水号
	 */
	protected String bizserno;

	/**
	 * 操作人编号
	 */
	protected String operid;

	/**
	 * 操作人名称
	 */
	protected String opername;

	/**
	 * 操作人机构编号
	 */

	protected String bankid;

	/**
	 * 业务拥有人
	 */
	protected String owner;

	/**
	 * 业务拥有人机构
	 */
	protected String ownorg;

	/**
	 * 操作时间
	 */
	protected String workdate;

	/**
	 * service
	 */
	protected BaseService service;

	/**
	 * 当前任务对象
	 */
	protected Map<String,Object> currentTask;

	/**
	 * 审批意见
	 */
	protected String result;

	/**
	 * 下一节点所处业务阶段
	 */
	protected String nextNodePhase;

	/**
	 * 业务处理参数键值对
	 */
	protected UnikMap para = new UnikMap();

	/**
	 * 当前节点信息
	 */
	protected Map<String,Object> currentNode;

	/**
	 * 当前节点所处业务阶段
	 */
	protected String phase;

	protected String currentTime;

	protected String pkey;

	/**
	 * 业务主键
	 */
	protected String pk;

	/**
	 * 流程描述模板
	 */
	protected String desc;
	/**
	 * 业务类型
	 */
	protected String busitype;

	protected String prodid;
}
