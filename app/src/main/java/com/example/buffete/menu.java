package com.example.buffete;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class menu extends AppCompatActivity {
    Button btncalendario,btnperfil,btnagenda,btnagregar,btnmail,btnacceso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        btncalendario=findViewById(R.id.btncalendario);
        btnperfil=findViewById(R.id.btnperfil);
        btnagenda=findViewById(R.id.btnagenda);
        btnagregar=findViewById(R.id.btnagregar);
        btnmail=findViewById(R.id.btnmail);
      //  btnacceso=findViewById(R.id.btnacceso);

        btncalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), citas.class);
                startActivityForResult(intent, 0);
            }
        });
        btnperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), perfil.class);
                startActivityForResult(intent, 0);
            }
        });

        btnagenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), registro.class);
                startActivityForResult(intent, 0);
            }
        });
        btnmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), email.class);
                startActivityForResult(intent, 0);
            }
        });

        /*btnacceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), Acceso.class);
                startActivityForResult(intent, 0);
            }
        });*/

    }
}