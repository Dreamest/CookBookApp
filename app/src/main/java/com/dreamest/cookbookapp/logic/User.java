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
    private ArrayList<String> pendingRecipes;
    private ArrayList<String> pendingFriends;
    private String displayName;
    private String phoneNumber;
    private StorageReference profileImage;

    public User(){
        myRecipes = new ArrayList<>();
        myFriends = new ArrayList<>();
        myChats = new ArrayList<>();
        pendingRecipes = new ArrayList<>();
        pendingFriends = new ArrayList<>();
    }



    public ArrayList<String> getPendingRecipes() {
        return pendingRecipes;
    }

    public User setPendingRecipes(ArrayList<String> pendingRecipes) {
        this.pendingRecipes = pendingRecipes;
        return this;
    }

    public ArrayList<String> getPendingFriends() {
        return pendingFriends;
    }

    public User setPendingFriends(ArrayList<String> pendingFriends) {
        this.pendingFriends = pendingFriends;
        return this;
    }

    public StorageReference getProfileImage() {
        return profileImage;
    }

    public User setProfileImage(StorageReference profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public static void addToCurrentUserDatabase(String id, int position, String key) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(key)
                .child(String.valueOf(position));
        ref.setValue(id);
    }

    public static void removePendingFromCurrentUser(int position, String pendingType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(pendingType)
                .child(String.valueOf(position));
        ref.removeValue();
    }


    public static void addFriendToCurrentUserDatabase(String friendID, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.KEYS.MY_FRIENDS)
                .child(String.valueOf(position));
        ref.setValue(friendID);
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
