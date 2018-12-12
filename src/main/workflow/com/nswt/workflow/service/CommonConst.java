package com.nswt.workflow.service;

/**
 * <p> * Title: �������������� * </p>
 * <p> * Description: �������� * </p>
 * <p> * Copyright: Copyright (c) 2003 * </p>
 * <p> * Company: �������ű�������Ƽ��ɷݹ�˾ * </p>
 * * @author Liu Shuwei
 * @version 1.0
 */

public class CommonConst {
	// �첽����������ɵ��ļ��ĸ�Ŀ¼��
	public static final String PATH_OUT_ROOT = "outputpath";

	public static final String PATH_OUT_ASYNC = "async";

	// �����ļ��ĸ�Ŀ¼��
	public static final String PATH_OUT_DATA = "data";

	public static final String PATH_OUT_CMIS = "cmis";
	
	public static final String PATH_OUT_EDOC = "edoc";
	/*
	 * �����Ϣ�����ļ�
	 */
	public static final String PATH_OUT_MSG = "msg";
	
	/*
	 * ��ŵ���ͼ��
	 */
	public static final String PATH_OUT_ARCH = "arch";
	
	/**
	 * �������������Ŀ¼
	 */
	public static final String PATH_OUT_WF = "workflow";
	
	public static final String PATH_OUT_PROD = "prod";

	public static final String PATH_OUT_PUB = "pub";

	public static final String PATH_OUT_JOIN = "join";

	public static final String PATH_OUT_LOG = "log";

	// SQL�����ļ��ĸ�Ŀ¼��
	// public static final String PATH_IN_SQL = "eval";
	// Ŀ¼�ָ���
	public static final String DIR_SEPARATOR = "/";

	// �첽����״̬
	public static final int ASYNC_STATE_WAIT = 0;

	public static final int ASYNC_STATE_WORK = 1;

	public static final int ASYNC_STATE_READY = 2;

	public static final int ASYNC_STATE_ERROR = 3;

	//
	public static final String RUNCFG_TRANFILTER = "tranfilter";

	public static final String RUNCFG_BANK = "bank";

	public static final String RUNCFG_MENU = "menu";

	public static final String RUNCFG_TRAN = "tran";

	//��Ʒ���볤�Ȳ�
	public static final int COMMON_PRODID_STEP = 1;
	
	//���׶�������
	/**
	 * ���׶�������:
	 * list         L="��ѯ"
	 */
	public static final String DEAL_TYPE_LIST = "list";
	
	/**
	 * ���׶�������:
	 * insert       I="����"
	 */ 
	public static final String DEAL_TYPE_INSERT = "insert";
	
	/**
	 * ���׶�������:
	 * update       U="�޸�"
	 */
	public static final String DEAL_TYPE_UPDATE = "update";
	
	/**
	 * ���׶�������:
	 * delete       D="ɾ��"
	 */
	public static final String DEAL_TYPE_DELETE = "delete";
	
	/**
	 * ���׶�������:
	 * select       S="���"
	 */ 
	public static final String DEAL_TYPE_SELETE = "selete";
	
	/**
	 * ���׶�������:
	 * tally        C1="����"
	 */ 
	public static final String DEAL_TYPE_TALLY = "tally";
	
	/**
	 * ���׶�������:
	 * sanction     C2="����(��Ч)"
	 */ 
	public static final String DEAL_TYPE_SANCTION = "sanction";
	
	/**
	 * ���׶�������:
	 * refer        F="�ύ"
	 */ 
	public static final String DEAL_TYPE_REFER = "refer";
	
	/**
	 * ���׶�������:
	 * withdrawal   B="�˻�"
	 */
	public static final String DEAL_TYPE_WITHDRAWAL = "withdrawal";
	
	/**
	 * ���׶�������:
	 * quitacc      C3="����(����)"
	 */ 
	public static final String DEAL_TYPE_QUITACC = "quitacc";
	
	/**
	 * ���׶�������:
	 * disfrock     C4="����"
	 */
	public static final String DEAL_TYPE_DISFROCK = "disfrock";
	
	/**
	 * ���׶�������:
	 * undisfrock   C5="���߳���"
	 */ 
	public static final String DEAL_TYPE_UNDISFROCK = "undisfrock";
	
	/**
	 * ���׶�������:
	 * auditing     A="��׼/���/����"
	 */ 
	public static final String DEAL_TYPE_AUDITING = "auditing";
	
	/**
	 * ���׶�������:
	 * treesee      L9="�������"
	 */
	public static final String DEAL_TYPE_TREESEE = "treesee";
	
	/**
	 * ���׶�������:
	 * treewin      L2="���ν���"
	 */ 
	public static final String DEAL_TYPE_TREEEIN = "treewin";
	
