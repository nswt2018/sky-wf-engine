package com.sky.workflow.engine;
/**
 * <p>Title: ����������</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: ������������������Ϣϵͳ���޹�˾ </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.2 ƽ̨Ǩ�� , 2008-5-22 ����11:12:21
 */
public class CommonConst {
	/**
	 * ������ѡ�˽���
	 */
	public static final String WF_SEL_WIN = "WF031F";
	/**
	 * ��������״̬"ȡ��"
	 */
	public static final String WF_RETAKE = "ȡ��";
	/**
	 * ��������״̬"�˻�"
	 */
	public static final String WF_UNTREAD = "�˻�";
	/**
	 * ���̽ڵ�����
	 * ��ʼ value="1"
	 */
	public static final String WF_NODETYPE_START = "1";
	/**
	 * ���̽ڵ�����
	 * �м� value="2"
	 */
	public static final String WF_NODETYPE_MID = "2";
	/**
	 * ���̽ڵ�����
	 * ���� value="3"
	 */
	public static final String WF_NODETYPE_END = "3";
	/**
	 * ���̽ڵ�����
	 * �ַ� value="4"
	 */
	public static final String WF_NODETYPE_OUT = "4";
	/**
	 * ���̽ڵ�����
	 * �ϲ� value="5"
	 */
	public static final String WF_NODETYPE_UNITE = "5";
	/**
	 * ���̽ڵ����������̡�
	 * ������ value="6"
	 */
	public static final String WF_NODETYPE_SUBWORKFLOW = "6";
	/**
	 * ����ҵ������
	 * �����-ǩ��ͬvalue="6"
	 */
	public static final String WF_TYPE_LOANPASS = "6";
	/**
	 * ����ҵ������
	 * ��ͬ����value="7"
	 */
	public static final String WF_TYPE_CONTAPPR = "7";
	/**
	 * ����ҵ������
	 * �������value="2"
	 */
	public static final String WF_TYPE_CREDAPPLY = "2";
	
	/**
	 * ����ҵ������
	 * ��������value="1"
	 */
	public static final String WF_TYPE_LOANAPPLY = "1";
	
	/**
	 * ����ҵ������
	 * �Թ���������value="8"
	 */
	public static final String WF_TYPE_PLOANAPPLY = "8";
	
	/**
	 * ����ҵ������
	 * ������� value="40"
	 */
	public static final String WF_TYPE_LOANAFTER = "40";
	/**
	 * ��һ�ڵ����÷�ʽ
	 * �����ж�value="1"
	 */
	public static final String WF_NODEROUTE_DYNAMIC = "1";
	/**
	 * ���̽ڵ㴦��ʽ
	 * ������� value="0"
	 */
	public static final String WF_PROCEMODE_RANDOM = "0";
	/**
	 * ���̽ڵ㴦��ʽ
	 * ���������� value="1"
	 */
	public static final String WF_PROCEMODE_WORKLEAST = "1";
	/**
	 * ���̽ڵ�����������
	 * �ֹ����� value="0"
	 */
	public static final String WF_TASKASSIGN_MANUAL = "0";
	/**
	 * ���̽ڵ�����������
	 * �Զ����� value="1"
	 */
	public static final String WF_TASKASSIGN_AUTO = "1";
	/**
	 * ���̽ڵ�����������
	 * ����������Ա value="2"
	 */
	public static final String WF_TASKASSIGN_ALL = "2";
	
	/**
	 * ���̽ڵ�����������
	 * �����������Ա value="3"
	 */
	public static final String WF_TASKASSIGN_WFOUT = "3";
	
	/**
	 * ���̽ڵ�������ɲ���
	 * ��������� value="0"
	 */
	public static final String WF_TASKCOMP_AMT = "0";
	
	/**
	 * ���̽ڵ�������ɲ���
	 * ����ɰٷֱ� value="1"
	 */
	public static final String WF_TASKCOMP_PER = "1";
	
	/**
	 * ���̽ڵ�������ɲ���
	 * �������һ�� value="2"
	 */
	public static final String WF_TASKCOMP_AUTO = "2";
	
