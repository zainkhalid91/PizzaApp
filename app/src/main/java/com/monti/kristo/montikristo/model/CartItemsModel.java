package com.monti.kristo.montikristo.model;

import java.io.Serializable;

public class CartItemsModel extends ItemsModel implements Serializable {

    private int ProductId;
    private String productName;
    private int productPrice;
    private String time;
    private int quantity;
    private int subTotal;
    private int customerId;
    private int ProductDiscount;

    public CartItemsModel() {

    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public int getCustomerID() {
        return customerId;
    }

    public void setCustomerID(int customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getQty() {
        return quantity;
    }

    public void setQty(int quantity) {
        this.quantity = quantity;
    }

/*
    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public int getSubTotal() {
        return subTotal;
    }
*/

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getProductDiscount() {
        return ProductDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        ProductDiscount = productDiscount;
    }
}
