package com.tocel.patrol.biz.ui.bean;

/**
 * Created by Limp on 2017/12/1.
 */

public class VoiceBean {
    public VoiceBean(String deviceId, String dataType, String data) {
        this.deviceId = deviceId;
        this.dataType = dataType;
        this.data = data;
    }

    public VoiceBean(String deviceId, String dataType) {
        this.deviceId = deviceId;
        this.dataType = dataType;
    }

    public VoiceBean() {
    }

    String deviceId;
    String dataType;
    String data;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
