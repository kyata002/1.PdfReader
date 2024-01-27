package com.admob.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;


import java.util.HashMap;

public class AdsConfigManager {
    private HashMap<String, Object> hashMap;
    @SuppressLint("StaticFieldLeak")
    private static AdsConfigManager instance;
    private Context context;

    public static AdsConfigManager getInstance() {
        if (instance == null) {
            instance = new AdsConfigManager();
        }
        return instance;
    }


    private AdsConfigManager() {
    }


    public long getTimeout() {
        try {
            return (long) hashMap.get("timeout");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 6;
    }

    public boolean hasAds(int idRes) {
        String key = context.getResources().getResourceEntryName(idRes);
        return (boolean) hashMap.get(key);
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }


}
