package com.monti.kristo.montikristo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusCartModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("arr")
    @Expose
    private CartItemsModel[] getAllCartItems;

    public StatusCartModel() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CartItemsModel[] getGetAllCartItems() {
        return getAllCartItems;
    }
}
