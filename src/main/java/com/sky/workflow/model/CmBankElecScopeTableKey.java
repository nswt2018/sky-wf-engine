package com.sky.workflow.model;

public class CmBankElecScopeTableKey {
    private String bankid;

    private String prodid;

    private String flowtype;

    private String flowid;

    private String submtrancode;

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid == null ? null : bankid.trim();
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid == null ? null : prodid.trim();
    }

    public String getFlowtype() {
        return flowtype;
    }

    public void setFlowtype(String flowtype) {
        this.flowtype = flowtype == null ? null : flowtype.trim();
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid == null ? null : flowid.trim();
    }

    public String getSubmtrancode() {
        return submtrancode;
    }

    public void setSubmtrancode(String submtrancode) {
        this.submtrancode = submtrancode == null ? null : submtrancode.trim();
    }
}