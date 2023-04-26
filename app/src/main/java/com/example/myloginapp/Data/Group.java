package com.example.myloginapp.Data;

public class Group {

    private int orgID;

    private String orgname, rolename;

    public Group(int orgID, String orgname, String rolename) {
        this.orgID = orgID;
        this.orgname = orgname;
        this.rolename = rolename;
    }

    public int getOrgID() {
        return orgID;
    }

    public String getOrgname() {
        return orgname;
    }

    public String getRolename() { return rolename; }
}
