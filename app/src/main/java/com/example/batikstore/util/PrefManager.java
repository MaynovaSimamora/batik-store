package com.example.batikstore.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static final String PREF_NAME = "batik_pref";
    private static final String KEY_NIGHT = "night_mode";
    private final SharedPreferences sp;

    public PrefManager(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isNightMode() {
        return sp.getBoolean(KEY_NIGHT, false);
    }

    public void setNightMode(boolean night) {
        sp.edit().putBoolean(KEY_NIGHT, night).apply();
    }
}