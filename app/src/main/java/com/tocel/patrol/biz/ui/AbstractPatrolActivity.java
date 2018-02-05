package com.tocel.patrol.biz.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.limp.tinkerdemo.MQTTHandBean;
import com.limp.tinkerdemo.R;
import com.limp.utils.L;
import com.tocel.patrol.biz.ui.bean.HandBean;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static com.baidu.location.h.k.n;


public class AbstractPatrolActivity extends AppCompatActivity {
    static boolean isHandDevice = true;
    private String ip = "";
    private String token = "";

    static {
        if (isHandDevice) {
            System.loadLibrary("infra_runtime");
        }
    }

    /**
     * host
     * org.kaaproject.kaa.client.channel.IpTransportInfo
     */

    DashboardView4 cn;
    Button button4;
    private Gson g;
    private MqttClient sampleClient;
    private String code;
    private String type;
    private int temperatureMin;
    private int temperatureMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstract_patrol);
        startService(new Intent(this, LocationService.class));
        initView();
        initMQTT();
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n;
                if (isHandDevice) {
                    n = (int) read_infra_native();
                } else {
                    n = getTemperature();
                }
                ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentNum", n);
                anim.setDuration(1500);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        cn.setVelocity(value);
                    }
                });
                anim.start();
//                sendmessage(g.toJson(new MQTTHandBean(""+n,LocationService.x+","+LocationService.y)));
//                sendmessage(g.toJson(new MQTTHandBean(LocationService.x+","+LocationService.y)));
                Data d = new Data();
                d.lat = LocationService.y;
                d.lng = LocationService.x;
                zzz("x=" + LocationService.x + ",Y=" + LocationService.y);
                d.temperature = "" + n;
                d.objectCode = code;
                d.objectType = type;
//                d.heartrate="85";
                sendmessage(g.toJson(new HandBean(gettime(), g.toJson(d))));
            }
        });
        try {
            sampleClient.subscribe("v1/devices/me/attributes", new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    zzz("收到消息:" + message.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public String gettime() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    public int getTemperature() {
        int n;
        while (true) {
            n = (int) (Math.random() * 100);
            if (n > temperatureMin & n < temperatureMax) {
                break;
            }
        }
        return n;
    }


    void sendmessage(String s) {
        zzz(s);
        MqttMessage message = new MqttMessage(s.getBytes());
        message.setQos(0);
        try {
            sampleClient.publish("v1/devices/me/telemetry", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initMQTT() {
//        String ip = "tcp://10.238.255.175:1883";
//        String clientId = "b445f310-8d2c-11e7-ae9d-c3dc40795b6c";

        String clientId = "b40fca10-b92d-11e7-96ba-09036b320ab9";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(ip, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(30);
            connOpts.setUserName(token);
//            connOpts.setUserName("SYYHeHJF1IImi7Y4naqY");
            zzz("Connecting to broker: " + ip);
            sampleClient.connect(connOpts);
            zzz("链接成功");
        } catch (MqttException me) {
            zzz("链接失败");
            zzz("reason " + me.getReasonCode());
            zzz("msg " + me.getMessage());
            zzz("loc " + me.getLocalizedMessage());
            zzz("cause " + me.getCause());
            zzz("excep " + me);
            me.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        stopService(new Intent(this, LocationService.class));
        try {
            if (isHandDevice) {
                close_infra_native();
            }
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        zzz("Disconnected");
        super.finish();

    }

    void zzz(String s) {
        Log.i("zzz", s);
    }

    public native boolean init_infra_native();

    public native int open_infra_native();

    public native int close_infra_native();

    public native float read_infra_native();

    public native int open_led_native();

    public native int close_led_native();

    public native int led_flash_native();


    private void initView() {
        code = getIntent().getStringExtra("code");
        type = getIntent().getStringExtra("type");
        ip = getIntent().getStringExtra("ip");
        token = getIntent().getStringExtra("token");
        temperatureMin = Integer.valueOf(getIntent().getStringExtra("min"));
        temperatureMax = Integer.valueOf(getIntent().getStringExtra("max"));
        cn = (DashboardView4) findViewById(R.id.cn);
        button4 = (Button) findViewById(R.id.button4);
        g = new Gson();
        if (isHandDevice) {
            init_infra_native();
            open_infra_native();
        }
    }


    class Data {
        String lat;
        String lng;
        String temperature;
        String objectCode;
        String objectType;
//        String heartrate;
    }
}
