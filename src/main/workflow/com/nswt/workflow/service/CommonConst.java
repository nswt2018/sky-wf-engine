package com.nswt.workflow.service;

/**
 * <p> * Title: 公共常量定义类 * </p>
 * <p> * Description: 常量定义 * </p>
 * <p> * Copyright: Copyright (c) 2003 * </p>
 * <p> * Company: 北京天桥北大青鸟科技股份公司 * </p>
 * * @author Liu Shuwei
 * @version 1.0
 */

public class CommonConst {
	// 异步处理程序生成的文件的根目录。
	public static final String PATH_OUT_ROOT = "outputpath";

	public static final String PATH_OUT_ASYNC = "async";

	// 导出文件的根目录。
	public static final String PATH_OUT_DATA = "data";

	public static final String PATH_OUT_CMIS = "cmis";
	
	public static final String PATH_OUT_EDOC = "edoc";
	/*
	 * 存放信息发布文件
	 */
	public static final String PATH_OUT_MSG = "msg";
	
	/*
	 * 存放档案图像
	 */
	public static final String PATH_OUT_ARCH = "arch";
	
	/**
	 * 工作流附件存放目录
	 */
	public static final String PATH_OUT_WF = "workflow";
	
	public static final String PATH_OUT_PROD = "prod";

	public static final String PATH_OUT_PUB = "pub";

	public static final String PATH_OUT_JOIN = "join";

	public static final String PATH_OUT_LOG = "log";

	// SQL配置文件的根目录。
	// public static final String PATH_IN_SQL = "eval";
	// 目录分隔符
	public static final String DIR_SEPARATOR = "/";

	// 异步处理状态
	public static final int ASYNC_STATE_WAIT = 0;

	public static final int ASYNC_STATE_WORK = 1;

	public static final int ASYNC_STATE_READY = 2;

	public static final int ASYNC_STATE_ERROR = 3;

	//
	public static final String RUNCFG_TRANFILTER = "tranfilter";

	public static final String RUNCFG_BANK = "bank";

	public static final String RUNCFG_MENU = "menu";

	public static final String RUNCFG_TRAN = "tran";

	//产品代码长度差
	public static final int COMMON_PRODID_STEP = 1;
	
	//交易动作类型
	/**
	 * 交易动作类型:
	 * list         L="查询"
	 */
	public static final String DEAL_TYPE_LIST = "list";
	
	/**
	 * 交易动作类型:
	 * insert       I="新增"
	 */ 
	public static final String DEAL_TYPE_INSERT = "insert";
	
	/**
	 * 交易动作类型:
	 * update       U="修改"
	 */
	public static final String DEAL_TYPE_UPDATE = "update";
	
	/**
	 * 交易动作类型:
	 * delete       D="删除"
	 */
	public static final String DEAL_TYPE_DELETE = "delete";
	
	/**
	 * 交易动作类型:
	 * select       S="浏览"
	 */ 
	public static final String DEAL_TYPE_SELETE = "selete";
	
	/**
	 * 交易动作类型:
	 * tally        C1="记账"
	 */ 
	public static final String DEAL_TYPE_TALLY = "tally";
	
	/**
	 * 交易动作类型:
	 * sanction     C2="复核(生效)"
	 */ 
	public static final String DEAL_TYPE_SANCTION = "sanction";
	
	/**
	 * 交易动作类型:
	 * refer        F="提交"
	 */ 
	public static final String DEAL_TYPE_REFER = "refer";
	
	/**
	 * 交易动作类型:
	 * withdrawal   B="退回"
	 */
	public static final String DEAL_TYPE_WITHDRAWAL = "withdrawal";
	
	/**
	 * 交易动作类型:
	 * quitacc      C3="冲账(撤销)"
	 */ 
	public static final String DEAL_TYPE_QUITACC = "quitacc";
	
	/**
	 * 交易动作类型:
	 * disfrock     C4="撤销"
	 */
	public static final String DEAL_TYPE_DISFROCK = "disfrock";
	
	/**
	 * 交易动作类型:
	 * undisfrock   C5="单边冲账"
	 */ 
	public static final String DEAL_TYPE_UNDISFROCK = "undisfrock";
	
	/**
	 * 交易动作类型:
	 * auditing     A="核准/审核/审批"
	 */ 
	public static final String DEAL_TYPE_AUDITING = "auditing";
	
	/**
	 * 交易动作类型:
	 * treesee      L9="树形浏览"
	 */
	public static final String DEAL_TYPE_TREESEE = "treesee";
	
	/**
	 * 交易动作类型:
	 * treewin      L2="树形界面"
	 */ 
	public static final String DEAL_TYPE_TREEEIN = "treewin";
	
