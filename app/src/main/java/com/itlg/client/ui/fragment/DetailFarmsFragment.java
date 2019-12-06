package com.itlg.client.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.bean.OperationLog;
import com.itlg.client.biz.OperationLogBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.activity.MyDetailFarmsActivity;
import com.itlg.client.ui.activity.WatchMonitorActivity;
import com.itlg.client.ui.adapter.OperationLogAdapter;
import com.itlg.client.ui.adapter.PreviewProductAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//农场主看到的农场详情页
public class DetailFarmsFragment extends BaseFragment {

    @BindView(R.id.farm_icon_imageView)
    ImageView farmIconImageView;
    @BindView(R.id.farm_name_textView)
    TextView farmNameTextView;
    @BindView(R.id.farm_note_textView)
    TextView farmNoteTextView;
    @BindView(R.id.farm_longitude_textView)
    TextView farmLongitudeTextView;
    @BindView(R.id.farm_latitude_textView)
    TextView farmLatitudeTextView;
    @BindView(R.id.product_list_textView)
    TextView productListTextView;
    @BindView(R.id.product_recyclerView)
    RecyclerView productRecyclerView;
    @BindView(R.id.operation_textView)
    TextView operationTextView;
    @BindView(R.id.operation_recyclerView)
    RecyclerView operationRecyclerView;

    private FarmInfoModel farmInfoModel;
    private PreviewProductAdapter previewProductAdapter;
    private OperationLogAdapter operationLogAdapter;
    private OperationLogBiz biz;
    private ArrayList<OperationLog> operationLogs;

    public static DetailFarmsFragment newInstance(FarmInfoModel farmInfoModel) {
        DetailFarmsFragment fragment = new DetailFarmsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MyUtils.KEY_FARM_INFO_MODEL, farmInfoModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            farmInfoModel = getArguments().getParcelable(MyUtils.KEY_FARM_INFO_MODEL);
        } else {
            if (getActivity() instanceof MyDetailFarmsActivity) {
                getActivity().finish();
            }
        }
        biz = new OperationLogBiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_farms, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        farmNameTextView.setText(getString(R.string.typename_id,
                farmInfoModel.getTypeName(), farmInfoModel.getFarmInfo().getId()));
        farmNoteTextView.setText(farmInfoModel.getFarmInfo().getNote());

        farmLongitudeTextView.setText(getString(R.string.longitude, farmInfoModel.getFarmInfo().getLongitude()));
        farmLatitudeTextView.setText(getString(R.string.latitude, farmInfoModel.getFarmInfo().getLongitude()));

        if (farmInfoModel != null) {
            setupProductRecyclerView();
        }

        if (operationLogs != null) {
            setupOperationRecyclerView();
        }
    }

    //这里执行与网络有关的耗时操作
    @Override
    public void onLazyLoad() {
        Glide.with(Objects.requireNonNull(getActivity())).load(Config.FILEURL + farmInfoModel.getFarmInfo().getImg())
                .placeholder(R.drawable.placeholder).into(farmIconImageView);

        //农产品列表,里面有图片,也属于耗时操作
        if (farmInfoModel.getProductInfos() == null || farmInfoModel.getProductInfos().isEmpty()) {
            productListTextView.setVisibility(View.GONE);
        } else {
            setupProductRecyclerView();
        }

        //操作日志列表
        biz.getOperationLogs(farmInfoModel.getFarmInfo().getId(), 1, new CommonCallback<ArrayList<OperationLog>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                operationTextView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(ArrayList<OperationLog> response) {
                operationLogs = response;
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putParcelableArrayList("key_operation_log", operationLogs);
                setArguments(bundle);
                setupOperationRecyclerView();
            }
        });

    }

    @OnClick(R.id.watch_monitor_button)
    void toWatchMonitorActivity() {
        Intent intent = new Intent(getActivity(), WatchMonitorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        biz.onDestroy();
        super.onDestroy();
    }

    private void setupProductRecyclerView() {
        if (previewProductAdapter == null) {
            previewProductAdapter = new PreviewProductAdapter(getActivity(), farmInfoModel.getProductInfos());
            productRecyclerView.setAdapter(previewProductAdapter);
        } else {
            previewProductAdapter.notifyDataSetChanged();
        }
    }

    private void setupOperationRecyclerView() {
        if (operationLogAdapter == null) {
            operationLogAdapter = new OperationLogAdapter(getActivity(), operationLogs);
            operationRecyclerView.setAdapter(operationLogAdapter);
        } else {
            operationLogAdapter.notifyDataSetChanged();
        }
    }

    public String getTitle() {
        if (getArguments() != null) {
            FarmInfoModel model = getArguments().getParcelable(MyUtils.KEY_FARM_INFO_MODEL);
            if (model != null) {
                return model.getTypeName() + model.getFarmInfo().getId();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}
