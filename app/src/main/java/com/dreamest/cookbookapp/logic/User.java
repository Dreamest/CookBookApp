package com.dreamest.cookbookapp.logic;

import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class User {
    private String userID;
    private HashMap<String, String> myRecipes;
    private HashMap<String, String> myFriends;
    private HashMap<String, Long> myChats;
    private HashMap<String, String> pendingRecipes;
    private HashMap<String, String> pendingFriends;
    private String displayName;
    private String phoneNumber;
    private String imagePath;

    public User(){
        myRecipes = new HashMap<>();
        myFriends = new HashMap<>();
        myChats = new HashMap<>();
        pendingRecipes = new HashMap<>();
        pendingFriends = new HashMap<>();
        imagePath = "";
    }

    public String getImagePath() {
        return imagePath;
    }

    public User setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public User setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public HashMap<String, String> getMyRecipes() {
        return myRecipes;
    }

    public User setMyRecipes(HashMap<String, String> myRecipes) {
        this.myRecipes = myRecipes;
        return this;
    }

    public HashMap<String, String> getMyFriends() {
        return myFriends;
    }

    public User setMyFriends(HashMap<String, String> myFriends) {
        this.myFriends = myFriends;
        return this;
    }

    public HashMap<String, Long> getMyChats() {
        return myChats;
    }

    public User setMyChats(HashMap<String, Long> myChats) {
        this.myChats = myChats;
        return this;
    }

    public HashMap<String, String> getPendingRecipes() {
        return pendingRecipes;
    }

    public User setPendingRecipes(HashMap<String, String> pendingRecipes) {
        this.pendingRecipes = pendingRecipes;
        return this;
    }

    public HashMap<String, String> getPendingFriends() {
        return pendingFriends;
    }

    public User setPendingFriends(HashMap<String, String> pendingFriends) {
        this.pendingFriends = pendingFriends;
        return this;
    }
}
