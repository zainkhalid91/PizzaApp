package com.monti.kristo.montikristo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginUserModel {

    @SerializedName("Name")
    @Expose
    private String fname = "";
    @SerializedName("Email")
    @Expose
    private String email = "";
    @SerializedName("contact")
    @Expose
    private String pNum = "";
    @SerializedName("address")
    @Expose
    private String address = "";
    @SerializedName("id")
    @Expose
    private Integer id = 0;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getpNum() {
        return pNum;
    }

    public void setpNum(String pNum) {
        this.pNum = pNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
