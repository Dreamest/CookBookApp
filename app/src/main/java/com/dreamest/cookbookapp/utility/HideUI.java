package com.dreamest.cookbookapp.utility;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HideUI {
    /**
     * Hides system UI, thus allowing the app to run on full screen.
     *
     * @param activity current activity
     */

    public static void hideSystemUI(AppCompatActivity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        // Dim the Status and Navigation Bars
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI(Activity myActivity) {
        View decorView = myActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void clearFocus(AppCompatActivity activity, View... views) {
        for (View v : views) {
            v.clearFocus();
        }
        hideSystemUI(activity);

    }

    public static void setNextFocus(AppCompatActivity activity, View next) {
        next.requestFocus();
        hideSystemUI(activity);
    }

}