	/**
	 * 交易动作类型:
	 * mainlog      S9="维护记录"
	 */
	public static final String DEAL_TYPE_MAINLOG = "mainlog";
	
	/**
	 * 审批类型:
	 * APPR_TYPE_MANU 1-手工审批类型
	 */
	public static final String DEAL_TYPE_EVALUATION = "evaluation";
	
	/**
	 * 动作交易类型:
	 * DEAL_TYPE_EVALUATION  评价
	 */
	public static final String APPR_TYPE_MANU = "1";//手工审批类型
	/**
	 * 审批类型:
	 * APPR_TYPE_FLOW 2-电子审批类型
	 */
	public static final String APPR_TYPE_FLOW = "2";//电子审批类型
	
	/**
	 * 会计科目代码:
	 * SUBJ_ID_LOAN_NORM_S 12201-短期贷款
	 */
	public static final String SUBJ_ID_LOAN_NORM_S = "12201";	//短期贷款
	/**
	 * 会计科目代码:
	 * SUBJ_ID_LOAN_NORM_L 12202-中长期贷款
	 */
	public static final String SUBJ_ID_LOAN_NORM_L = "12202";	//中长期贷款
	/**
	 * 会计科目代码:
	 * SUBJ_ID_LOAN_OVER_S 12211-短期违约
	 */
	public static final String SUBJ_ID_LOAN_OVER_S = "12211";//短期违约
	/**
	 * 会计科目代码:
	 * SUBJ_ID_LOAN_OVER_L 12212-中长期违约
	 */
	public static final String SUBJ_ID_LOAN_OVER_L = "12212";	//中长期违约
	/**
	 * 单据状态:
	 * LISTSTAT_TYPE_INIT	0-申请中
	 */
	public static final String LISTSTAT_TYPE_INIT = "0";	//申请中
	/**
	 * 单据状态:
	 * LISTSTAT_TYPE_CHARGE_UP	1-未记账
	 */	
	public static final String LISTSTAT_TYPE_CHARGE_UP = "1";	//未记账
	/**
	 * 单据状态:
	 * LISTSTAT_TYPE_CHECK 	3-已记账
	 */	
	public static final String LISTSTAT_TYPE_CHECK = "3";	//已记账
	/*
	 * <option value="0" text="申请中"/>
		<option value="1" text="未记账"/>
		<option value="2" text="已抹账"/>
		<option value="3" text="已记账"/>
		<option value="4" text="已作废"/>
		<option value="5" text="抹账待复核"/>
	 */
	/**
	 * 单据状态:
	 * LISTSTAT_TYPE_STRIKE	5-抹账待复核
	 */	
	public static final String LISTSTAT_TYPE_FCHECK = "5";	//抹账待复核
	
	
	/**
	 * 单据状态:
	 * LISTSTAT_TYPE_STRIKE	2-抹账已复核
	 */	
	public static final String LISTSTAT_TYPE_STRIKE = "2";	//已抹账
	
	/**
	 * 担保类型:
	 * REGIKIND_TYPE_PLEDGE	1-抵押
	 */
	public static final String REGIKIND_TYPE_PLEDGE = "1";	//抵押
	/**
	 * 担保类型:
	 * REGIKIND_TYPE_IMPAWN	2-质押
	 */	
	public static final String REGIKIND_TYPE_IMPAWN = "2";	//质押
	/**
	 * 担保类型:
	 * REGIKIND_TYPE_PLEDGE	3-抵押加保险
	 */
	public static final String REGIKIND_TYPE_INSURANCE = "3";	//抵押加保险
	/**
	 * 担保类型:
	 * REGIKIND_TYPE_ARTIFICIAL	4-法人保证
	 */	
	public static final String REGIKIND_TYPE_ARTIFICIAL = "4";	//法人保证
	/**
	 * 担保类型:
	 * REGIKIND_TYPE_PERSON	5-自然人保证
	 */	
	public static final String REGIKIND_TYPE_PERSON = "5";	//自然人保证
	
	/**
	 * 担保方式:
	 * RISKASSUKIND_TYPE_CREDIT	1-信用
	 */
	public static final String RISKASSUKIND_TYPE_CREDIT = "1";	//信用
	/**
	 * 担保方式:
	 * RISKASSUKINDD_TYPE_ASSURE 2-保证
	 */	
	public static final String RISKASSUKIND_TYPE_ASSURE = "2";	//保证
	/**
	 * 担保方式:
	 * RISKASSUKIND_TYPE_PLEDGE	3-抵押
	 */	
	public static final String RISKASSUKIND_TYPE_PLEDGE = "3";	//抵押
	
