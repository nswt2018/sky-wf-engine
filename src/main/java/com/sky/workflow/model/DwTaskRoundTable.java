package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwTaskRoundTable")
@Table(name = "DwTaskRound")
public class DwTaskRoundTable {
    private String wfid;

    private Integer taskround;

    private Date lastchangetime;

    public String getWfid() {
        return wfid;
    }

    public void setWfid(String wfid) {
        this.wfid = wfid == null ? null : wfid.trim();
    }

    public Integer getTaskround() {
        return taskround;
    }

    public void setTaskround(Integer taskround) {
        this.taskround = taskround;
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}