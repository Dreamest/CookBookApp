package com.dreamest.cookbookapp.logic;

import android.util.Log;

import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class User {
    private String userID;
    private ArrayList<String> myRecipes;
    private ArrayList<String> myFriends;
    private ArrayList<String> myChats;
    private String displayName;
    private String phoneNumber;
    private StorageReference profileImage;

    public User(){
        myRecipes = new ArrayList<>();
        myFriends = new ArrayList<>();
        myChats = new ArrayList<>();
    }

    public StorageReference getProfileImage() {
        return profileImage;
    }

    public User setProfileImage(StorageReference profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public static void addRecipeToCurrentUserDatabase(String recipeID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.KEYS.MY_RECIPES)
                .child(recipeID);
        ref.setValue(recipeID);
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

    public void updateFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(this.getUserID());
        ref.setValue(this);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(this.displayName)
                .build();
        firebaseUser.updateProfile(profileUpdates);
        firebaseAuth.updateCurrentUser(firebaseUser);
    }
}