	/**
	 * ���׶�������:
	 * mainlog      S9="ά����¼"
	 */
	public static final String DEAL_TYPE_MAINLOG = "mainlog";
	
	/**
	 * ��������:
	 * APPR_TYPE_MANU 1-�ֹ���������
	 */
	public static final String DEAL_TYPE_EVALUATION = "evaluation";
	
	/**
	 * ������������:
	 * DEAL_TYPE_EVALUATION  ����
	 */
	public static final String APPR_TYPE_MANU = "1";//�ֹ���������
	/**
	 * ��������:
	 * APPR_TYPE_FLOW 2-������������
	 */
	public static final String APPR_TYPE_FLOW = "2";//������������
	
	/**
	 * ��ƿ�Ŀ����:
	 * SUBJ_ID_LOAN_NORM_S 12201-���ڴ���
	 */
	public static final String SUBJ_ID_LOAN_NORM_S = "12201";	//���ڴ���
	/**
	 * ��ƿ�Ŀ����:
	 * SUBJ_ID_LOAN_NORM_L 12202-�г��ڴ���
	 */
	public static final String SUBJ_ID_LOAN_NORM_L = "12202";	//�г��ڴ���
	/**
	 * ��ƿ�Ŀ����:
	 * SUBJ_ID_LOAN_OVER_S 12211-����ΥԼ
	 */
	public static final String SUBJ_ID_LOAN_OVER_S = "12211";//����ΥԼ
	/**
	 * ��ƿ�Ŀ����:
	 * SUBJ_ID_LOAN_OVER_L 12212-�г���ΥԼ
	 */
	public static final String SUBJ_ID_LOAN_OVER_L = "12212";	//�г���ΥԼ
	/**
	 * ����״̬:
	 * LISTSTAT_TYPE_INIT	0-������
	 */
	public static final String LISTSTAT_TYPE_INIT = "0";	//������
	/**
	 * ����״̬:
	 * LISTSTAT_TYPE_CHARGE_UP	1-δ����
	 */	
	public static final String LISTSTAT_TYPE_CHARGE_UP = "1";	//δ����
	/**
	 * ����״̬:
	 * LISTSTAT_TYPE_CHECK 	3-�Ѽ���
	 */	
	public static final String LISTSTAT_TYPE_CHECK = "3";	//�Ѽ���
	/*
	 * <option value="0" text="������"/>
		<option value="1" text="δ����"/>
		<option value="2" text="��Ĩ��"/>
		<option value="3" text="�Ѽ���"/>
		<option value="4" text="������"/>
		<option value="5" text="Ĩ�˴�����"/>
	 */
	/**
	 * ����״̬:
	 * LISTSTAT_TYPE_STRIKE	5-Ĩ�˴�����
	 */	
	public static final String LISTSTAT_TYPE_FCHECK = "5";	//Ĩ�˴�����
	
	
	/**
	 * ����״̬:
	 * LISTSTAT_TYPE_STRIKE	2-Ĩ���Ѹ���
	 */	
	public static final String LISTSTAT_TYPE_STRIKE = "2";	//��Ĩ��
	
	/**
	 * ��������:
	 * REGIKIND_TYPE_PLEDGE	1-��Ѻ
	 */
	public static final String REGIKIND_TYPE_PLEDGE = "1";	//��Ѻ
	/**
	 * ��������:
	 * REGIKIND_TYPE_IMPAWN	2-��Ѻ
	 */	
	public static final String REGIKIND_TYPE_IMPAWN = "2";	//��Ѻ
	/**
	 * ��������:
	 * REGIKIND_TYPE_PLEDGE	3-��Ѻ�ӱ���
	 */
	public static final String REGIKIND_TYPE_INSURANCE = "3";	//��Ѻ�ӱ���
	/**
	 * ��������:
	 * REGIKIND_TYPE_ARTIFICIAL	4-���˱�֤
	 */	
	public static final String REGIKIND_TYPE_ARTIFICIAL = "4";	//���˱�֤
	/**
	 * ��������:
	 * REGIKIND_TYPE_PERSON	5-��Ȼ�˱�֤
	 */	
	public static final String REGIKIND_TYPE_PERSON = "5";	//��Ȼ�˱�֤
	
	/**
	 * ������ʽ:
	 * RISKASSUKIND_TYPE_CREDIT	1-����
	 */
	public static final String RISKASSUKIND_TYPE_CREDIT = "1";	//����
	/**
	 * ������ʽ:
	 * RISKASSUKINDD_TYPE_ASSURE 2-��֤
	 */	
	public static final String RISKASSUKIND_TYPE_ASSURE = "2";	//��֤
	/**
	 * ������ʽ:
	 * RISKASSUKIND_TYPE_PLEDGE	3-��Ѻ
	 */	
	public static final String RISKASSUKIND_TYPE_PLEDGE = "3";	//��Ѻ
	
