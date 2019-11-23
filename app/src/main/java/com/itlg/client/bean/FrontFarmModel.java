package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FrontFarmModel implements Parcelable {
    public static final Creator<FrontFarmModel> CREATOR = new Creator<FrontFarmModel>() {
        @Override
        public FrontFarmModel createFromParcel(Parcel in) {
            return new FrontFarmModel(in);
        }

        @Override
        public FrontFarmModel[] newArray(int size) {
            return new FrontFarmModel[size];
        }
    };
    private FarmInfo farmInfo;
    private String typeName;
    private SaleList saleList;

    private FrontFarmModel(Parcel in) {
        farmInfo = in.readParcelable(FarmInfo.class.getClassLoader());
        typeName = in.readString();
        saleList = in.readParcelable(SaleList.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(farmInfo, flags);
        dest.writeString(typeName);
        dest.writeParcelable(saleList, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public SaleList getSaleList() {
        return saleList;
    }

    public void setSaleList(SaleList saleList) {
        this.saleList = saleList;
    }
}
