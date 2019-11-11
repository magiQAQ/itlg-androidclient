package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceDataModel implements Parcelable {
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
    private DeviceData deviceData;
    private DeviceInfo deviceInfo;

    protected DeviceDataModel(Parcel in) {
        deviceData = in.readParcelable(DeviceData.class.getClassLoader());
        deviceInfo = in.readParcelable(DeviceInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(deviceData, flags);
        dest.writeParcelable(deviceInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
