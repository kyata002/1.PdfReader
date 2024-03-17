package com.kyata.pdfreader.task;

import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_FAVOURITE;
import static com.kyata.pdfreader.view.activity.MainActivity.ITEM_RECENT;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.kyata.pdfreader.App;
import com.kyata.pdfreader.data.model.FavoriteFile;
import com.kyata.pdfreader.data.model.RecentFile;
import com.kyata.pdfreader.view.activity.MainActivity;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class FetchDatabaseTask implements Callable {
    private final Context context;

    public FetchDatabaseTask(Context context) {
        this.context = context;
    }

    @Override
    public Object call() throws Exception {
        Thread.sleep(1000);
        try {
            MainActivity.categoryList.get(ITEM_FAVOURITE).clearData();
            List<FavoriteFile> favoriteFiles = App.getInstance().getDatabase().favoritetDao().getList();
            for (int i = 0; i < favoriteFiles.size(); i++) {
                File file = new File(favoriteFiles.get(i).getPath());
                if (!file.exists()) {
                    App.getInstance().getDatabase().favoritetDao().delete(file.getPath());
                    continue;
                }
                int pageNum = getNumberPage(file);
                MainActivity.categoryList.get(ITEM_FAVOURITE).addFile(file, pageNum);
            }
            MainActivity.categoryList.get(ITEM_RECENT).clearData();
            List<RecentFile> recentFiles = App.getInstance().getDatabase().recentDao().getList();
            for (int i = 0; i < recentFiles.size(); i++) {
                File file = new File(recentFiles.get(i).getPath());
                if (!file.exists()) {
                    App.getInstance().getDatabase().recentDao().delete(file.getPath());
                    continue;
                }
                int pageNum = getNumberPage(file);
                MainActivity.categoryList.get(ITEM_RECENT).addFile(file, pageNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    private int getNumberPage(File file) {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            return pdfiumCore.getPageCount(pdfDocument) - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
