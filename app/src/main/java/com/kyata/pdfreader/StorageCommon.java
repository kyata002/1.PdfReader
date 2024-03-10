package com.kyata.pdfreader;


//import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.interstitial.InterstitialAd;

public class StorageCommon {

    private static StorageCommon instance;
    public static final int TYPE_LIST = 0;
    public static final int TYPE_GRIB = 1;
    public static final int SORT_TYPE_SIZE = 2;
    public static final int SORT_TYPE_DATE = 3;
    public static final int SORT_TYPE_NAME = 4;

    public static final int FILTER_ALL = 5;
    public static final int FILTER_READING = 6;
    public static final int FILTER_COMPLETED = 7;
    public static final int FILTER_NEW = 8;

    private InterstitialAd interFile;
    private InterstitialAd interImage2Pdf;
    private int type = TYPE_LIST;
    private int sortType = SORT_TYPE_DATE;
    private int filterType = FILTER_ALL;

    public static StorageCommon getInstance() {
        if (instance == null) {
            instance = new StorageCommon();
        }
        return instance;
    }

    private StorageCommon() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public InterstitialAd getInterFile() {
        return interFile;
    }

    public void setInterFile(InterstitialAd interFile) {
        this.interFile = interFile;
    }

    public InterstitialAd getInterImage2Pdf() {
        return interImage2Pdf;
    }

    public void setInterImage2Pdf(InterstitialAd interImage2Pdf) {
        this.interImage2Pdf = interImage2Pdf;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getSortType() {
        return sortType;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }
}
