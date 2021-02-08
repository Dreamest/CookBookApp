package com.dreamest.cookbookapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MySharedPreferences {

    private static MySharedPreferences msp;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private MySharedPreferences(Context context) {
        settings = context.getApplicationContext().getSharedPreferences(KEYS.MY_SP, Context.MODE_PRIVATE);
        editor = settings.edit();
        gson = new Gson();
    }

    public static void init(Context context) {
        if (msp == null)
            msp = new MySharedPreferences(context);
    }

    public static MySharedPreferences getMsp() {
        return msp;
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void resetData() {
        editor.clear().apply();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return settings.getString(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return settings.getInt(key, defaultValue);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return settings.getLong(key, defaultValue);
    }

    public void putObject(String key, Object value) {
        putString(key, gson.toJson(value));
    }

    /**
     * Loads object back from sharedPreferences
     * @param key key of the item stored
     * @param defaultValue if loading failed, will load this
     * @return object saved under key
     */
    public Object getObject(String key, Object defaultValue, Type objectType) {
        String objectJson = getString(key, KEYS.NO_OBJECT);
        if (objectJson.equals(KEYS.NO_OBJECT))
            return defaultValue;
        return gson.fromJson(objectJson, objectType);
    }

    public interface KEYS {
        String MY_SP = "MY_SP";
        String NO_OBJECT = "NO_OBJECT";
        String RECIPE = "RECIPE";
        String USER = "USER";
        String INGREDIENT = "INGREDIENT";
        String UPDATED_INGREDIENT = "UPDATED_INGREDIENT";
        String USER_ID = "USER_ID";
        String LOGOUT = "LOGOUT";
        boolean LOGOUT_SIGNAL = true;
        boolean STAY_LOGGED = false;
        String FRIENDSLIST_ARRAY = "FRIENDS_ARRAY";
        String PENDING_FRIENDS_ARRAY = "PENDING_FRIENDS_ARRAY";
        String MY_RECIPES_ARRAY = "RECIPES_LIST";
        String PENDING_RECIPES_ARRAY = "PENDING_RECIPES_ARRAY";

    }


}