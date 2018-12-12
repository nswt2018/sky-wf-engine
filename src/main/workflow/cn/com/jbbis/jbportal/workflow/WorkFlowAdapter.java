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
 * @Description: ҵ������ģ��,��ģ��ʵ������������ģ��,���ڴ˻�������ȡ��һЩ���������뷽��,�����ظ�����
 * @author rOOkiE
 * @date 2012-7-24 ����9:06:06
 * 
 */
public abstract class WorkFlowAdapter implements WorkFlowTemplate, Serializable {

	
	/**
	 * ҵ�����̳�ʼ������,��ҵ�����о����õ��Ĳ�����ֵ
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
		
		//��ʼ����ʱ��,�����̲���ȫ�����뵽para����
		para.putAll(convertWorkFlowParam2Map(params));
		//end
		para.put("bizserno", bizserno);
		pkey = params.getPkey();
		desc = params.getDesc();
		//modefy by rOOkiE at 20130115 ���ڱ���ҵ�� 
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
	 * @Description: �����̲���ȥ�����̱�ʶ ,ת����ͨ��Map����
	 * @param @param params
	 * @param @return �趨�ļ�
	 * @return Map<String,String> ��������
	 * @throws
	 */
	public static Map<String, String> convertWorkFlowParam2Map(WFParams params) {

		Map<String, String> paramMap = new HashMap<String, String>();

		Map<String, Object> param = params.getParam();// ���̲���

		String pkey = params.getPkey();// ���̱�ʶ

		int len = pkey.length();

		Iterator<Entry<String, Object>> iterator = param.entrySet().iterator();

		while (iterator.hasNext()) {

			Entry<String, Object> map = iterator.next();
			String oldKey = map.getKey();
			// modefied by hfb at 20130701 ��ӿ��ж�
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

		this.bizInit(params);// ��ʼ��ҵ�����
		this.executeAtFinish(params);
	}

	@Override
	public void initialize(WFParams params) {
		this.bizInit(params);// ��ʼ��ҵ�����
		try {
			this.setApprmode(params);// ��������ģʽ����ֵ
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtInitialize(params);
	}

	@Override
	public void postApprove(WFParams params) {
		this.bizInit(params);// ��ʼ��ҵ�����

		this.executeAtPostApprove(params);
		try {
			String apprResult = null;
			if (result != null) {
				if ("ͬ��".equals(result.trim())) {
					apprResult = PubConstant.APPRRESULT_10;
				}
			} else {
				apprResult = "";// ���ں�ͬ�����ĵ�һ��û��������� ,����ֱ�Ӱ�cp_log��������������ֶ��� Ϊ""
			}

			this.updateCPLog(apprResult);// ����CP_LOG
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	public void postStarter(WFParams params) {
		this.bizInit(params);// ��ʼ��ҵ�����
		this.executeAtPostStarter(params);
		try {
			String apprResult = null;
			if (result != null) {
				if ("ͬ��".equals(result.trim())) {
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
		this.bizInit(params);// ��ʼ��ҵ�����
		try {
			this.setApprmode(params);// ��������ģʽ����ֵ
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtPreApprove(params);
	}

	@Override
	public void preStarter(WFParams params) {
		this.bizInit(params);// ��ʼ��ҵ�����
		try {
			this.setApprmode(params);// ��������ģʽ����ֵ
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.executeAtPreStarter(params);
	}

	/**
	 * 
	 * @Title: getNextNodePhase
	 * @Description: ��ȡ��һ�ڵ�ҵ��׶�
	 * @param @param params
	 * @param @return nextNodePhase
	 * @return String ��������
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
				nextNodePhase = (String)node.get("phase");// ��һ�ڵ�����ҵ��׶�
				break;
			}
		}

		return nextNodePhase;
	}

	/**
	 * @Title: updateCPLog
	 * @Description: ����ҵ����ˮ��(CP_LOG)
	 * @param @�趨�ļ�
	 * @return void ��������
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
//		service.executeProcedure(PubConstant.CP2609U, uMap);// �����Ŵ�ҵ����ˮ��CP_LOG��
	}

	/**
	 * 
	 * @Title: getApprmode
	 * @Description: ���ݽڵ������ҵ��׶λ�ȡ����ģʽ
	 * @param @param params
	 * @param @return
	 * @param @�趨�ļ�
	 * @return String ��������
	 * @throws
	 */
	public String getApprmode(WFParams params) {

		String apprmode = "";
		/****************** step 1:��ѯ���̲��� �� �Ƿ�������ģʽ������� ********************/
		Map<String, Object> paraMap = params.getParam();// ȡ�����е����̲���
		String pkey = params.getPkey();// ���̱�ʶ
		if (paraMap.containsKey(pkey + "_apprmode")) {// ���̲��������Ѷ����˸ò���

			apprmode = (String) paraMap.get(pkey + "_apprmode");
		}

		return apprmode;

	}

	/**
	 * 
	 * @Title: setApprmode
	 * @Description: 
	 *               ͨ���ڵ�����ҵ��׶ζ�����ģʽ���и�ֵ,����������������б���,ע��:�������ֻ�ܷ������̳�ʼ������,���������ύǰ�����в�����
	 *               ;
	 * @param @param params
	 * @param @�趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void setApprmode(WFParams params) {

		String apprmode = "";
		/****************** step 1:��ѯ���̲��� �� �Ƿ�������ģʽ������� ********************/
		Map<String, Object> paraMap = params.getParam();// ȡ�����е����̲���
		String pkey = params.getPkey();// ���̱�ʶ

		// ���̲��������Ѷ����˸ò���
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

		/*********************** step 2 :��apprmode��ֵ���浽���̲������� ��,����������浽���� ********************************/
		if (!"".equals(apprmode)) {
			params.getParam().put(pkey + "_apprmode", apprmode);
		}

	}

	/**
	 * 
	 * @Title: getTemplate
	 * @Description: ��ȡԶ��ģ�����
	 * @param @param service
	 * @param @param templateName
	 * @param @param systemid
	 * @param @return
	 * @param @�趨�ļ�
	 * @return WorkFlowAdapter ��������
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
	 * @Description: ���̽�����ִ�еķ���,��ʾ�ñ���������ȫ����
	 * @param @param params
	 * @param @�趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtFinish(WFParams params) {

	}

	/**
	 * @Title: executeAtInitialize
	 * @Description: ���̳�ʼ������,ֻ������������ʱ��ִ��
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtInitialize(WFParams params) {

	}

	/**
	 * @Title: executeAtPostApprove
	 * @Description: �������ύ����,��������һ���ڵ�
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPostApprove(WFParams params) {

	}

	/**
	 * @Title: executeAtPostStarter
	 * @Description: ���̵�һ���ڵ��ύ����,�� ����ֻ��Ե�һ���ڵ�
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPostStarter(WFParams params) {

	}

	/**
	 * @Title: executeAtPreApprove
	 * @Description: �������ύǰ����,��������һ���ڵ�
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPreApprove(WFParams params) {

	}

	/**
	 * @Title: executeAtPreStarter
	 * @Description: ���̵�һ���ڵ� �ύǰ����,ֻ��������еĵ�һ���ڵ�
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPreStarter(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtPreReplevy
	 * @Description: �����̻�ǰ����
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPreReplevy(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtPostReplevy
	 * @Description: �����̻غ����
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void executeAtPostReplevy(WFParams params) {

	}

	/**
	 * 
	 * @Title: executeAtFinishReplevy
	 * @Description: ���̷���ڵ�����̻ز���,�����̻ؽ�������
	 * @param @param params
	 * @param @throws RuntimeException �趨�ļ�
	 * @return void ��������
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
	 * ҵ����ˮ��
	 */
	protected String bizserno;

	/**
	 * �����˱��
	 */
	protected String operid;

	/**
	 * ����������
	 */
	protected String opername;

	/**
	 * �����˻������
	 */

	protected String bankid;

	/**
	 * ҵ��ӵ����
	 */
	protected String owner;

	/**
	 * ҵ��ӵ���˻���
	 */
	protected String ownorg;

	/**
	 * ����ʱ��
	 */
	protected String workdate;

	/**
	 * service
	 */
	protected BaseService service;

	/**
	 * ��ǰ�������
	 */
	protected Map<String,Object> currentTask;

	/**
	 * �������
	 */
	protected String result;

	/**
	 * ��һ�ڵ�����ҵ��׶�
	 */
	protected String nextNodePhase;

	/**
	 * ҵ���������ֵ��
	 */
	protected UnikMap para = new UnikMap();

	/**
	 * ��ǰ�ڵ���Ϣ
	 */
	protected Map<String,Object> currentNode;

	/**
	 * ��ǰ�ڵ�����ҵ��׶�
	 */
	protected String phase;

	protected String currentTime;

	protected String pkey;

	/**
	 * ҵ������
	 */
	protected String pk;

	/**
	 * ��������ģ��
	 */
	protected String desc;
	/**
	 * ҵ������
	 */
	protected String busitype;

	protected String prodid;
}
