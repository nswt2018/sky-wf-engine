package com.sky.workflow.model;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DbBusiWfMapTable")
@Table(name = "DbBusiWfMap")
public class DbBusiWfMapTable {
    private String wfid;

    private String loanid;

    private Long transeq;

    private String custid;

    private String custname;

    private String prodid;

    private String prodname;

    private String busitype;

    private String flowid;

    private String nodename;

    private String curoperid;

    private String curoperidname;

    private String curbankid;

    private String curbankname;

    private String recetime;

    private String approperid;

    private String appropername;

    private String apprbankid;

    private String apprbankname;

    private String apprdate;

    private String approperlev;

    private String isfinish;

    private String isprimaryauditnode;

    public String getWfid() {
        return wfid;
    }

    public void setWfid(String wfid) {
        this.wfid = wfid == null ? null : wfid.trim();
    }

    public String getLoanid() {
        return loanid;
    }

    public void setLoanid(String loanid) {
        this.loanid = loanid == null ? null : loanid.trim();
    }

    public Long getTranseq() {
        return transeq;
    }

    public void setTranseq(Long transeq) {
        this.transeq = transeq;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid == null ? null : custid.trim();
    }

    public String getCustname() {
        return custname;
    }

    public void setCustname(String custname) {
        this.custname = custname == null ? null : custname.trim();
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid == null ? null : prodid.trim();
    }

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname == null ? null : prodname.trim();
    }

    public String getBusitype() {
        return busitype;
    }

    public void setBusitype(String busitype) {
        this.busitype = busitype == null ? null : busitype.trim();
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid == null ? null : flowid.trim();
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename == null ? null : nodename.trim();
    }

    public String getCuroperid() {
        return curoperid;
    }

    public void setCuroperid(String curoperid) {
        this.curoperid = curoperid == null ? null : curoperid.trim();
    }

    public String getCuroperidname() {
        return curoperidname;
    }

    public void setCuroperidname(String curoperidname) {
        this.curoperidname = curoperidname == null ? null : curoperidname.trim();
    }

    public String getCurbankid() {
        return curbankid;
    }

    public void setCurbankid(String curbankid) {
        this.curbankid = curbankid == null ? null : curbankid.trim();
    }

    public String getCurbankname() {
        return curbankname;
    }

    public void setCurbankname(String curbankname) {
        this.curbankname = curbankname == null ? null : curbankname.trim();
    }

    public String getRecetime() {
        return recetime;
    }

    public void setRecetime(String recetime) {
        this.recetime = recetime == null ? null : recetime.trim();
    }

    public String getApproperid() {
        return approperid;
    }

    public void setApproperid(String approperid) {
        this.approperid = approperid == null ? null : approperid.trim();
    }

    public String getAppropername() {
        return appropername;
    }

    public void setAppropername(String appropername) {
        this.appropername = appropername == null ? null : appropername.trim();
    }

    public String getApprbankid() {
        return apprbankid;
    }

    public void setApprbankid(String apprbankid) {
        this.apprbankid = apprbankid == null ? null : apprbankid.trim();
    }

    public String getApprbankname() {
        return apprbankname;
    }

    public void setApprbankname(String apprbankname) {
        this.apprbankname = apprbankname == null ? null : apprbankname.trim();
    }

    public String getApprdate() {
        return apprdate;
    }

    public void setApprdate(String apprdate) {
        this.apprdate = apprdate == null ? null : apprdate.trim();
    }

    public String getApproperlev() {
        return approperlev;
    }

    public void setApproperlev(String approperlev) {
        this.approperlev = approperlev == null ? null : approperlev.trim();
    }

    public String getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(String isfinish) {
        this.isfinish = isfinish == null ? null : isfinish.trim();
    }

    public String getIsprimaryauditnode() {
        return isprimaryauditnode;
    }

    public void setIsprimaryauditnode(String isprimaryauditnode) {
        this.isprimaryauditnode = isprimaryauditnode == null ? null : isprimaryauditnode.trim();
    }
}