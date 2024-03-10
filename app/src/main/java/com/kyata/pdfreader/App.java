package com.kyata.pdfreader;

import androidx.appcompat.app.AppCompatDelegate;

import com.admob.control.AdsApplication;
import com.admob.control.AppOpenManager;
import com.google.gson.Gson;
import com.helper.permission.PlPermissionDialog;
import com.kyata.pdfreader.db.RoomDatabase;
import com.kyata.pdfreader.view.activity.SplashActivity;

public class App extends AdsApplication {
    private RoomDatabase database;
    private Gson gson;

    public RoomDatabase getDatabase() {
        return database;
    }

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = RoomDatabase.getDatabase(this);
        instance = this;
        gson = new Gson();


        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        AppOpenManager.getInstance().disableAppResumeWithActivity(PlPermissionDialog.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    protected boolean isPurchased() {
        return BuildConfig.PURCHASED;
    }

    @Override
    protected boolean isShowAdsTest() {
        return BuildConfig.DEBUG || BuildConfig.TEST_AD;
    }

    @Override
    public boolean enableAdsResume() {
        return false;
    }

    @Override
    public String getOpenAppAdId() {
        return BuildConfig.open_app;
    }

}
