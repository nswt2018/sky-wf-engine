package cn.com.jbbis.jbportal.workflow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.jbbis.afx.AppContext;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.Application;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.LoanApp;
import cn.com.jbbis.jbportal.BizLogic;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.util.Config;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.StringUtil;
import cn.com.jbbis.util.UnikMap;
import cn.com.jbbis.util.XmlConfig;

public class BusiDataLoad extends BizLogic{
	private LoanApp app;
	private AppRequest request;
	private AppContext context;
    private String selfield=null;
    public BusiDataLoad(AppContext ctx)
	{
		context = ctx;
		set(ctx);
		this.app = (LoanApp)ctx.getApplication();
		this.request = ctx.getRequest();
		
	}
	
	public BusiDataLoad(AppContext ctx,String selstr)
	{
		context = ctx;
		set(ctx);
		this.app = (LoanApp)ctx.getApplication();
		this.request = ctx.getRequest();
		this.selfield=selstr;
	}
	
	/**
	 * @deprecated
	 */
	public BusiDataLoad(Application app,Service service,AppContext ctx) {
		this(ctx);
	}
	
	/**
	 * 拼写where条件
	 * @return
	 * @throws RunTimeException
	 */
	public String getWhere(String tabName) throws Exception {
		//得到主键字段,可能是多个主键,所以要拆分
		String tmp = getTablePrimaryKeys(tabName);
		if(tmp == null){
			return null;
		}
		String[] strkeys = tmp.split(",");
		UnikMap keys = new UnikMap();
		for (int i = 0; i < strkeys.length; i++) {
			keys.put(strkeys[i], strkeys[i]);
		}
		
		//得到表字段信息
		Config cfg = getTableConfig(tabName);
		Iterator it = cfg.keySet().iterator();
		
		StringBuffer buf = new StringBuffer();
		while(it.hasNext()){
			String name = it.next().toString();
			//表的字段都是fields/字段
			if(name.startsWith("fields/")){
				name = name.substring(name.indexOf("/")+1);
			}else{
				continue;
			}
			
			//keys.get(name) == null说明此字段不是主键
			if (keys.get(name) == null) {
				continue;
			}
			
			String n = name.toLowerCase();
			String val = request.getField(n);
			if(val.length()==0)
				continue;
			
			buf.append(n);
			buf.append("='");
			buf.append(val);
			buf.append("' and ");
		}
		if(buf.length()>0)
			buf.delete(buf.length()-5, buf.length());
		return buf.toString();
	}
	
	public UnikMap getFieldInfo() throws Exception
	{
		/*
		 <sql name="getAllCmFieldInfo">
		 	select 
		 		fieldtab,
		 		fieldcode,
		 		fieldname,
		 		fielddescription,
		 		issubjuseflag,
		 		getsql
		 	from 
		 		cmFieldInfo 
		 	where
	 			getsql is null
				or getsql = ''
		 	order by FieldTab
		 </sql>
		 */		
		String bankid=request.getField("bankid");
		if("".equals(bankid)||null==bankid){
			request.setField("bankid",request.getField("s_bankid"));
		}
		DataList dl = executeProcedure(CommonConst.getTpl(this.getClass(),"getAllCmFieldInfo"),request);
		String tabName = null;
		LinkedHashMap tabs = new LinkedHashMap();
		List fields = new ArrayList();
		while(dl.next())
		{
			
			String tableName = dl.getString("fieldtab");
			String fieldName = dl.getString("fieldcode");
			if(tabName == null || !tabName.equalsIgnoreCase(tableName))
			{
				fields = new ArrayList();
				tabs.put(tableName, fields);
				tabName = tableName;
			}
			fields.add(fieldName);
		}
		
		return getData(tabs,selfield);
	}
	
