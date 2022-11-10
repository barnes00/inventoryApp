package com.example.larabarnesinventoryapp;

public class itemData {
    private long id;
    private String itemName;
    private int itemQty;

    public itemData(long id, String itemName, int itemQty){
        this.id = id;
        this.itemName = itemName;
        this.itemQty = itemQty;
    }

    public long getId(){return id;}

    public String getItemName() {
        return itemName;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }
}
