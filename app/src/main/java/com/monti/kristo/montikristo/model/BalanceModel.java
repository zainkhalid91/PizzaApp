package com.monti.kristo.montikristo.model;

import java.io.Serializable;

public class BalanceModel implements Serializable {
    double subTotal;
    double delieveryFee;
    double grandTotal;

    public BalanceModel() {
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDelieveryFee() {
        return delieveryFee;
    }

    public void setDelieveryFee(double delieveryFee) {
        this.delieveryFee = delieveryFee;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}
