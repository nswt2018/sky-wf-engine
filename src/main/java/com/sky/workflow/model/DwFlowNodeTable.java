package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwFlowNodeTable")
@Table(name = "DwFlowNode")
public class DwFlowNodeTable extends DwFlowNodeTableKey {
    private String nodename;

    private String nodedesc;

    private String nodetype;

    private String isunit;

    private String unitflowid;

    private Long mindealnum;

    private String autodisuserflag;

    private String taskoverpolicy;

    private Long assignminnum;

    private Long overpercent;

    private String selectoperflag;

    private String exectrancode;

    private String submtrancode;

    private String looktrancode;

    private Long tranterm;

    private String nextnodemode;

    private String nextnode;

    private String processmode;

    private String hisflag;

    private Date lastchgdate;

    private String lastchgtime;

    private String nodephase;

    private String isallowget;

    private String isallowreturn;

    private String isprimaryauditnode;

    private String assignmindealnumstyle;

    private String isdrivemessage;

    private String messageid;

    private Date lastchangetime;

    private String retutrancode;

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename == null ? null : nodename.trim();
    }

    public String getNodedesc() {
        return nodedesc;
    }

    public void setNodedesc(String nodedesc) {
        this.nodedesc = nodedesc == null ? null : nodedesc.trim();
    }

    public String getNodetype() {
        return nodetype;
    }

    public void setNodetype(String nodetype) {
        this.nodetype = nodetype == null ? null : nodetype.trim();
    }

    public String getIsunit() {
        return isunit;
    }

    public void setIsunit(String isunit) {
        this.isunit = isunit == null ? null : isunit.trim();
    }

    public String getUnitflowid() {
        return unitflowid;
    }

    public void setUnitflowid(String unitflowid) {
        this.unitflowid = unitflowid == null ? null : unitflowid.trim();
    }

    public Long getMindealnum() {
        return mindealnum;
    }

    public void setMindealnum(Long mindealnum) {
        this.mindealnum = mindealnum;
    }

    public String getAutodisuserflag() {
        return autodisuserflag;
    }

    public void setAutodisuserflag(String autodisuserflag) {
        this.autodisuserflag = autodisuserflag == null ? null : autodisuserflag.trim();
    }

    public String getTaskoverpolicy() {
        return taskoverpolicy;
    }

    public void setTaskoverpolicy(String taskoverpolicy) {
        this.taskoverpolicy = taskoverpolicy == null ? null : taskoverpolicy.trim();
    }

    public Long getAssignminnum() {
        return assignminnum;
    }

    public void setAssignminnum(Long assignminnum) {
        this.assignminnum = assignminnum;
    }

    public Long getOverpercent() {
        return overpercent;
    }

    public void setOverpercent(Long overpercent) {
        this.overpercent = overpercent;
    }

    public String getSelectoperflag() {
        return selectoperflag;
    }

    public void setSelectoperflag(String selectoperflag) {
        this.selectoperflag = selectoperflag == null ? null : selectoperflag.trim();
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

    public Long getTranterm() {
        return tranterm;
    }

    public void setTranterm(Long tranterm) {
        this.tranterm = tranterm;
    }

    public String getNextnodemode() {
        return nextnodemode;
    }

    public void setNextnodemode(String nextnodemode) {
        this.nextnodemode = nextnodemode == null ? null : nextnodemode.trim();
    }

    public String getNextnode() {
        return nextnode;
    }

    public void setNextnode(String nextnode) {
        this.nextnode = nextnode == null ? null : nextnode.trim();
    }

    public String getProcessmode() {
        return processmode;
    }

    public void setProcessmode(String processmode) {
        this.processmode = processmode == null ? null : processmode.trim();
    }

    public String getHisflag() {
        return hisflag;
    }

    public void setHisflag(String hisflag) {
        this.hisflag = hisflag == null ? null : hisflag.trim();
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

    public String getIsprimaryauditnode() {
        return isprimaryauditnode;
    }

    public void setIsprimaryauditnode(String isprimaryauditnode) {
        this.isprimaryauditnode = isprimaryauditnode == null ? null : isprimaryauditnode.trim();
    }

    public String getAssignmindealnumstyle() {
        return assignmindealnumstyle;
    }

    public void setAssignmindealnumstyle(String assignmindealnumstyle) {
        this.assignmindealnumstyle = assignmindealnumstyle == null ? null : assignmindealnumstyle.trim();
    }

    public String getIsdrivemessage() {
        return isdrivemessage;
    }

    public void setIsdrivemessage(String isdrivemessage) {
        this.isdrivemessage = isdrivemessage == null ? null : isdrivemessage.trim();
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid == null ? null : messageid.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }

    public String getRetutrancode() {
        return retutrancode;
    }

    public void setRetutrancode(String retutrancode) {
        this.retutrancode = retutrancode == null ? null : retutrancode.trim();
    }
}