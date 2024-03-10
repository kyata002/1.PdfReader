package com.kyata.pdfreader.view.activity;

import static com.kyata.pdfreader.view.adapter.ListFileAdapter.ACTION_UPDATE_FAVOURITE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.admob.control.AdmobManager;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.Constants;
import com.github.barteksc.pdfviewer.util.FileUtils;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.kyata.pdfreader.App;
import com.kyata.pdfreader.BuildConfig;
import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.ActivityPdfViewerBinding;
import com.kyata.pdfreader.model.FavoriteFile;
import com.kyata.pdfreader.utils.CommonUtils;
import com.kyata.pdfreader.utils.SharePreferenceUtils;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.dialog.JumpDialog;
import com.shockwave.pdfium.PdfPasswordException;

import java.io.File;
import java.util.List;

public class PdfReaderActivity extends BaseActivity<ActivityPdfViewerBinding> {
    public static final String SAVED_STATE = "prefs_saved_state";
    public static final String KEY_PREFS_STAY_AWAKE = "prefs_stay_awake";
    private final String TAG = PdfReaderActivity.class.getSimpleName();
    private int pageNumber;
    private FitPolicy fitPolicy;
    private final Handler mHideHandler = new Handler();
    private String mPassword = "1111", pdfFileLocation = "";
    private SharedPreferences sharedPreferences;
    private boolean mVisible = true;
    private boolean AUTO_HIDE;
    private boolean swipeHorizontalEnabled;
    //    private Uri uri;
    private final Runnable mHideRunnable = PdfReaderActivity.this::hide;
    private boolean nightMode;
    private boolean isFavourite;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pdf_viewer;
    }


    @Override
    public void initView() {
        logEvent("ACTIVITY_SHOW", "NAME_ACTIVITY", "READ PDF");

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean stayAwake = this.sharedPreferences.getBoolean(KEY_PREFS_STAY_AWAKE, true);
        int i = 0;
        this.AUTO_HIDE = this.sharedPreferences.getBoolean("prefs_auto_full_screen", false);
        Constants.THUMBNAIL_RATIO = 0.7f;
        Intent intent = getIntent();
        this.pdfFileLocation = intent.getStringExtra(Const.PDF_LOCATION);
        isFavourite = checkFavourite(pdfFileLocation);
        if (isFavourite) {
            binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading_checked);
        }
