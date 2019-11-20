package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceDataModel implements Parcelable {
    private int id; //id自增
    private String temperature; //温度
    private String humidity; //湿度
    private long dataTime; //时间

    private DeviceDataModel(Parcel in) {
        id = in.readInt();
        temperature = in.readString();
        humidity = in.readString();
        dataTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(temperature);
        dest.writeString(humidity);
        dest.writeLong(dataTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceDataModel> CREATOR = new Creator<DeviceDataModel>() {
        @Override
        public DeviceDataModel createFromParcel(Parcel in) {
            return new DeviceDataModel(in);
        }

        @Override
        public DeviceDataModel[] newArray(int size) {
            return new DeviceDataModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }
}
