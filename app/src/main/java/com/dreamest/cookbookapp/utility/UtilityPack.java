package com.dreamest.cookbookapp.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.activities.WelcomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.rilixtech.CountryCodePicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class UtilityPack {
    private static int[] allBackgrounds = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4};


    public static int randomBackground() {
        return allBackgrounds[new Random().nextInt(4)];
    }


    /**
     * Attaches country code to phone number, and drops leading zero if there is one
     */
    public static String extractPhoneNumber(CountryCodePicker ccp, TextInputEditText editText) {
            String phoneInput = editText.getText().toString();
            if (phoneInput.charAt(0) == '0' && phoneInput.length() == 10)
                phoneInput = phoneInput.substring(1);
            phoneInput = ccp.getSelectedCountryCodeWithPlus() + phoneInput;
            return phoneInput;
    }

    public static void cropImage(Activity activity, File image, String resultPrefix) {
        try {
            UCrop
                    .of(Uri.fromFile(image), Uri.fromFile(File.createTempFile(FirebaseAuth.getInstance().getCurrentUser().getUid(), UtilityPack.FILE_KEYS.img_POSTFIX)))
                    .withAspectRatio(1, 1)
                    .start(activity, UtilityPack.REQUEST_CODES.UCROP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadUCropResult(Activity activity, Intent data, ImageView imageView, int defaultIntDrawable) {
        Glide
                .with(activity)
                .load(UCrop.getOutput(data).getPath())
                .into(imageView)
                .onLoadStarted(ContextCompat.getDrawable(activity, defaultIntDrawable));
    }

    public interface KEYS {
        String USERS = "users";
        String RECIPES = "recipes";
        String MY_RECIPES = "myRecipes";
        String MY_FRIENDS = "myFriends";
        String MY_CHATS = "myChats";
        String PENDING_RECIPES = "pendingRecipes";
        String PENDING_FRIENDS = "pendingFriends";
        String PHONE_NUMBER = "phoneNumber";
        String DISPLAY_NAME = "displayName";
        String USER_ID = "userID";
        String PROFILE_IMAGE = "profileImage";
        String RECIPE_ID = "RecipeID";
        String DATE = "date";
        String DIFFICULTY = "difficulty";
        String METHOD = "method";
        String OWNER = "owner";
        String OWNER_ID = "ownerID";
        String PREP_TIME = "prepTime";
        String TITLE = "title";
        String IMAGE = "image";
        String INGREDIENTS = "ingredients";
    }

    public interface REQUEST_CODES {
        int UCROP = 1111;
        int GILGAR = 1112;
    }

    public interface FILE_KEYS {
        String img_POSTFIX = ".jpg";
    }
}
