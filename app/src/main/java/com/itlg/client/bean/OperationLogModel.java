package com.itlg.client.bean;

public class OperationLogModel {
    private OperationLog operationLog;
    private String operatorName;//操作员姓名
    private String dataInfo;//设备记录详细数据

    public OperationLog getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(OperationLog operationLog) {
        this.operationLog = operationLog;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
    }
}
