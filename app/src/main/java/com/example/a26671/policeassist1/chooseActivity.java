package com.example.a26671.policeassist1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class chooseActivity extends AppCompatActivity {
    //String caseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Button btn = findViewById(R.id.cNextButton);
        Bundle bundle = this.getIntent().getExtras();
        //caseID = bundle.getString("caseID");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(chooseActivity.this,chufaActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

    }
}
