package com.example.a26671.policeassist1;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class showPicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);
        Bundle bundle = this.getIntent().getExtras();
        String picURL = bundle.getString("picID0");
        String picURL1 = bundle.getString("picID1");
        Log.i("picURI",picURL);

        ImageView imageView = findViewById(R.id.imageView);
        ImageView imageView1 = findViewById(R.id.imageView2);
        File file = downloadFile(picURL);
        imageView.setImageURI(Uri.fromFile(file));
        if (!picURL1.equals("Null")){
            File file1 = downloadFile(picURL1);
            imageView1.setImageURI(Uri.fromFile(file1));
        }
    }

    public File downloadFile(String filename) {
        OkHttpClient okhttp = new OkHttpClient();
        if(filename == null || filename.isEmpty())
            return null;
        RequestBody body = new MultipartBody.Builder().addFormDataPart("filename",filename).build();

        FutureTask<File> task = new FutureTask<>(()->
        {
            ResponseBody responseBody = okhttp.newCall(
                    new Request.Builder().post(body).url("http://47.99.130.80:8080/api/downloadImage").build()
            ).execute().body();
            if(responseBody != null)
            {
                if(getExternalFilesDir(null) != null)
                {
                    File file = new File(getExternalFilesDir(null).toString() + "/" + filename);
                    try (
                            InputStream inputStream = responseBody.byteStream();
                            FileOutputStream outputStream = new FileOutputStream(file)
                    )
                    {
                        byte[] b = new byte[1024];
                        int n;
                        if((n = inputStream.read(b)) != -1)
                        {
                            outputStream.write(b,0,n);
                            while ((n = inputStream.read(b)) != -1)
                                outputStream.write(b, 0, n);
                            return file;
                        }
                        else
                        {
                            file.delete();
                            return null;
                        }
                    }
                }
            }
            return null;
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