	/**
	 * ���̽ڵ�������ɲ���
	 * �������ж���� value="3"
	 */
	public static final String WF_TASKCOMP_ALL = "3";
	
	/**
	 * ���ں�ʱ������
	 * ������ value="yyyyMMdd"
	 */
	public static final String DATE_TYPE_DATE = "yyyyMMdd";
	/**
	 * ���ں�ʱ������,Сʱ1-23,0:00:00����00:00:00������24:00:00
	 * ������ʱ���� value="yyyyMMddHHmmss"
	 */
	public static final String DATE_TYPE_TIMESTAMP = "yyyyMMddHHmmss";
	/**
	 * ���ں�ʱ������,Сʱ1-23,0:00:00����00:00:00������24:00:00
	 * ������ʱ������� value="yyyyMMddHHmmssSSS"
	 */
	public static final String DATE_TYPE_TIMESTAMP_MSEL = "yyyyMMddHHmmssSSS";
	/**
	 * ͨ���Ƿ��־
	 * 0-��,1-��
	 */
	public static final String WF_ISYESORNO_NO = "0";
	/**
	 * ͨ���Ƿ��־
	 * 0-��,1-��
	 */
	public static final String WF_ISYESORNO_YES = "1";
	/**
	 * ���̸�λ������Ϣ
	 * ��������@
	 */
	public static final String WF_POSTID_LOCAL = "@";
	/**
	 * ���̸�λ������Ϣ
	 * �ϼ���������$
	 */
	public static final String WF_POSTID_SUPERAPPR = "$";
	/**
	 * ���̸�λ������Ϣ
	 * �ϼ��������$
	 */
	public static final String WF_POSTID_SUPERMGR = "#";
	/**
	 * ���̸�λ������Ϣ
	 * ���̷������-
	 */
	public static final String WF_POSTID_WFINIT = "-";
	/**
	 * ���̸�λ������Ϣ
	 * ָ������ value='%'
	 */
	public static final String WF_POSTID_ASSIGN = "%";
	/**
	 * ���̸�λ������Ϣ
	 * ���̷���������ϼ���������^
	 */
	public static final String WF_POSTID_WFINIT_SUPERAPPR = "^";
	/**
	 * ���̸�λ������Ϣ
	 * ���̷����� value='*'
	 */
	public static final String WF_POSTID_WFINITUSER = "*";
	
	/**
	 * ����״̬��Ϣ
	 * ��ʼ�� value="1"
	 */
	public static final String WF_STATE_INIT = "1";
	/**
	 * ����״̬��Ϣ
	 * ���� value="2"
	 */
	public static final String WF_STATE_START = "2";
	/**
	 * ����״̬��Ϣ
	 * ��ͣʹ�� value="8"
	 */
	public static final String WF_STATE_PAUSE = "8";
	/**
	 * ����״̬��Ϣ
	 * ���� value="9"
	 */
	public static final String WF_STATE_STOP = "9";
	/**
	 * �û�״̬
	 * �ڸ� value='1'
	 */
	public static final String WF_USERLOGON_IN = "1";
	/**
	 * �û�״̬
	 * ��� value='0'
	 */
	public static final String WF_USERLOGON_OUT = "0";
	/**
	 * �Ƿ������Ա��ʷ��ѯ,����dwtaskhis�в�ѯ�Ѿ����������Աid
	 * �� value='1'
	 */
	public static final String WF_HISUSER_ON = "1";
	/**
	 * �Ƿ������Ա��ʷ��ѯ,����dwtaskhis�в�ѯ�Ѿ����������Աid
	 * �� value='0'
	 */
	public static final String WF_HISUSER_OFF = "0";
	
	/**
	 * ��������
	 * ��������
	 */
	public static final int WF_TASKTYPE_DEAL = 2;
	
	/**
	 * ��������
	 * �������
	 */
	public static final int WF_TASKTYPE_VIEW = 1;
	

