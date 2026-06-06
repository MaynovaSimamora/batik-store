package com.example.batikstore;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.batikstore.util.PrefManager;

public class BatikApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefManager pm = new PrefManager(this);
        AppCompatDelegate.setDefaultNightMode(
                pm.isNightMode()
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
    }
}