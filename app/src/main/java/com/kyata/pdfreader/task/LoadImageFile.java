package com.kyata.pdfreader.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.kyata.pdfreader.view.OnActionCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class LoadImageFile extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final OnActionCallback callback;

    public LoadImageFile(Context context,OnActionCallback callback) {
        this.callback = callback;
        this.context=context;
    }

    private final List<File> list = new ArrayList<>();

    @SuppressLint("Recycle")
    @Override
    protected Void doInBackground(Void... voids) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, orderBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null) {
            return null;
        }
        int columnData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            String path = cursor.getString(columnData);
            File file=new File(path);
            if (!file.exists()) {
                continue;
            }
            list.add(file);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null) {
            callback.callback(null, list);
        }
    }

}
