package com.example.buffete;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class perfil extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1; // Código para identificar la solicitud de la cámara
    private static final int REQUEST_CAMERA_PERMISSION = 200; // Código para la solicitud de permiso
    EditText etusu, etcon;
    Button btnedit, btnTomar;
    ImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil);

        etusu = findViewById(R.id.etusu);
        etcon = findViewById(R.id.etcon);
        btnedit = findViewById(R.id.btnedit);
        btnTomar = findViewById(R.id.btnTomar); // Vincula el botón de tomar foto
        imgFoto = findViewById(R.id.imgFoto); // Vincula el ImageView para la foto

        // Configurar el botón para tomar foto
        btnTomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Verificar si se tiene permiso para la cámara
                    if (ContextCompat.checkSelfPermission(perfil.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        tomarFoto();
                    } else {
                        // Solicitar permiso
                        ActivityCompat.requestPermissions(perfil.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } else {
                    // En versiones anteriores, no es necesario solicitar permisos en tiempo de ejecución
                    tomarFoto();
                }
            }
        });

        // Configurar el botón de editar perfil
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes implementar la lógica para editar el perfil, si es necesario
            }
        });

        // Llamar al método para validar al usuario con los datos del formulario
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarUsuario(etusu.getText().toString(), etcon.getText().toString());
            }
        });
    }

    private void tomarFoto() {
        // Crea un Intent para abrir la cámara
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Inicia la actividad y espera el resultado
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, tomar la foto
                tomarFoto();
            } else {
                // Permiso denegado, mostrar mensaje
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Obtiene el bitmap de la imagen capturada
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            // Muestra la imagen en el ImageView
            imgFoto.setImageBitmap(bitmap);
            // Convierte la imagen a base64 y sube a PHP
            String imageString = convertBitmapToBase64(bitmap);
            subirImagen(imageString);
        } else {
            // Muestra un mensaje de cancelación
            Toast.makeText(this, "Proceso cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void subirImagen(String imageString) {
        // La URL de tu script PHP
        String url = "http://192.168.100.18/projectdb/subirimagen.php";

        // Crear la solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Aquí puedes manejar la respuesta del servidor
                        Toast.makeText(perfil.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores
                        Toast.makeText(perfil.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Aquí se envía el dato de la imagen
                Map<String, String> params = new HashMap<>();
                params.put("imagen", imageString); // Clave 'imagen' debe coincidir con el nombre del parámetro en el PHP
                return params;
            }
        };

        // Crear la cola de solicitudes y agregarla
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validarUsuario(final String usuario, final String password) {
        String url = "http://192.168.100.18/projectdb/validar_usuario.php";

        // Crear la solicitud de validación
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Verifica la respuesta JSON del servidor
                        Log.d("Respuesta del servidor", response);  // Agregar este log
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("error")) {
                                // Si hay un error
                                Toast.makeText(perfil.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                // Si la autenticación es exitosa, muestra los datos
                                String nombre = jsonResponse.getString("nombre"); // Asegúrate de que estos campos existan en tu tabla
                                String correo = jsonResponse.getString("correo");

                                // Mostrar los datos en la interfaz
                                etusu.setText(nombre);
                                etcon.setText(correo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(perfil.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja el error de la solicitud
                        Toast.makeText(perfil.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar los parámetros del usuario y la contraseña
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("password", password);
                return params;
            }
        };

        // Crear una cola de solicitudes y agregarla
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
