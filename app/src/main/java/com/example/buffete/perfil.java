package com.example.buffete;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class perfil extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PERMISSION = 100;

    EditText etusu, etbus, etcon;
    Button btnbus, btnedit, btnGaleria, btnCamara;
    ImageView ivFotoPerfil;
    RequestQueue requestQueue;
    Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil);

        // Inicialización de componentes
        etusu = findViewById(R.id.etusu);
        etbus = findViewById(R.id.etid);
        etcon = findViewById(R.id.etcon);
        btnbus = findViewById(R.id.button_send);
        btnedit = findViewById(R.id.btnedit);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnCamara = findViewById(R.id.btnCamara);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        // Inicialización de RequestQueue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        btnedit.setOnClickListener(view -> ejecutarServicio("http://192.168.90.198/projectdb/editar_perfil.php"));

        btnbus.setOnClickListener(view -> Buscar("http://192.168.90.198/projectdb/buscar_perfil.php?usuario=" + etbus.getText()));

        btnGaleria.setOnClickListener(view -> {
            if (checkPermissions()) {
                openGallery();
            } else {
                requestPermissions();
            }
        });

        btnCamara.setOnClickListener(view -> {
            if (checkPermissions()) {
                openCamera();
            } else {
                requestPermissions();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                Uri selectedImageUri = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                    ivFotoPerfil.setImageBitmap(selectedImageBitmap);
                    uploadImage(selectedImageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                ivFotoPerfil.setImageBitmap(selectedImageBitmap);
                uploadImage(selectedImageBitmap);
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        String encodedImage = encodeImageToBase64(bitmap);
        String URL = "http://192.168.90.198/projectdb/upload_image.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getApplicationContext(), "Imagen subida exitosamente", Toast.LENGTH_SHORT).show(),
                error -> {
                    Toast.makeText(getApplicationContext(), "Error al subir imagen: " + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadError", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", etusu.getText().toString());
                parametros.put("image", encodedImage);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void ejecutarServicio(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show(),
                error -> {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("ServiceError", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", etusu.getText().toString());
                parametros.put("contraseña", etcon.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void Buscar(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        etusu.setText(jsonObject.getString("usuario"));
                        etcon.setText(jsonObject.getString("contraseña"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonArrayRequest);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
