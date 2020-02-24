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
import android.widget.EditText;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class addUser extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("6","msg.what="+msg.what);
            switch (msg.what) {
                case 1:
                    showDialog1();
                    break;
                case -1:
                    showDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Button btn = (Button) findViewById(R.id.button4);
        Bundle bundle = this.getIntent().getExtras();
        String name = bundle.getString("name");
        String ID = bundle.getString("ID");

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText et = findViewById(R.id.password);
                EditText et1 = findViewById(R.id.password1);
                String password = et.getText().toString();
                String password1 = et1.getText().toString();
                Log.e("7", "password"+password+password1+name+ID);
                if (!password.equals(password1)){
                    showDialog();
                }
                String url = "http://47.99.130.80:8080/api/insertRegistUser?ID="+ID+"&password="+password+"&name="+name;
                //插入在数据库中的插入操作
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                            Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                                    .url(url)//请求接口。如果需要传参拼接到接口后面。
                                    .build();//创建Request 对象
                            Response response = null;
                            response = client.newCall(request).execute();//得到Response 对象  得到的response对象是http协议返回的对象，
                            if (response.isSuccessful()) {               //response.body()才是你的服务器设置返回的结果。
                                JSONObject object = new JSONObject(response.body().string());
                                int code = object.getInt("code");
                                Message msg = new Message();
                                msg.what = code;
                                handler.sendMessage(msg);
                                Log.i("5555","得到code是"+code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();
                //Intent是一种运行时绑定（run-time binding）机制，它能在程序运行过程中连接两个不同的组件。 
                //在存放资源代码的文件夹下下， 
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage("密码不一致！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void showDialog1(){//
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage("注册成功，请登录！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(addUser.this , LoginActivity.class);
                        startActivity(ii);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}
