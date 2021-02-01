package com.dreamest.cookbookapp.logic;

import java.util.ArrayList;

public class User {
    private String userID;
    private ArrayList<String> myRecipes;
    private ArrayList<String> myFriends;
    private ArrayList<String> myChats;
    private String displayName;
    private String phoneNumber;
//    private UNKNOWN userPhoto; todo: Figure out how to save user image

    public User(){
        myRecipes = new ArrayList<>();
        myFriends = new ArrayList<>();
        myChats = new ArrayList<>();
    }

    public String getUserID() {
        return userID;
    }

    public User setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public ArrayList<String> getMyRecipes() {
        return myRecipes;
    }

    public User setMyRecipes(ArrayList<String> myRecipes) {
        this.myRecipes = myRecipes;
        return this;
    }

    public ArrayList<String> getMyFriends() {
        return myFriends;
    }

    public User setMyFriends(ArrayList<String> myFriends) {
        this.myFriends = myFriends;
        return this;
    }

    public ArrayList<String> getMyChats() {
        return myChats;
    }

    public User setMyChats(ArrayList<String> myChats) {
        this.myChats = myChats;
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
}
