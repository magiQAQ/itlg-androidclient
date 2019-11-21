package com.itlg.client.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.ui.adapter.ProductMallAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductMallFragment extends Fragment {

    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_SCH_KEYWORD = "sch_keyword";
    private static final String KEY_SCH_TYPE = "sch_type";

    @BindView(R.id.search_editText)
    EditText searchEditText;
    @BindView(R.id.type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.price_spinner)
    Spinner priceSpinner;
    @BindView(R.id.product_recyclerView)
    RecyclerView productRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int sch_page;
    private String sch_keyword;
    private int sch_type;

    private ProductMallAdapter adapter;

    public static ProductMallFragment newInstance() {
        return new ProductMallFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sch_page = getArguments().getInt(KEY_SCH_PAGE);
            sch_keyword = getArguments().getString(KEY_SCH_KEYWORD);
            sch_type = getArguments().getInt(KEY_SCH_TYPE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_mall, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
