package com.sky.workflow.model;

import java.util.Date;

import javax.persistence.Table;

import org.apache.ibatis.type.Alias;

@Alias("DwFlowRouteTable")
@Table(name = "DwFlowRoute")
public class DwFlowRouteTable extends DwFlowRouteTableKey {
    private String routedesc;

    private String routecond;

    private String nextnodeid;

    private String expfirst1;

    private Long expval1;

    private String expsecond1;

    private String expfield1;

    private String expconst1;

    private String expfirst2;

    private Long expval2;

    private String expsecond2;

    private String expfield2;

    private String expfirst3;

    private Long expval3;

    private String expsecond3;

    private String expfield3;

    private String expconst3;

    private String expfirst4;

    private Long expval4;

    private String expsecond4;

    private String expfield4;

    private String expconst4;

    private String expconst2;

    private Date lastchgdate;

    private String lastchgtime;

    private String expconsttype1;

    private String expconsttype2;

    private String expconsttype3;

    private String expconsttype4;

    private String routetype;

    private String taskassignstyle;

    private Date lastchangetime;

    public String getRoutedesc() {
        return routedesc;
    }

    public void setRoutedesc(String routedesc) {
        this.routedesc = routedesc == null ? null : routedesc.trim();
    }

    public String getRoutecond() {
        return routecond;
    }

    public void setRoutecond(String routecond) {
        this.routecond = routecond == null ? null : routecond.trim();
    }

    public String getNextnodeid() {
        return nextnodeid;
    }

    public void setNextnodeid(String nextnodeid) {
        this.nextnodeid = nextnodeid == null ? null : nextnodeid.trim();
    }

    public String getExpfirst1() {
        return expfirst1;
    }

    public void setExpfirst1(String expfirst1) {
        this.expfirst1 = expfirst1 == null ? null : expfirst1.trim();
    }

    public Long getExpval1() {
        return expval1;
    }

    public void setExpval1(Long expval1) {
        this.expval1 = expval1;
    }

    public String getExpsecond1() {
        return expsecond1;
    }

    public void setExpsecond1(String expsecond1) {
        this.expsecond1 = expsecond1 == null ? null : expsecond1.trim();
    }

    public String getExpfield1() {
        return expfield1;
    }

    public void setExpfield1(String expfield1) {
        this.expfield1 = expfield1 == null ? null : expfield1.trim();
    }

    public String getExpconst1() {
        return expconst1;
    }

    public void setExpconst1(String expconst1) {
        this.expconst1 = expconst1 == null ? null : expconst1.trim();
    }

    public String getExpfirst2() {
        return expfirst2;
    }

    public void setExpfirst2(String expfirst2) {
        this.expfirst2 = expfirst2 == null ? null : expfirst2.trim();
    }

    public Long getExpval2() {
        return expval2;
    }

    public void setExpval2(Long expval2) {
        this.expval2 = expval2;
    }

    public String getExpsecond2() {
        return expsecond2;
    }

    public void setExpsecond2(String expsecond2) {
        this.expsecond2 = expsecond2 == null ? null : expsecond2.trim();
    }

    public String getExpfield2() {
        return expfield2;
    }

    public void setExpfield2(String expfield2) {
        this.expfield2 = expfield2 == null ? null : expfield2.trim();
    }

    public String getExpfirst3() {
        return expfirst3;
    }

    public void setExpfirst3(String expfirst3) {
        this.expfirst3 = expfirst3 == null ? null : expfirst3.trim();
    }

    public Long getExpval3() {
        return expval3;
    }

    public void setExpval3(Long expval3) {
        this.expval3 = expval3;
    }

    public String getExpsecond3() {
        return expsecond3;
    }

    public void setExpsecond3(String expsecond3) {
        this.expsecond3 = expsecond3 == null ? null : expsecond3.trim();
    }

    public String getExpfield3() {
        return expfield3;
    }

    public void setExpfield3(String expfield3) {
        this.expfield3 = expfield3 == null ? null : expfield3.trim();
    }

    public String getExpconst3() {
        return expconst3;
    }

    public void setExpconst3(String expconst3) {
        this.expconst3 = expconst3 == null ? null : expconst3.trim();
    }

    public String getExpfirst4() {
        return expfirst4;
    }

    public void setExpfirst4(String expfirst4) {
        this.expfirst4 = expfirst4 == null ? null : expfirst4.trim();
    }

    public Long getExpval4() {
        return expval4;
    }

    public void setExpval4(Long expval4) {
        this.expval4 = expval4;
    }

    public String getExpsecond4() {
        return expsecond4;
    }

    public void setExpsecond4(String expsecond4) {
        this.expsecond4 = expsecond4 == null ? null : expsecond4.trim();
    }

    public String getExpfield4() {
        return expfield4;
    }

    public void setExpfield4(String expfield4) {
        this.expfield4 = expfield4 == null ? null : expfield4.trim();
    }

    public String getExpconst4() {
        return expconst4;
    }

    public void setExpconst4(String expconst4) {
        this.expconst4 = expconst4 == null ? null : expconst4.trim();
    }

    public String getExpconst2() {
        return expconst2;
    }

    public void setExpconst2(String expconst2) {
        this.expconst2 = expconst2 == null ? null : expconst2.trim();
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

    public String getExpconsttype1() {
        return expconsttype1;
    }

    public void setExpconsttype1(String expconsttype1) {
        this.expconsttype1 = expconsttype1 == null ? null : expconsttype1.trim();
    }

    public String getExpconsttype2() {
        return expconsttype2;
    }

    public void setExpconsttype2(String expconsttype2) {
        this.expconsttype2 = expconsttype2 == null ? null : expconsttype2.trim();
    }

    public String getExpconsttype3() {
        return expconsttype3;
    }

    public void setExpconsttype3(String expconsttype3) {
        this.expconsttype3 = expconsttype3 == null ? null : expconsttype3.trim();
    }

    public String getExpconsttype4() {
        return expconsttype4;
    }

    public void setExpconsttype4(String expconsttype4) {
        this.expconsttype4 = expconsttype4 == null ? null : expconsttype4.trim();
    }

    public String getRoutetype() {
        return routetype;
    }

    public void setRoutetype(String routetype) {
        this.routetype = routetype == null ? null : routetype.trim();
    }

    public String getTaskassignstyle() {
        return taskassignstyle;
    }

    public void setTaskassignstyle(String taskassignstyle) {
        this.taskassignstyle = taskassignstyle == null ? null : taskassignstyle.trim();
    }

    public Date getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(Date lastchangetime) {
        this.lastchangetime = lastchangetime;
    }
}