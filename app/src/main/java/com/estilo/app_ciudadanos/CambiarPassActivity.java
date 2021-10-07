package com.estilo.app_ciudadanos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.estilo.app_ciudadanos.model.VolleySingleton;

import java.util.HashMap;
import java.util.Map;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class CambiarPassActivity extends AppCompatActivity {
    EditText cambiarPass;
    Button cambiar;
    StringRequest stringRequest;//SE MODIFICA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);
        cambiarPass = (EditText) findViewById(R.id.passActual);
        cambiar = (Button) findViewById(R.id.btnCambiarPass);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validar()) return;
                webServiceActualizar();
            }
        });
    }

    private boolean validar() {
        boolean valid = true;

        String sPassword = cambiarPass.getText().toString();
        if (sPassword.isEmpty() || sPassword.length() < 8 || sPassword.length() > 21) {
            cambiarPass.setError("Entre 8 y 20 caracteres alfanuméricos");
            valid = false;
        } else {
            cambiarPass.setError(null);
        }

        return valid;
    }

    private void webServiceActualizar() {
        SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        String idusuario = preferences.getString("id_usuario", "1");
        String ip = getString(R.string.ip);
        String url = ip + "/cambiarContraseña.php?id_usuario=" + idusuario;

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("actualiza")) {

                    Toast.makeText(getApplicationContext(), "Se ha Actualizado con exito", Toast.LENGTH_SHORT).show();
                    Intent i;
                    i = new Intent(CambiarPassActivity.this, PerfilActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "La contraseña no se cambió. Verifique el estado de Conexión", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
                String idusuario = preferences.getString("id_usuario", "1");
                String codigo = idusuario;
                String pass = cambiarPass.getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", codigo);
                parametros.put("pass_usuario", pass);


                return parametros;
            }
        };
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}