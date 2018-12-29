package com.sky.workflow.engine;
/**
 * <p>Title: 工作流变量</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: 北京北大青鸟商用信息系统有限公司 </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.2 平台迁移 , 2008-5-22 上午11:12:21
 */
public class CommonConst {
	/**
	 * 工作流选人界面
	 */
	public static final String WF_SEL_WIN = "WF031F";
	/**
	 * 工作流程状态"取回"
	 */
	public static final String WF_RETAKE = "取回";
	/**
	 * 工作流程状态"退回"
	 */
	public static final String WF_UNTREAD = "退回";
	/**
	 * 流程节点类型
	 * 开始 value="1"
	 */
	public static final String WF_NODETYPE_START = "1";
	/**
	 * 流程节点类型
	 * 中间 value="2"
	 */
	public static final String WF_NODETYPE_MID = "2";
	/**
	 * 流程节点类型
	 * 结束 value="3"
	 */
	public static final String WF_NODETYPE_END = "3";
	/**
	 * 流程节点类型
	 * 分发 value="4"
	 */
	public static final String WF_NODETYPE_OUT = "4";
	/**
	 * 流程节点类型
	 * 合并 value="5"
	 */
	public static final String WF_NODETYPE_UNITE = "5";
	/**
	 * 流程节点类型子流程、
	 * 子流程 value="6"
	 */
	public static final String WF_NODETYPE_SUBWORKFLOW = "6";
	/**
	 * 流程业务种类
	 * 贷款发放-签合同value="6"
	 */
	public static final String WF_TYPE_LOANPASS = "6";
	/**
	 * 流程业务种类
	 * 合同流程value="7"
	 */
	public static final String WF_TYPE_CONTAPPR = "7";
	/**
	 * 流程业务种类
	 * 额度申请value="2"
	 */
	public static final String WF_TYPE_CREDAPPLY = "2";
	
	/**
	 * 流程业务种类
	 * 贷款申请value="1"
	 */
	public static final String WF_TYPE_LOANAPPLY = "1";
	
	/**
	 * 流程业务种类
	 * 对公贷款申请value="8"
	 */
	public static final String WF_TYPE_PLOANAPPLY = "8";
	
	/**
	 * 流程业务种类
	 * 贷后管理 value="40"
	 */
	public static final String WF_TYPE_LOANAFTER = "40";
	/**
	 * 下一节点设置方式
	 * 条件判断value="1"
	 */
	public static final String WF_NODEROUTE_DYNAMIC = "1";
	/**
	 * 流程节点处理方式
	 * 随机分配 value="0"
	 */
	public static final String WF_PROCEMODE_RANDOM = "0";
	/**
	 * 流程节点处理方式
	 * 工作量最少 value="1"
	 */
	public static final String WF_PROCEMODE_WORKLEAST = "1";
	/**
	 * 流程节点任务分配策略
	 * 手工分配 value="0"
	 */
	public static final String WF_TASKASSIGN_MANUAL = "0";
	/**
	 * 流程节点任务分配策略
	 * 自动分配 value="1"
	 */
	public static final String WF_TASKASSIGN_AUTO = "1";
	/**
	 * 流程节点任务分配策略
	 * 分配所有人员 value="2"
	 */
	public static final String WF_TASKASSIGN_ALL = "2";
	
	/**
	 * 流程节点任务分配策略
	 * 流程外分配人员 value="3"
	 */
	public static final String WF_TASKASSIGN_WFOUT = "3";
	
	/**
	 * 流程节点任务完成策略
	 * 按完成数量 value="0"
	 */
	public static final String WF_TASKCOMP_AMT = "0";
	
	/**
	 * 流程节点任务完成策略
	 * 按完成百分比 value="1"
	 */
	public static final String WF_TASKCOMP_PER = "1";
	
	/**
	 * 流程节点任务完成策略
	 * 任意完成一个 value="2"
	 */
	public static final String WF_TASKCOMP_AUTO = "2";
	
	/**
	 * 流程节点任务完成策略
	 * 必须所有都完成 value="3"
	 */
	public static final String WF_TASKCOMP_ALL = "3";
	
	/**
	 * 日期和时间类型
	 * 年月日 value="yyyyMMdd"
	 */
	public static final String DATE_TYPE_DATE = "yyyyMMdd";
	/**
	 * 日期和时间类型,小时1-23,0:00:00返回00:00:00而不是24:00:00
	 * 年月日时分秒 value="yyyyMMddHHmmss"
	 */
	public static final String DATE_TYPE_TIMESTAMP = "yyyyMMddHHmmss";
	/**
	 * 日期和时间类型,小时1-23,0:00:00返回00:00:00而不是24:00:00
	 * 年月日时分秒毫秒 value="yyyyMMddHHmmssSSS"
	 */
	public static final String DATE_TYPE_TIMESTAMP_MSEL = "yyyyMMddHHmmssSSS";
	/**
	 * 通用是否标志
	 * 0-否,1-是
	 */
	public static final String WF_ISYESORNO_NO = "0";
	/**
	 * 通用是否标志
	 * 0-否,1-是
	 */
	public static final String WF_ISYESORNO_YES = "1";
	/**
	 * 流程岗位机构信息
	 * 本级机构@
	 */
	public static final String WF_POSTID_LOCAL = "@";
	/**
	 * 流程岗位机构信息
	 * 上级审批机构$
	 */
	public static final String WF_POSTID_SUPERAPPR = "$";
	/**
	 * 流程岗位机构信息
	 * 上级管理机构$
	 */
	public static final String WF_POSTID_SUPERMGR = "#";
	/**
	 * 流程岗位机构信息
	 * 流程发起机构-
	 */
	public static final String WF_POSTID_WFINIT = "-";
	/**
	 * 流程岗位机构信息
	 * 指定机构 value='%'
	 */
	public static final String WF_POSTID_ASSIGN = "%";
	/**
	 * 流程岗位机构信息
	 * 流程发起机构及上级审批机构^
	 */
	public static final String WF_POSTID_WFINIT_SUPERAPPR = "^";
	/**
	 * 流程岗位机构信息
	 * 流程发起人 value='*'
	 */
	public static final String WF_POSTID_WFINITUSER = "*";
	
