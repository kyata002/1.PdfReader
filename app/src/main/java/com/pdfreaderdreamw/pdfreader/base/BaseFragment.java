package com.pdfreaderdreamw.pdfreader.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.admob.control.AdmobManager;
import com.admob.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.pdfreaderdreamw.pdfreader.App;
import com.pdfreaderdreamw.pdfreader.BuildConfig;
import com.pdfreaderdreamw.pdfreader.StorageCommon;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.model.RecentFile;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;
import com.pdfreaderdreamw.pdfreader.view.activity.MainActivity;
import com.pdfreaderdreamw.pdfreader.view.activity.SplashActivity;
import com.pdfreaderdreamw.pdfreader.view.adapter.ListFileAdapter;

import java.util.List;


abstract public class BaseFragment<T extends ViewDataBinding> extends Fragment implements View.OnClickListener {
    protected Context context;
    protected View rootView;
    protected OnActionCallback mCallback;
    protected T binding;


    public void setmCallback(OnActionCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
//        filter.addAction(MainActivity.ACTION_COMPLETE);
//        filter.addAction(MainActivity.ACTION_NEW);
//        filter.addAction(MainActivity.ACTION_READING);
        filter.addAction(MainActivity.ACTION_SORT_DATE);
        filter.addAction(MainActivity.ACTION_SORT_NAME);
        filter.addAction(MainActivity.ACTION_SORT_SIZE);
        filter.addAction(MainActivity.ACTION_ALL_FILE);
        filter.addAction(ListFileAdapter.ACTION_UPDATE_FAVOURITE);
        filter.addAction(MainActivity.ACTION_SEARCH_MAIN);
        filter.addAction(SplashActivity.ACTION_UPDATE_DATA);
        context.registerReceiver(receiver, filter);
    }

    protected ItemFile findItem(String path, List<ItemFile> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPath().equals(path)) {
                return list.get(i);
            }
        }
        return null;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        addEvent();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void addEvent();


    protected String[] getPermissions() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onClick(View v) {
    }

    protected String getPathFromUri(Uri uri) {
        Cursor cursor =
                context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf("") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " =  ", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        try {
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } catch (Exception e) {
            return "";
        }
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

    protected void showInterFileAds() {
        AdmobManager.getInstance().showInterstitial(getActivity(), StorageCommon.getInstance().getInterFile(), new AdCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterFileAds();
            }
        });
    }

    private void loadInterFileAds() {
        AdmobManager.getInstance().loadInterAds(getActivity(), BuildConfig.inter_file, new AdCallback() {
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
