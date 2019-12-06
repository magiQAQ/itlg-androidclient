package com.itlg.client.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.itlg.client.ui.fragment.DetailFarmsFragment;

import java.util.List;


public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (fragments.get(position) instanceof DetailFarmsFragment) {
            return ((DetailFarmsFragment) fragments.get(position)).getTitle();
        } else {
            return super.getPageTitle(position);
        }
    }
}
