package com.estilo.app_ciudadanos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.estilo.app_ciudadanos.model.Ciudadano;
import com.estilo.app_ciudadanos.model.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class PerfilActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static final String CARPETA_PRINCIPAL = "app_bomberos/";//directorio principal
    private static final String CARPETA_IMAGEN = "perfil_fotos";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private EditText Nombre, direccion, telefono;
    TextView id, usuario, url_foto, txtIdUser;
    // Button foto;
    FloatingActionButton confirmar;
    JsonObjectRequest jsonObjectRequest;
    private int request_code = 1;
    StringRequest stringRequest;//SE MODIFICA
    ProgressDialog pDialog;
    ImageView foto;
    ImageView Perfil;
    Button btnCambiarPass, btnDesactivarCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Perfil = findViewById(R.id.FotoPerfil);
        id = findViewById(R.id.txtidCiudadano);
        Nombre = findViewById(R.id.txtNombreCiudadano);
        direccion = findViewById(R.id.txtDireccionCiudadano);
        telefono = findViewById(R.id.txtNroTelefono);
        btnCambiarPass = (Button) findViewById(R.id.btnCambiarPass);
        txtIdUser = findViewById(R.id.txtid);
        url_foto = findViewById(R.id.txturl_foto);
        url_foto.setVisibility(View.GONE);
        confirmar = findViewById(R.id.idModificarPerfil);
        ((TextView) findViewById(R.id.txtid)).setVisibility(View.GONE);

        id.setVisibility(View.GONE);


        SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        String idusuario = preferences.getString("id_usuario", "1");

        boolean sesion = preferences.getBoolean("sesion", false);
        if (!sesion) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
        ((TextView) findViewById(R.id.txtid)).setText((idusuario));
        usuario = findViewById(R.id.txtid);
        usuario.setText(idusuario);

        Perfil = findViewById(R.id.FotoPerfil);
        telefono.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        buscarCiudadano();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                //verificacion de la version de plataforma
                if (Build.VERSION.SDK_INT < 19) {
                    //android 4.3  y anteriores
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    //android 4.4 y superior
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                }
                i.setType("image/*");
                startActivityForResult(i, request_code);

            }
        });

    }

    public void cambiarPass(View v) {
        Intent i = new Intent(PerfilActivity.this, CambiarPassActivity.class);
        startActivity(i);
        finish();
    }

    public void DesactivarCuenta(View view) {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences preference = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        SharedPreferences preferenc = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
Context context;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro que desea Desactivar definitivamente esta cuenta?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cambiarEstadoCuenta();
                        preferences.edit().clear().commit();
                        preference.edit().clear().commit();
                        preferenc.edit().clear().commit();
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void cambiarEstadoCuenta() {
        SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        String idusuario = preferences.getString("id_usuario", "1");
        String ip = getString(R.string.ip);
        String url = ip + "/DesactivarCuenta.php?id_usuario=" + idusuario;

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.trim().equalsIgnoreCase("actualiza")) {


                } else {
                    Toast.makeText(getApplicationContext(), "La cuenta no pudo desactivarse. Verifique el estado de Conexión", Toast.LENGTH_SHORT).show();
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
                String estado = "INACTIVO";

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", codigo);
                parametros.put("estado_usuario", estado);


                return parametros;
            }
        };
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == request_code) {
            Perfil.setImageURI(data.getData());

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                Perfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"SI", "NO"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(PerfilActivity.this);//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("SI")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", PerfilActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(PerfilActivity.this, "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIS_PERMISOS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {//el dos representa los 2 permisos
                Toast.makeText(getApplicationContext(), "Permisos aceptados", Toast.LENGTH_SHORT);
                foto.setEnabled(true);
            }
        } else {
            solicitarPermisosManual();
        }
    }


    public void buscarCiudadano() {
        String ip = getString(R.string.ip);


        String url = ip + "/obtenerCiudadano.php?id_usuario=" + usuario.getText().toString();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "COMPRUEBE LA CONEXION A INTERNET" + error.toString(), Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        id.setText(String.valueOf(preferences.getInt("id_usuario", 1)));
        Nombre.setText(preferences.getString("nombre_usuario", "Sin nombre "));

    }

    @Override
    public void onResponse(JSONObject response) {

        Ciudadano cCiudadano = new Ciudadano();

        JSONArray json = response.optJSONArray("ciudadanos");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            cCiudadano.setIdCiudadano(jsonObject.optInt("id_ciudadano"));
            cCiudadano.setNombreCiudadano(jsonObject.optString("nombre_ciudadano"));
            cCiudadano.setTelefonoCiudadano(jsonObject.optString("telefono_ciudadano"));
            cCiudadano.setDireccionCiudadano(jsonObject.optString("direccion_ciudadano"));
            cCiudadano.setFoto(jsonObject.optString("foto_usuario"));

            if (!cCiudadano.getFoto().equals("sin imagen")) {
                String ip = getString(R.string.ip);

                String url_image = ip + "/" + cCiudadano.getFoto();
                url_image = url_image.replace(" ", "%20");
                try {
                    Log.i("RESPUESTA IMAGEN: ", "" + url_image);
                    Glide.with(this).load(url_image).into(Perfil);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        id.setText("" + cCiudadano.getIdCiudadano());
        Nombre.setText("" + cCiudadano.getNombreCiudadano());
        telefono.setText("" + cCiudadano.getTelefonoCiudadano());
        direccion.setText("" + cCiudadano.getDireccionCiudadano());
        url_foto.setText("" + cCiudadano.getFoto());


    }

    private void webServiceActualizar() {
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Actualizando...");

        String ip = getString(R.string.ip);

        String url = ip + "/UpdatePerfilCiudadano.php?id_ciudadano=" + id.getText().toString();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pDialog.hide();

                if (response.trim().equalsIgnoreCase("actualiza")) {


                } else {
                    Toast.makeText(getApplicationContext(), "No se ha podido actualizar los datos del perfil ", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "COMPRUEBE LA CONEXION A INTERNET", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String idCiudadano = id.getText().toString();
                String nombre = Nombre.getText().toString();
                String Direccion = direccion.getText().toString();
                String Telefono = telefono.getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_ciudadano", idCiudadano);
                parametros.put("nombre_ciudadano", nombre);
                parametros.put("direccion_ciudadano", Direccion);
                parametros.put("telefono_ciudadano", Telefono);

                SharedPreferences preference = getSharedPreferences("preferenciasCiudadano", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("id_ciudadano", idCiudadano);
                editor.putString("nombre_ciudadano", nombre);
                editor.putString("direccion_ciudadano", Direccion);
                editor.putString("telefono_ciudadano", Direccion);
                editor.commit();
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void actualizarPerfil(View V) {
        if (!validar()) return;
        webServiceActualizar();
        webServiceActualizarUsuario();
    }

    private boolean validar() {
        boolean valid = true;

        String cNombre = Nombre.getText().toString();
        String cTelefono = telefono.getText().toString();
        String cDireccion = direccion.getText().toString();

        if (cNombre.isEmpty() || cNombre.length() < 6) {
            Nombre.setError("Ingrese al menos 10 caracteres");
            valid = false;
        } else {
            Nombre.setError(null);
        }


        if (cTelefono.isEmpty() || cTelefono.length() > 11 || cTelefono.length() < 10) {
            telefono.setError("Ingrese 10 caracteres alfanuméricos");
            valid = false;
        } else {
            telefono.setError(null);
        }
        if (cDireccion.isEmpty() || cDireccion.length() < 6) {
            direccion.setError("Ingrese al menos 6");
            valid = false;
        } else {
            direccion.setError(null);
        }
        return valid;
    }

    private void webServiceActualizarUsuario() {

        String ip = getString(R.string.ip);

        String url = ip + "/UpdatePerfilUsuarioC.php?id_usuario=" + ((TextView) findViewById(R.id.txtid)).getText().toString();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pDialog.hide();

                if (response.trim().equalsIgnoreCase("actualiza")) {
                    guardarpreferecias();

                    Toast.makeText(getApplicationContext(), "Se ha Actualizado con exito", Toast.LENGTH_SHORT).show();
                    Intent i;
                    i = new Intent(PerfilActivity.this, NavigationDrawer.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(PerfilActivity.this, "Lo sentimos... No se ha podido actualizar los datos", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String codigo = ((TextView) findViewById(R.id.txtid)).getText().toString();
                String unombres = Nombre.getText().toString();
                String imagen = convertirImgString(bitmap);

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", codigo);
                parametros.put("nombre_usuario", unombres);
                parametros.put("foto_usuario", imagen);
                SharedPreferences preference = getSharedPreferences("preferenciasImageUsuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("id_usuario", codigo);
                editor.putString("foto_usuario", imagen);
                editor.commit();

                return parametros;
            }
        };
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);


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

    private void guardarpreferecias() {
        SharedPreferences preferences = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        String email = preferences.getString("email_usuario", "sin email");

        SharedPreferences preference = getSharedPreferences("preferenciasMain", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("nombre_usuario", Nombre.getText().toString());
        editor.putString("email_usuario", email);
        editor.putBoolean("sesion", true);
        editor.commit();

    }


}