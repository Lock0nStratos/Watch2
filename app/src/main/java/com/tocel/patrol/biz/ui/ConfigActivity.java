package com.tocel.patrol.biz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.limp.tinkerdemo.R;
import com.limp.utils.UtilSP;

public class ConfigActivity extends AppCompatActivity {

    EditText edcode;
    EditText edtype;
    EditText edip;
    EditText edtoken;
    EditText edmin;
    EditText edmax;
    Button b;

    private String ip = "tcp://10.238.255.93:1883";
    private String token = "dt3aYYKnVfgI0xSZoPfq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edcode.getText().toString();
                String type = edtype.getText().toString();
                UtilSP.put(ConfigActivity.this,"ip",edip.getText().toString());
                UtilSP.put(ConfigActivity.this,"token",edtoken.getText().toString());
                if (code.equals("") || type.equals("")) {
                    Toast.makeText(ConfigActivity.this, "请输入内容!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ConfigActivity.this, AbstractPatrolActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("type", type);
                    intent.putExtra("ip", edip.getText().toString());
                    intent.putExtra("token", edtoken.getText().toString());
                    intent.putExtra("min", edmin.getText().toString());
                    intent.putExtra("max", edmax.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initView() {
        edcode = (EditText) findViewById(R.id.ed_code);
        edtype = (EditText) findViewById(R.id.ed_type);
        edip = (EditText) findViewById(R.id.ed_ip);
        edtoken = (EditText) findViewById(R.id.ed_token);
        edmin = (EditText) findViewById(R.id.ed_min);
        edmax = (EditText) findViewById(R.id.ed_max);
        edip.setText((String) UtilSP.get(this, "ip", ip));
        edtoken.setText((String) UtilSP.get(this, "token", token));
        b = (Button) findViewById(R.id.b);
    }
}
