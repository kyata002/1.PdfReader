package com.pdfreaderdreamw.pdfreader.view.dialog;

import static com.documentmaster.documentscan.extention.ViewExtensionKt.disableFocus;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.DialogJumpBinding;
import com.pdfreaderdreamw.pdfreader.databinding.DialogRenameBinding;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;

import java.io.File;

public class JumpDialog extends BaseActivity<DialogJumpBinding> {
    private static OnActionCallback callback;

    public static void start(Context context, OnActionCallback mCallback) {
        callback = mCallback;
        Intent starter = new Intent(context, JumpDialog.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_jump;
    }

    @Override
    protected void initView() {
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void addEvent() {
        binding.btOk.setOnClickListener(v -> {
            hideKeyboard(binding.edtPage);
            disableFocus(binding.edtPage);
            if (binding.edtPage.getText() == null) {
                finish();
                return;
            }
            String numText = binding.edtPage.getText().toString().trim();
            int number = 0;
            try {
                number = Integer.parseInt(numText);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (callback != null) {
                callback.callback(null, number);
            }
            finish();
        });
        binding.container.setOnClickListener(v -> finish());
        binding.btCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