	/**
	 * ������ʽ:
	 * RISKASSUKIND_TYPE_IMPAWN	4-��Ѻ
	 */	
	public static final String RISKASSUKIND_TYPE_IMPAWN = "4";	//��Ѻ
	
	/**
	 * ������ʽ:
	 * RISKASSUKIND_TYPE_IMPAWN	5-��Ѻ�ӱ���
	 */	
	public static final String RISKASSUKIND_TYPE_INSURANCE = "5";	//��Ѻ�ӱ���

	/**
	 * �������:
	 * NOT_APPLIED 0-δ����
	 */
	public final static String NOT_APPLIED = "0";	//δ����
	/**
	 * �������:
	 * AGRER  1-ͬ��
	 */
	public final static String AGRER = "1";		//ͬ��
	/**
	 * �������:
	 * NOT_AGRER 2-��ͬ��
	 */
	public final static String NOT_AGRER = "2";		//��ͬ��
	
	/**
	 * ���״̬:
	 * STATE_NORMAL 0-����
	 */
	public final static String STATE_NORMAL = "0";
	
	/**
	 * ����״̬:
	 * APPR_FLAG_NODO 0-δ����
	 */
	public final static String APPR_FLAG_NODO = "0"; // δ����
	
	
	//��ͬɾ��
	public static final String DCOUNT_MAIN = "1";	//����ͬ
	
	public static final String DCOUNT_FRAE = "2";	//�Ӻ�ͬ
	
	public static final String DCOUNT_FULL = "3";	//ȫ��
	
	//�ڵ�����
	public static final String NODE_TYPE_START = "1";	//��ʼ
	
	public static final String NODE_TYPE_MID = "2";	//����
	
	public static final String NODE_TYPE_END = "3";	//����
	
	public static final String NODE_TYPE_OUT = "4";	//�ַ�
	
	public static final String NODE_TYPE_UNITE = "5";	//�ϲ�
	
	/*
	����ҵ�����࣬����60��С��80
	���˴����-63
	added by lixc 20080717
	 */
	public static final String SAVE_KIND = "63"; 
	
	//compcapikind ��Ϣ����
	public static final String COMPCAPI_KIND_BALANCE = "0";	//��������
	
	public static final String COMPCAPI_KIND_AHEAD = "1";	//����ǰ�黹�������
	
	//��Ϣ���� CompDayKind
	public static final String COMPDAY_KIND_BALANCE = "0";	//�����ڼ���
	
	public static final String COMPDAY_KIND_AHEAD = "1";	//��ʵ����������
	
	//aheakind ��ǰ��������
	public static final String AHEAK_KIND_PayInte = "1";	//������Ϣ
	
	public static final String AHEAK_KIND_PART = "3";	//��ǰ����
	
	public static final String AHEAK_KIND_SETTLE = "2";	//��ǰ����

	public static final String AHEAK_KIND_PrepayRate = "4";	//��ǰ��Ϣ
	
	//��Ƿ��־/���ڱ�־
	public static final String OVER_FLAG_NOT_D = "0";	//δ����
	
	public static final String OVER_FLAG_OVERDUE = "1";	//����
	
	public static final String OVER_FLAG_WAIT = "2";	//��ת����
	
	//��������ִ�з�ʽ
	public static final String FINERATETYPE_NORMAL = "1"; //���ڴ�������
	
	public static final String FINERATETYPE_NOTICE = "2"; //�����Ƹ���������
	
	public static final String FINERATETYPE_ZERO = "0";
	
	//��������״̬
	/**
	 * 04     �ſ����
	 */
	public static final String APPR_STATE_OPEN = "11"; 
	/**
	 * 06     ���ſ�
	 */
	public static final String APPR_STATE_WAIT = "06"; 
	
	//��������
	public static final String BANK_LEVEL_FIRST = "1"; //����
	public static final String BANK_LEVEL_SECOND = "2"; //һ��֧��
	public static final String BANK_LEVEL_THIRD = "3"; //����֧��
	
	/**
	 * ����ԭ��
	 * ALLOT_TYPE_EMPLOYEE 1-���η�������ƽ��ԭ��
	 */
	
	public static final String ALLOT_TYPE_EMPLOYEE ="1";  //���η�������ƽ��ԭ��
	
	/**
	 * ����ԭ��
	 * ALLOT_TYPE_BUSINESS 2-���ɺ�������ƽ��ԭ��
	 */
	
	public static final String ALLOT_TYPE_BUSINESS ="2" ; //���ɺ�������ƽ��ԭ��
	
	/**
	 * ����״̬��
	 * FLOW_STATE_INIT  1-��ʼ��
	 */
	
	public static final String FLOW_STATE_INIT ="1" ; //��ʼ��
	/**
	 * ����״̬��
	 * FLOW_STATE_RELEASE 2-����
	 */
	
