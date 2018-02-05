package com.tocel.patrol.biz.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.limp.tinkerdemo.R;
import com.tocel.patrol.biz.ui.bean.WSBean;

import java.util.Timer;
import java.util.TimerTask;

public class DialogActivity extends Activity {

    TextView tvheart;
    Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initView();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if (data != null) {
            tvheart.setText(data);
        }
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }, 3000);
    }

    @Override
    public void finish() {
        if (t != null) {
            t.cancel();
            t = null;
        }
        super.finish();
    }

    private void initView() {
        tvheart = (TextView) findViewById(R.id.tv_heart);
    }
}
