package com.tocel.patrol.biz.ui;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.limp.tinkerdemo.R;
import com.limp.utils.L;
import com.limp.utils.UtilIO;
import com.limp.utils.UtilSDCard;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.tocel.patrol.biz.ui.bean.VoiceBean;
import com.tocel.patrol.biz.ui.bean.WSBean;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class HeartActivity extends AppCompatActivity {
    public static String deviceid = "操作人员";
    public static int heart = 0;
    public static double longitude;
    public static double latitude;
    public static ImageView img_gps;
    public static ImageView img_bl;
    public static HeartActivity context;
    private MqttClient sampleClient;
    TextView tv_time;
    public static Gson g;
    private TextView tv_heart;
    private Vibrator vib;
    private TextView tv_name;
    private SimpleDateFormat sdf;
    private Date d;
    private float mx,my,mz;
    private int move_count;
    private SensorEventListener myhertSensorListener = new SensorEventListener() {
        //传感器的值改变调用此方法
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[0] != 0 && heart != (int) event.values[0]) {
                heart = (int) event.values[0];
                h.sendEmptyMessage(heart);
            }
        }

        //传感器的精确度改变调用此方法
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener myAccelerateSensorListener = new SensorEventListener() {
        //传感器的值改变调用此方法
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String data_a="x："+x+"  y："+y+"  z："+z;
            Message message=a.obtainMessage();
            message.obj=data_a;
            a.sendMessage(message);

            if ((Math.pow((x-mx),2)+Math.pow((y-my),2)+Math.pow((z-mz),2)>6)){
                b.sendEmptyMessage(1);
            }else {
                b.sendEmptyMessage(0);
            }

            mx=x;my=y;mz=z;
        }

        //传感器的精确度改变调用此方法
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Handler b=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if (what==1){
                tv_move.setText("运动");
                Vibrate(400);
                move_count++;
            }
            else if (what==0){
                tv_move.setText("静止");
            }
            else if (what==2){
                tv_move.setText("长时间未活动");
                Vibrate(1000);
                Toast.makeText(HeartActivity.this,"长时间未活动",Toast.LENGTH_LONG).show();
            }
        }
    };

    Timer move_timer=new Timer();

    private SensorManager sm;
    private WebSocketUtil webSocket;
    Timer t;
    Handler a=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String s=msg.obj.toString();
            tv_accelerate.setText(s);
        }
    };
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
//            sendmessage(g.toJson(new WatchBean("" + what, LocationService.x + "," + LocationService.y)));


            tv_heart.setText("" + what);
            d.setTime(SystemClock.currentThreadTimeMillis());
            tv_time.setText(sdf.format(new Date()));
        }
    };


    Timer heart_timer=new Timer();


    //音频
    private ImageView img_talk;

    private String voice_path = "";
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private TextView tv_helper;
    private TextView tv_accelerate;
    private TextView tv_move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deviceid = getIntent().getStringExtra("name");
        SKYBeaconManager.getInstance().init(this);
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(1000);
        startService(new Intent(this, LocationService.class));
        initView();
        voiceinit();
        initHeart();
        initAccelerate();
        initwebsocket();
        initloc();
        new Thread(){
            @Override
            public void run() {
                MQTTUtil.getincetense().initconnet();
            }
        }.start();
