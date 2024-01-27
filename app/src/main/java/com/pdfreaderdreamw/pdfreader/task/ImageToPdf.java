package com.pdfreaderdreamw.pdfreader.task;

import static com.pdfreaderdreamw.pdfreader.view.activity.MainActivity.ITEM_ALL_FILE;
import static com.pdfreaderdreamw.pdfreader.view.activity.MainActivity.categoryList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class ImageToPdf extends AsyncTask<String, String, String> {
    private final ProgressDialog dialog;
    private final List<String> imagesUri;
    private final Context context;
    private String path;
    private final OnActionCallback callback;


    public ImageToPdf(List<String> imagesUri, Context context, OnActionCallback callback) {
        this.callback = callback;
        this.imagesUri = imagesUri;
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(context.getString(R.string.converting) + "... 0%");
        dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), (dialog, which) -> {
            ImageToPdf.this.cancel(true);
            if (path != null)
                new File(path).delete();
            dismissProgressDialog();
        });
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        path = params[0];
        long totalSize = getTotalSize();
        long currentConvert = 0;
        Document document = new Document(PageSize.A4, 38, 38, 50, 38);
        Rectangle documentRect = document.getPageSize();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            for (int i = 0; i < imagesUri.size(); i++) {
                currentConvert += new File(imagesUri.get(i)).length();
                int percent = (int) ((currentConvert * 1.0 / totalSize) * 100);
                publishProgress(percent + "%", imagesUri.get(i));
                Thread.sleep(200);
                Image image = Image.getInstance(imagesUri.get(i));
//                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                        - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
////                image.scalePercent(scaler);

                if (image.getWidth() > documentRect.getWidth() || image.getHeight() > documentRect.getHeight()) {
                    image.scaleToFit(documentRect.getWidth() - 16, documentRect.getHeight() - 16);
                } else {
                    image.scaleToFit(image.getWidth(), image.getHeight());
                }
                image.setAbsolutePosition((documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);

                image.setAlignment(Image.ALIGN_CENTER);
                image.setBorder(Image.BOX);
                document.add(image);
                document.newPage();
            }
            document.close();
            File file = new File(path);
            int totalPage = getNumberPage(file) - 1;
            try {
                categoryList.get(ITEM_ALL_FILE).addFile(0, file, totalPage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.d("TAG", "doInBackground: " + e.getMessage());
            e.printStackTrace();
        }
        imagesUri.clear();
        return null;
    }

    private int getNumberPage(File file) {
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

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        String percent = values[0];
        dialog.setMessage("Creating..." + percent);
    }

    private long getTotalSize() {
        long rs = 0;
        for (int i = 0; i < imagesUri.size(); i++) {
            rs += new File(imagesUri.get(i)).length();
        }
        return rs;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (((Activity) context).isDestroyed())
            return;
        dismissProgressDialog();
        try {
            ItemFile itemFile = new ItemFile(path);
            callback.callback(null, itemFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
