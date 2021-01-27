package com.dreamest.cookbookapp.logic;

import java.util.ArrayList;

public class User {
    private String userID;
    private ArrayList<String> myRecipes;
    private ArrayList<String> myFriends;
    private ArrayList<String> myChats;
    private String name;
    private String phoneNumber;

    public User(){}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getMyRecipes() {
        return myRecipes;
    }

    public void setMyRecipes(ArrayList<String> myRecipes) {
        this.myRecipes = myRecipes;
    }

    public ArrayList<String> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(ArrayList<String> myFriends) {
        this.myFriends = myFriends;
    }

    public ArrayList<String> getMyChats() {
        return myChats;
    }

    public void setMyChats(ArrayList<String> myChats) {
        this.myChats = myChats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
