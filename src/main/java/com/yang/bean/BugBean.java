package com.yang.bean;

/**
 * Created by
 * yangshuang on 2020/1/6.
 */
public class BugBean {

    private int id;
    private String name;
    private int level;
    private String state = "NULL";
    private String status = "NULL";
    private String creator = "NULL";
    private String appoint = "NULL";
    private String fix = "NULL";

    public BugBean() {
    }

    public BugBean(int id, String name, int level, String state, String status, String creator, String appoint, String fix) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.state = state;
        this.status = status;
        this.creator = creator;
        this.appoint = appoint;
        this.fix = fix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAppoint() {
        return appoint;
    }

    public void setAppoint(String appoint) {
        this.appoint = appoint;
    }

    public String getFix() {
        return fix;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    public String toPrintString() {
        return "id : " + id
                + " name : " + name
                + " level : " + level
                + " state : " + state
                + " status : " + status
                + " creator : " + creator
                + " appoint : " + appoint
                + " fix : " + fix;
    }
}
