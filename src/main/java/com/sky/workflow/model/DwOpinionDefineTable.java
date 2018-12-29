package com.sky.workflow.model;

import java.util.Date;

public class DwOpinionDefineTable {
    private Integer opinid;

    private String flowid;

    private String nodeid;

    private String opincode;

    private String opindesc;

    private Date lastchangetime;

    public Integer getOpinid() {
        return opinid;
    }

    public void setOpinid(Integer opinid) {
        this.opinid = opinid;
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid == null ? null : flowid.trim();
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid == null ? null : nodeid.trim();
    }

    public String getOpincode() {
        return opincode;
    }

    public void setOpincode(String opincode) {
        this.opincode = opincode == null ? null : opincode.trim();
    }

    public String getOpindesc() {
        return opindesc;
    }

    public void setOpindesc(String opindesc) {
        this.opindesc = opindesc == null ? null : opindesc.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}