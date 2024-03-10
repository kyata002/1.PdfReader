package com.kyata.pdfreader.view.activity;

import static com.documentmaster.documentscan.extention.ViewExtensionKt.disableFocus;
import static com.documentmaster.documentscan.extention.ViewExtensionKt.enableFocus;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.admob.control.AdmobManager;
import com.admob.control.funtion.AdCallback;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.kyata.pdfreader.BuildConfig;
import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.StorageCommon;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.ActivityMainBinding;
import com.kyata.pdfreader.model.Category;
import com.kyata.pdfreader.model.ItemFile;
import com.kyata.pdfreader.model.Language;
import com.kyata.pdfreader.task.LoadPdfFile;
import com.kyata.pdfreader.utils.CommonUtils;
import com.kyata.pdfreader.utils.LanguageUtils;
import com.kyata.pdfreader.utils.SharePreferenceUtils;
import com.kyata.pdfreader.view.OnCommonCallback;
import com.kyata.pdfreader.view.adapter.CustomFragmentPagerAdapter;
import com.kyata.pdfreader.view.adapter.ListFileAdapter;
import com.kyata.pdfreader.view.fragment.AllFileFragment;
import com.kyata.pdfreader.view.fragment.FavouriteFragment;
import com.kyata.pdfreader.view.fragment.RecentFileFragment;
import com.rate.control.OnCallback;
import com.rate.control.dialog.RateAppBlueDialog;
import com.subvip.SubActivity;
import com.subvip.manage.PurchaseManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    public static final String ACTION_SORT_SIZE = "action_sort_size";
    public static final String ACTION_SORT_DATE = "action_sort_date";
    public static final String ACTION_SORT_NAME = "action_sort_name";
    public static final String ACTION_COMPLETE = "action_complete";
    public static final String ACTION_READING = "action_reading";
    public static final String ACTION_NEW = "action_new";
    public static final String ACTION_ALL_FILE = "action_all_file";
    public static final String ACTION_SEARCH_MAIN = "action_search";
    public static final String SEARCH_UPDATE = "search_update";

    public static List<Category> categoryList = new ArrayList<>();
    public static List<Category> categorySearchList = new ArrayList<>();
    public static final int ITEM_ALL_FILE = 0;
    public static final int ITEM_RECENT = 1;
    public static final int ITEM_FAVOURITE = 2;

    private static final int ALL_FILE = 0;
    private static final int RECENT = 1;
    private static final int FAVORITE = 2;
    private int state = ALL_FILE;

    private Handler handler;
    private boolean isSeaching;
    private ListFileAdapter adapterSearch;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Language language = LanguageUtils.getCurrentLanguage();
        Glide.with(this).load("android_asset/flag/" + language.getCode() + ".webp").into(binding.menu.ivLanguage);

        if (StorageCommon.getInstance().getInterFile() == null) {
            loadInterFileAds();
        }
        if (PurchaseManager.getInstance().isPremiumed(this)) {
            binding.main.ivRemoveAds.setVisibility(View.GONE);
//            binding.menu.llRemoveAds.setVisibility(View.GONE);
        }
        disableFocus(binding.main.edtSearch);
