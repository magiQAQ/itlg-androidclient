package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductInfo implements Parcelable {
    public static final Creator<ProductInfo> CREATOR = new Creator<ProductInfo>() {
        @Override
        public ProductInfo createFromParcel(Parcel in) {
            return new ProductInfo(in);
        }

        @Override
        public ProductInfo[] newArray(int size) {
            return new ProductInfo[size];
        }
    };
    private int id;                    //产品id
    private String productName;        //产品名称
    private int farmId;                //原产地id
    private int typeId;                //产品类型
    private int shelfTime;            //保质期--月份
    private long productTime;        //生产日期
    private String productQrcode;    //产品二维码
    private String note;            //备注
    private String img;                //产品照片
    private float price;            //价格
    private String unit;            //单位

    private ProductInfo(Parcel in) {
        id = in.readInt();
        productName = in.readString();
        farmId = in.readInt();
        typeId = in.readInt();
        shelfTime = in.readInt();
        productTime = in.readLong();
        productQrcode = in.readString();
        note = in.readString();
        img = in.readString();
        price = in.readFloat();
        unit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(productName);
        dest.writeInt(farmId);
        dest.writeInt(typeId);
        dest.writeInt(shelfTime);
        dest.writeLong(productTime);
        dest.writeString(productQrcode);
        dest.writeString(note);
        dest.writeString(img);
        dest.writeFloat(price);
        dest.writeString(unit);
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getShelfTime() {
        return shelfTime;
    }

    public void setShelfTime(int shelfTime) {
        this.shelfTime = shelfTime;
    }

    public long getProductTime() {
        return productTime;
    }

    public void setProductTime(long productTime) {
        this.productTime = productTime;
    }

    public String getProductQrcode() {
        return productQrcode;
    }

    public void setProductQrcode(String productQrcode) {
        this.productQrcode = productQrcode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
