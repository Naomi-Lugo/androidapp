package com.example.buffete;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class registro extends AppCompatActivity {
    EditText etnom, etap, etus,etcon,etbu,etid;
    Button btnsign, btnbu,btnedi;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etnom=findViewById(R.id.etnom);
        etap=findViewById(R.id.etap);
        etus=findViewById(R.id.etus);
        etcon=findViewById(R.id.etcon);
        etid=findViewById(R.id.etid);
        btnsign=findViewById(R.id.btnsign);
        btnbu=findViewById(R.id.btnbu);
        etbu=findViewById(R.id.etbu);
        btnedi=findViewById(R.id.btnedi);

        btnbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buscar("http://192.168.100.18/projectdb/buscar_usuario.php?id="+etbu.getText()+"");
            }
        });
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarUsuario("http://192.168.100.18/projectdb/registrar.php");
            }
        });

        btnedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarUsuario("http://192.168.100.18/projectdb/editar_usuario.php");
            }
        });
    }
    private void RegistrarUsuario (String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            Toast.makeText(registro.this, "OPERACION EXITOSA", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(registro.this, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println(error.toString());
                    }
                }) {
            @Nullable
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put("id", etid.getText().toString());
                parametros.put("nombre", etnom.getText().toString());
                parametros.put("apellido", etap.getText().toString());
                parametros.put("usuario", etus.getText().toString());
                parametros.put("contraseña",etcon.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void Buscar (String URL){
        JsonArrayRequest JsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        etid.setText(jsonObject.getString("id"));
                        etnom.setText(jsonObject.getString("nombre"));
                        etap.setText(jsonObject.getString("apellido"));
                        etus.setText(jsonObject.getString("usuario"));
                        etcon.setText(jsonObject.getString("contraseña"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(JsonArrayRequest);
    }
}