//        this.uri = intent.getData();
//        Log.d("dsk", "uri: " + uri);
        this.binding.pdfView.setKeepScreenOn(stayAwake);

        this.pageNumber = i;
        this.swipeHorizontalEnabled = SharePreferenceUtils.isHorizontal(this);

        if (this.swipeHorizontalEnabled) {
            binding.ivSwipe.setRotation(0);
            binding.tvSwipe.setText(getString(R.string.horizontal));
        } else {
            binding.ivSwipe.setRotation(90);
            binding.tvSwipe.setText(getString(R.string.vertical));
        }

        int currentPage = SharePreferenceUtils.getCurrentPageReading(this, pdfFileLocation);
        if (currentPage == -1) {
            currentPage = 0;
        }
        this.fitPolicy = (this.swipeHorizontalEnabled) ? FitPolicy.HEIGHT : FitPolicy.WIDTH;
        loadSelectedPdfFile(this.mPassword, currentPage - 1, this.swipeHorizontalEnabled, false, fitPolicy);
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_reader);
    }

    private boolean checkFavourite(String pdfFileLocation) {
        try {
            List<FavoriteFile> list = App.getInstance().getDatabase().favoritetDao().getList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPath().equals(pdfFileLocation)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addEvent() {
        binding.pdfView.setOnClickListener(v -> toggle());
        binding.btSwipe.setOnClickListener(v -> executeSwipe());
        binding.btMode.setOnClickListener(v -> setNightMode());

        binding.btJump.setOnClickListener(v -> jumpToPageOfPdf());

//        binding.ivShareAsPicture.setOnClickListener(v -> sharePdfAsPicture());
        binding.btPrint.setOnClickListener(v -> printPdf());

        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.btShare.setOnClickListener(v -> sharePdf());
        binding.btFavourite.setOnClickListener(v -> executeFavourite());
    }

    private void executeFavourite() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "FAVORITE_CLICK");
        setUserProperty("CLICK_FAVOURITE_AT_READER");
        if (isFavourite) {
            App.getInstance().getDatabase().favoritetDao().delete(pdfFileLocation);
            if (nightMode) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading_dark);
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading);
            }
        } else {
            App.getInstance().getDatabase().favoritetDao().add(new FavoriteFile(pdfFileLocation));
            binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading_checked);
        }
        isFavourite = !isFavourite;

        Intent intent = new Intent(ACTION_UPDATE_FAVOURITE);
        intent.putExtra("data", pdfFileLocation);
        intent.putExtra("value", isFavourite);
        sendBroadcast(intent);
    }

    private void setNightMode() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "DARK_CLICK");
        setUserProperty("CLICK_LIGHT");
        nightMode = !nightMode;
        binding.pdfView.setNightMode(nightMode);
        binding.pdfView.invalidate();
        if (nightMode) {
            binding.tvMode.setText(getString(R.string.dark));
            binding.ivNightMode.setImageResource(R.drawable.ic_daymode);

            binding.llBottom.setSelected(true);
            binding.llHeader.setSelected(true);
            getWindow().setStatusBarColor(Color.parseColor("#323131"));
            if (!isFavourite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading_dark);
            }
        } else {
            binding.tvMode.setText(getString(R.string.light));

            binding.ivNightMode.setImageResource(R.drawable.ic_night_mode);

            binding.llBottom.setSelected(false);
            binding.llHeader.setSelected(false);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_toolbar));

            if (!isFavourite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favourite_reading);
            }
        }

        SharePreferenceUtils.setNightMode(this, nightMode);
    }


    public void onDestroy() {
        this.sharedPreferences.edit().putInt(SAVED_STATE, 0).apply();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    public void loadSelectedPdfFile(String str, int i, boolean z, boolean z2, FitPolicy fitPolicy) {
        if (!TextUtils.isEmpty(this.pdfFileLocation)) {
            File file = new File(pdfFileLocation);
            binding.tvTitle.setText(file.getName());
            DefaultScrollHandle scroll=null;
            if (z) {
                scroll = null;
            }
            this.binding.pdfView.fromFile(file).password(str).enableAnnotationRendering(true)
                    .spacing(6).defaultPage(i).swipeHorizontal(z).autoSpacing(z).pageFling(z).pageSnap(z)
                    .nightMode(z2).onPageChange(this.onPageChangeListener).onLoad(this.onLoadCompleteListener)
                    .onError(this.onErrorListener).scrollHandle(scroll).load();
        }

    }


    private final OnErrorListener onErrorListener = th -> {
        if (th instanceof PdfPasswordException) {
            enterPasswordDialog();
            return;
        }
        binding.progressOpenPdf.setVisibility(View.GONE);
    };
    private final OnLoadCompleteListener onLoadCompleteListener = i -> {
        binding.progressOpenPdf.setVisibility(View.GONE);
        binding.tvPdfPageNumbers.setVisibility(View.VISIBLE);
        nightMode = !SharePreferenceUtils.getNightMode(this);
        setNightMode();
    };
    private final OnPageChangeListener onPageChangeListener = (i, i2) -> {
        String sb = (i + 1) +
                " / " +
                i2;
        binding.tvPdfPageNumbers.setText(sb);
    };


    private void executeSwipe() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "DIRECTION_CLICK");
        setUserProperty("CLICK_HORIZONTAL_VERTICAL");
        if (!this.swipeHorizontalEnabled) {
            loadSelectedPdfFile(this.mPassword, this.binding.pdfView.getCurrentPage(), !this.swipeHorizontalEnabled, nightMode, FitPolicy.HEIGHT);
            Toast.makeText(this, getString(R.string.hor_enable), Toast.LENGTH_SHORT).show();
            binding.ivSwipe.setRotation(0);
            binding.tvSwipe.setText(getString(R.string.horizontal));

        } else {
            loadSelectedPdfFile(this.mPassword, this.binding.pdfView.getCurrentPage(), !this.swipeHorizontalEnabled, nightMode, FitPolicy.WIDTH);
            Toast.makeText(this, getString(R.string.ver_enable), Toast.LENGTH_SHORT).show();
            binding.ivSwipe.setRotation(90);
            binding.tvSwipe.setText(getString(R.string.vertical));
        }
        binding.pdfView.invalidate();
        swipeHorizontalEnabled = !swipeHorizontalEnabled;
        SharePreferenceUtils.setHorizontal(this, swipeHorizontalEnabled);
    }


    private void jumpToPageOfPdf() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "JUMP_CLICK");
        setUserProperty("CLICK_JUMP");
        JumpDialog.start(this, new OnActionCallback() {
            @Override
            public void callback(String key, Object data) {
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int number = (int) data;
                        binding.pdfView.jumpTo(number - 1, true);
                    }
                }, 200);
            }
        });
    }


    private void enterPasswordDialog() {
        float f = getResources().getDisplayMetrics().density;
        final EditText editText = new EditText(this);
        editText.setHint(getString(R.string.enter_password));
        editText.setInputType(129);
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.password_protected)).setPositiveButton(R.string.ok, null).setCancelable(false).setNegativeButton(R.string.cancel, (dialogInterface, i) -> finish());
        final AlertDialog create = builder.create();
        int i = (int) (24.0f * f);
        create.setView(editText, i, (int) (8.0f * f), i, (int) (f * 5.0f));
        create.show();
        create.getButton(-1).setOnClickListener(view -> {
            mPassword = editText.getText().toString();
            if (!TextUtils.isEmpty(mPassword)) {
                try {
                    loadSelectedPdfFile(mPassword, pageNumber, swipeHorizontalEnabled, false, fitPolicy);
                    create.dismiss();
                } catch (Exception e) {
                    editText.setError(getString(R.string.invalid_password));
                }
            } else {
                editText.setError(getString(R.string.invalid_password));
                Log.d(TAG, "Invalid Password");
            }
        });
    }

    private boolean isValidPageNumber(String str) {
        boolean z = false;
        if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) {
            return false;
        }
        int pageCount = this.binding.pdfView.getPageCount();
        try {
            int intValue = Integer.parseInt(str);
            if (intValue > 0 && intValue <= pageCount) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void toggle() {
        if (this.mVisible) {
            hide();
            return;
        }
        show();
        if (this.AUTO_HIDE) {
            delayedHide();
        }
    }

    private void hide() {
        this.mVisible = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.ll_header).setVisibility(View.GONE);
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }

    @SuppressLint({"InlinedApi"})
    private void show() {
        this.mVisible = true;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.ll_header).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_bottom).setVisibility(View.VISIBLE);
    }

    private void delayedHide() {
        this.mHideHandler.removeCallbacks(this.mHideRunnable);
        this.mHideHandler.postDelayed(this.mHideRunnable, 10000);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printPdf() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "PRINT_CLICK");
        setUserProperty("CLICK_PRINT");

        try {
            FileUtils.printPdfFile(this, pdfFileLocation);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdf() {
        logEvent("VIEW_CLICK", "PDF_EVENT", "SHARE_CLICK");
        if (pdfFileLocation == null) {
            return;
        }
        CommonUtils.getInstance().shareFile(this, new File(pdfFileLocation));
    }


    @Override
    public void onBackPressed() {
        SharePreferenceUtils.setCurrentPageReading(this, pdfFileLocation, binding.pdfView.getCurrentPage());
        super.onBackPressed();
    }
}
