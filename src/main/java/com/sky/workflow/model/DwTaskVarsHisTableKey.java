package com.sky.workflow.model;

public class DwTaskVarsHisTableKey {
    private String wfid;

    private Long taskser;

    private String varname;

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

    public String getVarname() {
        return varname;
    }

    public void setVarname(String varname) {
        this.varname = varname == null ? null : varname.trim();
    }
}