	private UnikMap getData(Map map,String selfield) throws Exception
	{
		UnikMap retMap = new UnikMap();
		Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			String tabName = String.valueOf(it.next());
			//从对应TABLE.XML中获取表的主键信息			
			String strKeys = getTablePrimaryKeys(tabName);

			// 从request中取主键字段进行查询
			
			if(checkKeys(strKeys, request))
			{				
				Object selectFields = map.get(tabName);
				if(selectFields == null) continue;
								
				context.put("select", listToString((List)selectFields));
				SingleResult sr = performAction(SELECT, tabName.toLowerCase(), request);
				context.remove("select");
				if(sr != null)
				{
					Iterator itor = sr.keySet().iterator();
					while(itor.hasNext())
					{
						String k = String.valueOf(itor.next());
						
						String v = sr.getField(k);
						//selfield 为需要用para.xml 进行解析的变量 用逗号隔开多个变量 如",A,B," 前后加上逗号
						//&&selfield.indexOf(","+k+",")!=-1 &&selfield!=null 
						if(!"".equals(selfield)){
							v=getSelectText(k,v);	
						}
						if(v.length() > 0)
						{
							retMap.put(tabName + "." + k, v);
						}
					}
				}
			}
		}
		/*<sql name="getCmFieldInfo">
		 	select 
		 		* 
		 	from 
		 		cmFieldInfo
		 	where 
		 	    getsql is not null
		 	    or getsql != ''
		 	order by FieldTab
		 </sql>*/
	 
		
		DataList dl = executeProcedure(CommonConst.getTpl(this.getClass(),"getCmFieldInfo"));
		while (dl.next()) 
		{
			String name = dl.getString("fieldtab");
			String field = dl.getString("fieldcode");
			String getSql = dl.getString("getsql");
			if(getSql!=null && !getSql.equals(""))
			{
				delaExecSql(name, field, getSql, retMap);
			}
		}
		
		
		return retMap;
	}
	
	private String listToString(List list)
	{
		StringBuffer buf = new StringBuffer();
		Iterator it = list.iterator();
		while(it.hasNext())
		{
			buf.append(it.next()).append(",");
		}
		int len = buf.length();
		return len > 1 ? buf.delete(len - 1, len).toString() : buf.toString();
	}
	
	/**
	 * 如果params里没有KEY字段或KEY字段值为空则返回false
	 * @param arg0
	 * @param params
	 * @return
	 */
	private boolean checkKeys(String arg0, UnikMap params) 
	{
		boolean flag = true;
		if(arg0 != null)
		{
			String[] keys = arg0.split(",");
			for (int i = 0; i < keys.length; i++)
			{
				if(params.getString(keys[i]) == null)
				{
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 请使用getFieldInfo方法
	 * @deprecated 
	 * @return
	 * @throws Exception
	 */
	public UnikMap loadData() throws Exception {
		return getFieldInfo();
		
	}

	/**
	 * @discription:得到表配置信息,主要是字段名和字段描述
	 * @param:
	 * @return:
	 * @throws Exception
	 */
	public Config getTableConfig(String table) throws Exception
	{
		XmlConfig cfg = new XmlConfig();
		InputStream in = app.getResource("table/" + table + ".xml");
		if(in == null){
			return new Config();
		}
		//Errors.Assert(in != null, "TableNotFound", table);
		cfg.load(in);
		return cfg;
	}

	/**
	 * @discription:得到对应表的主键字段名
	 * @param:
	 * @return:
	 * @throws Exception
	 */
	protected String getTablePrimaryKeys(String tabName) throws Exception{
		Config cfg = getTableConfig(tabName);
		return cfg.getString("primarykeys");
	}
	public void delaExecSql(String tableName,String field,String getSql,UnikMap m) throws Exception{
		UnikMap dp = new UnikMap();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String n = it.next().toString();
			String value = m.getString(n);
			getSql = StringUtil.replace(getSql, "{" + n + "}", value);
		}

		dp.clear();
		dp.put("procedure", getSql);
		try {
			SingleResult rs = querySingle("COMMON", dp,null);
			if(rs != null){
				String obj = rs.getField(field);
				m.put(tableName + "." + field, !"".equals(obj)?obj:" ");
			}
		} catch (Exception e) {
			//System.out.println("e===getSql===============" + getSql);
			log(Service.DEBUG, getSql);
		}
	}
	public Document getSelectConfig(String select) throws Exception{
		InputStream in = application.getResource("select/" + select + ".xml");
		Document doc = null;
		if(in != null){
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = b.parse(in);
			in.close();
		}
		return doc;
	}
	public String getSelectText(String select,String value) throws Exception{
		String text = value;
		Document doc = getSelectConfig(select);
		if(doc == null)
			return text;
		Element root = doc.getDocumentElement(); 
		NodeList list = root.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE){
				NamedNodeMap m = n.getAttributes();
				if(m.getNamedItem("value").getNodeValue().equals(value)){
					text = m.getNamedItem("text").getNodeValue();
					break;
				}else{
					continue;
				}
			}
		}
		return text;
	}
	
	/**
	 * 得到select中的所有值
	 * @param select
	 * @return
	 * @throws Exception
	 */
	public String[][] getSelectList(String select) throws Exception
	{
		
		Document doc = getSelectConfig(select);
		if(doc == null)
			return null;
		Element root = doc.getDocumentElement(); 
		NodeList list = root.getChildNodes();
		
		String[][] selList = new String[list.getLength()][2];
		
		for(int i=0;i<list.getLength();i++){
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE){
				NamedNodeMap m = n.getAttributes();
				selList[i][0]= m.getNamedItem("value").getNodeValue();
				selList[i][1]= m.getNamedItem("text").getNodeValue();
				
			}
		}
		return selList;
	}
}
