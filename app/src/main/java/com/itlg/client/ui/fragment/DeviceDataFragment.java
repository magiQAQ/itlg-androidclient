package com.itlg.client.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.itlg.client.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceDataFragment extends Fragment {


    private DeviceDataFragment() {
    }

    public static DeviceDataFragment getInstance() {
        return new DeviceDataFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_data, container, false);
    }

}
