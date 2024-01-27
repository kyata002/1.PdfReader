package com.pdfreaderdreamw.pdfreader.view.activity;

import static com.helper.permission.PlPermissionUtils.isStoragePermissionGranted;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.admob.control.AdmobManager;
import com.admob.control.funtion.AdCallback;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.helper.permission.PlPermissionCallback;
import com.helper.permission.PlPermissionUtils;
import com.pdfreaderdreamw.pdfreader.BuildConfig;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.ActivitySplashBinding;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.task.LoadPdfFile;
import com.pdfreaderdreamw.pdfreader.utils.LanguageUtils;
import com.wxiwei.office.constant.MainConstant;
import com.wxiwei.office.utils.RealPathUtil;

import java.io.File;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    public static final String ACTION_FINISH = "action_finish";
    public static final String ACTION_UPDATE_DATA = "action_update_data";
    private String path;

    @Override
    protected void initLanguage() {
        LanguageUtils.loadLocale(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        Glide.with(this).load(R.drawable.bg_splash).into(binding.ivSplash);
        if (isStoragePermissionGranted(this)) {
            loadData(true);
        } else {
            loadAd();
        }
    }


    private void requestPermission() {
        logEvent("PERMISSION_SHOW", "EVENT", "SHOW");
        PlPermissionUtils.instance().showPermissionDialogIfNeed(SplashActivity.this,
                new PlPermissionCallback() {
                    @Override
                    public void onPermissionNotGranted() {
                        MainActivity.categoryList.clear();
                    }

                    @Override
                    public void onPermissionGranted() {
                        logEvent("PERMISSION_SHOW", "EVENT", "ALLOW");
                        loadData(false);

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(SplashActivity.this, "Permission is denied!", Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent(ACTION_FINISH));
                    }

                    @Override
                    public void onPermissionAborted() {
                        logEvent("PERMISSION_SHOW", "EVENT", "DENY");
                        sendBroadcast(new Intent(ACTION_FINISH));
                    }
                }
        );
    }


    private void loadData(boolean loadAd) {
        LoadPdfFile loadPdfFile = new LoadPdfFile(this);
        if (loadPdfFile.getStatus() == AsyncTask.Status.RUNNING) {
            loadPdfFile.cancel(true);
        }
        loadPdfFile.setCallback((key, data) -> {
            if (loadAd) {
                loadAd();
            }
            sendBroadcast(new Intent(ACTION_UPDATE_DATA));
        });
        loadPdfFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean getSystemNightMode() {
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void loadAd() {
        String interSplash = BuildConfig.inter_splash;
        if (getIntent().getData() != null) {
            logEvent("ACTIVITY_SHOW", "NAME_ACTIVITY", "SPLASH");
            path = RealPathUtil.getPathFromData(this, getIntent().getData());
            if (path != null) {
                logEvent("READ_FILE_SHOW", "FILE_TYPE", getFileType(path));
            }
            interSplash = BuildConfig.inter_open_app;
            logEvent("ACTIVITY_SHOW", "NAME_ACTIVITY", "OPEN_OTHER_APP");
        }

        AdmobManager.getInstance().loadInterAds(this, interSplash, new AdCallback() {
            @Override
            public void interCallback(InterstitialAd interstitialAd) {
                super.interCallback(interstitialAd);
                showInter(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                super.onAdFailedToLoad(i);
                loadMainUI();
            }
        });
    }

    private String getFileType(String path) {
        String name = path.trim().toLowerCase();
        if (name.endsWith(".pdf")) {
            return "PDF";
        }
        if (name.endsWith(".doc") || name.endsWith(".docx")) {
            return "DOC";
        }
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            return "EXCEL";
        }
        if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
            return "POWERPOINT";
        }

        return "OTHER";
    }

    private void showInter(InterstitialAd interstitialAd) {
        loadMainUI();
        AdmobManager.getInstance().showInterstitial(this, interstitialAd, null);
    }

    private void loadMainUI() {
        if (getIntent().getData() != null && path != null) {
            ItemFile itemFile = new ItemFile(path);
            viewFile(itemFile);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            if (!isStoragePermissionGranted(this)) {
                requestPermission();
            }
        }
        finish();
    }

    protected void viewFile(ItemFile itemFile) {
        Intent intent;
        if (itemFile.getPath().endsWith(".pdf")) {
            intent = new Intent(this, PdfReaderActivity.class);
            intent.putExtra(Const.PDF_LOCATION, itemFile.getPath());
        } else {
            intent = new Intent(this, DocReaderActivity.class);
            intent.putExtra(MainConstant.INTENT_FILED_FILE_URI, Uri.fromFile(new File(itemFile.getPath())));
            intent.putExtra(MainConstant.INTENT_FILED_FILE_PATH, itemFile.getPath());
            intent.putExtra(MainConstant.INTENT_FILED_FILE_NAME, new File(itemFile.getPath()).getName());
            intent.putExtra(MainConstant.INTENT_OBJECT_ITEM, itemFile);
        }
        startActivity(intent);
    }


    @Override
    protected void addEvent() {

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        path = null;
    }
}