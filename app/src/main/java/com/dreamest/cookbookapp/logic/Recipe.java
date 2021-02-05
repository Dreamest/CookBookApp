package com.dreamest.cookbookapp.logic;

import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Recipe {
    private String title;
    private String recipeID;
//    private String owner;
    private String ownerID;
    private String date;
    private StorageReference image; // TODO: 1/27/21 change format? [Storage Referensce?]
    private ArrayList<Ingredient> ingredients;
    private String method;
    private int prepTime;
    private int difficulty;

    public Recipe() {
        ingredients = new ArrayList<>();
        recipeID = "";
        difficulty = 1;
        prepTime = 5;
    }

    public static String CreateRecipeID(String uid) {
        return uid + System.currentTimeMillis();
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

    public void storeInFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.RECIPES).child(this.getRecipeID());
        ref.setValue(this);
    }

}
