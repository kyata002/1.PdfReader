package com.kyata.pdfreader.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.DialogDeleteBinding;
import com.kyata.pdfreader.view.OnActionCallback;

import java.io.File;

public class DeleteDialog extends BaseActivity<DialogDeleteBinding> {
    private static OnActionCallback callback;

    public void setCallback(OnActionCallback callback) {
        DeleteDialog.callback = callback;
    }

    public static void start(Context context, String path, OnActionCallback mCallback) {
        callback = mCallback;
        Intent starter = new Intent(context, DeleteDialog.class);
        starter.putExtra("data", path);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_delete;
    }

    @Override
    protected void initView() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void addEvent() {
        String path = getIntent().getStringExtra("data");
        File file = new File(path);
        binding.tvTitle.setText(getString(R.string.are_you_sure_you_want_to_delete_s) + " " + file.getName() + "?");
        binding.container.setOnClickListener(v -> finish());
        binding.btCancel.setOnClickListener(v -> finish());
        binding.btDelete.setOnClickListener(v -> {
            callback.callback(null, null);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