	/**
	 * 担保方式:
	 * RISKASSUKIND_TYPE_IMPAWN	4-质押
	 */	
	public static final String RISKASSUKIND_TYPE_IMPAWN = "4";	//质押
	
	/**
	 * 担保方式:
	 * RISKASSUKIND_TYPE_IMPAWN	5-抵押加保险
	 */	
	public static final String RISKASSUKIND_TYPE_INSURANCE = "5";	//抵押加保险

	/**
	 * 审批意见:
	 * NOT_APPLIED 0-未处理
	 */
	public final static String NOT_APPLIED = "0";	//未处理
	/**
	 * 审批意见:
	 * AGRER  1-同意
	 */
	public final static String AGRER = "1";		//同意
	/**
	 * 审批意见:
	 * NOT_AGRER 2-不同意
	 */
	public final static String NOT_AGRER = "2";		//不同意
	
	/**
	 * 额度状态:
	 * STATE_NORMAL 0-正常
	 */
	public final static String STATE_NORMAL = "0";
	
	/**
	 * 审批状态:
	 * APPR_FLAG_NODO 0-未审批
	 */
	public final static String APPR_FLAG_NODO = "0"; // 未审批
	
	
	//合同删除
	public static final String DCOUNT_MAIN = "1";	//主合同
	
	public static final String DCOUNT_FRAE = "2";	//从合同
	
	public static final String DCOUNT_FULL = "3";	//全部
	
	//节点类型
	public static final String NODE_TYPE_START = "1";	//开始
	
	public static final String NODE_TYPE_MID = "2";	//结束
	
	public static final String NODE_TYPE_END = "3";	//结束
	
	public static final String NODE_TYPE_OUT = "4";	//分发
	
	public static final String NODE_TYPE_UNITE = "5";	//合并
	
	/*
	储蓄业务种类，大于60，小于80
	个人贷款本金-63
	added by lixc 20080717
	 */
	public static final String SAVE_KIND = "63"; 
	
	//compcapikind 计息本金
	public static final String COMPCAPI_KIND_BALANCE = "0";	//按余额计算
	
	public static final String COMPCAPI_KIND_AHEAD = "1";	//按提前归还本金计算
	
	//计息天数 CompDayKind
	public static final String COMPDAY_KIND_BALANCE = "0";	//按整期计算
	
	public static final String COMPDAY_KIND_AHEAD = "1";	//按实际天数计算
	
	//aheakind 提前还款类型
	public static final String AHEAK_KIND_PayInte = "1";	//还本付息
	
	public static final String AHEAK_KIND_PART = "3";	//提前还本
	
	public static final String AHEAK_KIND_SETTLE = "2";	//提前结清

	public static final String AHEAK_KIND_PrepayRate = "4";	//提前还息
	
	//拖欠标志/逾期标志
	public static final String OVER_FLAG_NOT_D = "0";	//未逾期
	
	public static final String OVER_FLAG_OVERDUE = "1";	//逾期
	
	public static final String OVER_FLAG_WAIT = "2";	//待转逾期
	
	//逾期利率执行方式
	public static final String FINERATETYPE_NORMAL = "1"; //基于贷款利率
	
	public static final String FINERATETYPE_NOTICE = "2"; //基于牌告逾期利率
	
	public static final String FINERATETYPE_ZERO = "0";
	
	//贷款审批状态
	/**
	 * 04     放款审核
	 */
	public static final String APPR_STATE_OPEN = "11"; 
	/**
	 * 06     待放款
	 */
	public static final String APPR_STATE_WAIT = "06"; 
	
	//机构级别
	public static final String BANK_LEVEL_FIRST = "1"; //总行
	public static final String BANK_LEVEL_SECOND = "2"; //一级支行
	public static final String BANK_LEVEL_THIRD = "3"; //二级支行
	
	/**
	 * 分配原则：
	 * ALLOT_TYPE_EMPLOYEE 1-本次分派总量平均原则
	 */
	
	public static final String ALLOT_TYPE_EMPLOYEE ="1";  //本次分派总量平均原则
	
	/**
	 * 分配原则：
	 * ALLOT_TYPE_BUSINESS 2-分派后工作总量平均原则
	 */
	
	public static final String ALLOT_TYPE_BUSINESS ="2" ; //分派后工作总量平均原则
	
	/**
	 * 流程状态：
	 * FLOW_STATE_INIT  1-初始化
	 */
	
	public static final String FLOW_STATE_INIT ="1" ; //初始化
	/**
	 * 流程状态：
	 * FLOW_STATE_RELEASE 2-发布
	 */
	
