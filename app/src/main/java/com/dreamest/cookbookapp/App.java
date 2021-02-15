package com.dreamest.cookbookapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.dreamest.cookbookapp.utility.MySharedPreferences;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MySharedPreferences.init(this);

        //Disables dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}