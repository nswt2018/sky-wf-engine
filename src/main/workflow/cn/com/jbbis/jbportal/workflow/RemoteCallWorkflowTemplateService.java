package cn.com.jbbis.jbportal.workflow;

/**
 * 
* @ClassName: RemoteCallWorkflowTemplateService 
* @Description: 远程调用流程模板通用接口 
* @author rOOkiE
* @date 2012-10-23 下午2:47:36 
*
 */
public interface RemoteCallWorkflowTemplateService {

	/**
	 * 
	* @Title: call 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param params 流程引擎参数
	* @param templateName  模板名称,用于区分调用 哪个模板 
	* @param method 要调用 的模板的方法名称
	* @return void    返回类型 
	* @throws
	 */
	public void call(WFParams params,String templateName,String method) throws Exception;
	
	/**
	 * 
	* @Title: getRemoteObject 
	* @Description: 根据模板名称获取远程模板对象 
	* @param @param templateName
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	public Object getRemoteObject(String templateName) throws Exception;
}
