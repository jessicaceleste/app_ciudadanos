package com.estilo.app_ciudadanos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estilo.app_ciudadanos.model.Usuario;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

public class LoginActivity extends AppCompatActivity  {

    ProgressBar progressBar;

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final int SIGN_IN_CODE = 777;

    private ProgressDialog progreso;
    private RequestQueue requestQueue;
    StringRequest stringRequest;
    EditText usuario, password;
    Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progreso);
        progressBar.setVisibility(View.INVISIBLE);

        usuario = (EditText) findViewById(R.id.txt_usuario);
        password = (EditText) findViewById(R.id.txt_password);
        login = (Button) findViewById(R.id.btnLogin);



        recuperarPreferencias();
        progreso = new ProgressDialog(this);
        progreso.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar();
            }
        });

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



    public void Registrarse(View v) {
        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
        startActivity(intent);
        finish();
    }

    private void iniciar() {

        if (!validar()) return;
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String ip = getString(R.string.ip);

                String url = ip + "/sesion.php?";

                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Usuario userParcelable = new Usuario();
                        ;
                        Log.i("RESPUESTA JSON: ", "" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            if (jsonObject.names().get(0).equals("success")) {

                                guardarpreferecias();

                                usuario.setText("");
                                password.setText("");
                                userParcelable.setId(jsonObject.getJSONArray("usuarios").getJSONObject(0).getInt("id_usuario"));
                                userParcelable.setEmail(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("email_usuario"));
                                userParcelable.setNombre(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("nombre_usuario"));

                                SharedPreferences preference = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preference.edit();
                                editor.putString("id_usuario", userParcelable.setId(jsonObject.getJSONArray("usuarios").getJSONObject(0).getInt("id_usuario")));
                                editor.putString("nombre_usuario", userParcelable.setNombre(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("nombre_usuario")));
                                editor.putString("email_usuario", userParcelable.setEmail(jsonObject.getJSONArray("usuarios").getJSONObject(0).getString("email_usuario")));
                                editor.commit();

                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                progreso.dismiss();
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawer.class);
                                intent.putExtra("DATA_USER", userParcelable);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progreso.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {//para enviar los datos mediante POST
                        String sEmail = usuario.getText().toString();
                        String sPassword = password.getText().toString();

                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("email_usuario", sEmail);
                        parametros.put("pass_usuario", sPassword);
                        //estos parametros son enviados a nuestro web service

                        return parametros;
                    }
                };

                requestQueue.add(stringRequest);
            }
        }, 2000);

    }

    private boolean validar() {
        boolean valid = true;

        String sEmail = usuario.getText().toString();
        String sPassword = password.getText().toString();

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            usuario.setError("Introduzca una dirección de correo electrónico válida");
            valid = false;
        } else {
            usuario.setError(null);
        }

        if (sPassword.isEmpty() || password.length() < 8 || password.length() > 21) {
            password.setError("Entre 8 y 20 caracteres alfanuméricos");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void guardarpreferecias() {
        SharedPreferences preference = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("usuario", usuario.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putBoolean("sesion", true);
        editor.commit();

    }

    public void recuperarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        usuario.setText(preferences.getString("usuario", ""));
        password.setText(preferences.getString("password", ""));


    }


}