package com.example.crudo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class redireccion extends AppCompatActivity {

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redireccion);
        bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("keeplogin", Context.MODE_PRIVATE);
                boolean sesion = pref.getBoolean("login",false);
                Toast.makeText(redireccion.this, "Cargando datos...", Toast.LENGTH_SHORT).show();
                if(sesion){
                    Intent intent = new Intent(getApplicationContext(),home.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        },1500);
    }
}