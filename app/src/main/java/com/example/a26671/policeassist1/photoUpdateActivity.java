package com.example.a26671.policeassist1;

import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class photoUpdateActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private  int count = 0;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File[] upFile = new File[2];
    String caseID;//案件编号。
    //ImageView imageView = findViewById(R.id.zhengju);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_update);
        Bundle bundle = this.getIntent().getExtras();
        caseID = bundle.getString("caseID");
        Log.i("5","获取到caseID是"+caseID);
        Button btn = findViewById(R.id.pzButton);
        Button btn1 = findViewById(R.id.scButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
                count++;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //发送okhttp请求
                        String ev1 = caseID+"_1.jpg";
                        String ev2 = caseID+"_2.jpg";
                        uploadFile(upFile[0],ev1);//图片名称加后缀
                        uploadFile(upFile[1],ev2);
                    }
                }).start();
                showDialog();
            }
        });
    }
/*    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/
   private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.a26671.policeassist1",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            upFile[count-1] = new File(currentPhotoPath);
            Log.i("555","currentpath"+currentPhotoPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath,options);
            ImageView imageView = null;
            if(count==1){
                imageView = findViewById(R.id.zhengju);
            }
            else{
                imageView = findViewById(R.id.zhengju1);
            }
            imageView.setImageBitmap(imageBitmap);
            //Log.i("5","String file 输出"+File[0]);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" +  "_ksbl00001";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = java.io.File.createTempFile(imageFileName,".jpg",storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public boolean uploadFile(File file,String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        if(!file.exists())
            return false;
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filename,RequestBody.create(MediaType.parse("multipart/form-data"),file))
                .addFormDataPart("filename",filename)
                .build();
        FutureTask<Boolean> task = new FutureTask<>(()->
        {
            try
            {
                ResponseBody responseBody = okhttp.newCall(
                        new Request.Builder().post(body).url("http://47.99.130.80:8080/api/uploadImage").build()
                ).execute().body();

                if(responseBody != null)
                    return Boolean.parseBoolean(responseBody.string());
                return false;
            }
            catch (IOException e)
            {
                return false;
            }
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    private void showDialog(){//提示信息
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.picture);
        builder.setTitle("温馨提示");
        builder.setMessage("请向违法行为人告知拟做出的行政处罚决定的事实，理由及依据，并告知其依法享有陈述权和申辩权。");
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
        builder.setMessage("请听取违法行为人的陈述和申辩！");
        builder.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(photoUpdateActivity.this , chooseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("caseID", caseID);
                        ii.putExtras(bundle);
                        startActivity(ii);
                    }
                });
        AlertDialog dialog1=builder.create();
        dialog1.show();

    }
}
