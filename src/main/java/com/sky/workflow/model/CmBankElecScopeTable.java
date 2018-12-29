package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("CmBankElecScopeTable")
@Table(name = "CmBankElecScope")
public class CmBankElecScopeTable extends CmBankElecScopeTableKey {
    private String prodname;

    private String flowname;

    private String iselecapprflag;

    private String flowbindstyle;

    private Date lastchangetime;

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname == null ? null : prodname.trim();
    }

    public String getFlowname() {
        return flowname;
    }

    public void setFlowname(String flowname) {
        this.flowname = flowname == null ? null : flowname.trim();
    }

    public String getIselecapprflag() {
        return iselecapprflag;
    }

    public void setIselecapprflag(String iselecapprflag) {
        this.iselecapprflag = iselecapprflag == null ? null : iselecapprflag.trim();
    }

    public String getFlowbindstyle() {
        return flowbindstyle;
    }

    public void setFlowbindstyle(String flowbindstyle) {
        this.flowbindstyle = flowbindstyle == null ? null : flowbindstyle.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}