package com.tocel.patrol.biz.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.limp.tinkerdemo.R;
import com.limp.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    ListView lv;
    List<String> list = new ArrayList<>();
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        initView();
//        Toast.makeText(this, ""+getIMEI(this), Toast.LENGTH_LONG).show();
        getID();
//        lv.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return list.size();
//            }
//
//            @Override
//            public Object getItem(int i) {
//                return null;
//            }
//
//            @Override
//            public long getItemId(int i) {
//                return 0;
//            }
//
//            @Override
//            public View getView(int i, View view, ViewGroup viewGroup) {
//                view = View.inflate(LoginActivity.this, R.layout.lv_item, null);
//                TextView tv = (TextView) view.findViewById(R.id.textView);
//                tv.setText(list.get(i));
//                return view;
//            }
//        });
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(LoginActivity.this, HeartActivity.class);
//                intent.putExtra("name", list.get(i));
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void getID() {
        String imei = getIMEI(getApplicationContext());

        Map<String,String> hm=new HashMap<>();
        //苇子坑手环
        hm.put("868343005798694","y7JMw6TV3ATisuk1Jg0p");
        //西安手环
        hm.put("864190030971190","ycdI6lFWqKPbKasccROJ");

        MQTTUtil.getincetense().token=hm.get(imei);

        L.zzz("开始请求初始化数据");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hand_code", imei);//开发测试
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String s = jsonObject.toString();
        L.zzz(s);
        RetrofitUtil.getincetense().initDeviceHand(s).enqueue(new Callback<DeviceHandBean>() {
            @Override
            public void onResponse(Call<DeviceHandBean> call, Response<DeviceHandBean> response) {
                String DEVICE_ID = response.body().getData().getDEVICE_ID();
                String DEVICE_NAME = response.body().getData().getHAND_NAME();
                L.zzz("设备号获取成功:" + DEVICE_ID+","+DEVICE_NAME);
                Intent intent = new Intent(LoginActivity.this, HeartActivity.class);
                intent.putExtra("name", DEVICE_ID);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<DeviceHandBean> call, Throwable t) {
                L.zzz("开始请求初始化数据失败" + t.toString());
//                tv.setText("请求设备号失败");
            }
        });
    }


    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        tv = (TextView) findViewById(R.id.tv);
        list.add("操作人员01");
        list.add("操作人员02");
        list.add("操作人员03");
        list.add("Limp测试");
        tv.setText("正在请求数据中...");
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (imei.equals("000000000000000")) {
            return getSerialNumber();
        }
        return imei;
    }

    /**
     * 获得手机Serial
     */

    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;

    }
}
