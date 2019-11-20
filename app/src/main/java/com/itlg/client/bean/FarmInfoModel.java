package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class FarmInfoModel implements Parcelable {
    private FarmInfo farmInfo;
    private String typeName;
    public static final Creator<FarmInfoModel> CREATOR = new Creator<FarmInfoModel>() {
        @Override
        public FarmInfoModel createFromParcel(Parcel in) {
            return new FarmInfoModel(in);
        }

        @Override
        public FarmInfoModel[] newArray(int size) {
            return new FarmInfoModel[size];
        }
    };
    private List<ProductInfo> productInfos;

    private FarmInfoModel(Parcel in) {
        farmInfo = in.readParcelable(FarmInfo.class.getClassLoader());
        typeName = in.readString();
        productInfos = in.createTypedArrayList(ProductInfo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(farmInfo, flags);
        dest.writeString(typeName);
        dest.writeTypedList(productInfos);
    }

    public FarmInfo getFarmInfo() {
        return farmInfo;
    }

    public void setFarmInfo(FarmInfo farmInfo) {
        this.farmInfo = farmInfo;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ProductInfo> getProductInfos() {
        return productInfos;
    }

    public void setProductInfos(List<ProductInfo> productInfos) {
        this.productInfos = productInfos;
    }
}
