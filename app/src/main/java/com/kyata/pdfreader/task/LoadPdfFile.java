package com.kyata.pdfreader.task;

import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_ALL_FILE;
import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_FAVOURITE;
import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_RECENT;
import static com.kyata.pdfreader.view.activity.MainActivity.categoryList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.data.model.Category;
import com.kyata.pdfreader.view.OnActionCallback;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class LoadPdfFile extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private OnActionCallback callback;

    public LoadPdfFile(Context context) {
        initCategory();
        this.context = context;
    }

    private void initCategory() {
        categoryList.clear();
        categoryList.add(ITEM_ALL_FILE, new Category("All files", Const.TYPE_ALL_FILE));
        categoryList.add(ITEM_RECENT, new Category("Recent files", Const.TYPE_RECENT));
        categoryList.add(ITEM_FAVOURITE, new Category("Favourite files", Const.TYPE_FAVOURITE));
    }

    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }

    private final List<File> list = new ArrayList<>();

    @Override
    protected Void doInBackground(Void... voids) {
        executeLoadFile();

        categoryList.get(ITEM_ALL_FILE).clearData();

        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            int totalPage = getNumberPage(context, file) - 1;
            categoryList.get(ITEM_ALL_FILE).addFile(file, totalPage);
        }


        return null;
    }

    public static int getNumberPage(Context context, File file) {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            return pdfiumCore.getPageCount(pdfDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void executeLoadFile() {
        Uri table = MediaStore.Files.getContentUri("external");
        String selection = "_data LIKE '%.pdf'";

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(table, null, selection, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                return;
            }
            int data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            do {
                String path = cursor.getString(data);
                File file = new File(path);
                if (file.length() == 0) {
                    continue;
                }
                list.add(file);
            }
            while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDocument(File file) {
        return (file.getName().endsWith(Const.TYPE_PDF) || file.getName().endsWith(Const.TYPE_PDF.toUpperCase()))
                && file.length() > 0;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null) {
            callback.callback(null, categoryList);
        }
    }

}
