package com.kyata.pdfreader.view.activity;

import static com.wxiwei.office.constant.MainConstant.INTENT_FILED_FILE_PATH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.admob.control.AdmobManager;
import com.github.barteksc.pdfviewer.source.FileSource;
import com.github.barteksc.pdfviewer.util.FileUtils;
import com.kyata.pdfreader.BuildConfig;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.utils.CommonUtils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.wxiwei.office.officereader.BaseDocActivity;
import com.wxiwei.office.wp.scroll.ScrollBarView;

import java.io.File;
import java.io.IOException;

public class DocReaderActivity extends BaseDocActivity {
    private String path;
    private TextView tvPage;
    private ImageView btPrint;
    //    private final List<MySlide> list = new ArrayList<>();
//    private LinearLayoutCompat btSlideShow;
//    private FrameLayout frSlide;
//    private RecyclerView rvSlide;
    //    private SlideAdapter adapter;
    private TextView tvScrollPage;
    private TextView tvZoom;

    public static void start(Context context, String path) {
        Intent starter = new Intent(context, DocReaderActivity.class);
        starter.putExtra(INTENT_FILED_FILE_PATH, path);
        context.startActivity(starter);
    }

//    public static void start(Context context, String path, Category category) {
//        Intent starter = new Intent(context, DocReaderActivity.class);
//        starter.putExtra(INTENT_FILED_FILE_PATH, path);
//        starter.putExtra("cat", category);
//        context.startActivity(starter);
//    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reader;
    }

    @Override
    protected FrameLayout getFrameLayoutDoc() {
        return findViewById(R.id.fr_doc);
    }

    @Override
    protected ScrollBarView getScrollBarView() {
        return findViewById(R.id.scrollView);
    }

    @Override
    protected void initView(String path) {
        this.path = path;
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_reader);
        TextView title = findViewById(R.id.tv_title);
        btPrint = findViewById(R.id.bt_print);
        tvPage = findViewById(R.id.tv_page);
//        btSlideShow = findViewById(R.id.bt_slide_show);
//        frSlide = findViewById(R.id.fr_slide);
//        rvSlide = findViewById(R.id.rvSlide);
        tvZoom = findViewById(R.id.tvZoom);
        tvScrollPage = findViewById(R.id.tvScrollPage);

//        adapter = new SlideAdapter(list, DocReaderActivity.this);
//        adapter.setmCallback(new OnActionCallback() {
//            @Override
//            public void callback(String key, Object data) {
//                MySlide slide = (MySlide) data;
//                int index = list.indexOf(slide);
//                gotoSlide(index);
//                updateList(index);
//            }
//        });
//        rvSlide.setLayoutManager(new LinearLayoutManager(DocReaderActivity.this, LinearLayoutManager.HORIZONTAL, false));
//        rvSlide.setAdapter(adapter);

//        ConstraintLayout layoutToolbar = findViewById(R.id.layoutToolbar);
//        Category category = (Category) getIntent().getSerializableExtra("cat");
//        if (category != null) {
//            String colorHex = category.getColorTint();
//            tvPage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorHex)));
//            layoutToolbar.setBackgroundColor(Color.parseColor(colorHex));
//            getWindow().setStatusBarColor(Color.parseColor(colorHex));
//        }

        title.setText(new File(path).getName());
        if (path.toLowerCase().endsWith(".pdf")) {
//            tvPage.setVisibility(View.VISIBLE);
            btPrint.setVisibility(View.VISIBLE);
        }
        if (path.toLowerCase().endsWith(".xls") || path.toLowerCase().endsWith(".xlsx")) {
            tvPage.setVisibility(View.GONE);
        }
        addEvent();


    }

    @Override
    protected void showPageToast() {
        tvScrollPage.setVisibility(View.VISIBLE);
        tvPage.setVisibility(View.GONE);
    }

    @Override
    protected void hidePageToast() {
        tvScrollPage.setVisibility(View.GONE);
        tvPage.setVisibility(View.VISIBLE);
    }

    private void addEvent() {
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.bt_share).setOnClickListener(v -> {
            CommonUtils.getInstance().shareFile(this, new File(path));
        });
        btPrint.setOnClickListener(view -> FileUtils.printPdfFile(this, path));
    }

    private void updateList(int index) {
//        try {
//            int currentSelected = getCurrentSelected();
//            list.get(currentSelected).setSelected(false);
//            list.get(index).setSelected(true);
//            adapter.notifyItemChanged(currentSelected);
//            adapter.notifyItemChanged(index);
//
//            rvSlide.scrollToPosition(index);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private int getCurrentSelected() {
        int pos = 0;
//        for (MySlide sl : list
//        ) {
//            if (sl.isSelected()) {
//                return list.indexOf(sl);
//            }
//        }
        return pos;

    }


    @Override
    public void changeZoom(int percent) {
        tvZoom.setVisibility(View.VISIBLE);
        tvZoom.setText(percent + " %");
    }

    @Override
    public void onError(int errorCode) {
//        if (errorCode == ERROR_PDF) {
//            PasswordDialog passDialog = PasswordDialog.newInstance();
//            passDialog.setCallback(new OnActionCallback() {
//                @Override
//                public void callback(String key, Object data) {
//                    if (key != null && key.equals("cancel")) {
//                        finish();
//                        return;
//                    }
//                    String pass = (String) data;
//                    if (!passwordCorrect(pass)) {
//                        Toast.makeText(DocReaderActivity.this, "The password you entered is incorrect", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    readPdfFile(pass);
//                    passDialog.dismiss();
//                }
//            });
//            passDialog.show(getSupportFragmentManager(), null);
//        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void pageChanged(int page, int pageCount) {
        String currentPage = page + "/" + pageCount;
        tvScrollPage.setText(currentPage);
        updateList(page - 1);
        tvPage.setText(currentPage);
    }


    @Override
    public void openFileFinish() {
        super.openFileFinish();

        try {
//            int count = getPresentation().getSlideCount();
//            if (count == 0) {
//                return;
//            }
//            btSlideShow.setVisibility(View.VISIBLE);
//            frSlide.setVisibility(View.VISIBLE);
//            list.clear();
//            for (int i = 0; i < count; i++) {
//                list.add(new MySlide(null));
//            }
//            list.get(0).setSelected(true);
//            adapter.setViewSlide(getPresentation());
//            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void hideZoomToast() {
        tvZoom.setVisibility(View.GONE);
    }

    private boolean passwordCorrect(String pass) {
        FileSource docSource = new FileSource(new File(path));
        try {
            PdfDocument pdfDocument = docSource.createDocument(this, new PdfiumCore(this), pass);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
