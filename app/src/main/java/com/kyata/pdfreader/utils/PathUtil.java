package com.kyata.pdfreader.utils;

import static com.kyata.pdfreader.utils.FileUtils.DEFAULT_TEMP;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;


import com.kyata.pdfreader.App;

import java.io.File;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

/**
 * Created by Aki on 1/7/2017.
 */

public class PathUtil {
    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.d("TAG", "getPath: ");
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString;
        if (fileSize <= 0) {
            fileSizeString = "0KB";
        } else if (fileSize < (1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < (1024 * 1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / (1024 * 1024)) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / (1024 * 1024 * 1024)) + "GB";
        }
        return fileSizeString;
    }


    public static String getFileExtensionNoPoint(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        return getFileExtensionNoPoint(new File(path));
    }


    private static String getFileExtensionNoPoint(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        if (fileName != null && fileName.length() > 0) {
            int lastIndex = fileName.lastIndexOf('.');
            if ((lastIndex > -1) && (lastIndex < (fileName.length() - 1))) {
                return fileName.substring(lastIndex + 1);
            }
        }
        return "";
    }


    public static int getFileIcon(boolean isDir, String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return getFileIcon(isDir, filePath);
        } else {
            return 0;
        }
    }

    private static boolean isCompareFiles(String path1, String path2) {
        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }
        if (path1.equalsIgnoreCase(path2)) {
            return true;
        } else {
            return isCompareFiles(new File(path1), new File(path2));
        }
    }

    private static boolean isCompareFiles(File file1, File file2) {
        if (file1 == null || file2 == null) {
            return false;
        }
        return file1.getPath().equalsIgnoreCase(file2.getPath());
    }




    private static boolean isSDExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static String getSDCardFilesPath() {
        if (!isSDExist()) {
            return "";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }


    private static String getSDCardDownloadPath() {
        if (!isSDExist()) {
            return "";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
    }


    public static String getFileNameNoExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getFileNameNoExtension(new File(filePath));
    }


    public static String getFileNameNoExtension(File file) {
        if (file == null) {
            return "";
        }
        String filename = file.getName();
        if (!TextUtils.isEmpty(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    private static boolean isFileExist(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                return file.exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    private static String getFileMimeTypeFromExtension(String fileType) {
        try {
            if (TextUtils.isEmpty(fileType)) {
                return "*/*";
            }
            fileType = fileType.replace(".", "");
            if (fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("wps")) {
                fileType = "doc";
            } else if (fileType.equalsIgnoreCase("xlsx")) {
                fileType = "xls";
            } else if (fileType.equalsIgnoreCase("pptx")) {
                fileType = "ppt";
            }
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            if (mimeTypeMap.hasExtension(fileType)) {
                return mimeTypeMap.getMimeTypeFromExtension(fileType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "*/*";
    }

    public static String getAppTempPath() {
        return getAvailableFilesPathAndroidData(true) + DEFAULT_TEMP;
    }


    private static String getAvailableFilesPathAndroidData(boolean boolToCache) {
        if (!isSDExist()) {
            if (boolToCache) {
                return App.getInstance().getCacheDir().getAbsolutePath() + "/";
            }
            return App.getInstance().getFilesDir().getAbsolutePath() + "/";
        } else {
            if (boolToCache) {
                return App.getInstance().getExternalCacheDir().getAbsolutePath() + "/";
            }
            return App.getInstance().getExternalFilesDir("").getAbsolutePath() + "/";
        }
    }
}