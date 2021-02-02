package com.dreamest.cookbookapp.utility;

import com.dreamest.cookbookapp.R;

import java.util.Random;

public class UtilityPack {
    private static int[] allBackgrounds = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4};


    public static int randomBackground() {
        return allBackgrounds[new Random().nextInt(4)];
    }

    public interface KEYS {
        String USERS = "users";
        String RECIPES = "recipes";
        String MY_RECIPES = "myRecipes";
        String MY_FRIENDS = "myFriends";
        String MY_CHATS = "myChats";
        String PHONE_NUMBER = "phoneNumber";
        String DISPLAY_NAME = "displayName";
        String USER_ID = "userID";
        String PROFILE_IMAGE = "profileImage";
    }
}
