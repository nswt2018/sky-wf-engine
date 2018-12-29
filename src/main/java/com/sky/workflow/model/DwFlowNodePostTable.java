package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwFlowNodePostTable")
@Table(name = "DwFlowNodePost")
public class DwFlowNodePostTable extends DwFlowNodePostTableKey {
    private String postbankid;

    private String postauthority;

    private Date lastchgdate;

    private String lastchgtime;

    private String superbankscope;

    private String bindprodid;

    private Date lastchangetime;

    public String getPostbankid() {
        return postbankid;
    }

    public void setPostbankid(String postbankid) {
        this.postbankid = postbankid == null ? null : postbankid.trim();
    }

    public String getPostauthority() {
        return postauthority;
    }

    public void setPostauthority(String postauthority) {
        this.postauthority = postauthority == null ? null : postauthority.trim();
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

    public String getSuperbankscope() {
        return superbankscope;
    }

    public void setSuperbankscope(String superbankscope) {
        this.superbankscope = superbankscope == null ? null : superbankscope.trim();
    }

    public String getBindprodid() {
        return bindprodid;
    }

    public void setBindprodid(String bindprodid) {
        this.bindprodid = bindprodid == null ? null : bindprodid.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}