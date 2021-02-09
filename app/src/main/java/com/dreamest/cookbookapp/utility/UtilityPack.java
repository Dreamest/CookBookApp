package com.dreamest.cookbookapp.utility;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.rilixtech.CountryCodePicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class UtilityPack {

    /**
     * @return background from random predetermined background options
     */
    public static int randomBackground() {
        int[] allBackgrounds = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4};
        return allBackgrounds[new Random().nextInt(allBackgrounds.length)];
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

    /**
     * Creates a UCrop activity for File image
     * @param activity activity context
     * @param image the image to be cropped
     * @param resultPrefix how the result file will be called.
     */
    public static void cropImage(Activity activity, File image, String resultPrefix) {
        try {
            UCrop
                    .of(Uri.fromFile(image), Uri.fromFile(
                            File.createTempFile(resultPrefix, FirebaseTools.FILE_KEYS.JPG)))
                    .withAspectRatio(1, 1)
                    .start(activity, UtilityPack.REQUEST_CODES.UCROP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads cropped image into imageView using Glide
     * @param activity activity context
     * @param path path to image File
     * @param imageView imageView to load into
     * @param defaultIntDrawable placeholder drawable
     */
    public static void loadUCropResult(Activity activity, String path, ImageView imageView, int defaultIntDrawable) {
        Glide
                .with(activity)
                .load(path)
                .into(imageView)
                .onLoadStarted(ContextCompat.getDrawable(activity, defaultIntDrawable));
    }

    public static String CreateRecipeID(String uid) {
        return uid + System.currentTimeMillis();
    }

    public interface REQUEST_CODES {
        int UCROP = 1111;
        int GILGAR = 1112;
    }

}
