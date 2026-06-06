package com.example.batikstore.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "batik_session";
    private static final String KEY_LOGGED = "logged_in";
    private static final String KEY_USER = "username";
    private final SharedPreferences sp;

    public SessionManager(Context c) {
        sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void login(String username) {
        sp.edit().putBoolean(KEY_LOGGED, true).putString(KEY_USER, username).apply();
    }

    public boolean isLoggedIn() { return sp.getBoolean(KEY_LOGGED, false); }
    public String getUsername() { return sp.getString(KEY_USER, "User"); }
    public void logout() { sp.edit().clear().apply(); }
}