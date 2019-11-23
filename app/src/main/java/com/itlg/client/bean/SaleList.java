package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SaleList implements Parcelable {
    public static final Creator<SaleList> CREATOR = new Creator<SaleList>() {
        @Override
        public SaleList createFromParcel(Parcel in) {
            return new SaleList(in);
        }

        @Override
        public SaleList[] newArray(int size) {
            return new SaleList[size];
        }
    };
    private int id; //待售id
    private int farmId; //农场id
    private int startYear; //开始年份
    private int startMonth; //开始月份
    private int endYear; //结束年份
    private int endMonth; //结束月份
    private int userId; //购买者Id
    private int state; //购买状态
    private float price; //农田/养殖场价格

    private SaleList(Parcel in) {
        id = in.readInt();
        farmId = in.readInt();
        startYear = in.readInt();
        startMonth = in.readInt();
        endYear = in.readInt();
        endMonth = in.readInt();
        userId = in.readInt();
        state = in.readInt();
        price = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(farmId);
        dest.writeInt(startYear);
        dest.writeInt(startMonth);
        dest.writeInt(endYear);
        dest.writeInt(endMonth);
        dest.writeInt(userId);
        dest.writeInt(state);
        dest.writeFloat(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}