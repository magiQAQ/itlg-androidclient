package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceData implements Parcelable {

    public static final Creator<DeviceData> CREATOR = new Creator<DeviceData>() {
        @Override
        public DeviceData createFromParcel(Parcel in) {
            return new DeviceData(in);
        }

        @Override
        public DeviceData[] newArray(int size) {
            return new DeviceData[size];
        }
    };
    private int id;//设备信息记录自增id
    private int deviceId;//对应的设备id
    private long dataTime;//记录的时间戳
    private String dataInfo;//详细数据

    protected DeviceData(Parcel in) {
        id = in.readInt();
        deviceId = in.readInt();
        dataTime = in.readLong();
        dataInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(deviceId);
        dest.writeLong(dataTime);
        dest.writeString(dataInfo);
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    public String getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
    }
}
