package com.kyata.pdfreader.view.dialog;

import android.content.Context;
import android.content.Intent;

import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.DialogInfoBinding;
import com.kyata.pdfreader.data.model.ItemFile;
import com.kyata.pdfreader.utils.CommonUtils;
import com.kyata.pdfreader.utils.FileUtils;

import java.io.File;

public class InfoDialog extends BaseActivity<DialogInfoBinding> {
    public static void start(Context context, ItemFile itemFile) {
        Intent starter = new Intent(context, InfoDialog.class);
        starter.putExtra("data", itemFile);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_info;
    }

    @Override
    protected void initView() {
        ItemFile itemFile = (ItemFile) getIntent().getSerializableExtra("data");
        if (itemFile == null) {
            finish();
            return;
        }
        File file = new File(itemFile.getPath());

        binding.tvName.setText(file.getName());
        binding.tvDate.setText(CommonUtils.getInstance().formatDate(file.lastModified()));
        binding.tvPath.setText(file.getAbsolutePath());
        binding.tvSize.setText(FileUtils.formatFileSize(file.length()));
    }

    @Override
    protected void addEvent() {
        binding.ivClose.setOnClickListener(v -> finish());
        binding.container.setOnClickListener(v -> finish());
    }
}
