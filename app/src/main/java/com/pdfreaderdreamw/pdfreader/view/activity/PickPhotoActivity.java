package com.pdfreaderdreamw.pdfreader.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.admob.control.AdmobManager;
import com.admob.control.funtion.AdCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.pdfreaderdreamw.pdfreader.BuildConfig;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.StorageCommon;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.ActivityMediaBinding;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.model.Media;
import com.pdfreaderdreamw.pdfreader.task.ImageToPdf;
import com.pdfreaderdreamw.pdfreader.task.LoadImageFile;
import com.pdfreaderdreamw.pdfreader.view.adapter.MediaAdapter;
import com.pdfreaderdreamw.pdfreader.view.dialog.CreatePdfDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PickPhotoActivity extends BaseActivity<ActivityMediaBinding> implements MediaAdapter.MediaListener {
    private final List<Media> list = new ArrayList<>();
    private MediaAdapter adapter;
    private ArrayList<Media> listChecked = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (StorageCommon.getInstance().getInterImage2Pdf() == null) {
            loadInterFileAds();
        }
    }


    protected void showInterFileAds() {
        AdmobManager.getInstance().showInterstitial(this,
                StorageCommon.getInstance().getInterImage2Pdf(), new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        loadInterFileAds();
                    }
                });
    }

    protected void loadInterFileAds() {
        AdmobManager.getInstance().loadInterAds(this, BuildConfig.inter_image2pdf, new AdCallback() {
            @Override
            public void interCallback(InterstitialAd interstitialAd) {
                super.interCallback(interstitialAd);
                StorageCommon.getInstance().setInterImage2Pdf(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                super.onAdFailedToLoad(i);
                StorageCommon.getInstance().setInterImage2Pdf(null);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media;
    }

    @Override
    protected void initView() {
        initList();
        loadData();
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_image2pdf);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadData() {
        new LoadImageFile(this, (key, data) -> {
            binding.progress.setVisibility(View.GONE);
            List<File> files = (List<File>) data;
/*//            for (int i = 0; i < files.size() - 1; i++) {
//                for (int j = i; j < files.size(); j++) {
//                    if (files.get(i).lastModified() > files.get(j).lastModified()) {
//                        Collections.swap(files, i, j);
//                    }
//                }
//            }*/
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                list.add(new Media(file.lastModified(),
                        file.getPath(),
                        file.getPath(),
                        file.getName(),
                        "", file.length(),
                        "", null
                ));
            }
            Collections.reverse(list);
            adapter.notifyDataSetChanged();
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initList() {
        adapter = new MediaAdapter(this, list, this);
        binding.recycleView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recycleView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void addEvent() {
        binding.llCreate.setOnClickListener(v -> {
            CreatePdfDialog dialog = new CreatePdfDialog(this);
            dialog.setCallback((key, data) -> {
                String name = (String) data;
                createImage2Pdf(name);
            });
            dialog.show();
        });
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createImage2Pdf(String name) {
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < listChecked.size(); i++) {
            paths.add(listChecked.get(i).getImage());
        }
        File file = new File(Const.BASE_PATH_2);
        if (!file.exists()) {
            file.mkdirs();
        }
        new ImageToPdf(paths, this, (key, data) -> {
            for (int i = 0; i < listChecked.size(); i++) {
                listChecked.get(i).setChecked(false);
            }
            listChecked.clear();
            adapter.notifyDataSetChanged();

            ItemFile itemFile = (ItemFile) data;
            openPdfFile(itemFile.getPath());
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(itemFile.getPath()))));
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Const.BASE_PATH_2 + "/" + name + ".pdf");
    }


    @Override
    public void onSelected(@NonNull ArrayList<Media> itemChecked) {
        this.listChecked = itemChecked;
        if (listChecked.size() == 0) {
            binding.viewBottom.setVisibility(View.GONE);
            return;
        }
        binding.viewBottom.setVisibility(View.VISIBLE);
    }
}