	public static final String FLOW_STATE_RELEASE ="2" ; //发布
	/**
	 * 流程状态：
	 * FLOW_STATE_PAUSE  8-暂停使用
	 */
	
	public static final String FLOW_STATE_PAUSE ="8" ; //暂停使用
	/**
	 * 流程状态：
	 * FLOW_STATE_STOP 9-禁用
	 */
	
	public static final String FLOW_STATE_STOP ="9" ; //禁用
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_LOAN_APPLY 1-对私贷款申请
	 */
	public static final String FLOW_TYPE_LOAN_APPLY = "1" ; 
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_LOAN_G_CONT 7-对公贷款申请
	 */
	public static final String FLOW_TYPE_LOAN_G_CONT = "7" ; 
	
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_LOAN_CONT_F 0-贷款合同解冻
	 */
	public static final String FLOW_TYPE_LOAN_CONT_F = "0" ; 
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_LOAN_G_APPLY 8-对公贷款申请
	 */
	public static final String FLOW_TYPE_LOAN_G_APPLY = "8" ; 
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_METE_RELE 6-贷款发放
	 */
	public static final String FLOW_TYPE_LOAN_RELE = "6" ; 
		
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_BADCANCEL_APPLY 8-贷款核销
	 */
	public static final String FLOW_TYPE_BADCANCEL_APPLY = "8" ;
	/**
	 * 处理意见：
	 * DEAL_OPIN_AGREE 同意
	 */
	public static final String FLOW_TYPE_METE_APPLY = "2" ; 
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_METE_TUNE 3-额度调整
	 */
	public static final String FLOW_TYPE_METE_TUNE = "3" ;
	/**
	 * 流程业务种类：
	 * FLOW_TYPE_CORPPORJ_APPLY 4-合作项目额度申请
	 */
	public static final String DEAL_OPIN_AGREE = "同意" ; 
	/**
	 * 处理意见：
	 * DEAL_OPIN_REFUSE 不同意
	 * 
	 */
	public static final String DEAL_OPIN_REFUSE = "不同意" ; 
	
	/**
	 * 处理意见：
	 * DEAL_OPIN_INITSTATUS 重新调查
	 * 
	 */
	public static final String DEAL_OPIN_INITSTATUS = "重新调查" ; 
	
	
	/**
	 * 风险分类:下次分类时间间隔
	 * RISK_NEXTSORTDATE = 20
	 */
	public static final int RISK_NEXTSORTDATE = 0;
	
	/**
	 * 对帐状态
	 * 0：未对帐成功
	 */
	public static final String AC_CHECKSTATE_SF = "0";
	
	/**
	 * 对帐状态
	 * 1：对帐成功
	 */
	public static final String AC_CHECKSTATE_SS = "1";
	
	/**
	 * 合作项目状态
	 * 1：受理
	 */
	public static final String PROJSTAT_INIT = "1";
	
	/**
	 * 合作项目状态
	 * 2：审批
	 */
	public static final String PROJSTAT_APPR = "2";
	
	/**
	 * 合作项目状态
	 * 3：审批退回
	 */
	public static final String PROJSTAT_APPR_RETU = "3";
	
	/**
	 * 合作项目状态
	 * 4：生效
	 */
	public static final String PROJSTAT_EFF = "4";
	
	/**
	 * 合作项目状态
	 * 5：暂停
	 */
	public static final String PROJSTAT_PAUSE = "5";
	
	/**
	 * 合作项目状态
	 * 6：重启
	 */
	public static final String PROJSTAT_RESET = "6";
	
	/**
	 * 合作项目状态
	 * 7：退出
	 */
	public static final String PROJSTAT_STOP = "7";
	
	/**
	 * 图像顶部间距 10px
	 */
	public final static int IMG_TOP = 10;
	
	/**
	 * 图像底部间距 20px
	 */
	public final static int IMG_BOTTOM = 20;
	
	/**
	 * 图像左边间距 10px
	 */
	public final static int IMG_LEFT = 10;
	
	/**
	 * 图像右边间距 10px
	 */
	public final static int IMG_RIGHT = 10;
	
	/**
	 * 黑名单转列标志
	 * 3：待转入
	 */
	public static final String CHANFLAG_ONIN = "3";
	
	/**
	 * 黑名单转列标志
	 * 1：转入
	 */
	public static final String CHANFLAG_IN = "1";
	
	/**
	 * 黑名单转列标志
	 * 2：待转出
	 */
	public static final String CHANFLAG_ONOUT = "2";
	
	/**
	 * 黑名单转列标志
	 * 0：转出
	 */
	public static final String CHANFLAG_OUT = "0";
	
}