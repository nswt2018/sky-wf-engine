package cn.com.jbbis.jbportal.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WFParams implements java.io.Serializable {
	private static final long serialVersionUID = 9175049697027984188L;
	public static final String ENV_CURORG = "FLOW_GLOBAL_CURORG";
	public static final String ENV_CURUSER = "FLOW_GLOBAL_CURUSER";
	public static final String ENV_OWNORG = "FLOW_GLOBAL_OWNORG";
	public static final String ENV_OWNER = "FLOW_GLOBAL_OWNER";
	public static final String ENV_CURDATE = "FLOW_GLOBAL_CURDATE";
//	public static final String ENV_CURPROCESSID = "CURPROCESSID";
//	public static final String ENV_CURPROCESSINSTID = "CURPROCESSINSTID";
	public static final String ENV_CURNODEID = "FLOW_GLOBAL_CURNODEID";
	public static final String ENV_CURNODEINSTID = "FLOW_GLOBAL_CURNODEINSTID";
	public static final String ENV_CURTASKID = "FLOW_GLOBAL_CURTASKID";
	public static final String ENV_OPINION = "FLOW_GLOBAL_OPINION";
	public static final String ENV_OPINIONDESC = "FLOW_GLOBAL_OPINIONDESC";
	public static final String ENV_LEVEL = "FLOW_GLOBAL_LEVEL";
	
    /**
     * ���������ֶΣ������õ��ֶ������Է�ʽ��װ��WFParam�����У�
     * ��װ�����Բ���Ҫ��wf_para���ж��壬ҵ��ϵͳ����ʱ������ҵ��ϵͳֱ����WFParam������ֵ��
     * ��Щ�����ֶ���Ҫ���浽wf_paraval���С�
     * ����flowid��piid��wf_paraval���������֣���ÿ����¼���У�����Ҫ�ٱ���ɶ�����¼��
     * nid,niid��һ������ʵ�����ж��������Ҳ������
     */	
	private String flowid;  	//���̱��
	private String wfid;	//����ʵ�����
	private String nodeid;  	//�ڵ���
	private String niid;	//�ڵ�ʵ�����
	
	private String bankid;  //��ǰ����
	private String operid;  //��ǰ�û�
	private String opername;  //��ǰ�û�����
	private String ownorg;  //ӵ���߻���
	private String owner;  //ӵ����
	private String ownername;  //ӵ��������
	private String curdate;  //��ǰ���ڣ���4λ��-2λ��-2λ�ձ�ʾ		
	private String taskser;	//��ǰ�����
	private String dealopin; 	//�������
	private String otheropin; //�������˵��
	private String instancylevel;  //�����̶�

	
	public String getOwnorg() {
		return ownorg;
	}
	public void setOwnorg(String ownorg) {
		this.ownorg = ownorg;
	}
	public String getFlowid() {
		return flowid;
	}
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}
	public String getWfid() {
		return wfid;
	}
	public void setWfid(String wfid) {
		this.wfid = wfid;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getNiid() {
		return niid;
	}
	public void setNiid(String niid) {
		this.niid = niid;
	}
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public String getOperid() {
		return operid;
	}
	public void setOperid(String operid) {
		this.operid = operid;
	}
	public String getOpername() {
		return opername;
	}
	public void setOpername(String opername) {
		this.opername = opername;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOwnername() {
		return ownername;
	}
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	public String getCurdate() {
		return curdate;
	}
	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}
	public String getTaskser() {
		return taskser;
	}
	public void setTaskser(String taskser) {
		this.taskser = taskser;
	}
	public String getDealopin() {
		return dealopin;
	}
	public void setDealopin(String dealopin) {
		this.dealopin = dealopin;
	}
	public String getOtheropin() {
		return otheropin;
	}
	public void setOtheropin(String otheropin) {
		this.otheropin = otheropin;
	}
	public String getInstancylevel() {
		return instancylevel;
	}
	public void setInstancylevel(String instancylevel) {
		this.instancylevel = instancylevel;
	}

	/**
	 * ������ʱ�ֶΣ������Է�װ��WFParam������; 
	 * ����Ҫ��wf_para���ж��壬����ʱֱ���÷�����ֵ��
	 * ��Щ�ֶβ���Ҫ���浽wf_paraval���У�������flowid�ɲ鵽pkey
	 */
	//���̱�ʶ��������ѯ���°汾������
	private String pkey; 
	/**
	 * �ڵ��ɫ�µ���Ա�б��ж��ִ�ֵ��ʽΪ��
	 * 1���ָ�����ʽ��"�ڵ���nid|��ɫ���postid|�û����uid|�û�����username"
	 * 2��Map<�ڵ��ţ��û������б�>
	 */
	private List<String> nodeRoleUserList ;
	//private Map<Integer,List<CMUser>> nodeRoleUserMap;

	/**
	 * ��ѯ����
	 */
	private Map<String,String> conditionMap;
    /** 
     * �ǹ����ֶΣ�ÿ�����������еģ���Map��ʽ��װ��WFParam�С�
     * ��װ����Щ�ֶΣ���Ҫ������wf_para���С�
     * �������ն�Ҫ���浽wf_paraval���С� 
     */
    private Map<String, Object> param;
    /**
     * ��������ģ�幩ҵ��ʹ�õĲ���
     */
    private Map<String, Object> currentNode;	//��ǰ�ڵ�
    //private Nodeinst currentNodeinst;	//��ǰ�ڵ�ʵ��
    private List<Map<String, Object>> currentTasks;		//��ǰ�ڵ����񼯺�
    private Map<String, Object> currentTask;	//��ǰ����
    private Map<Map<String, Object>,List<Map<String, Object>>> nextNodesInfo;	//�½ڵ㡢�ڵ�ʵ���������Ӧ��������Ϣ
    private String conclusion;	//��ǩ����
    private String desc; //��������
    /**
     * �����������
     */
    //private DelibInfo delibInfo;
    //private DelibVote delibVote;	//����������
    /**
     * ָ��������Խڵ�
     */
    //private Node policyNode;
    /**
     * �����б��ʶ
     */
    private String state;	//�����б��ʶ
    
    public WFParams() {
    	param = new HashMap<String, Object>();
    }
    
    /**
     * �õ���ǰ����Ҫ���浽���ݿ�����̲���
     * �������������ֶ�9�����ǹ����ֶ�n��
     * @return
     */
    private String[] pubParas = {ENV_CURORG, ENV_CURUSER, ENV_OWNORG, 
    		ENV_OWNER, ENV_CURDATE, ENV_CURTASKID, 
    		ENV_OPINION, ENV_OPINIONDESC, ENV_LEVEL};
    public Map<String,Object> getAllParams() {
    	Map<String, Object> allParam = new HashMap<String, Object>();
    	allParam.putAll(param);
    	allParam.put(ENV_CURORG, this.getBankid());
    	allParam.put(ENV_CURUSER, this.getOperid());
    	allParam.put(ENV_OWNORG, this.getOwnorg());
    	allParam.put(ENV_OWNER, this.getOwner());
    	allParam.put(ENV_CURDATE, this.getCurdate());    	
//    	allParam.put(ENV_CURNODEID, this.getNid());
//    	allParam.put(ENV_CURNODEINSTID, this.getNiid());
    	allParam.put(ENV_CURTASKID, this.getFlowid());
    	allParam.put(ENV_OPINION, this.getDealopin());
    	allParam.put(ENV_OPINIONDESC, this.getOtheropin());
    	allParam.put(ENV_LEVEL, this.getInstancylevel());
    	
    	return allParam;
    }
    /**
     * �������ݿ��ȡ�����еĲ�����ֵ�Ժ󣬻�ԭ���˶�����
     * ����ʱ���еļ�ֵ�Ծ�Ϊ�ַ��ͣ�ȡ����ʱ����ת������Ӧ������
     * @param allParam
     */
	public void setAllParams(Map<String,Object> p) {
    	this.setBankid(String.valueOf(p.get(ENV_CURORG)));
    	this.setOperid(String.valueOf(p.get(ENV_CURUSER)));
    	this.setOwnorg(String.valueOf(p.get(ENV_OWNORG)));
    	this.setOwner(String.valueOf(p.get(ENV_OWNER)));
    	this.setCurdate(String.valueOf(p.get(ENV_CURDATE)));
    	
    	Object _tmp;
    	if((_tmp = p.get(ENV_CURTASKID))==null) _tmp="0";
    	this.setFlowid(String.valueOf(_tmp));
    	this.setDealopin(String.valueOf(p.get(ENV_OPINION)));
    	this.setOtheropin(String.valueOf(p.get(ENV_OPINIONDESC)));
    	this.setInstancylevel(String.valueOf(p.get(ENV_LEVEL)));

    	/**
    	 * p��ʣ�µ�Ӧ�ö�������ǰ̨�����Ĳ��������ڷǹ�������para��
    	 * ��֮ǰ���
    	 */
    	param.clear();
    	Set<String> set = p.keySet();
    	boolean isSkip ;
    	for(String key : set) {
    		isSkip = false;
    		for(String var : pubParas) {
    			if(key.trim().equals(var)) {
    				isSkip=true; break;
    			}
    		}
    		if(!isSkip) param.put(key, p.get(key));
    	}
    }
	public String getPkey() {
		return pkey;
	}
	public void setPkey(String pkey) {
		this.pkey = pkey;
	}
	public List<String> getNodeRoleUserList() {
		return nodeRoleUserList;
	}
	public void setNodeRoleUserList(List<String> nodeRoleUserList) {
		this.nodeRoleUserList = nodeRoleUserList;
	}
	public Map<String, String> getConditionMap() {
		return conditionMap;
	}
	public void setConditionMap(Map<String, String> conditionMap) {
		this.conditionMap = conditionMap;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public Map<String, Object> getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(Map<String, Object> currentNode) {
		this.currentNode = currentNode;
	}
	public List<Map<String, Object>> getCurrentTasks() {
		return currentTasks;
	}
	public void setCurrentTasks(List<Map<String, Object>> currentTasks) {
		this.currentTasks = currentTasks;
	}
	public Map<String, Object> getCurrentTask() {
		return currentTask;
	}
	public void setCurrentTask(Map<String, Object> currentTask) {
		this.currentTask = currentTask;
	}
	public Map<Map<String, Object>, List<Map<String, Object>>> getNextNodesInfo() {
		return nextNodesInfo;
	}
	public void setNextNodesInfo(Map<Map<String, Object>, List<Map<String, Object>>> nextNodesInfo) {
		this.nextNodesInfo = nextNodesInfo;
	}
	public String getConclusion() {
		return conclusion;
	}
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String[] getPubParas() {
		return pubParas;
	}
	public void setPubParas(String[] pubParas) {
		this.pubParas = pubParas;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/*public String getCurorg() {
		return curorg;
	}

	public void setCurorg(String curorg) {
		this.curorg = curorg;
	}

	public String getCuruser() {
		return curuser;
	}

	public void setCuruser(String curuser) {
		this.curuser = curuser;
	}
	
	public String getCurusername() {
		return curusername;
	}

	public void setCurusername(String curusername) {
		this.curusername = curusername;
	}
	
	public String getOwnorg() {
		return ownorg;
	}

	public void setOwnorg(String ownorg) {
		this.ownorg = ownorg;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCurdate() {
		return curdate;
	}

	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}

	public Integer getFlowid() {
		return flowid;
	}

	public void setFlowid(Integer flowid) {
		this.flowid = flowid;
	}

	public Integer getPiid() {
		return piid;
	}

	public void setPiid(Integer piid) {
		this.piid = piid;
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getNiid() {
		return niid;
	}

	public void setNiid(Integer niid) {
		this.niid = niid;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getOpiniondesc() {
		return opiniondesc;
	}

	public void setOpiniondesc(String opiniondesc) {
		this.opiniondesc = opiniondesc;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String flowlevel) {
		this.level = flowlevel;
	}
	
	public void setParam(Map<String, Object> flowParam) {
		this.param = flowParam;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	
	public void setConditionMap(Map<String, String> conditionMap) {
		this.conditionMap = conditionMap;
	}
	public Map<String, String> getConditionMap() {
		return conditionMap;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public List<String> getNodeRoleUserList() {
		return nodeRoleUserList;
	}

	public void setNodeRoleUserList(List<String> nodeRoleUserList) {
		this.nodeRoleUserList = nodeRoleUserList;
	}

	public Map<Map<String, Object>,List<Map<String, Object>>> getNextNodesInfo() {
		return nextNodesInfo;
	}

	public void setNextNodesInfo(Map<Map<String, Object>,List<Map<String, Object>>> nextNodesInfo) {
		this.nextNodesInfo = nextNodesInfo;
	}
	public Map<String, Object> getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Map<String, Object> currentNode) {
		this.currentNode = currentNode;
	}

	public Map<String, Object> getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Map<String, Object> currentTask) {
		this.currentTask = currentTask;
	}
	public List<Map<String, Object>> getCurrentTasks() {
		return currentTasks;
	}

	public void setCurrentTasks(List<Map<String, Object>> currentTasks) {
		this.currentTasks = currentTasks;
	}
	public String getConclusion() {
		return conclusion;
	}

	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	} 
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}*/
	
	
}