//        binding.menu.swNightMode.setChecked(SharePreferenceUtils.isNightModeApp(this));
    }

    @Override
    protected void initView() {
        initPermissionReceiver();
        initSearchReceiver();
        updateState();
        initNativeAds();
        initViewPager();
        reloadData();
    }

    private void initPermissionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SplashActivity.ACTION_FINISH);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SplashActivity.ACTION_FINISH)) {
                    finish();
                }
            }
        }, filter);
    }

    private void initSearchReceiver() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int numAllFile = 0;
                int numRecent = 0;
                int numFavourite = 0;
                try {
                    numAllFile = categorySearchList.get(ITEM_ALL_FILE).getList().size();
                    numRecent = categorySearchList.get(ITEM_RECENT).getList().size();
                    numFavourite = categorySearchList.get(ITEM_FAVOURITE).getList().size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (numAllFile == 0 && numRecent == 0 && numFavourite == 0) {
                    binding.main.llEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.main.llEmpty.setVisibility(View.GONE);
                }

                setStateSearch(binding.main.llSearchRecent, numRecent);
                setStateSearch(binding.main.llSearchFavorite, numFavourite);
                setStateSearch(binding.main.llSearchAllFile, numAllFile);

                try {
                    if (binding.main.llSearchAllFile.isSelected()) {
                        adapterSearch.updateList(categorySearchList.get(ITEM_ALL_FILE).getList());
                    } else if (binding.main.llSearchRecent.isSelected()) {
                        adapterSearch.updateList(categorySearchList.get(ITEM_RECENT).getList());
                    } else {
                        adapterSearch.updateList(categorySearchList.get(ITEM_FAVOURITE).getList());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            private void setStateSearch(View view, int number) {
                if (number == 0) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }, new IntentFilter(SEARCH_UPDATE));
    }

    private void initNativeAds() {
        AdmobManager.getInstance().loadUnifiedNativeAd(this, BuildConfig.native_main, new AdCallback() {
            @Override
            public void onNativeAds(NativeAd nativeAd) {
                super.onNativeAds(nativeAd);
                @SuppressLint("InflateParams") NativeAdView nativeAdView =
                        (NativeAdView) LayoutInflater.from(MainActivity.this).inflate(com.admob.control.R.layout.custom_native,
                                null);
                AdmobManager.getInstance().populateUnifiedNativeAdView(nativeAd, nativeAdView);
                binding.main.frAds.removeAllViews();
                binding.main.frAds.addView(nativeAdView);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                super.onAdFailedToLoad(i);
                binding.main.frAds.setVisibility(View.GONE);
            }
        });
    }

    private void reloadData() {
        if (MainActivity.categoryList == null || MainActivity.categoryList.size() == 0) {
            updateData();
        } else {
            updateUI();
        }
    }


    @Override
    protected void addEvent() {
        binding.main.llAllFile.setOnClickListener(view -> {
            logEvent("VIEW_CLICK", "EVENT", "TAB_ALL_CLICK");
            binding.main.viewPager.setCurrentItem(ITEM_ALL_FILE);
        });

        binding.main.llRecent.setOnClickListener(view -> {
            logEvent("VIEW_CLICK", "EVENT", "TAB_RECENT_CLICK");
            binding.main.viewPager.setCurrentItem(ITEM_RECENT);
        });
        binding.main.llFavorite.setOnClickListener(view -> {
            logEvent("VIEW_CLICK", "EVENT", "TAB_FAVORITE_CLICK");
            binding.main.viewPager.setCurrentItem(ITEM_FAVOURITE);
        });


        binding.main.ivBrowse.setOnClickListener(v -> {
            logEvent("VIEW_CLICK", "EVENT", "FOLDER_CLICK");
            setUserProperty("CLICK_FILE_SELECTION");
            startActivity(new Intent(this, BrowseActivity.class));
        });
        binding.main.ivRemoveAds.setOnClickListener(v -> startActivity(new Intent(this, SubActivity.class)));

        binding.main.ivCreateFromImage.setOnClickListener(v -> {
            logEvent("VIEW_CLICK", "EVENT", "CREATE_PDF_CLICK");
            setUserProperty("CLICK_IMAGE_TO_PDF");

            startActivity(new Intent(this, PickPhotoActivity.class));
        });

        binding.main.ivOption.setOnClickListener(v -> {
            logEvent("VIEW_CLICK", "EVENT", "SORT_CLICK");
            showPopupMenu(binding.main.ivOption);
        });

        binding.menu.llCommonIssues.setOnClickListener(v -> {
            logEvent("VIEW_CLICK", "EVENT", "DIRECTION_CLICK");
            setUserProperty("CLICK_COMMON_ISSUES");
            CommonUtils.getInstance().openWeb(this, CommonUtils.ISSUES_URL);
            closeMenu();
        });
        binding.menu.llFeedback.setOnClickListener(v -> {
            CommonUtils.getInstance().support(this);
            setUserProperty("CLICK_FEEDBACK_US");
            closeMenu();
        });
        binding.menu.llLanguage.setOnClickListener(v -> {
            setUserProperty("CLICK_LANGUAGE");
            LanguageActivity.start(this);
            closeMenu();
        });
        binding.menu.llPolicy.setOnClickListener(v -> {
            setUserProperty("CLICK_PRIVACY");
            CommonUtils.getInstance().showPolicy(this);
            closeMenu();
        });
        binding.menu.llRate.setOnClickListener(v -> showRateDialog(false));
        binding.menu.llTipReading.setOnClickListener(v -> {
            setUserProperty("CLICK_TIP_READING");
            CommonUtils.getInstance().openWeb(this, CommonUtils.TIP_URL);
            closeMenu();
        });
//        binding.menu.llRemoveAds.setOnClickListener(v -> {
//            setUserProperty("CLICK_REMOVE_AD");
//            startActivity(new Intent(this, SubActivity.class));
//            closeMenu();
//        });

        // banner_read, native_main
        binding.main.ivMenu.setOnClickListener(v -> {
            logEvent("VIEW_CLICK", "EVENT", "SETTING_CLICK");
            binding.drawer.openDrawer(GravityCompat.START);
            disableFocus(binding.main.edtSearch);
            hideKeyboard(binding.main.edtSearch);
        });

        binding.menu.llTheme.setOnClickListener(v -> {
            if (SharePreferenceUtils.isNightModeApp(this)) {
                SharePreferenceUtils.setNightModeApp(this, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                SharePreferenceUtils.setNightModeApp(this, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        if (SharePreferenceUtils.isRated(this)) {
            binding.menu.llRate.setVisibility(View.GONE);
        }
        binding.menu.rating.setOnRatingChangedListener((v, v1) -> {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            handler = new Handler();
            Runnable rd = () -> showRateDialog(false);
            handler.postDelayed(rd, 200);
        });
        eventSearch();
    }

    private void eventSearch() {
        binding.main.llEmpty.setVisibility(View.GONE);
        binding.main.llSearchAllFile.setSelected(true);
        disableFocus(binding.main.edtSearch);
        binding.main.edtSearch.setOnHideKeyboardListener(() -> {
            disableFocus(binding.main.edtSearch);
        });
        binding.main.ivBackSearch.setOnClickListener(v -> {
            if (isSeaching) {
                onBackPressed();
            }
        });
        binding.main.edtSearch.setOnClickListener(v -> {
            setUserProperty("CLICK_SEARCH_AT_HOME");
            prepairSearch();
        });

        binding.main.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                disableFocus(binding.main.edtSearch);
            }
            return false;
        });
        binding.main.edtSearch.addTextChangedListener(new OnCommonCallback() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                search(s.toString());
            }
        });

        binding.main.ivCancel.setOnClickListener(v -> binding.main.edtSearch.setText(""));


        binding.main.llSearchAllFile.setOnClickListener(view -> {
            binding.main.llSearchAllFile.setSelected(true);
            binding.main.llSearchRecent.setSelected(false);
            binding.main.llSearchFavorite.setSelected(false);
            try {
                adapterSearch.updateList(categorySearchList.get(ITEM_ALL_FILE).getList());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        binding.main.llSearchRecent.setOnClickListener(view -> {
            binding.main.llSearchAllFile.setSelected(false);
            binding.main.llSearchRecent.setSelected(true);
            binding.main.llSearchFavorite.setSelected(false);
            try {
                adapterSearch.setRecentTab(true);
                adapterSearch.updateList(categorySearchList.get(ITEM_RECENT).getList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.main.llSearchFavorite.setOnClickListener(view -> {
            binding.main.llSearchAllFile.setSelected(false);
            binding.main.llSearchRecent.setSelected(false);
            binding.main.llSearchFavorite.setSelected(true);
            try {
                adapterSearch.setFavouriteTab(true);
                adapterSearch.updateList(categorySearchList.get(ITEM_FAVOURITE).getList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void prepairSearch() {
        categorySearchList.clear();
        categorySearchList.addAll(categoryList);
        isSeaching = true;
        enableFocus(binding.main.edtSearch);
        showKeyboard(binding.main.edtSearch);
        binding.main.frSearch.setVisibility(View.VISIBLE);
        binding.main.ivBackSearch.setImageResource(R.drawable.ic_back);
        initViewSearch();


    }

    private void initViewSearch() {
        try {
            binding.main.rvSearch.setLayoutManager(new LinearLayoutManager(this));
            adapterSearch = new ListFileAdapter(categorySearchList.get(ITEM_ALL_FILE).getList(), this);
            adapterSearch.setmCallback((key, data) -> {
                if (key.equals(Const.KEY_CLICK_ITEM)) {
                    ItemFile itemFile = (ItemFile) data;
                    openPdfFile(itemFile.getPath());
                }
            });
            binding.main.rvSearch.setAdapter(adapterSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cancelSearch() {
        binding.main.edtSearch.setText("");
        disableFocus(binding.main.edtSearch);
        hideKeyboard(binding.main.edtSearch);
        binding.main.frSearch.setVisibility(View.GONE);
        binding.main.ivCancel.setVisibility(View.INVISIBLE);
        isSeaching = false;
        binding.main.ivBackSearch.setImageResource(R.drawable.ic_search);

        binding.main.llSearchAllFile.setSelected(true);
        binding.main.llSearchRecent.setSelected(false);
        binding.main.llSearchFavorite.setSelected(false);

    }

    private void search(String text) {
        Intent intent = new Intent(ACTION_SEARCH_MAIN);
        intent.putExtra("data", text);
        sendBroadcast(intent);

        if (TextUtils.isEmpty(text)) {
            binding.main.ivCancel.setVisibility(View.INVISIBLE);
            return;
        }
        binding.main.ivCancel.setVisibility(View.VISIBLE);
    }

    private void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void closeMenu() {
        binding.drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressLint("NonConstantResourceId")
    private void menuItemClicked(int id) {
        switch (id) {
            case R.id.bt_sort_size:
                StorageCommon.getInstance().setSortType(StorageCommon.SORT_TYPE_SIZE);
                sendBroadcast(new Intent(ACTION_SORT_SIZE));
                break;
            case R.id.bt_sort_date:
                StorageCommon.getInstance().setSortType(StorageCommon.SORT_TYPE_DATE);
                sendBroadcast(new Intent(ACTION_SORT_DATE));
                break;
            case R.id.bt_sort_name:
                StorageCommon.getInstance().setSortType(StorageCommon.SORT_TYPE_NAME);
                sendBroadcast(new Intent(ACTION_SORT_NAME));

                break;
            case R.id.bt_all:
                StorageCommon.getInstance().setFilterType(StorageCommon.FILTER_ALL);
                sendBroadcast(new Intent(ACTION_ALL_FILE));

                break;
            case R.id.bt_reading:
                StorageCommon.getInstance().setFilterType(StorageCommon.FILTER_READING);
                sendBroadcast(new Intent(ACTION_READING));
                break;
            case R.id.bt_completed:
                StorageCommon.getInstance().setFilterType(StorageCommon.FILTER_COMPLETED);
                sendBroadcast(new Intent(ACTION_COMPLETE));
                break;
            case R.id.bt_new:
                StorageCommon.getInstance().setFilterType(StorageCommon.FILTER_NEW);
                sendBroadcast(new Intent(ACTION_NEW));
                break;
        }

    }


    private void updateState() {
        binding.main.llAllFile.setSelected(false);
        binding.main.llRecent.setSelected(false);
        binding.main.llFavorite.setSelected(false);
        if (state == ALL_FILE) {
            binding.main.llAllFile.setSelected(true);
            binding.main.ivOption.setVisibility(View.VISIBLE);
            return;
        }
        if (state == RECENT) {
            binding.main.llRecent.setSelected(true);
            binding.main.ivOption.setVisibility(View.GONE);
            return;
        }
        if (state == FAVORITE) {
            binding.main.llFavorite.setSelected(true);
            binding.main.ivOption.setVisibility(View.GONE);
        }
    }

    private int index = 3;

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view) {
        LinearLayout viewGroup = findViewById(R.id.llSortChangePopup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_sort, viewGroup);

        RadioGroup radioGroup = layout.findViewById(R.id.rd_group);
        RadioButton r = (RadioButton) radioGroup.getChildAt(index);
        if (r == null) {
            return;
        }
        r.setChecked(true);

        PopupWindow changeSortPopUp = new PopupWindow(this);
        changeSortPopUp.setContentView(layout);
        changeSortPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeSortPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeSortPopUp.setFocusable(true);
        changeSortPopUp.setBackgroundDrawable(new BitmapDrawable());


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rd1:
                    index = 0;
                    Intent intent = new Intent(ACTION_SORT_NAME);
                    intent.putExtra("alpha", true);
                    sendBroadcast(intent);
                    break;
                case R.id.rd2:
                    index = 1;
                    intent = new Intent(ACTION_SORT_NAME);
                    intent.putExtra("alpha", false);
                    sendBroadcast(intent);
                    break;
                case R.id.rd3:
                    sendBroadcast(new Intent(ACTION_SORT_SIZE));
                    index = 2;
                    break;
                case R.id.rd4:
                    index = 3;
                    sendBroadcast(new Intent(ACTION_SORT_DATE));
                    break;
            }
            changeSortPopUp.dismiss();
        });

        changeSortPopUp.showAsDropDown(view);
    }


    private void initViewPager() {
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllFileFragment(), getString(R.string.menu_all_file));
        adapter.addFragment(new RecentFileFragment(), getString(R.string.menu_recent_file));
        adapter.addFragment(new FavouriteFragment(), getString(R.string.menu_favorite));
        binding.main.viewPager.setPagingEnabled(true);
        binding.main.viewPager.setAdapter(adapter);
        binding.main.viewPager.setPagingEnabled(false);
        binding.main.viewPager.setOffscreenPageLimit(4);
        binding.main.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("CheckResult")
            @Override
            public void onPageSelected(int position) {
                state = position;
                updateState();
//                sendBroadcast(new Intent(ACTION_UPDATE_DATA));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateData() {
        binding.main.progressCircular.setVisibility(View.VISIBLE);
        LoadPdfFile loadPdfFile = new LoadPdfFile(this);
        loadPdfFile.setCallback((key, data) -> {
            Log.d("TAG", "updateData: " + categoryList);
            updateUI();
        });
        loadPdfFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void updateUI() {
        try {
            binding.main.progressCircular.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_OPEN_FILE) {
            SharePreferenceUtils.isRated(this);
        }
    }

    @Override
    public void callback(String key, Object data) {
        if (key.equals(Const.KEY_CLICK_ITEM)) {
            ItemFile itemFile = (ItemFile) data;
            Intent intent = new Intent(this, PdfReaderActivity.class);
            intent.putExtra(Const.PDF_LOCATION, itemFile.getPath());
            startActivity(intent);
            addRecent(itemFile);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (isSeaching) {
            cancelSearch();
            return;
        }
        if (SharePreferenceUtils.shouldShowRatePopup(this)) {
            showRateDialog(true);
            return;
        }
        executeBack();
    }

    private void showRateDialog(boolean isFinish) {
        RateAppBlueDialog dialog = new RateAppBlueDialog(this);
        dialog.setCallback(new OnCallback() {
            @Override
            public void onMaybeLater() {
                if (isFinish) {
                    SharePreferenceUtils.increaseCountRate(MainActivity.this);
                    finishAffinity();
                }
            }

            @Override
            public void onSubmit(String review) {
                Toast.makeText(MainActivity.this, R.string.thank_you, Toast.LENGTH_SHORT).show();
                SharePreferenceUtils.setRated(MainActivity.this);
                if (isFinish) {
                    finishAffinity();
                }
            }

            @Override
            public void onRate() {
                CommonUtils.getInstance().rateApp(MainActivity.this);
                SharePreferenceUtils.setRated(MainActivity.this);
            }
        });
        dialog.show();
    }


    boolean doubleBackToExitPressedOnce = false;

    private void executeBack() {
        if (doubleBackToExitPressedOnce) {
            SharePreferenceUtils.increaseCountRate(MainActivity.this);
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.clcik_back_again), Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}