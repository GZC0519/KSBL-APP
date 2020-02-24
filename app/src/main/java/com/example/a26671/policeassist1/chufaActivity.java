package com.example.a26671.policeassist1;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class chufaActivity extends AppCompatActivity {
    String caseID;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showDialog();
                    break;
                case 2:
                    Intent ii = new Intent(chufaActivity.this , Main1Activity.class);
                    startActivity(ii);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chufa);
        Button btn = (Button) findViewById(R.id.button5);
        Bundle bundle = this.getIntent().getExtras();
        caseID = bundle.getString("caseID");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.editText);
                CheckBox ch = findViewById(R.id.jinggao);
                CheckBox ch1 = findViewById(R.id.fakuan);
                boolean flag;
                if(ch.isChecked()){
                    flag = false;
                }
                else{
                    flag = true;
                }
                if (flag){
                    int rmb = Integer.parseInt(et.getText().toString());
                    if(!(0<=rmb&&rmb<=200)){
                        Log.i("what happen"," idont know");
                        showDialog1();
                    }
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String url;
                                    url = "http://47.99.130.80:8080/api/updateCaseTable?str1=result&str2=罚款"+et.getText().toString()+"元&str3="+caseID;
                                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                                    Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                                            .build();//创建Request 对象
                                    Response response = null;
                                    response = client.newCall(request).execute();
                                    Message msg = new Message();
                                    if (response.isSuccessful()){
                                        msg.what=1;
                                        handler.sendMessage(msg);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String url;
                                url = "http://47.99.130.80:8080/api/updateCaseTable?str1=result&str2=警告&str3="+caseID;
                                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                                Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                                        .url(url)//请求接口。如果需要传参拼接到接口后面。
                                        .build();//创建Request 对象
                                Response response = null;
                                response = client.newCall(request).execute();
                                Message msg = new Message();
                                if (response.isSuccessful()){
                                    msg.what=2;
                                    handler.sendMessage(msg);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });
    }

    private void showDialog(){//提示信息
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("");
        builder.setMessage("请填写《行政处罚决定书》，《罚款收据》交付被处罚人！");
        builder.setPositiveButton("完成",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(chufaActivity.this , Main1Activity.class);
                        startActivity(ii);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void showDialog1(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("");
        builder.setMessage("处罚金额超过200元，不适用于快速办理！");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(chufaActivity.this , chufaActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("caseID", caseID);
                        ii.putExtras(bundle);
                        startActivity(ii);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
