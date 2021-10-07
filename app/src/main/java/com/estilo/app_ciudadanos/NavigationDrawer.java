package com.estilo.app_ciudadanos;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.estilo.app_ciudadanos.model.Usuario;
import com.estilo.app_ciudadanos.model.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
public class NavigationDrawer extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener /*, GoogleApiClient.OnConnectionFailedListener*/ {
    CardView cardInicio, CardReportar, CardCerrarSesion, CardPerfil, CardAcercaDe, CardMisReportes, cardPreguntasFrecuentes;

    private Bitmap bitmap;
    private ImageView foto;
    private int request_code = 1;
    TextView nombreUsuario, idUser,email;
    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private static final String TAG = NavigationDrawer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        idUser = findViewById(R.id.txtIdUser);
        idUser.setVisibility(View.GONE);
        foto = findViewById(R.id.FotoPerfil);
        nombreUsuario = findViewById(R.id.txtNombreUSer);
        nombreUsuario.setGravity(Gravity.CENTER);
        email = findViewById(R.id.txtEmailUser);
        email.setVisibility(View.GONE);
        cardInicio = findViewById(R.id.carInicio);
        CardReportar = findViewById(R.id.cardReportar);
        CardPerfil = findViewById(R.id.cardPerfil);
        CardCerrarSesion = findViewById(R.id.cardCerrar);
        CardAcercaDe = findViewById(R.id.carInicio);
        CardMisReportes = findViewById(R.id.cardMisReportes);
        cardPreguntasFrecuentes = findViewById(R.id.cardPreguntasFrecuentes);

        CardAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(NavigationDrawer.this, AcercaDeActivity.class);
                startActivity(i);
            }
        });
        CardReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(NavigationDrawer.this, DenunciasActivity.class);
                startActivity(i);
            }
        });
        CardPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(NavigationDrawer.this, PerfilActivity.class);
                startActivity(i);
            }
        });
        CardCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();

            }
        });
        CardMisReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(NavigationDrawer.this, ListaReportesActivity.class);
                startActivity(i);
            }
        });
        cardPreguntasFrecuentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NavigationDrawer.this, ContenedorInstruccionesActivity.class);
                startActivity(i);
            }
        });


        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        boolean sesion = preferences.getBoolean("sesion", false);

        if (!sesion) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            recuperarpreferencias();
            buscarID();

        }


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




    public void onClick(View view) {

    }

    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences preference = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        SharedPreferences preferenc = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea cerrar sesión y salir  de la aplicación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        preferences.edit().clear().commit();
                        preference.edit().clear().commit();
                        preferenc.edit().clear().commit();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();

    }

    private void recuperarpreferencias() {
        SharedPreferences preferences = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.txtIdUser)).setText(String.valueOf(preferences.getInt("id_usuario", 1)));
        ((TextView) findViewById(R.id.txtNombreUSer)).setText(preferences.getString("nombre_usuario", "Sin nombre "));
        ((TextView) findViewById(R.id.txtEmailUser)).setText(preferences.getString("email_usuario", "Sin email"));

    }

    public void buscarID() {
        String ip = getString(R.string.ip);

        String url = ip + "/ConsultarIdUserCiudadano.php?email_usuario=" + ((TextView) findViewById(R.id.txtEmailUser)).getText().toString();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        // request.add(jsonObjectRequest);
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "COMPRUEBE LA CONEXION A INTERNET", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {


        Usuario uUsuario = new Usuario();

        JSONArray json = response.optJSONArray("usuarios");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            uUsuario.setId(jsonObject.optInt("id_usuario"));
            uUsuario.setNombre(jsonObject.optString("nombre_usuario"));
            uUsuario.setFoto(jsonObject.optString("foto_usuario"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        idUser.setText("" + uUsuario.getId());
        nombreUsuario.setText("" + uUsuario.getNombre());


        if (!uUsuario.getFoto().equals("sin imagen")) {
            String ip = getString(R.string.ip);

            String url_image = ip + "/" + uUsuario.getFoto();
            url_image = url_image.replace(" ", "%20");
            try {
                Log.i("RESPUESTA IMAGEN: ", "" + url_image);
                Glide.with(this).load(url_image).into(foto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        SharedPreferences preference = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("id_usuario", ((TextView) findViewById(R.id.txtIdUser)).getText().toString());
        editor.putBoolean("sesion", true);
        editor.apply();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == request_code) {
            foto.setImageURI(data.getData());

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }







}