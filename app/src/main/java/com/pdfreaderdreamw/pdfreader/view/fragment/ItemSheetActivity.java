package com.pdfreaderdreamw.pdfreader.view.fragment;

import android.content.Context;
import android.content.Intent;

import com.pdfreaderdreamw.pdfreader.App;
import com.pdfreaderdreamw.pdfreader.Const;
import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.FragmentSheetItemBinding;
import com.pdfreaderdreamw.pdfreader.model.FavoriteFile;
import com.pdfreaderdreamw.pdfreader.model.ItemFile;
import com.pdfreaderdreamw.pdfreader.view.OnActionCallback;

import java.util.List;

public class ItemSheetActivity extends BaseActivity<FragmentSheetItemBinding> {

    private static OnActionCallback callback;

    public static void start(Context context, ItemFile itemFile, OnActionCallback mcaCallback) {
        callback = mcaCallback;
        Intent starter = new Intent(context, ItemSheetActivity.class);
        starter.putExtra("data", itemFile);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sheet_item;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void addEvent() {
        ItemFile itemFile = (ItemFile) getIntent().getSerializableExtra("data");
        boolean favourite = isFavourite(itemFile);
        itemFile.setFavorite(favourite);

        binding.ivFavorite.setImageResource(favourite ? R.drawable.ic_favourite_checked : R.drawable.ic_favourite);
        binding.llRename.setOnClickListener(v -> onMenuClick(R.id.ll_rename));
        binding.llDelete.setOnClickListener(v -> onMenuClick(R.id.ll_delete));
        binding.llDetail.setOnClickListener(v -> onMenuClick(R.id.ll_detail));
        binding.llShare.setOnClickListener(v -> onMenuClick(R.id.ll_share));
        binding.llFavorite.setOnClickListener(v -> onMenuClick(R.id.ll_favorite));
        binding.ivClose.setOnClickListener(v -> finish());
        binding.container.setOnClickListener(v -> finish());
    }

    private boolean isFavourite(ItemFile itemFile) {
        try {
            List<FavoriteFile> list = App.getInstance().getDatabase().favoritetDao().getList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPath().equals(itemFile.getPath())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void onMenuClick(int id) {
        callback.callback(Const.KEY_CLICK_ITEM, id);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callback = null;
    }


}