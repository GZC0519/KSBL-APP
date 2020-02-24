package com.example.a26671.policeassist1;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log ;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("6","msg.what="+msg.what);
            switch (msg.what) {
                case 1:
                    appSettings.logUser=msg.obj.toString();
                    gotoMain1Acivity();
                    break;
                case -2:
                    showDialog("账户不存在！");
                    break;
                case -3:
                    showDialog("密码错误！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText et = findViewById(R.id.IDNumber);
                EditText et1 = findViewById(R.id.Password);
                String IDnum = et.getText().toString();
                String password = et1.getText().toString();
                String url = "http://47.99.130.80:8080/api/login?ID="+IDnum+"&password="+password;
                Log.i("555",url);
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
                                String logUser = object.getString("result");
                                Message msg = new Message();
                                msg.what = code;
                                msg.obj=logUser;
                                handler.sendMessage(msg);
                                Log.i("5555","得到code是"+code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }
    private void showDialog(String str){//错误信息提示
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage(str);
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private  void gotoMain1Acivity(){
        Intent ii = new Intent(LoginActivity.this , Main1Activity.class);
        /*Bundle bundle = new Bundle();
        bundle.putString("ID", IDnum);
        bundle.putString("name", name);
        ii.putExtras(bundle);*/
        startActivity(ii);
    }
}
