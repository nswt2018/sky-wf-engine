package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("CmFieldInfoTable")
@Table(name = "CmFieldInfo")
public class CmFieldInfoTable extends CmFieldInfoTableKey {
    private String fieldname;

    private String fielddescription;

    private String issubjuseflag;

    private String getsql;

    private Date lastchgdate;

    private String lastchgtime;

    private String infotype;

    private Date lastchangetime;

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname == null ? null : fieldname.trim();
    }

    public String getFielddescription() {
        return fielddescription;
    }

    public void setFielddescription(String fielddescription) {
        this.fielddescription = fielddescription == null ? null : fielddescription.trim();
    }

    public String getIssubjuseflag() {
        return issubjuseflag;
    }

    public void setIssubjuseflag(String issubjuseflag) {
        this.issubjuseflag = issubjuseflag == null ? null : issubjuseflag.trim();
    }

    public String getGetsql() {
        return getsql;
    }

    public void setGetsql(String getsql) {
        this.getsql = getsql == null ? null : getsql.trim();
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

    public String getInfotype() {
        return infotype;
    }

    public void setInfotype(String infotype) {
        this.infotype = infotype == null ? null : infotype.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}