	public static final String FLOW_STATE_RELEASE ="2" ; //����
	/**
	 * ����״̬��
	 * FLOW_STATE_PAUSE  8-��ͣʹ��
	 */
	
	public static final String FLOW_STATE_PAUSE ="8" ; //��ͣʹ��
	/**
	 * ����״̬��
	 * FLOW_STATE_STOP 9-����
	 */
	
	public static final String FLOW_STATE_STOP ="9" ; //����
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_LOAN_APPLY 1-��˽��������
	 */
	public static final String FLOW_TYPE_LOAN_APPLY = "1" ; 
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_LOAN_G_CONT 7-�Թ���������
	 */
	public static final String FLOW_TYPE_LOAN_G_CONT = "7" ; 
	
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_LOAN_CONT_F 0-�����ͬ�ⶳ
	 */
	public static final String FLOW_TYPE_LOAN_CONT_F = "0" ; 
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_LOAN_G_APPLY 8-�Թ���������
	 */
	public static final String FLOW_TYPE_LOAN_G_APPLY = "8" ; 
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_METE_RELE 6-�����
	 */
	public static final String FLOW_TYPE_LOAN_RELE = "6" ; 
		
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_BADCANCEL_APPLY 8-�������
	 */
	public static final String FLOW_TYPE_BADCANCEL_APPLY = "8" ;
	/**
	 * ���������
	 * DEAL_OPIN_AGREE ͬ��
	 */
	public static final String FLOW_TYPE_METE_APPLY = "2" ; 
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_METE_TUNE 3-��ȵ���
	 */
	public static final String FLOW_TYPE_METE_TUNE = "3" ;
	/**
	 * ����ҵ�����ࣺ
	 * FLOW_TYPE_CORPPORJ_APPLY 4-������Ŀ�������
	 */
	public static final String DEAL_OPIN_AGREE = "ͬ��" ; 
	/**
	 * ���������
	 * DEAL_OPIN_REFUSE ��ͬ��
	 * 
	 */
	public static final String DEAL_OPIN_REFUSE = "��ͬ��" ; 
	
	/**
	 * ���������
	 * DEAL_OPIN_INITSTATUS ���µ���
	 * 
	 */
	public static final String DEAL_OPIN_INITSTATUS = "���µ���" ; 
	
	
	/**
	 * ���շ���:�´η���ʱ����
	 * RISK_NEXTSORTDATE = 20
	 */
	public static final int RISK_NEXTSORTDATE = 0;
	
	/**
	 * ����״̬
	 * 0��δ���ʳɹ�
	 */
	public static final String AC_CHECKSTATE_SF = "0";
	
	/**
	 * ����״̬
	 * 1�����ʳɹ�
	 */
	public static final String AC_CHECKSTATE_SS = "1";
	
	/**
	 * ������Ŀ״̬
	 * 1������
	 */
	public static final String PROJSTAT_INIT = "1";
	
	/**
	 * ������Ŀ״̬
	 * 2������
	 */
	public static final String PROJSTAT_APPR = "2";
	
	/**
	 * ������Ŀ״̬
	 * 3�������˻�
	 */
	public static final String PROJSTAT_APPR_RETU = "3";
	
	/**
	 * ������Ŀ״̬
	 * 4����Ч
	 */
	public static final String PROJSTAT_EFF = "4";
	
	/**
	 * ������Ŀ״̬
	 * 5����ͣ
	 */
	public static final String PROJSTAT_PAUSE = "5";
	
	/**
	 * ������Ŀ״̬
	 * 6������
	 */
	public static final String PROJSTAT_RESET = "6";
	
	/**
	 * ������Ŀ״̬
	 * 7���˳�
	 */
	public static final String PROJSTAT_STOP = "7";
	
	/**
	 * ͼ�񶥲���� 10px
	 */
	public final static int IMG_TOP = 10;
	
	/**
	 * ͼ��ײ���� 20px
	 */
	public final static int IMG_BOTTOM = 20;
	
	/**
	 * ͼ����߼�� 10px
	 */
	public final static int IMG_LEFT = 10;
	
	/**
	 * ͼ���ұ߼�� 10px
	 */
	public final static int IMG_RIGHT = 10;
	
	/**
	 * ������ת�б�־
	 * 3����ת��
	 */
	public static final String CHANFLAG_ONIN = "3";
	
	/**
	 * ������ת�б�־
	 * 1��ת��
	 */
	public static final String CHANFLAG_IN = "1";
	
	/**
	 * ������ת�б�־
	 * 2����ת��
	 */
	public static final String CHANFLAG_ONOUT = "2";
	
	/**
	 * ������ת�б�־
	 * 0��ת��
	 */
	public static final String CHANFLAG_OUT = "0";
	
}