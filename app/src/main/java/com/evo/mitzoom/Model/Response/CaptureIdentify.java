package com.evo.mitzoom.Model.Response;

import com.evo.mitzoom.Model.Request.dataSession;
import com.google.gson.annotations.SerializedName;

public class CaptureIdentify {

    @SerializedName("err_code")
    private int err_code;

    @SerializedName("similarity")
    private double similarity;

    @SerializedName("customer")
    private boolean customer;

    @SerializedName("idDips")
    private String idDips;

    @SerializedName("name")
    private String name;

    @SerializedName("session")
    private dataSession dataSession;

    public com.evo.mitzoom.Model.Request.dataSession getDataSession() {
        return dataSession;
    }

    public void setDataSession(com.evo.mitzoom.Model.Request.dataSession dataSession) {
        this.dataSession = dataSession;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public boolean isCustomer() {
        return customer;
    }

    public void setCustomer(boolean customer) {
        this.customer = customer;
    }

    public String getIdDips() {
        return idDips;
    }

    public void setIdDips(String idDips) {
        this.idDips = idDips;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
