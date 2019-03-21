package com.monti.kristo.montikristo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusTokenModel {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("token")
    @Expose
    private String token;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}