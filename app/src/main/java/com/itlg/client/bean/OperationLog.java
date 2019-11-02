package com.itlg.client.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class OperationLog implements Parcelable {
    private int id;
    private int userID;
    private String operationInfo;
    private long operationTime;
    private int FarmId;

    protected OperationLog(Parcel in) {
        id = in.readInt();
        userID = in.readInt();
        operationInfo = in.readString();
        operationTime = in.readLong();
        FarmId = in.readInt();
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
        return FarmId;
    }

    public void setFarmId(int farmId) {
        FarmId = farmId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userID);
        parcel.writeString(operationInfo);
        parcel.writeLong(operationTime);
        parcel.writeInt(FarmId);
    }
}
