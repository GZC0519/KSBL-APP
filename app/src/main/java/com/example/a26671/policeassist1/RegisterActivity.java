package com.example.a26671.policeassist1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    boolean flag = false;
    String IDnum = "";
    String  name = "";
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
                    case  -2:
                        showDialog();
                        break;
                }
            }
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //final boolean flag = false ;
                EditText et = findViewById(R.id.IDNumber);
                EditText et1 = findViewById(R.id.Name);
                IDnum = et.getText().toString();
                name = et1.getText().toString();
                Log.i("5", "高志材真他么帅 ID:"+IDnum+"姓名:"+name);
                String url = "http://47.99.130.80:8080/api/matchPD?ID="+IDnum+"&name="+name;
                new Thread(new Runnable() {
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
                }).start();
            }
        });
    }

    private void showDialog(){//人民警察数据库中不存在输入的警员信息时显示
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage("未查询到警员信息！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void showDialog1(){//警察库验证成功，输入密码添加账户
                        Intent ii = new Intent(RegisterActivity.this , addUser.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", IDnum);
                        bundle.putString("name", name);
                        ii.putExtras(bundle);
                        startActivity(ii);
    }
}
