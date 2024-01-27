package com.subvip;

import com.admob.control.R;
import com.admob.control.databinding.ActivitySubBinding;
import com.subvip.manage.PurchaseManager;


public class SubActivity extends BaseActivity<ActivitySubBinding> implements PurchaseCallback {
    private static final String POLICY_URL = "";
    private int time;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sub;
    }

    @Override
    protected void initView() {
        PurchaseManager.getInstance().setCallback(this);
        binding.tvPrice.setText(PurchaseManager.getInstance().getPrice(PurchaseManager.PRODUCT_LIFETIME));
    }

    @Override
    protected void addEvent() {
        binding.tvBuy.setOnClickListener(view -> PurchaseManager.getInstance().purchase(SubActivity.this, PurchaseManager.PRODUCT_LIFETIME));
        binding.ivClose.setOnClickListener(view -> finish());
    }


    @Override
    public void purchaseSucess() {
        finish();
    }

    @Override
    public void purchaseFail() {
    }

    @Override
    public void onBackPressed() {
    }
}
