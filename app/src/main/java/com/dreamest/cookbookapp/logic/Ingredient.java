package com.dreamest.cookbookapp.logic;

public class Ingredient {
    private String units;
    private Float amount;
    private String item;
    public Ingredient(){
    }

    public Ingredient(String units, Float amount, String item) {
        this.units = units;
        this.amount = amount;
        this.item = item;
    }

    public String getUnits() {
        return units;
    }

    public Ingredient setUnits(String units) {
        this.units = units;
        return this;
    }

    public Float getAmount() {
        return amount;
    }

    public Ingredient setAmount(Float amount) {
        this.amount = amount;
        return this;
    }

    public String getItem() {
        return item;
    }

    public Ingredient setItem(String item) {
        this.item = item;
        return this;
    }
}