	/**
	 * ��ȡ�ϼ�������Χ
	 * 1��ֱ���ϼ�����
	 */
	public static final String WF_SUPERBANKSCOPE_ONE = "1";
	
	/**
	 * ��ȡ�ϼ�������Χ
	 * 2��ȫ���ϼ�����
	 */
	public static final String WF_SUPERBANKSCOPE_ALL = "2";
	
	/**
	 * �¼�simon 20170906
	 */
	public static final String BANK_LEVEL_FIRST="1";
	
	/**
	 * ҵ����ʽ
	 * 1���������ύ���Թ���  
	 */	
	public static final String WF_BUSIOPERATESTYLE_G = "1";
	/**
	 * ҵ����ʽ
	 * 2���б����ύ����˽��  
	 */	
	public static final String WF_BUSIOPERATESTYLE_S = "2";
	/**
	 * ҵ����ʽ
	 * 4���б����ύ��С��ҵ��  
	 */	
	public static final String WF_BUSIOPERATESTYLE_P = "4";
	/**
	 * ҵ����ʽ
	 * 5���б����ύ��������  
	 */	
	public static final String WF_BUSIOPERATESTYLE_N = "5";
	/**
	 * ҵ����ʽ
	 * 6���б����ύ���Ǽǲ�Ʒ��  
	 */	
	public static final String WF_BUSIOPERATESTYLE_F = "6";
	
	/**
	 * ҵ����ʽ
	 * 3���б����ύ�����������Ʒû�й�ϵ�ģ�  
	 */	
	public static final String WF_BUSIOPERATESTYLE_O = "3";
	
	/**
	 * ҵ������
	 * 10  ����ҵ����������  
	 */	
	public static final String WF_BUSITYPE_SX = "10";
	public static final String WF_BUSITYPE_ZQ = "11";
	/**
	 * ҵ������
	 * 20  ��ͬ�������
	 */	
	public static final String WF_BUSITYPE_HS = "20";
	
	/**
	 * ҵ������
	 * 21  ��ͬ�ⶳ����
	 */	
	public static final String WF_BUSITYPE_HJ = "21";
	public static final String WF_BUSITYPE_HZ = "22";
	public static final String WF_BUSITYPE_HD = "23";
	/**
	 * ҵ������
	 * 30  �ſ��������
	 */	
	public static final String WF_BUSITYPE_FS = "30";
	/**
	 * ҵ������
	 * 40  ����������
	 */	
	public static final String WF_BUSITYPE_DC = "40";

	public static final String WF_BUSITYPE_DH = "41"; //������
	public static final String WF_BUSITYPE_DL = "42"; //���ʱ��
	public static final String WF_BUSITYPE_DD = "43"; //�������
	
	public static final String WF_BUSITYPE_DP = "44"; //����������
	public static final String WF_BUSITYPE_DFY = "45"; //�ش����Ԥ��
	public static final String WF_BUSITYPE_DFL = "46"; //���շ���
	public static final String WF_BUSITYPE_DJC = "47"; //�����ֵ����
	
	/**
	 * ҵ������
	 * 50  �ʲ���ȫ����
	 */	
	public static final String WF_BUSITYPE_BQ = "50";
	public static final String WF_BUSITYPE_BFC = "51"; //���ɴ�ʩ����
	public static final String WF_BUSITYPE_BDZ = "52";
	

	/**
	 * ҵ������
	 * 60 ��������
	 */
	public static final String WF_BUSITYPE_PJSX = "60";
	public static final String WF_BUSITYPE_SYPJ = "61"; //��������
	public static final String WF_BUSITYPE_ZHSX = "62"; //�ۺ�����
	public static final String WF_BUSITYPE_HEZSX = "63"; //����������

	
	/**
	 * �Ƿ�����ڵ�
	 * 1  ��
	 */	
	public static final String WF_ISPRIMARYAUDITNODE_YES = "1";
	/**
	 * �Ƿ�����ڵ�
	 * 2 ��
	 */	
	public static final String WF_ISPRIMARYAUDITNODE_NO = "2";	
	
}
