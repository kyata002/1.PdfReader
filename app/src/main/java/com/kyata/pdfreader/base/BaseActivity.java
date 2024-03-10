package com.kyata.pdfreader.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.admob.control.AdmobManager;
import com.admob.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kyata.pdfreader.App;
import com.kyata.pdfreader.BuildConfig;
import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.StorageCommon;
import com.kyata.pdfreader.model.ItemFile;
import com.kyata.pdfreader.model.RecentFile;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.activity.PdfReaderActivity;

import java.util.List;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements OnActionCallback {
    protected T binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        setTheme(R.style.Theme_AppThemeDark);

        initLanguage();
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.executePendingBindings();
        initView();
        addEvent();
    }


    public void logEvent(String name, String param, String value) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(param, value);
            mFirebaseAnalytics.logEvent(name, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserProperty(String name) {
        try {
            mFirebaseAnalytics.setUserProperty(name, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected boolean setThemeApp() {
        return true;
    }

    protected void initLanguage() {

    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void addEvent();

    private void performDataBinding() {

    }

    protected String[] getStoragePermission() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    protected boolean permissionGranted(String[] storagePermission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String s : storagePermission) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void callback(String key, Object data) {

    }

    protected void addRecent(ItemFile itemFile) {
        List<RecentFile> list = App.getInstance().getDatabase().recentDao().getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPath().equals(itemFile.getPath())) {
                App.getInstance().getDatabase().recentDao().delete(itemFile.getPath());
                App.getInstance().getDatabase().recentDao().add(new RecentFile(itemFile.getPath()));
                return;
            }
        }
        App.getInstance().getDatabase().recentDao().add(new RecentFile(itemFile.getPath()));
    }

    public void openPdfFile(String path) {
        Intent intent = new Intent(this, PdfReaderActivity.class);
        intent.putExtra(Const.PDF_LOCATION, path);
        startActivity(intent);
        addRecent(new ItemFile(path));
        showInterFileAds();
        setUserProperty("CLICK_READ_PDF");
    }



    protected void showInterFileAds() {
        AdmobManager.getInstance().showInterstitial(this, StorageCommon.getInstance().getInterFile(), new AdCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterFileAds();
            }
        });
    }

    protected void loadInterFileAds() {
        AdmobManager.getInstance().loadInterAds(this, BuildConfig.inter_file, new AdCallback() {
            @Override
            public void interCallback(InterstitialAd interstitialAd) {
                super.interCallback(interstitialAd);
                StorageCommon.getInstance().setInterFile(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                super.onAdFailedToLoad(i);
                StorageCommon.getInstance().setInterFile(null);
            }
        });
    }
}
