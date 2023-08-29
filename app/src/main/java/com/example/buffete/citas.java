package com.example.buffete;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

public class citas extends AppCompatActivity {
    EditText etnombre, etapellidop, etapellidom,etmail, ettelefono, etbuscar, etid;
    Button btnagregar,btnbuscar, btneditar;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_citas);

        etid=findViewById(R.id.etid);
        etnombre=findViewById(R.id.etnombre);
        etapellidop=findViewById(R.id.etapellidop);
        etapellidom=findViewById(R.id.etapellidom);
        etmail=findViewById(R.id.etus);
        ettelefono=findViewById(R.id.ettelefono);
        etbuscar=findViewById(R.id.etbuscar);
        btnagregar=findViewById(R.id.close_button);
        btnbuscar=findViewById(R.id.btnbuscar);
        btneditar=findViewById(R.id.btneditar);


        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
ejecutarServicio("http://192.168.0.12" +
        "/projectdb/consulta.php");
            }
        });
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buscar("http://192.168.0.12/projectdb/buscar.php?id="+etbuscar.getText()+"");
            }
        });

        btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.0.12/projectdb/editar.php");
            }
        });
    }
    private void ejecutarServicio(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
                // Usar Log en lugar de System.out.println
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id", etid.getText().toString());
                parametros.put("nombre", etnombre.getText().toString());
                parametros.put("apellido_p", etapellidop.getText().toString());
                parametros.put("apellido_m", etapellidom.getText().toString());
                parametros.put("email", etmail.getText().toString());
                parametros.put("tel", ettelefono.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(getApplicationContext()); // Usar getApplicationContext() en lugar de this
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
                        etnombre.setText(jsonObject.getString("nombre"));
                        etapellidop.setText(jsonObject.getString("apellido_p"));
                        etapellidom.setText(jsonObject.getString("apellido_m"));
                        etmail.setText(jsonObject.getString("email"));
                        ettelefono.setText(jsonObject.getString("tel"));
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







