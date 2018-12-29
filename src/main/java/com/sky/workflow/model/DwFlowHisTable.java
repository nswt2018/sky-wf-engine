package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwFlowHisTable")
@Table(name = "DwFlowHis")
public class DwFlowHisTable {
    private String wfid;

    private String flowid;

    private String flowname;

    private String flowdesc;

    private String flowtype;

    private String bankid;

    private String operid;

    private String opername;

    private String creattime;

    private String nodename;

    private String wfstate;

    private String exectrancode;

    private String submtrancode;

    private String looktrancode;

    private Long taskser;

    private String manusql;

    private String execsql;

    private String unitwfid;

    private String instancylevel;

    private Date lastchgdate;

    private String lastchgtime;

    private String iswritebusiwfmap;

    private String busioperatestyle;

    private Date lastchangetime;

    public String getWfid() {
        return wfid;
    }

    public void setWfid(String wfid) {
        this.wfid = wfid == null ? null : wfid.trim();
    }

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

    public String getFlowtype() {
        return flowtype;
    }

    public void setFlowtype(String flowtype) {
        this.flowtype = flowtype == null ? null : flowtype.trim();
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid == null ? null : bankid.trim();
    }

    public String getOperid() {
        return operid;
    }

    public void setOperid(String operid) {
        this.operid = operid == null ? null : operid.trim();
    }

    public String getOpername() {
        return opername;
    }

    public void setOpername(String opername) {
        this.opername = opername == null ? null : opername.trim();
    }

    public String getCreattime() {
        return creattime;
    }

    public void setCreattime(String creattime) {
        this.creattime = creattime == null ? null : creattime.trim();
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename == null ? null : nodename.trim();
    }

    public String getWfstate() {
        return wfstate;
    }

    public void setWfstate(String wfstate) {
        this.wfstate = wfstate == null ? null : wfstate.trim();
    }

    public String getExectrancode() {
        return exectrancode;
    }

    public void setExectrancode(String exectrancode) {
        this.exectrancode = exectrancode == null ? null : exectrancode.trim();
    }

    public String getSubmtrancode() {
        return submtrancode;
    }

    public void setSubmtrancode(String submtrancode) {
        this.submtrancode = submtrancode == null ? null : submtrancode.trim();
    }

    public String getLooktrancode() {
        return looktrancode;
    }

    public void setLooktrancode(String looktrancode) {
        this.looktrancode = looktrancode == null ? null : looktrancode.trim();
    }

    public Long getTaskser() {
        return taskser;
    }

    public void setTaskser(Long taskser) {
        this.taskser = taskser;
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

    public String getUnitwfid() {
        return unitwfid;
    }

    public void setUnitwfid(String unitwfid) {
        this.unitwfid = unitwfid == null ? null : unitwfid.trim();
    }

    public String getInstancylevel() {
        return instancylevel;
    }

    public void setInstancylevel(String instancylevel) {
        this.instancylevel = instancylevel == null ? null : instancylevel.trim();
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

    public String getBusioperatestyle() {
        return busioperatestyle;
    }

    public void setBusioperatestyle(String busioperatestyle) {
        this.busioperatestyle = busioperatestyle == null ? null : busioperatestyle.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}