package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductTypes implements Parcelable {
    public static final Creator<ProductTypes> CREATOR = new Creator<ProductTypes>() {
        @Override
        public ProductTypes createFromParcel(Parcel in) {
            return new ProductTypes(in);
        }

        @Override
        public ProductTypes[] newArray(int size) {
            return new ProductTypes[size];
        }
    };
    private int id; //自增id
    private int pid; //默认：0为1级标题,关联当前表中id
    private String name; //类别名称

    private ProductTypes(Parcel in) {
        id = in.readInt();
        pid = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(pid);
        dest.writeString(name);
    }
}
