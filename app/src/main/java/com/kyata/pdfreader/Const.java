package com.kyata.pdfreader;

import android.os.Environment;

public class Const {
    public static final String SHARE_PREF_NAME = "SHARE_PREF_NAME";
    public static final String IS_FIRST = "IS_FIRST";
    public static final String MODE = "mode";
    public static final String IS_POLICY_ACCEPT = "IS_POLICY_ACCEPT";
    public static final String IS_RATE = "IS_RATE";
    public static final String COUNT_RATE = "COUNT_RATE";


    public static final String TYPE_ALL_FILE = "TYPE_ALL_FILE";
    public static final String TYPE_PDF = ".pdf";
    public static final String TYPE_WORD = ".doc";
    public static final String TYPE_POWER = ".ppt";
    public static final String TYPE_EXCEL = ".xls";
    public static final String TYPE_TEXT = ".txt";
    public static final String TYPE_RECENT = "TYPE_RECENT";
    public static final String TYPE_FAVOURITE = "TYPE_FAVOURITE";
    public static final int REQUEST_STORAGE_PERMISSION = 1001;
    public static final String KEY_CLICK_ITEM = "KEY_CLICK_ITEM";
    public static final String DATA = "data";
    public static final String PDF_LOCATION = "PDF_LOCATION";
    public static final String KEY_DELETE = "KEY_DELETE";
    public static final int REQUEST_OPEN_FILE = 1002;
    public static final String KEY_CLICK_FILE = "KEY_CLICK_FILE";
    public static final String KEY_CLICK_DIRECTORY = "KEY_CLICK_DIRECTORY";
    public static final String BASE_PATH_2 = Environment.getExternalStorageDirectory() +"/"+Environment.DIRECTORY_DOCUMENTS+ "/PdfReader";
}
