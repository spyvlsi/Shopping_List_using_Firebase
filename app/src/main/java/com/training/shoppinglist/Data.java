package com.training.shoppinglist;

public class Data {

    String type;
    String amount;
    String note;

    public Data() {
    }

    public Data(String type, String amount, String note) {
        this.type = type;
        this.amount = amount;
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
