package com.example.a26671.policeassist1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class viewYBActivity extends AppCompatActivity {
    String logUser = appSettings.logUser;
    String url = "http://47.99.130.80:8080/api/getAllCase?handler="+logUser;
    ArrayList<JSONObject> object ;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            object =(ArrayList<JSONObject>) msg.obj;
            try {
                appendTable(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //appendTable(object);
        }
    };





    //定义GridView
    private GridView gridView;
    //定义数据
    private List<Map<String, Object>> dataList;
    //定义适配器
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_yb);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()//url这里有个bug，等到换成高版本安卓的时候估计会报错，因为谷歌的什么安全协议对http传输不允许明文了。不能改HTTPS，因为springboot要求http。
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    Message msg = new Message();
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {               //response.body()才是你的服务器设置返回的结果。
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        Log.d("kwwl","res=="+jsonArray.get(0));
                        ArrayList<JSONObject> AL = new ArrayList<JSONObject>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject j = (JSONObject) jsonArray.get(i);
                            AL.add(j);
                        }
                        msg.obj=AL;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void appendTable(ArrayList<JSONObject> object) throws JSONException {

        for(int i = 0;i<object.size();i++){
            JSONObject js = object.get(i);
            TableLayout tl = findViewById(R.id.TL);
            TableRow tableRow = new TableRow(this);
            TextView tv1 = new TextView(this);
            tv1.setText(js.getString("value1"));
            tv1.setWidth(50);
            tv1.setGravity(Gravity.CENTER);
            TextView tv2 = new TextView(this);
            tv2.setText(js.getString("value2"));
            tv2.setWidth(50);
            tv2.setGravity(Gravity.CENTER);
            TextView tv3 = new TextView(this);
            tv3.setText(js.getString("value3"));
            tv3.setWidth(60);
            tv3.setGravity(Gravity.CENTER);
            TextView tv4 = new TextView(this);
            tv4.setText(js.getString("value4"));
            tv4.setWidth(50);
            tv4.setGravity(Gravity.CENTER);
            TextView tv5 = new TextView(this);
            tv5.setText(js.getString("value5"));
            tv5.setWidth(50);
            tv5.setGravity(Gravity.CENTER);
            TextView tv6 = new TextView(this);
            tv6.setText(js.getString("value6"));
            tv6.setWidth(60);
            tv6.setGravity(Gravity.CENTER);
            tv6.setClickable(true);
            tv6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("picID0",js.getString("value6"));
                        bundle.putString("picID1",js.getString("value7"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(viewYBActivity.this,showPicActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            tableRow.addView(tv1);
            tableRow.addView(tv2);
            tableRow.addView(tv3);
            tableRow.addView(tv4);
            tableRow.addView(tv5);
            tableRow.addView(tv6);
            tl.addView(tableRow);

        }



    }


}
