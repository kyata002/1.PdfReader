package com.pdfreaderdreamw.pdfreader.view.activity;

import com.pdfreaderdreamw.pdfreader.R;
import com.pdfreaderdreamw.pdfreader.base.BaseActivity;
import com.pdfreaderdreamw.pdfreader.databinding.ActivityBrowseBinding;
import com.pdfreaderdreamw.pdfreader.view.fragment.BrowseFragment;

public class BrowseActivity extends BaseActivity<ActivityBrowseBinding> {
    private BrowseFragment browseFrg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browse;
    }

    @Override
    protected void initView() {
        browseFrg = new BrowseFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fr_content, browseFrg).commit();
    }

    @Override
    protected void addEvent() {

    }

    @Override
    public void onBackPressed() {
        if (browseFrg.onBackPress()) {
            return;
        }
        super.onBackPressed();

    }
}
