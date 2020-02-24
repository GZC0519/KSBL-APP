package com.example.a26671.policeassist1;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Button btn1 = (Button) findViewById(R.id.zcButton);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){//点击按钮执行的动作
                Intent ii = new Intent(Main1Activity.this , pdfViewActivity.class);
                startActivity(ii);

/*                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://47.99.130.80/home/zhengce/guiding.pdf";
                        downFile(url);
                        Message msg = new Message();

                        //handler.sendMessage(msg);
                    }
                }).start();*/

            }
        });
        Button btn2 = findViewById(R.id.ksblButton);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("黄赌毒案件不适用于快速办理！");
            }
        });
        Button btn3 = findViewById(R.id.yblButton);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoYibanli();
            }
        });
    }

    private void downFile(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //实现连接
            connection.connect();

            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                //以下为下载操作
                byte[] arr = new byte[1];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int n = is.read(arr);
                while (n > 0) {
                    bos.write(arr);
                    n = is.read(arr);
                }
                bos.close();
                String path = Environment.getExternalStorageDirectory()
                        + "/download/";
                String[] name = urlString.split("/");
                path = path + name[name.length - 1];
                File file = new File(path);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.close();
                //关闭网络连接
                connection.disconnect();
                Log.d("下载完成","下载完成");
                openPDF(file);//打开PDF文件
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
    }

    private void openPDF(File file) {
        if (file.exists()) {
            Log.d("打开","打开");
            Uri path1 = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path1, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            }
            catch (Exception e) {
                Log.d("打开失败","打开失败");
            }
        }
    }

    private void showDialog(String message){//提示信息
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage(message);
        builder.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDialog1();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void showDialog1(){//提示信息
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("");
        builder.setMessage("请询问违法行为人是否同意使用快速办理程序处理案件，如果同意请出示《行政案件权利与义务告知书》让违法行为人签字阅读并签字确认。");
        builder.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDialog2();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void showDialog2(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("");
        builder.setMessage("请打开执法记录仪，向违法人行为人表明执法身份！");
        builder.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(Main1Activity.this , wfrInsMsgActivity.class);
                        startActivity(ii);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void gotoYibanli(){
        Intent i = new Intent(Main1Activity.this,viewYBActivity.class);
        startActivity(i);
    }

}