	/**
	 * 流程状态信息
	 * 初始化 value="1"
	 */
	public static final String WF_STATE_INIT = "1";
	/**
	 * 流程状态信息
	 * 发布 value="2"
	 */
	public static final String WF_STATE_START = "2";
	/**
	 * 流程状态信息
	 * 暂停使用 value="8"
	 */
	public static final String WF_STATE_PAUSE = "8";
	/**
	 * 流程状态信息
	 * 禁用 value="9"
	 */
	public static final String WF_STATE_STOP = "9";
	/**
	 * 用户状态
	 * 在岗 value='1'
	 */
	public static final String WF_USERLOGON_IN = "1";
	/**
	 * 用户状态
	 * 离岗 value='0'
	 */
	public static final String WF_USERLOGON_OUT = "0";
	/**
	 * 是否进行人员历史查询,即从dwtaskhis中查询已经处理过的人员id
	 * 是 value='1'
	 */
	public static final String WF_HISUSER_ON = "1";
	/**
	 * 是否进行人员历史查询,即从dwtaskhis中查询已经处理过的人员id
	 * 否 value='0'
	 */
	public static final String WF_HISUSER_OFF = "0";
	
	/**
	 * 任务类型
	 * 处理任务
	 */
	public static final int WF_TASKTYPE_DEAL = 2;
	
	/**
	 * 任务类型
	 * 浏览任务
	 */
	public static final int WF_TASKTYPE_VIEW = 1;
	

	/**
	 * 获取上级机构范围
	 * 1：直接上级机构
	 */
	public static final String WF_SUPERBANKSCOPE_ONE = "1";
	
	/**
	 * 获取上级机构范围
	 * 2：全部上级机构
	 */
	public static final String WF_SUPERBANKSCOPE_ALL = "2";
	
	/**
	 * 新加simon 20170906
	 */
	public static final String BANK_LEVEL_FIRST="1";
	
	/**
	 * 业务处理方式
	 * 1：交易中提交（对公）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_G = "1";
	/**
	 * 业务处理方式
	 * 2：列表中提交（对私）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_S = "2";
	/**
	 * 业务处理方式
	 * 4：列表中提交（小企业）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_P = "4";
	/**
	 * 业务处理方式
	 * 5：列表中提交（个贷）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_N = "5";
	/**
	 * 业务处理方式
	 * 6：列表中提交（登记产品）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_F = "6";
	
	/**
	 * 业务处理方式
	 * 3：列表中提交（其他：与产品没有关系的）  
	 */	
	public static final String WF_BUSIOPERATESTYLE_O = "3";
	
	/**
	 * 业务类型
	 * 10  授信业务审批流程  
	 */	
	public static final String WF_BUSITYPE_SX = "10";
	public static final String WF_BUSITYPE_ZQ = "11";
	/**
	 * 业务类型
	 * 20  合同审查流程
	 */	
	public static final String WF_BUSITYPE_HS = "20";
	
	/**
	 * 业务类型
	 * 21  合同解冻流程
	 */	
	public static final String WF_BUSITYPE_HJ = "21";
	public static final String WF_BUSITYPE_HZ = "22";
	public static final String WF_BUSITYPE_HD = "23";
	/**
	 * 业务类型
	 * 30  放款审查流程
	 */	
	public static final String WF_BUSITYPE_FS = "30";
	/**
	 * 业务类型
	 * 40  贷后检查流程
	 */	
	public static final String WF_BUSITYPE_DC = "40";

	public static final String WF_BUSITYPE_DH = "41"; //还款变更
	public static final String WF_BUSITYPE_DL = "42"; //利率变更
	public static final String WF_BUSITYPE_DD = "43"; //担保变更
	
	public static final String WF_BUSITYPE_DP = "44"; //贷后检查评价
	public static final String WF_BUSITYPE_DFY = "45"; //重大风险预警
	public static final String WF_BUSITYPE_DFL = "46"; //风险分类
	public static final String WF_BUSITYPE_DJC = "47"; //单项键值测试
	
	/**
	 * 业务类型
	 * 50  资产保全流程
	 */	
	public static final String WF_BUSITYPE_BQ = "50";
	public static final String WF_BUSITYPE_BFC = "51"; //法律措施流程
	public static final String WF_BUSITYPE_BDZ = "52";
	

	/**
	 * 业务类型
	 * 60 评级授信
	 */
	public static final String WF_BUSITYPE_PJSX = "60";
	public static final String WF_BUSITYPE_SYPJ = "61"; //信用评级
	public static final String WF_BUSITYPE_ZHSX = "62"; //综合授信
	public static final String WF_BUSITYPE_HEZSX = "63"; //合作方授信

	
	/**
	 * 是否终审节点
	 * 1  是
	 */	
	public static final String WF_ISPRIMARYAUDITNODE_YES = "1";
	/**
	 * 是否终审节点
	 * 2 否
	 */	
	public static final String WF_ISPRIMARYAUDITNODE_NO = "2";	
	
}
