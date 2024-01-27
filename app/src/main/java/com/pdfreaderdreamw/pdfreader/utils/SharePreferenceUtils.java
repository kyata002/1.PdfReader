package com.pdfreaderdreamw.pdfreader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.view.activity.SplashActivity;


public class SharePreferenceUtils {

    public static boolean shouldShowRatePopup(Context context) {
        if (isRated(context)) {
            return false;
        }
        int count = getCountRate(context);
        return count == 0 || count == 1 || count == 2 || count == 3 || count == 5 || count == 7 || count == 10 || count == 13 || count == 18 || count == 24;
    }


    public static boolean isFirstTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        boolean b = pre.getBoolean(Const.IS_FIRST, true);
        if (b) {
            pre.edit().putBoolean(Const.IS_FIRST, false).apply();
        }
        return b;
    }

    public static void setModeplay(Context context, int mode) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putInt(Const.MODE, mode).apply();
    }

    public static int getModeplay(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(Const.MODE, 1);
    }


    public static boolean isPolicyAccepted(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(Const.IS_POLICY_ACCEPT, false);

    }

    public static void disablePolicyAccept(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(Const.IS_POLICY_ACCEPT, true).apply();
    }


    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(Const.IS_RATE, false);

    }

    public static void setRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(Const.IS_RATE, true).apply();
    }

    public static int getCountRate(Context context) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt(Const.COUNT_RATE, 0);
    }

    public static int getCurrentPageReading(Context context, String key) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt(key, -1) + 1;
    }

    public static void setCurrentPageReading(Context context, String key, int value) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static void increaseCountRate(Context context) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putInt(Const.COUNT_RATE, getCountRate(context) + 1).apply();
    }

    public static void setNightMode(Context context, boolean nightMode) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("night_mode", nightMode).apply();
    }

    public static boolean getNightMode(Context context) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("night_mode", false);
    }

    public static void setHorizontal(Context context, boolean horizontal) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("horizontal", horizontal).apply();
    }

    public static boolean isHorizontal(Context context) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("horizontal", false);

    }

    public static boolean isNightModeApp(Context context) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean("night_mode_app", false);

    }

    public static void setNightModeApp(Context context, boolean value) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("night_mode_app", value).apply();

    }

    public static void forceUpdateLanguage(Context context) {
        context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean("language_changed", true).apply();
    }

    public static boolean isUpdatedLanguage(Context context) {
        return context.getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean("language_changed", false);
    }
}
