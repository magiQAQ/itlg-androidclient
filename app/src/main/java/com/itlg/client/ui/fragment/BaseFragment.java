package com.itlg.client.ui.fragment;

import androidx.fragment.app.Fragment;

/**
 * 懒加载基类,适应androidx
 */
public abstract class BaseFragment extends Fragment {

    private boolean isFirstLoad = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            onLazyLoad();
            isFirstLoad = false;
        }
    }

    public abstract void onLazyLoad();
}
