package cn.com.jbbis.jbportal.workflow;

/**
 * 
* @ClassName: RemoteCallWorkflowTemplateService 
* @Description: Զ�̵�������ģ��ͨ�ýӿ� 
* @author rOOkiE
* @date 2012-10-23 ����2:47:36 
*
 */
public interface RemoteCallWorkflowTemplateService {

	/**
	 * 
	* @Title: call 
	* @Description: TODO(������һ�仰�����������������) 
	* @param params �����������
	* @param templateName  ģ������,�������ֵ��� �ĸ�ģ�� 
	* @param method Ҫ���� ��ģ��ķ�������
	* @return void    �������� 
	* @throws
	 */
	public void call(WFParams params,String templateName,String method) throws Exception;
	
	/**
	 * 
	* @Title: getRemoteObject 
	* @Description: ����ģ�����ƻ�ȡԶ��ģ����� 
	* @param @param templateName
	* @param @return    �趨�ļ� 
	* @return Object    �������� 
	* @throws
	 */
	public Object getRemoteObject(String templateName) throws Exception;
}
