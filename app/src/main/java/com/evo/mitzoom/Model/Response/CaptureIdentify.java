package com.evo.mitzoom.Model.Response;

import com.google.gson.annotations.SerializedName;

public class CaptureIdentify {

    @SerializedName("err_code")
    private int err_code;

    @SerializedName("message")
    private String message;

    @SerializedName("similarity")
    private double similarity;

    @SerializedName("customer")
    private boolean customer;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("name")
    private String name;

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
