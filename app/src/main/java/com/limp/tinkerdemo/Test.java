//package com.limp.tinkerdemo;
//
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//
//import com.limp.tinkerdemo.R;
//import com.tocel.patrol.biz.ui.DashboardView4;
//
//import org.kaaproject.kaa.client.AndroidKaaPlatformContext;
//import org.kaaproject.kaa.client.Kaa;
//import org.kaaproject.kaa.client.KaaClient;
//import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
//import org.kaaproject.kaa.client.configuration.base.ConfigurationListener;
//import org.kaaproject.kaa.client.logging.BucketInfo;
//import org.kaaproject.kaa.client.logging.RecordInfo;
//import org.kaaproject.kaa.client.logging.future.RecordFuture;
//import org.kaaproject.kaa.client.logging.strategies.RecordCountLogUploadStrategy;
//import org.kaaproject.kaa.schema.sample.Configuration;
//
//import java.io.IOException;
//import java.util.Random;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class Test extends AppCompatActivity {
////    static {
////        System.loadLibrary("infra_runtime");
////    }
//
//
//    /**
//     * host
//     * org.kaaproject.kaa.client.channel.IpTransportInfo
//     */
//
//    DashboardView4 cn;
//    Button button4;
//
//    private static final int LOGS_DEFAULT_THRESHOLD = 1;
//    private static final int MIN_TEMPERATURE = -25;
//    private static final int MAX_TEMPERATURE = 45;
//    private static final int MAX_SECONDS_TO_INIT_KAA = 2;
//    private static final int MAX_SECONDS_BEFORE_STOP = 3;
//
//    private static int samplePeriodInSeconds = 5;
//    private KaaClient kaaClient;
//    private static ScheduledExecutorService executor;
//    private static ScheduledFuture<?> executorHandle;
//    private static volatile AtomicInteger sentRecordsCount = new AtomicInteger(0);
//    private static volatile AtomicInteger confirmationsCount = new AtomicInteger(0);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_abstract_patrol);
////        init_infra_native();
////        open_infra_native();
//
//        initView();
//        initKAA();
//        button4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                int n = (int) read_infra_native();
//                int n = 40;
//                ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentNum", n);
//                anim.setDuration(1500);
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        int value = (int) animation.getAnimatedValue();
//                        cn.setVelocity(value);
//                    }
//                });
//                anim.start();
//                sendkaainfo(n);
//            }
//        });
//    }
//
//    public void sendkaainfo(final int n) {
//        new Thread() {
//            @Override
//            public void run() {
//                sentRecordsCount.incrementAndGet();
//                DataCollection record = generateTemperatureSample(n);
//                RecordFuture future = kaaClient.addLogRecord(record); // submit log record for sending to Kaa node
//                zzz("Log record {} submitted for sending" + record.toString());
//                try {
//                    RecordInfo recordInfo = future.get(); // wait for log record delivery error
//                    BucketInfo bucketInfo = recordInfo.getBucketInfo();
//                    zzz("Received log record delivery info. Bucket Id [{}]. Record delivery time [{} ms]." +
//                            bucketInfo.getBucketId() + recordInfo.getRecordDeliveryTimeMs());
//                    confirmationsCount.incrementAndGet();
//                } catch (Exception e) {
//                    zzz("Exception was caught while waiting for log's delivery report." + e.toString());
//                }
//            }
//        }.start();
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
////        close_infra_native();
////        stopMeasurement();
//        kaaClient.stop();
//        displayResults();
//    }
//
//    private void initKAA() {
//        kaaClient = Kaa.newClient(new AndroidKaaPlatformContext(this), new SimpleKaaClientStateListener() {
//            @Override
//            public void onStarted() {
//                zzz("--= Kaa client started =--");
//            }
//
//            @Override
//            public void onStopped() {
//                zzz("--= Kaa client stopped =--");
//            }
//        }, true);
//        kaaClient.setLogUploadStrategy(new RecordCountLogUploadStrategy(LOGS_DEFAULT_THRESHOLD));
//        kaaClient.addConfigurationListener(new ConfigurationListener() {
//            @Override
//            public void onConfigurationUpdate(Configuration configuration) {
//                zzz("--= Endpoint configuration was updated =--");
//                displayConfiguration(configuration);
//
//                Integer newSamplePeriod = configuration.getSamplePeriod();
//                if ((newSamplePeriod != null) && (newSamplePeriod > 0)) {
////                    changeMeasurementPeriod(kaaClient, newSamplePeriod);
//                } else {
//                    zzz("Sample period value (= {}) in updated configuration is wrong, so ignore it.");
//                }
//            }
//        });
//        kaaClient.start();
////        sleepForSeconds(MAX_SECONDS_TO_INIT_KAA);
////        startMeasurement(kaaClient);
//    }
//
//
//    private void displayResults() {
//        zzz("--= Measurement summary =--");
//        zzz("Current sample period = {} seconds" + samplePeriodInSeconds);
//        zzz("Total temperature samples sent = {}" + sentRecordsCount);
//        zzz("Total confirmed = {}" + confirmationsCount);
//    }
//
//    private void stopMeasurement() {
//        zzz("Stopping measurements...");
//        try {
//            executor.awaitTermination(MAX_SECONDS_BEFORE_STOP, TimeUnit.SECONDS);
//            executor.shutdownNow();
//            zzz("--= Temperature measurement is finished =--");
//        } catch (InterruptedException e) {
//            zzz("Can't stop temperature measurement correctly." + e.toString());
//        }
//    }
//
////    private void startMeasurement(KaaClient kaaClient) {
////        executor = Executors.newSingleThreadScheduledExecutor();
////        executorHandle = executor.scheduleAtFixedRate(new MeasureSender(kaaClient), 0, samplePeriodInSeconds, TimeUnit.SECONDS);
////        zzz("--= Temperature measurement is started =--");
////    }
//
////    private class MeasureSender implements Runnable {
////        KaaClient kaaClient;
////
////        MeasureSender(KaaClient kaaClient) {
////            this.kaaClient = kaaClient;
////        }
////
////        @Override
////        public void run() {
////            sentRecordsCount.incrementAndGet();
////            DataCollection record = generateTemperatureSample(2);
////            RecordFuture future = kaaClient.addLogRecord(record); // submit log record for sending to Kaa node
////            zzz("Log record {} submitted for sending" + record.toString());
////            try {
////                RecordInfo recordInfo = future.get(); // wait for log record delivery error
////                BucketInfo bucketInfo = recordInfo.getBucketInfo();
////                zzz("Received log record delivery info. Bucket Id [{}]. Record delivery time [{} ms]." +
////                        bucketInfo.getBucketId() + recordInfo.getRecordDeliveryTimeMs());
////                confirmationsCount.incrementAndGet();
////            } catch (Exception e) {
////                zzz("Exception was caught while waiting for log's delivery report." + e.toString());
////            }
////        }
////    }
//
//    private static Random rand = new Random();
//
//    private DataCollection generateTemperatureSample(int num) {
//        return new DataCollection(num, System.currentTimeMillis());
//    }
//
//    private void sleepForSeconds(int seconds) {
//        try {
//            TimeUnit.SECONDS.sleep(seconds);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
////    private void changeMeasurementPeriod(KaaClient kaaClient, Integer newPeriod) {
////        if (executorHandle != null) {
////            executorHandle.cancel(false);
////        }
////        samplePeriodInSeconds = newPeriod;
////        executorHandle = executor.scheduleAtFixedRate(new MeasureSender(kaaClient), 0, samplePeriodInSeconds, TimeUnit.SECONDS);
////        zzz("Set new sample period = {} seconds.");
////    }
//
//    private void displayConfiguration(Configuration configuration) {
//        zzz("Configuration = {}" + configuration.toString());
//    }
//
//    void zzz(String s) {
//        Log.i("zzz", s);
//    }
//
//
//
//    private void initView() {
//        cn = (DashboardView4) findViewById(R.id.cn);
//        button4 = (Button) findViewById(R.id.button4);
//    }
//}
