package com.evo.mitzoom.Model.Request;

import com.google.gson.annotations.SerializedName;

public class dataSession {

    @SerializedName("name")
    private String nameSession;

    @SerializedName("pass")
    private String pass;

    public String getNameSession() {
        return nameSession;
    }

    public void setNameSession(String nameSession) {
        this.nameSession = nameSession;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
