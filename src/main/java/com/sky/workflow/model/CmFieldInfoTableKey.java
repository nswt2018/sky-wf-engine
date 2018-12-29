package com.sky.workflow.model;

public class CmFieldInfoTableKey {
    private String fieldtab;

    private String fieldcode;

    public String getFieldtab() {
        return fieldtab;
    }

    public void setFieldtab(String fieldtab) {
        this.fieldtab = fieldtab == null ? null : fieldtab.trim();
    }

    public String getFieldcode() {
        return fieldcode;
    }

    public void setFieldcode(String fieldcode) {
        this.fieldcode = fieldcode == null ? null : fieldcode.trim();
    }
}