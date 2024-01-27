package com.common.control.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class RatePrefUtils {

    private static final String SHARE_PREF_NAME = "setting.pref";

    public static boolean shouldShowRatePopup(Context context) {
        if (isRated(context)) {
            return false;
        }
        int count = getCountRate(context);
        return count == 0 || count == 1 || count == 2 || count == 3 || count == 5 || count == 7 || count == 10 || count == 13 || count == 18 || count == 24;
    }


    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);

    }

    public static void setRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean("rated", true).apply();
    }

    public static int getCountRate(Context context) {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt("count_rate", 0);
    }

    public static int getCurrentPageReading(Context context, String key) {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt(key, -1) + 1;
    }

    public static void setCurrentPageReading(Context context, String key, int value) {
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static void increaseCountRate(Context context) {
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putInt("count_rate", getCountRate(context) + 1).apply();
    }

    public static void setNightMode(Context context, boolean nightMode) {
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("night_mode", nightMode).apply();
    }

    public static boolean getNightMode(Context context) {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("night_mode", false);
    }

    public static void setHorizontal(Context context, boolean horizontal) {
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("horizontal", horizontal).apply();
    }

    public static boolean isHorizontal(Context context) {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("horizontal", false);

    }

    public static boolean isNightModeApp(Context context) {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("night_mode_app", false);

    }

    public static void setNightModeApp(Context context, boolean value) {
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("night_mode_app", value).apply();

    }
}
