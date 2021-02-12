package com.dreamest.cookbookapp.logic;

import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Recipe {
    private String title;
    private String recipeID;
    private String ownerID;
    private String date;
    private String imagePath;
    private ArrayList<Ingredient> ingredients;
    private String method;
    private int prepTime;
    private int difficulty;

    public Recipe() {
        ingredients = new ArrayList<>();
        recipeID = "";
        difficulty = 1;
        prepTime = 5;
        imagePath = "";
    }

    public String getOwnerID() {
        return ownerID;
    }

    public Recipe setOwnerID(String ownerID) {
        this.ownerID = ownerID;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Recipe setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public Recipe setRecipeID(String recipeID) {
        this.recipeID = recipeID;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Recipe setDate(String date) {
        this.date = date;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Recipe setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Recipe setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Recipe setMethod(String method) {
        this.method = method;
        return this;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public Recipe setPrepTime(int prepTime) {
        this.prepTime = Math.abs(prepTime);
        return this;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Recipe setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        return this;
    }

}
