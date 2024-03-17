package com.kyata.pdfreader.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.DialogRenameBinding;
import com.kyata.pdfreader.data.model.ItemFile;
import com.kyata.pdfreader.view.OnActionCallback;
import com.kyata.pdfreader.view.OnCommonCallback;
import com.kyata.pdfreader.view.widget.CustomRenameEditText;

import java.io.File;

public class RenameDialog extends BaseActivity<DialogRenameBinding> {
    private static OnActionCallback callback;
    private File file;

    public static void start(Context context, ItemFile itemFile, OnActionCallback mCallback) {
        callback = mCallback;
        Intent starter = new Intent(context, RenameDialog.class);
        starter.putExtra("data", itemFile);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rename;
    }

    @Override
    protected void initView() {
        ItemFile filePdf = (ItemFile) getIntent().getSerializableExtra("data");
        file = new File(filePdf.getPath());
        String name = file.getName().replace(".pdf", "").replace(".PDF", "");
        binding.edtName.setText(name);
        binding.edtName.setSelection(binding.edtName.getText().length());
        updateTextUI(name);
    }

    @Override
    protected void addEvent() {
        binding.btSave.setOnClickListener(v -> {
            String currentName = binding.edtName.getText().toString().trim();
            String newName = (currentName.endsWith(".pdf") | currentName.endsWith(".PDF")) ? currentName : currentName + ".pdf";
            File newFile = new File(file.getParent() + "/" + newName);
            if (newFile.exists()) {
                Toast.makeText(this, "File name is existed", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!currentName.isEmpty()) {
                callback.callback(null, newFile.getAbsolutePath());
                finish();
            } else {
                Toast.makeText(this, "File name is empty", Toast.LENGTH_SHORT).show();
            }
        });
//        binding.container.setOnClickListener(v -> finish());
        binding.btCancel.setOnClickListener(v -> finish());


        binding.edtName.addTextChangedListener(new OnCommonCallback() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                updateTextUI(s);
            }
        });
        binding.edtName.setDrawableClickListener(target -> {
            if (target == CustomRenameEditText.DrawableClickListener.DrawablePosition.RIGHT) {
                binding.edtName.setText("");
            }
        });
    }

    private void updateTextUI(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            binding.edtName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_clear_rename,
                    0
            );
        } else {
            binding.edtName.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
