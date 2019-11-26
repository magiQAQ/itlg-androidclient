package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FarmInfo implements Parcelable {

    private int id;//农场自增编号
    private int typeId;//农场类型
    private String farmQrcode;//农场二维码
    private double longitude;//GPS经度
    private double latitude;//GPS纬度
    public static final Creator<FarmInfo> CREATOR = new Creator<FarmInfo>() {
        @Override
        public FarmInfo createFromParcel(Parcel in) {
            return new FarmInfo(in);
        }

        @Override
        public FarmInfo[] newArray(int size) {
            return new FarmInfo[size];
        }
    };
    private String img;//农场图片

    private FarmInfo(Parcel in) {
        id = in.readInt();
        typeId = in.readInt();
        farmQrcode = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        img = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(typeId);
        dest.writeString(farmQrcode);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(img);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getFarmQrcode() {
        return farmQrcode;
    }

    public void setFarmQrcode(String farmQrcode) {
        this.farmQrcode = farmQrcode;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
