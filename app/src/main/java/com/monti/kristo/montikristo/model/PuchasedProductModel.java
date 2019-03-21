package com.monti.kristo.montikristo.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PuchasedProductModel implements Serializable {

    BalanceModel balanceModel;
    ArrayList<ItemsModel> itemsModels;


    public BalanceModel getBalanceModel() {
        return balanceModel;
    }

    public void setBalanceModel(BalanceModel balanceModel) {
        this.balanceModel = balanceModel;
    }

    public ArrayList<ItemsModel> getCartItemsModels() {
        return itemsModels;
    }

    public void setCartItemsModels(ArrayList<ItemsModel> cartItemsModels) {
        this.itemsModels = cartItemsModels;
    }
}
