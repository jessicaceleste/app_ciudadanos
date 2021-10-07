package com.estilo.app_ciudadanos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estilo.app_ciudadanos.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class RegistroActivity extends AppCompatActivity {
    private static final String TAG = RegistroActivity.class.getSimpleName();
    ProgressBar progressBar;
    private Button btnGuardar;
    private EditText nombre, email, pass;
    private int request_code = 1;
    private Bitmap bitmap;
    private ProgressDialog progreso;
    RequestQueue requestQueue; //permitara la conexion directamente del web service
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = (EditText) findViewById(R.id.txt_nombreU);
        email = (EditText) findViewById(R.id.txt_emailU);
        pass = (EditText) findViewById(R.id.txt_passwordU);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);

        progreso = new ProgressDialog(this);
        progreso.setCancelable(false);
        requestQueue = Volley.newRequestQueue(this);
        progressBar = (ProgressBar) findViewById(R.id.progresoRegistro);
        progressBar.setVisibility(View.INVISIBLE);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validar()) return;
                Registrar();
            }
        });


    }

    public void ButtonIrLogin(View v) {
        Intent intent = new Intent(RegistroActivity.this, NavigationDrawer.class);
        startActivity(intent);
        finish();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true; }
        return super.onKeyDown(keyCode, event);
    }




    private void Registrar() {
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String ip = getString(R.string.ip);

                String url = ip + "/registrarciudadadano1.php?";

                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Usuario usuario = new Usuario();
                        ;
                        Log.i("RESPUESTA JSON: ", "" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.names().get(0).equals("success")) {
                                guardarpreferecias();
                                progressBar.setVisibility(View.INVISIBLE);

                                email.setText("");
                                nombre.setText("");
                                pass.setText("");
                                usuario.setId(jsonObject.getJSONArray("usuarios").getJSONObject(0).getInt("id_usuario"));
                                usuario.setEmail(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("email_usuario"));
                                usuario.setNombre(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("nombre_usuario"));

                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                                SharedPreferences preference = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preference.edit();
                                editor.putString("id_usuario", usuario.setId(jsonObject.getJSONArray("usuarios").getJSONObject(0).getInt("id_usuario")));
                                editor.putString("nombre_usuario", usuario.setNombre(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("nombre_usuario")));
                                editor.putString("email_usuario", usuario.setEmail(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("email_usuario")));
                                editor.commit();

                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                                Intent intent = new Intent(RegistroActivity.this, NavigationDrawer.class);
                                intent.putExtra("DATA_USER", usuario);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                Log.i("RESPUESTA JSON: ", "" + jsonObject.getString("error"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progreso.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Lo sentimos no se pudo crear la cuenta =(...Inténtelo de Nuevo! " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        progreso.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {//para enviar los datos mediante POST
                        String sEmail = email.getText().toString();
                        String sPassword = pass.getText().toString();
                        String sNombre = nombre.getText().toString();
                        String sImagePhoto = convertirImgString(bitmap);

                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("email_usuario", sEmail);
                        parametros.put("foto_usuario", sImagePhoto);
                        parametros.put("nombre_usuario", sNombre);
                        parametros.put("pass_usuario", sPassword);
                        //estos parametros son enviados a nuestro web service

                        return parametros;
                    }
                };

                requestQueue.add(stringRequest);
            }
        }, 2000);

    }

    private String convertirImgString(Bitmap bitmap) {

        String imagenString;
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
            byte[] imagenByte = array.toByteArray();
            imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        } else {
            imagenString = "no imagen"; //se enviara este string en caso de no haber imagen
        }

        return imagenString;
    }

    private boolean validar() {
        boolean valid = true;

        String sNombre = nombre.getText().toString();
        String sPassword = pass.getText().toString();
        String sEmail = email.getText().toString();

        if (sNombre.isEmpty() || sNombre.length() < 3) {
            nombre.setError("Ingrese al menos 3 caracteres");
            valid = false;
        } else {
            nombre.setError(null);
        }

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Dirección de correo electrónico no válida");
            valid = false;
        } else {
            email.setError(null);
        }

        if (sPassword.isEmpty() || pass.length() < 8 || pass.length() > 21) {
            pass.setError("Ingrese entre 8 a 20 caracteres alfanuméricos");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    private void guardarpreferecias() {
        SharedPreferences preference = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("usuario", email.getText().toString());
        editor.putString("password", pass.getText().toString());
        editor.putBoolean("sesion", true);
        editor.commit();

    }

}