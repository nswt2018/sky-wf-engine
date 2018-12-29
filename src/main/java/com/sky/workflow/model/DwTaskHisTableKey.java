package com.sky.workflow.model;

public class DwTaskHisTableKey {
    private String wfid;

    private Long taskser;

    public String getWfid() {
        return wfid;
    }

    public void setWfid(String wfid) {
        this.wfid = wfid == null ? null : wfid.trim();
    }

    public Long getTaskser() {
        return taskser;
    }

    public void setTaskser(Long taskser) {
        this.taskser = taskser;
    }
}