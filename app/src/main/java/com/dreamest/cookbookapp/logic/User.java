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
import java.util.HashMap;

public class User {
    private String userID;
    private HashMap<String, String> myRecipes;
    private HashMap<String, String> myFriends;
    private HashMap<String, String> myChats;
    private HashMap<String, String> pendingRecipes;
    private HashMap<String, String> pendingFriends;
    private String displayName;
    private String phoneNumber;
    private StorageReference profileImage;

    public static final int ADD = 1;
    public static final int REMOVE = 2;

    public User(){
        myRecipes = new HashMap<>();
        myFriends = new HashMap<>();
        myChats = new HashMap<>();
        pendingRecipes = new HashMap<>();
        pendingFriends = new HashMap<>();
    }

    public StorageReference getProfileImage() {
        return profileImage;
    }

    public User setProfileImage(StorageReference profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public static void actionToCurrentUserDatabase(int action, String id, String key) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(key)
                .child(id);
        if(action == ADD) {
            ref.setValue(id);
        } else if (action == REMOVE) {
            ref.removeValue();
        }
    }
//    public static void addFriendToCurrentUserDatabase(String friendID, int position) {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
//                .child(firebaseUser.getUid())
//                .child(UtilityPack.KEYS.MY_FRIENDS)
//                .child(String.valueOf(position));
//        ref.setValue(friendID);
//    }

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

    public HashMap<String, String> getMyChats() {
        return myChats;
    }

    public User setMyChats(HashMap<String, String> myChats) {
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
