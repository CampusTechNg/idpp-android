package com.campustechng.aminu.idpenrollment.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.campustechng.aminu.idpenrollment.activity.LoginActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Muhammad Amin on 5/4/2017.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "IDP_ENROLLMENT_PREF";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_SYNC = "auto_sync";

    public static final String KEY_COODINATES = "geo_tag";

    public static final String KEY_LOCATION_NAME = "location_name";

    public static final String KEY_LOGOUT_TIME = "logout_time";

    public static final String KEY_SYNC_IDS = "idp_ids";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getStringValue(String key) {
        return pref.getString(key,"");
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key,false);
    }

    public Set<String> getStringSetValue(String key) {
        return pref.getStringSet(key, new HashSet<String>());
    }

    public void setStringValue(String key, String value) {
        editor.putString(key,value);
        editor.commit();
    }

    public void setBooleanValue(String key, boolean value) {

        editor.putBoolean(key,value);
        editor.commit();
    }

    public void setStringSetValue(String key, String value) {
        Set<String> s = new HashSet<>(pref.getStringSet(key, new HashSet<String>()));
        s.add(value);
        editor.putStringSet(key, s);
        editor.commit();
    }

    public void removeStringSetValue(String key, String value) {
        Set<String> s = new HashSet<>(pref.getStringSet(key, new HashSet<String>()));
        s.remove(value);
        editor.putStringSet(key, s);
        editor.commit();
    }


}
