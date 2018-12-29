package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwFlowMainTable")
@Table(name = "DwFlowMain")
public class DwFlowMainTable {
	@Id
	private String flowid;

	private String flowname;

	private String flowdesc;

	private String flowstate;

	private Date effdate;

	private Date enddate;

	private String manusql;

	private String execsql;

	private Date lastchgdate;

	private String lastchgtime;

	private String iswritebusiwfmap;

	private String isstarttempauth;

	private Date lastchangetime;

	private String fieldtabs;

	private String fieldtabsname;

	private String flowcndesc;

	public String getFlowid() {
		return flowid;
	}

	public void setFlowid(String flowid) {
		this.flowid = flowid == null ? null : flowid.trim();
	}

	public String getFlowname() {
		return flowname;
	}

	public void setFlowname(String flowname) {
		this.flowname = flowname == null ? null : flowname.trim();
	}

	public String getFlowdesc() {
		return flowdesc;
	}

	public void setFlowdesc(String flowdesc) {
		this.flowdesc = flowdesc == null ? null : flowdesc.trim();
	}

	public String getFlowstate() {
		return flowstate;
	}

	public void setFlowstate(String flowstate) {
		this.flowstate = flowstate == null ? null : flowstate.trim();
	}

	public Date getEffdate() {
		return effdate;
	}

	public void setEffdate(Date effdate) {
		this.effdate = effdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getManusql() {
		return manusql;
	}

	public void setManusql(String manusql) {
		this.manusql = manusql == null ? null : manusql.trim();
	}

	public String getExecsql() {
		return execsql;
	}

	public void setExecsql(String execsql) {
		this.execsql = execsql == null ? null : execsql.trim();
	}

	public Date getLastchgdate() {
		return lastchgdate;
	}

	public void setLastchgdate(Date lastchgdate) {
		this.lastchgdate = lastchgdate;
	}

	public String getLastchgtime() {
		return lastchgtime;
	}

	public void setLastchgtime(String lastchgtime) {
		this.lastchgtime = lastchgtime == null ? null : lastchgtime.trim();
	}

	public String getIswritebusiwfmap() {
		return iswritebusiwfmap;
	}

	public void setIswritebusiwfmap(String iswritebusiwfmap) {
		this.iswritebusiwfmap = iswritebusiwfmap == null ? null : iswritebusiwfmap.trim();
	}

	public String getIsstarttempauth() {
		return isstarttempauth;
	}

	public void setIsstarttempauth(String isstarttempauth) {
		this.isstarttempauth = isstarttempauth == null ? null : isstarttempauth.trim();
	}

	public Date getLastchangetime() {
		return lastchangetime;
	}

	public void setLastchangetime(Date lastchangetime) {
		this.lastchangetime = lastchangetime;
	}

	public String getFieldtabs() {
		return fieldtabs;
	}

	public void setFieldtabs(String fieldtabs) {
		this.fieldtabs = fieldtabs == null ? null : fieldtabs.trim();
	}

	public String getFieldtabsname() {
		return fieldtabsname;
	}

	public void setFieldtabsname(String fieldtabsname) {
		this.fieldtabsname = fieldtabsname == null ? null : fieldtabsname.trim();
	}

	public String getFlowcndesc() {
		return flowcndesc;
	}

	public void setFlowcndesc(String flowcndesc) {
		this.flowcndesc = flowcndesc == null ? null : flowcndesc.trim();
	}

}