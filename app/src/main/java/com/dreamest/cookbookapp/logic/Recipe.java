package com.dreamest.cookbookapp.logic;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Recipe {
    private String title;
    private String recipeID;
    private String owner;
    private String date;
    private StorageReference image; // TODO: 1/27/21 change format? [Storage Referensce?]
    private ArrayList<Ingredient> ingredients;
    private String method;
    private int prepTime;
    private int difficulty;

    public Recipe() {
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

    public String getOwner() {
        return owner;
    }

    public Recipe setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Recipe setDate(String date) {
        this.date = date;
        return this;
    }

    public StorageReference getImage() {
        return image;
    }

    public Recipe setImage(StorageReference image) {
        this.image = image;
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
