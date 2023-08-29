package com.example.buffete;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class carga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_carga);
        TimerTask tarea=new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(carga.this, login.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo= new Timer();
        tiempo.schedule(tarea, 500);
    }
}
