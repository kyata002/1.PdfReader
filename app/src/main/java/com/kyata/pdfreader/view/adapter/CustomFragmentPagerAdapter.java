package com.kyata.pdfreader.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kyata.pdfreader.base.BaseFragment;

import java.util.ArrayList;

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<BaseFragment> mFragment = new ArrayList<>();
    private final ArrayList<String> mTitles = new ArrayList<>();

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(BaseFragment fragment, String title) {
        mFragment.add(fragment);
        mTitles.add(title);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }


}
