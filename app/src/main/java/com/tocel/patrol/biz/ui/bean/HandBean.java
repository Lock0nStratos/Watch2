package com.tocel.patrol.biz.ui.bean;

/**
 * Created by Limp on 2017/9/25.
 */

public class HandBean {
    public HandBean(String time, String data) {
        this.time = time;
        this.data = data;
    }

    /**
     * time : 2017-09-12 10:00:00
     * data :
     */

    private String time;
    private String data;

    public void setTime(String time) {
        this.time = time;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public String getData() {
        return data;
    }
}
