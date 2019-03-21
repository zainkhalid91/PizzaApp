package com.monti.kristo.montikristo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PushNotificatonsModel implements Serializable {

    @SerializedName("status")
    @Expose
    boolean status;

    @SerializedName("msg")
    @Expose
    String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
