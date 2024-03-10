package com.kyata.pdfreader;

import android.graphics.Bitmap;

import java.util.HashMap;

public class CacheImage {
    private static CacheImage instance;
    private final HashMap<String, Bitmap> hashMap = new HashMap<>();

    public Bitmap getBitmap(String key) {
        return hashMap.get(key);
    }

    public void putBitmap(String key, Bitmap value) {
        hashMap.put(key, value);
    }

    public static CacheImage getInstance() {
        if (instance == null) {
            instance = new CacheImage();
        }
        return instance;
    }

    private CacheImage() {

    }
}
