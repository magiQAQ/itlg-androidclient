package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FarmInfoModel implements Parcelable {
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
    private FarmInfo farmInfo;
    private String typeName;

    private FarmInfoModel(Parcel in) {
        farmInfo = in.readParcelable(FarmInfo.class.getClassLoader());
        typeName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(farmInfo, flags);
        dest.writeString(typeName);
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

}
