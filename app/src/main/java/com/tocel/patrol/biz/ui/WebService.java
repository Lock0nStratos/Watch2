package com.tocel.patrol.biz.ui;



import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Limp on 2017/1/18.
 * 网络设置
 */

public interface WebService {
    /**
     * 数据请求根目录
     */

//    String root = "http://10.238.18.59:8010/Webservices/BasicsService.asmx/";
    String root = "http://202.96.41.40:8010/Webservices/BasicsService.asmx/";

    /**
     * 位置上传
     */
    @FormUrlEncoded
    @POST("UploadGIS")
    Call<String> loadupGIS(@Field("jsonParams") String json);


    /**
     * 初始化设备信息
     */
    @GET("InitDeviceHand")
    Call<DeviceHandBean> initDeviceHand(@Query("jsonParams") String json);

}
