package com.pdfreaderdreamw.pdfreader.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.ActivityLanguageBinding;
import com.pdfreaderdreamw.pdfreader.model.Language;
import com.pdfreaderdreamw.pdfreader.utils.LanguageUtils;
import com.pdfreaderdreamw.pdfreader.view.adapter.LanguageAdapter;


public class LanguageActivity extends BaseActivity<ActivityLanguageBinding> {
    public static void start(Context context) {
        Intent intent = new Intent(context, LanguageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    protected void initView() {
        binding.recycleViewLanguage.setLayoutManager(new LinearLayoutManager(this));
        LanguageAdapter adapter = new LanguageAdapter(LanguageUtils.getLanguageData(), this);
        adapter.setmCallback((key, data) -> {
            if (Const.KEY_CLICK_ITEM.equals(key)) {
                Language language = (Language) data;
                LanguageUtils.changeLanguage(LanguageActivity.this, language);
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
        binding.recycleViewLanguage.setAdapter(adapter);
    }

    @Override
    protected void addEvent() {
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
