package com.tocel.patrol.biz.ui;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Limp on 2017/1/18.
 */

public class RetrofitUtil {
    static WebService webService;

    public static WebService getincetense() {
        if (webService == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(WebService.root)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();
            webService = retrofit.create(WebService.class);
        }
        return webService;
    }

    static WebService StringwebService;

    public static WebService getStringincetense() {
        if (StringwebService == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(WebService.root)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
            StringwebService = retrofit.create(WebService.class);
        }
        return StringwebService;
    }
    private static final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(60, TimeUnit.SECONDS).
            readTimeout(60, TimeUnit.SECONDS).
            writeTimeout(60, TimeUnit.SECONDS).build();
}
