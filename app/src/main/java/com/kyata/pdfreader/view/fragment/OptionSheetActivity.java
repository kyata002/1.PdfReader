package com.kyata.pdfreader.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.kyata.pdfreader.Const;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.StorageCommon;
import com.kyata.pdfreader.base.BaseActivity;
import com.kyata.pdfreader.databinding.FragmentSheetModeBinding;
import com.kyata.pdfreader.view.OnActionCallback;

public class OptionSheetActivity extends BaseActivity<FragmentSheetModeBinding> {

    private static OnActionCallback callback;

    public static void start(Context context, OnActionCallback mCallback) {
        callback = mCallback;
        Intent starter = new Intent(context, OptionSheetActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sheet_mode;
    }

    @Override
    protected void initView() {
        updateUI();
    }

    private void updateUI() {
        if (StorageCommon.SORT_TYPE_SIZE == StorageCommon.getInstance().getSortType()) {
            binding.btSortSize.setSelected(true);
            binding.btSortName.setSelected(false);
            binding.btSortDate.setSelected(false);
        } else if (StorageCommon.SORT_TYPE_NAME == StorageCommon.getInstance().getSortType()) {
            binding.btSortName.setSelected(true);
            binding.btSortSize.setSelected(false);
            binding.btSortDate.setSelected(false);
        } else {
            binding.btSortDate.setSelected(true);
            binding.btSortName.setSelected(false);
            binding.btSortSize.setSelected(false);
        }

        if (StorageCommon.FILTER_ALL == StorageCommon.getInstance().getFilterType()) {
            binding.btReading.setSelected(false);
            binding.btCompleted.setSelected(false);
            binding.btNew.setSelected(false);
        } else if (StorageCommon.FILTER_READING == StorageCommon.getInstance().getFilterType()) {
            binding.btReading.setSelected(true);
            binding.btCompleted.setSelected(false);
            binding.btNew.setSelected(false);
        } else if (StorageCommon.FILTER_COMPLETED == StorageCommon.getInstance().getFilterType()) {
            binding.btCompleted.setSelected(true);
            binding.btReading.setSelected(false);
            binding.btNew.setSelected(false);
        } else {
            binding.btNew.setSelected(true);
            binding.btCompleted.setSelected(false);
            binding.btReading.setSelected(false);
        }
    }

    @Override
    protected void addEvent() {
        binding.btSortDate.setOnClickListener(v -> {

            onMenuClick(R.id.bt_sort_date);
        });
        binding.btSortName.setOnClickListener(v -> {

            onMenuClick(R.id.bt_sort_name);
        });
        binding.btSortSize.setOnClickListener(v -> {

            onMenuClick(R.id.bt_sort_size);
        });

        binding.btReading.setOnClickListener(v -> {
            if (resetFilter(v)) {
                return;
            }
            onMenuClick(R.id.bt_reading);
        });
        binding.btCompleted.setOnClickListener(v -> {
            if (resetFilter(v)) {
                return;
            }
            onMenuClick(R.id.bt_completed);
        });
        binding.btNew.setOnClickListener(v -> {
            if (resetFilter(v)) {
                return;
            }
            onMenuClick(R.id.bt_new);
        });

        binding.container.setOnClickListener(v -> finish());
        binding.ivClose.setOnClickListener(v -> finish());
    }

    private boolean resetFilter(View v) {
        if (v.isSelected()) {
            onMenuClick(R.id.bt_all);
            return true;
        }
        return false;
    }

    private void onMenuClick(int id) {
        callback.callback(Const.KEY_CLICK_ITEM, id);
        updateUI();
    }


}