package com.monti.kristo.montikristo.model;

public class GrandTotalModel {

    double total = 0;
    double subtotal = 0;
    int deliveryfee = 0;

    public GrandTotalModel() {
    }

    public GrandTotalModel(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(int deliveryfee) {
        this.deliveryfee = deliveryfee;
    }
}
