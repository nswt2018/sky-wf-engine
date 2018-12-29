package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwTaskHisTable")
@Table(name = "DwTaskHis")
public class DwTaskHisTable extends DwTaskHisTableKey {
    private String bankid;

    private String operid;

    private String recetime;

    private String dealtime;

    private String dealsystime;

    private String nodeid;

    private String nodename;

    private String exectrancode;

    private String submtrancode;

    private String looktrancode;

    private String taskdesc;

    private String forenodeid;

    private Date lastchgdate;

    private String lastchgtime;

    private String tasktype;

    private String nodephase;

    private String isallowget;

    private String isallowreturn;

    private Integer taskround;

    private String busioperatestyle;

    private String isprimaryauditnode;

    private Long assignmindealnum;

    private String assignmindealnumstyle;

    private Date lastchangetime;

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

    public String getRecetime() {
        return recetime;
    }

    public void setRecetime(String recetime) {
        this.recetime = recetime == null ? null : recetime.trim();
    }

    public String getDealtime() {
        return dealtime;
    }

    public void setDealtime(String dealtime) {
        this.dealtime = dealtime == null ? null : dealtime.trim();
    }

    public String getDealsystime() {
        return dealsystime;
    }

    public void setDealsystime(String dealsystime) {
        this.dealsystime = dealsystime == null ? null : dealsystime.trim();
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid == null ? null : nodeid.trim();
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename == null ? null : nodename.trim();
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

    public String getTaskdesc() {
        return taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        this.taskdesc = taskdesc == null ? null : taskdesc.trim();
    }

    public String getForenodeid() {
        return forenodeid;
    }

    public void setForenodeid(String forenodeid) {
        this.forenodeid = forenodeid == null ? null : forenodeid.trim();
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

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype == null ? null : tasktype.trim();
    }

    public String getNodephase() {
        return nodephase;
    }

    public void setNodephase(String nodephase) {
        this.nodephase = nodephase == null ? null : nodephase.trim();
    }

    public String getIsallowget() {
        return isallowget;
    }

    public void setIsallowget(String isallowget) {
        this.isallowget = isallowget == null ? null : isallowget.trim();
    }

    public String getIsallowreturn() {
        return isallowreturn;
    }

    public void setIsallowreturn(String isallowreturn) {
        this.isallowreturn = isallowreturn == null ? null : isallowreturn.trim();
    }

    public Integer getTaskround() {
        return taskround;
    }

    public void setTaskround(Integer taskround) {
        this.taskround = taskround;
    }

    public String getBusioperatestyle() {
        return busioperatestyle;
    }

    public void setBusioperatestyle(String busioperatestyle) {
        this.busioperatestyle = busioperatestyle == null ? null : busioperatestyle.trim();
    }

    public String getIsprimaryauditnode() {
        return isprimaryauditnode;
    }

    public void setIsprimaryauditnode(String isprimaryauditnode) {
        this.isprimaryauditnode = isprimaryauditnode == null ? null : isprimaryauditnode.trim();
    }

    public Long getAssignmindealnum() {
        return assignmindealnum;
    }

    public void setAssignmindealnum(Long assignmindealnum) {
        this.assignmindealnum = assignmindealnum;
    }

    public String getAssignmindealnumstyle() {
        return assignmindealnumstyle;
    }

    public void setAssignmindealnumstyle(String assignmindealnumstyle) {
        this.assignmindealnumstyle = assignmindealnumstyle == null ? null : assignmindealnumstyle.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}