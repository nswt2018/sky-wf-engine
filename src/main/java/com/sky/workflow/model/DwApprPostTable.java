package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwApprPostTable")
@Table(name = "DwApprPost")
public class DwApprPostTable {
    private String postid;

    private String postname;

    private String postdesc;

    private String mutexpostid;

    private String mutexpostname;

    private Date lastchgdate;

    private String lastchgtime;

    private Date lastchangetime;

    private String orgtype;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid == null ? null : postid.trim();
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname == null ? null : postname.trim();
    }

    public String getPostdesc() {
        return postdesc;
    }

    public void setPostdesc(String postdesc) {
        this.postdesc = postdesc == null ? null : postdesc.trim();
    }

    public String getMutexpostid() {
        return mutexpostid;
    }

    public void setMutexpostid(String mutexpostid) {
        this.mutexpostid = mutexpostid == null ? null : mutexpostid.trim();
    }

    public String getMutexpostname() {
        return mutexpostname;
    }

    public void setMutexpostname(String mutexpostname) {
        this.mutexpostname = mutexpostname == null ? null : mutexpostname.trim();
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

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype == null ? null : orgtype.trim();
    }
}