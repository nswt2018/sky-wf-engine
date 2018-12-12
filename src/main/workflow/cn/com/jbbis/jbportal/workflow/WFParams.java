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
     * 公共永久字段，即常用的字段以属性方式封装到WFParam对象中；
     * 封装的属性不需要在wf_para表中定义，业务系统调用时，告诉业务系统直接用WFParam方法赋值；
     * 这些公共字段需要保存到wf_paraval表中。
     * 其中flowid与piid在wf_paraval的列上体现，即每条记录都有，不需要再保存成二条记录了
     * nid,niid在一个流程实例中有多个，保存也无意义
     */	
	private String flowid;  	//流程编号
	private String wfid;	//流程实例编号
	private String nodeid;  	//节点编号
	private String niid;	//节点实例编号
	
	private String bankid;  //当前机构
	private String operid;  //当前用户
	private String opername;  //当前用户名称
	private String ownorg;  //拥有者机构
	private String owner;  //拥有人
	private String ownername;  //拥有人名称
	private String curdate;  //当前日期，用4位年-2位月-2位日表示		
	private String taskser;	//当前任务号
	private String dealopin; 	//审批意见
	private String otheropin; //审批意见说明
	private String instancylevel;  //紧急程度

	
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
	 * 公共临时字段，以属性封装到WFParam对象中; 
	 * 不需要在wf_para表中定义，调用时直接用方法赋值；
	 * 这些字段不需要保存到wf_paraval表中，况且由flowid可查到pkey
	 */
	//流程标识，用来查询最新版本的流程
	private String pkey; 
	/**
	 * 节点角色下的人员列表，有二种传值格式为：
	 * 1）分隔符形式："节点编号nid|角色编号postid|用户编号uid|用户名称username"
	 * 2）Map<节点编号，用户对象列表>
	 */
	private List<String> nodeRoleUserList ;
	//private Map<Integer,List<CMUser>> nodeRoleUserMap;

	/**
	 * 查询条件
	 */
	private Map<String,String> conditionMap;
    /** 
     * 非公共字段，每个流程所特有的，以Map形式封装到WFParam中。
     * 封装的哪些字段，需要定义在wf_para表中。
     * 并且最终都要保存到wf_paraval表中。 
     */
    private Map<String, Object> param;
    /**
     * 传给流程模板供业务使用的参数
     */
    private Map<String, Object> currentNode;	//当前节点
    //private Nodeinst currentNodeinst;	//当前节点实例
    private List<Map<String, Object>> currentTasks;		//当前节点任务集合
    private Map<String, Object> currentTask;	//当前任务
    private Map<Map<String, Object>,List<Map<String, Object>>> nextNodesInfo;	//下节点、节点实例及其相对应的任务信息
    private String conclusion;	//会签结论
    private String desc; //流程描述
    /**
     * 集体审议情况
     */
    //private DelibInfo delibInfo;
    //private DelibVote delibVote;	//集体审议表决
    /**
     * 指定分配策略节点
     */
    //private Node policyNode;
    /**
     * 事项列表标识
     */
    private String state;	//事项列表标识
    
    public WFParams() {
    	param = new HashMap<String, Object>();
    }
    
    /**
     * 得到当前所有要保存到数据库的流程参数
     * 包括公共永久字段9个，非公共字段n个
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
     * 当从数据库获取到所有的参数键值对后，还原到此对象中
     * 保存时所有的键值对均为字符型，取出来时可以转换成相应的类型
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
    	 * p中剩下的应该都是属于前台传来的参数，放在非公共部分para中
    	 * 放之前清空
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
