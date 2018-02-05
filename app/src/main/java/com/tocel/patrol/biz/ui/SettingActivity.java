package com.tocel.patrol.biz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Process;

import com.limp.tinkerdemo.R;
import com.limp.utils.UtilSP;

import static com.limp.utils.UtilSP.get;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvstop;
    EditText ed_upload;
    EditText ed_cache;
    private TextView tvsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        setlistener();
    }

    private void setlistener() {
        tvstop.setOnClickListener(this);
        tvsave.setOnClickListener(this);
    }

    private void initView() {
        tvstop = (TextView) findViewById(R.id.tv_stop);
        tvsave = (TextView) findViewById(R.id.tv_save);
        ed_upload = (EditText) findViewById(R.id.ed_uploadtime);
        ed_cache = (EditText) findViewById(R.id.ed_cache);
        int time = (int) UtilSP.get(this, "uploadtime", 3000);
        ed_upload.setText((time / 1000) + "");
        int cachetime = (int) UtilSP.get(this, "cachetime", 3000);
        ed_cache.setText((cachetime / 1000) + "");
        Activity a;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_stop:
                finish();
                HeartActivity.context.finish();
                break;
            case R.id.tv_save:
                savedata();
                finish();
                HeartActivity.context.finish();
                break;
        }
    }

    private void savedata() {
        String s = ed_upload.getText().toString();
        String s2 = ed_cache.getText().toString();
        if (!s.equals("")) {
            UtilSP.put(this, "uploadtime", Integer.valueOf(s) * 1000);
        }
        if (!s2.equals("")) {
            UtilSP.put(this, "cachetime", Integer.valueOf(s2) * 1000);
        }
    }
}
