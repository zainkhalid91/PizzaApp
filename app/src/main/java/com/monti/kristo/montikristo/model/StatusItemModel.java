package com.monti.kristo.montikristo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusItemModel {

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("data")
    @Expose
    private ItemsModel[] getAllItems;

    public StatusItemModel() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ItemsModel[] getGetAllItems() {
        return getAllItems;
    }
}
