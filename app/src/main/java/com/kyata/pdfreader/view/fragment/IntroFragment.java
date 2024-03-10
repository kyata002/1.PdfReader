package com.kyata.pdfreader.view.fragment;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.base.BaseFragment;


public class IntroFragment extends BaseFragment {
    private String path;

    public static IntroFragment newInstance(String path) {
        IntroFragment introFragment = new IntroFragment();
        introFragment.setPath(path);
        return introFragment;
    }

    private void setPath(String path) {
        this.path = path;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_intro;
    }


    @Override
    protected void initView() {
        ImageView ivPreview = rootView.findViewById(R.id.ivPreview);
        try {
            Glide.with(getContext()).load(path).into(ivPreview);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void addEvent() {

    }
}
