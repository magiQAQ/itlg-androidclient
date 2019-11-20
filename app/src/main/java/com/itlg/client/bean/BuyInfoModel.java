package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BuyInfoModel implements Parcelable {
    public static final Creator<BuyInfoModel> CREATOR = new Creator<BuyInfoModel>() {
        @Override
        public BuyInfoModel createFromParcel(Parcel in) {
            return new BuyInfoModel(in);
        }

        @Override
        public BuyInfoModel[] newArray(int size) {
            return new BuyInfoModel[size];
        }
    };
    private int id; //购买id自增
    private String username; //用户姓名
    private String productname; //产品名称
    private int orderId; //订单编号
    private int count; //购买数量
    private float price; //购买价格
    private long createTime; //订单创建时间
    private String img;

    private BuyInfoModel(Parcel in) {
        id = in.readInt();
        username = in.readString();
        productname = in.readString();
        orderId = in.readInt();
        count = in.readInt();
        price = in.readFloat();
        createTime = in.readLong();
        img = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(productname);
        dest.writeInt(orderId);
        dest.writeInt(count);
        dest.writeFloat(price);
        dest.writeLong(createTime);
        dest.writeString(img);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
