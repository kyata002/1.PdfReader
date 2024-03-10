package com.kyata.pdfreader.model;

public class Language {

    private final int mId;
    private final String mName;
    private final String mCode;

    public Language(int id, String name, String code) {
        mId = id;
        mName = name;
        mCode = code;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getCode() {
        return mCode;
    }
}