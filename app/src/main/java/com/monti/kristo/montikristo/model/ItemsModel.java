package com.monti.kristo.montikristo.model;

import java.io.Serializable;

public class ItemsModel implements Serializable {

    private int productId;
    private String name;
    private int price;
    private String picurl;
    private int productDiscount;
    private int Deliverfee;
    private int quantity;
    private double subTotal = 0;
    private String Type;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return productId;
    }

    public void setId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getThumbnail() {
        return picurl;
    }

    public void setThumbnail(String picurl) {
        this.picurl = picurl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getDeliverfee() {
        return Deliverfee;
    }

    public void setDeliverfee(int deliverfee) {
        Deliverfee = deliverfee;
    }
}
