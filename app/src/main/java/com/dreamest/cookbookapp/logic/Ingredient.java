package com.dreamest.cookbookapp.logic;

public class Ingredient {
    private Float units;
    private String measurement;
    private String description;
    public Ingredient(){}

    public Ingredient(Float units, String measurement, String description) {
        this.units = units;
        this.measurement = measurement;
        this.description = description;
    }

    public Float getUnits() {
        return units;
    }

    public void setUnits(Float units) {
        this.units = units;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
