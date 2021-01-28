package com.dreamest.cookbookapp;

import android.app.Application;

import com.dreamest.cookbookapp.utility.MySharedPreferences;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MySharedPreferences.init(this);


    }
}