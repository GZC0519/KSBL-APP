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


import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

public class wfrInsMsgActivity extends AppCompatActivity {
    String CaseID = "";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CaseID = msg.obj.toString();
            Log.i("777777","最后的主程序获取到的caseID是"+CaseID);
            switch (msg.what) {
                case 1:
                    Intent ii = new Intent(wfrInsMsgActivity.this , photoUpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("caseID", CaseID);
                    ii.putExtras(bundle);
                    startActivity(ii);
                    break;
                case -1:
                    showDialog("插入信息出错，请稍后重试 ！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfr_ins_msg);
        Button btn = (Button) findViewById(R.id.nextButton);
        //generateCaseID();
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //final boolean flag = false ;
                EditText et = findViewById(R.id.name);
                EditText et1 = findViewById(R.id.ID);
                CheckBox ch = findViewById(R.id.issicked);
                CheckBox ch1 = findViewById(R.id.isrenda);
                EditText et3 = findViewById(R.id.jqsname);
                EditText et4 = findViewById(R.id.jqsphone);
                String wfrname = et.getText().toString();
                String wfrID = et1.getText().toString();
                boolean issicked;
                boolean isrenda;
                if(ch.isChecked()){
                    issicked = true;
                }else {
                    issicked = false;
                }
                if(ch1.isChecked()){
                    isrenda = true;
                }else{
                    isrenda = false;
                }
                String jqsname = et3.getText().toString();
                String jqsphone = et4.getText().toString();
                String chuliren = appSettings.logUser;
                Log.i("20200217","获取信息输出："+wfrname+wfrID+isrenda+issicked+jqsname+jqsphone+"caseID"+CaseID+chuliren);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                            Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                                    .url("http://47.99.130.80:8080/api/getCaseID")//请求接口。如果需要传参拼接到接口后面。
                                    .build();//创建Request 对象
                            Response response = null;
                            Response response1 = null;
                            Message msg = new Message();
                            String caseID="";
                            response = client.newCall(request).execute();
                            if (response.isSuccessful()) {               //response.body()才是你的服务器设置返回的结果。
                                JSONObject json = new JSONObject(response.body().string());
                                caseID=json.getString("result");
                                Log.d("kwwl","response.code()=="+response.code());
                                Log.d("kwwl","response.message()=="+response.message());
                                Log.d("kwwl","res=="+caseID);
                                msg.obj=caseID;
                                //时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                                //handler.sendMessage(msg);
                            }
                            String url = "http://47.99.130.80:8080/api/insertCase?caseID="+caseID+"&wfrName="+wfrname+"&wfrID="+wfrID+"&issick="+issicked+"&isOfficial="+isrenda+"&jqsName="+jqsname+"&jqsTel="+jqsphone+"&handler="+chuliren;
                            Log.i("pppppp",url);
                            Request request1 = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                                    .url(url)//请求接口。如果需要传参拼接到接口后面。
                                    .build();//创建Request 对象
                            response1 = client.newCall(request1).execute();//得到Response 对象  得到的response对象是http协议返回的对象，
                            if (response1.isSuccessful()) {               //response.body()才是你的服务器设置返回的结果。
                                JSONObject object1 = new JSONObject(response1.body().string());
                                int code = object1.getInt("code");
                                msg.what = code;
                                Log.i("5555","得到code是"+code);
                            }
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

/*    private  void generateCaseID(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                            .url("http://192.168.101.20:8080/api/getCaseID")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象  得到的response对象是http协议返回的对象，
                    if (response.isSuccessful()) {               //response.body()才是你的服务器设置返回的结果。
                        JSONObject json = new JSONObject(response.body().string());
                        String caseID=json.getString("result");
                        msg.obj=caseID;
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        Log.d("kwwl","res=="+caseID);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        //handler.sendMessage(msg);
                    }
                    } catch (Exception e) {
                    e.printStackTrace();
                     }
                      }
        }).start();
    }*/

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
}
