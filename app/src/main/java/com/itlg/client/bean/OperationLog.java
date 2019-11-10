package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class OperationLog implements Parcelable {
    private int id;
    private int userId;
    private String operationInfo;
    private long operationTime;
    private int farmId;

    private OperationLog(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        operationInfo = in.readString();
        operationTime = in.readLong();
        farmId = in.readInt();
    }

    public static final Creator<OperationLog> CREATOR = new Creator<OperationLog>() {
        @Override
        public OperationLog createFromParcel(Parcel in) {
            return new OperationLog(in);
        }

        @Override
        public OperationLog[] newArray(int size) {
            return new OperationLog[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOperationInfo() {
        return operationInfo;
    }

    public void setOperationInfo(String operationInfo) {
        this.operationInfo = operationInfo;
    }

    public long getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(long operationTime) {
        this.operationTime = operationTime;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeString(operationInfo);
        parcel.writeLong(operationTime);
        parcel.writeInt(farmId);
    }
}