//        initMQTT();
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                help("18847799522");
////                webSocket.send(getlocationparmers());
//            }
//        });
        img_talk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        img_talk.setImageResource(R.mipmap.inspection_result_img_rec_select);
                        luyin();
                        break;
                    case MotionEvent.ACTION_UP:
                        img_talk.setImageResource(R.mipmap.inspection_result_img_rec_normal);
                        new Thread() {
                            @Override
                            public void run() {
                                SystemClock.sleep(1000);
                                saverecard();
                                try {
                                    FileInputStream fileInputStream = new FileInputStream(voice_path);
                                    String s = g.toJson(new VoiceBean(deviceid, "4", UtilIO.inputStream2base64(fileInputStream)));
                                    webSocket.send(s);
                                    L.zzz("大小:" + s.length() / 1000);
//                            InputStream inputStream = UtilIO.base642inputStream(s);
//                            L.zzz("结果:"+createFileByInputStream(inputStream, UtilSDCard.getSDCardPath() + "aaa2.amr"));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;
                }
                return true;
            }
        });

        move_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (move_count==0){
//                    Toast.makeText(HeartActivity.this,"长时间未活动",Toast.LENGTH_SHORT).show();
                    b.sendEmptyMessage(2);
                }else {
                    move_count=0;
                }
            }
        },0,10000);
    }

    public static boolean createFileByInputStream(InputStream inputStream, String outputFileName) throws IOException {
        // 创建文件夹
        createDirs(outputFileName);
        OutputStream out = new FileOutputStream(outputFileName);
        // 判断输入或输出是否准备好
        if (inputStream != null && out != null) {
            int temp = 0;
            // 开始拷贝
            while ((temp = inputStream.read()) != -1) {
                // 边读边写
                out.write(temp);
            }
            // 关闭输入输出流
            inputStream.close();
            out.close();
            return true;
        } else {
            return false;
        }
    }

    private static void createDirs(String targetFilePath) {
        // 将路径中的/或者\替换成\\
        String path = Pattern.compile("[\\/]").matcher(targetFilePath).replaceAll(File.separator);
        int endIndex = path.lastIndexOf(File.separator);
        path = path.substring(0, endIndex);
        File f = new File(path);
        f.mkdirs();
    }

    private void voiceinit() {
//        voice_path = UtilSDCard.getSDCardPath() + "voice_cache.amr";
        voice_path = UtilSDCard.getSDCardPath() + "aaa.amr";
    }

    private void luyin() {
        // TODO Auto-generated method stub
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(voice_path);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    private void saverecard() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void play() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(voice_path);
            mPlayer.prepare();
        } catch (IOException e) {
        }
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            if (t == null) {
                t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(0);
                    }
                }, 0, 1000);
            }
        }
    }

    private void play(String path) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (IOException e) {
        }
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            if (t == null) {
                t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(0);
                    }
                }, 0, 1000);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 265) {
            help("15801143775");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //    private void initUpload() {
//        t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
////                if (LocationService.list.size() != 0 || !LocationService.x.equals("")) {
//                if (LocationService.list.size() != 0 || latitude != 0) {
//                    webSocket.send(getlocationparmers());
//                }
//                h.sendEmptyMessage(heart);
//            }
//        }, 500, 3000);
//    }
//
//
//    private String getlocationparmers() {
//        if (LocationService.list.size() != 0) {
//            String s = g.toJson(new WSBean(g.toJson(LocationService.list), "2", deviceid, heart + ""));
//            LocationService.list.clear();
//            return s;
//        } else {
////            return g.toJson(new WSBean(LocationService.x + "," + LocationService.y, "1", deviceid, heart + ""));
//            return g.toJson(new WSBean(longitude + "," + latitude, "1", deviceid, heart + ""));
////            return g.toJson(new WSBean("116.385233, 40.000369", "1", deviceid, heart + ""));
//        }
//    }

    private void initwebsocket() {
        try {
            webSocket = new WebSocketUtil(new URI(WebSocketUtil.ip), new WebSocketUtil.TCPCallBack() {
                @Override
                public void success() {
                    webSocket.send(g.toJson(new VoiceBean(deviceid, "0")));
                }

                @Override
                public void fall() {

                }

                @Override
                public void message(final String s) {
                    VoiceBean bean = g.fromJson(s, VoiceBean.class);
                    if (bean != null) {
                        switch (bean.getDataType()) {
                            //警告级别的，信息显示
                            case "106":
                                dialog(bean.getData());
                                break;
                            //危险级别的：振动加信息
                            case "104":
                                dialog(bean.getData());
                                Vibrate(3000);
                                break;
                            //危险级别的：振动加信息
                            case "4":
//                              L.zzz("大小:" + s.length() / 1000 + "k");
                                InputStream inputStream = UtilIO.base642inputStream(bean.getData());
                                try {
                                    L.zzz("接收结果:" + createFileByInputStream(inputStream, UtilSDCard.getSDCardPath() + "bbb.amr"));
                                    play(UtilSDCard.getSDCardPath() + "bbb.amr");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                        }

                    }
                }
            });
            webSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void dialog(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HeartActivity.this, DialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });
    }

    private void help(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
        webSocket.send(g.toJson(new WSBean(deviceid + "在呼救", "3", deviceid, heart + "")));
//        Intent intent = new Intent(Intent.ACTION_MAIN,null);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
    }


    @Override
    public void finish() {
        stopService(new Intent(this, LocationService.class));
        sm.unregisterListener(myhertSensorListener);
//        if (webSocket.isConnecting()) {
//            webSocket.close();
//        }
        mWakeLock.release();
        MQTTUtil.getincetense().close();
        if (t != null) {
            t.cancel();
            t = null;
        }
        super.finish();
//        try {
//            sampleClient.disconnect();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }

//    private void initMQTT() {
////        String ip = "tcp://10.238.255.175:1883";
////        String clientId = "9e018c00-8d49-11e7-96b4-c3dc40795b6c";
//
//        String ip = "tcp://192.168.4.15:1883";
//        String clientId = "669e6fd0-8e1b-11e7-b984-514f371fca80";
//        MemoryPersistence persistence = new MemoryPersistence();
//
//        try {
//            sampleClient = new MqttClient(ip, clientId, persistence);
//            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
//            connOpts.setKeepAliveInterval(30);
////            connOpts.setUserName("P7im0v4bGfBrT6gOGl4q");
//            connOpts.setUserName("z3iCQNbUdR886nK6kAol");
//            zzz("Connecting to broker: " + ip);
//            sampleClient.connect(connOpts);
//            zzz("Connected");
//        } catch (MqttException me) {
//            zzz("reason " + me.getReasonCode());
//            zzz("msg " + me.getMessage());
//            zzz("loc " + me.getLocalizedMessage());
//            zzz("cause " + me.getCause());
//            zzz("excep " + me);
//            me.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    void sendmessage(String s) {
//        zzz(s);
//        MqttMessage message = new MqttMessage(s.getBytes());
//        message.setQos(0);
//        try {
//            sampleClient.publish("v1/devices/me/telemetry", message);
////            sampleClient.publish("v1/devices/me/attributes", message);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    private void initHeart() {
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        /**
         * 若没有心跳传感器
         * 用于在手机上模拟心跳
         * */
        if (defaultSensor==null){
            heart_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Random random=new Random();
                    heart=70+random.nextInt(15);
                    h.sendEmptyMessage(heart);
                }
            },0,1000);
        }

        sm.registerListener(myhertSensorListener, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void initAccelerate(){
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(myAccelerateSensorListener,defaultSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private PowerManager.WakeLock mWakeLock;

    private void initView() {
        sdf = new SimpleDateFormat("HH:mm");
        d = new Date();
        g = new Gson();
        context = this;

        img_talk = (ImageView) findViewById(R.id.img_talk);

        tv_helper = (TextView) findViewById(R.id.tv_helper);
        tv_heart = (TextView) findViewById(R.id.tv_heart);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_name = (TextView) findViewById(R.id.tv_name);

        tv_accelerate=(TextView)findViewById(R.id.tv_accelerate);
        tv_move=(TextView)findViewById(R.id.tv_move);

        ImageView img_setting = (ImageView) findViewById(R.id.setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HeartActivity.this, SettingActivity.class));
            }
        });
        img_gps = (ImageView) findViewById(R.id.img_gps);
        img_bl = (ImageView) findViewById(R.id.img_bl);
        tv_name.setText(deviceid);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PartialWakeLockTag");
        mWakeLock.acquire();
        vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        tv_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocket != null&webSocket.isOpen()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //dataType=3   呼救
                        jsonObject.put("dataType", "3");
                        jsonObject.put("deviceId",deviceid);
                        //0手持仪,1手环
                        jsonObject.put("deviceType", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webSocket.send(jsonObject.toString());
                }
            }
        });
    }

    private void initloc() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lManager == null) {
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //经度
                longitude = location.getLongitude();
                //纬度
                latitude = location.getLatitude();
                //海拔
                double altitude = location.getAltitude();
                img_gps.setVisibility(View.VISIBLE);
//                tv.setText("经度:==>" + longitude + " \n 纬度==>" + latitude + "\n" + "海拔==>" + altitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                switch (i) {

                    case LocationProvider.AVAILABLE:

                        Toast.makeText(HeartActivity.this, "当前GPS为可用状态!", Toast.LENGTH_SHORT).show();

                        break;

                    case LocationProvider.OUT_OF_SERVICE:

                        Toast.makeText(HeartActivity.this, "当前GPS不在服务内", Toast.LENGTH_SHORT).show();

                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:

                        Toast.makeText(HeartActivity.this, "当前GPS为暂停服务状态", Toast.LENGTH_SHORT).show();
                        break;


                }
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(HeartActivity.this, "GPS开启了", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(HeartActivity.this, "GPS关闭了", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void zzz(String s) {
        Log.i("zzz", s);
    }

    public void Vibrate(long milliseconds) {
        Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }


}
