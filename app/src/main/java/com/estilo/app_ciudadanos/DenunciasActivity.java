package com.estilo.app_ciudadanos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estilo.app_ciudadanos.model.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class DenunciasActivity extends AppCompatActivity {
    private static final String CARPETA_PRINCIPAL = "ImagenesDenuncias/";//directorio principal
    private static final String CARPETA_IMAGEN = "AuxlioK66";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int MIS_PERMISOS = 0x100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    ProgressBar progressBar;

    private final String CARPETA_RAIZ = "ImagenesDenuncias/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "AuxilioK66";
    private static final int WRITE_PERMISSION = 0x01;
    ImageView foto;
    Button fotoDenuncia;


    ConstraintLayout constraintLayout;
    Spinner spinnerIncidentes;


    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS, idCiudadano;

    AsyncHttpClient client;
    Button botonGuardar, btnUbicacion;
    ProgressDialog progreso;
    private ProgressDialog pDialog;
    List<String> listaIdIncidentes; //guarda el id del incidente
    List<String> listaIncidentes; //guarda el tipo de incidente
    FloatingActionButton denunciar;

    private static final String TAG = DenunciasActivity.class.getSimpleName();

    TextView txtId;

    ConstraintLayout layoutRegistrar;//permisos

    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;//SE MODIFICA
    private String[] projection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncias);
        spinnerIncidentes = (Spinner) findViewById(R.id.spinnerTipoIncidente);
        constraintLayout = findViewById(R.id.ConstraintIncidente);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progresoDenuncias);
        progressBar.setVisibility(View.INVISIBLE);
        longitudeValueGPS = (TextView) findViewById(R.id.valoresdelongitudGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.valoresdelatitudGPS);
        btnUbicacion = (Button) findViewById(R.id.btnUbicacion);

        client = new AsyncHttpClient();
        botonGuardar = (Button) findViewById(R.id.btnGuardar);
        txtId = (TextView) findViewById(R.id.textId);
        txtId.setVisibility(View.GONE);//ocultar etiqueta de id
        denunciar = findViewById(R.id.idDenunciar);
        idCiudadano = findViewById(R.id.idCiudadano);

        fotoDenuncia = (Button) findViewById(R.id.btnFotoIncidente);
        foto = findViewById(R.id.fotograma);
        //Permisos

        if (validaPermisos()) {
            fotoDenuncia.setEnabled(true);
        } else {
            fotoDenuncia.setEnabled(false);
        }

        cargarIncidentes();
        SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        String idusuario = preferences.getString("id_usuario", "1");
        boolean sesion = preferences.getBoolean("sesion", false);
        if (!sesion) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
        ((TextView) findViewById(R.id.idCiudadano)).setText((idusuario));
        idCiudadano.setVisibility(View.GONE);


        spinnerIncidentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String incidente = spinnerIncidentes.getSelectedItem().toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */

                for (int i = 0; i < listaIdIncidentes.size(); i++) {
                    for (int j = 0; j < listaIncidentes.size(); j++) {
                        if (incidente == listaIncidentes.get(i)) {
                            String idincidente = listaIdIncidentes.get(i);
                            txtId.setText(String.valueOf(idincidente));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
        fotoDenuncia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogOpciones();
            }
        });
        findViewById(R.id.ConstraintIncidente).post(new Runnable() {
            public void run() {
                PopupWindow popupWindow = new PopupWindow(DenunciasActivity.this);
                popupWindow.showAtLocation(findViewById(R.id.ConstraintIncidente), Gravity.CENTER, 0, 0);
            }
        });
        final Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {


            if (Build.VERSION.SDK_INT >= 29) {

                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(0));

                try (ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(imageUri, "r")) {
                    if (pfd != null) {
                        bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                    }
                } catch (IOException ex) {

                }
            } else {
            }
        }
    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
        }

        return false;
    }

    private void mostrarDialogOpciones() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(DenunciasActivity.this);

        //builder.setTitle("Elige una Opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")) {
                    abrirCamara();
                } else {
                    if (opciones[i].equals("Elegir de Galeria")) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                    } else {
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_SELECCIONA) {
            try {
                if (data != null) {
                    Uri miPath = data.getData();
                    if (miPath != null) {
                        foto.setImageURI(miPath);
                        bitmap = MediaStore.Images.Media.getBitmap(DenunciasActivity.this.getContentResolver(), miPath);
                        foto.setImageBitmap(bitmap);
                        bitmap = redimensionarImagen(bitmap, 600, 800);

                    }
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == COD_FOTO) {

            MediaScannerConnection.scanFile(this, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Ruta de almacenamiento", "Path: " + path);
                        }
                    });

            bitmap = BitmapFactory.decodeFile(path);
            foto.setImageBitmap(bitmap);
            bitmap = redimensionarImagen(bitmap, 400, 800);


        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            foto.setImageBitmap(imgBitmap);
        }

    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > anchoNuevo || alto > altoNuevo) {
            float escalaAncho = anchoNuevo / ancho;
            float escalaAlto = altoNuevo / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);

        } else {
            return bitmap;
        }


    }


    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }


    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(DenunciasActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
            }
        });
        dialogo.show();
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }


    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        DenunciasActivity mainActivity;

        public DenunciasActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(DenunciasActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();

            this.mainActivity.setLocation(loc);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //  mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
            //setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void guardar(View v) {
        String dLatitud = latitudeValueGPS.getText().toString();
        String dLongitud = longitudeValueGPS.getText().toString();
        if (dLatitud.equals("Latitud") && dLongitud.equals("Longitud")) {
            Toast.makeText(getApplicationContext(), "Debe compartir su ubicación", Toast.LENGTH_SHORT).show();
        } else {
            cargarWebService();
        }
    }

    private void cargarWebService() {
        pDialog = new ProgressDialog(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());// Obtener la hora actual
                String str = formatter.format(curDate);
                String ip = getString(R.string.ip);
                String url = ip + "/RegistroDenuncias.php?id_incidente=" + txtId.getText().toString() + "&fecha_solicitud=" + date + "&hora_solicitud=" + str + "&latitud_solicitud=" + latitudeGPS + "&longitud_solicitud=" + longitudeGPS + "&id_usuario=" + ((TextView) findViewById(R.id.idCiudadano)).getText().toString();

                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.d(TAG, "Mensaje de Registro: " + response.toString());
                        hideDialog();

                        if (response.trim().equalsIgnoreCase("inserta")) {
                            latitudeValueGPS.setText("Latitud");
                            longitudeValueGPS.setText("Longitud");
                            agradecimiento();
                            progressBar.setVisibility(View.INVISIBLE);

                        } else {
                            latitudeValueGPS.setText("Latitud");
                            longitudeValueGPS.setText("Longitud");
                            agradecimiento();
                            progressBar.setVisibility(View.INVISIBLE);

                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Verifique el estado de conexion de su dispositivo", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        String incidente = spinnerIncidentes.getSelectedItem().toString();
                        String latitud = String.valueOf(latitudeGPS);
                        String longitud = String.valueOf(longitudeGPS);
                        String usuario = ((TextView) findViewById(R.id.idCiudadano)).getText().toString();
                        String imagen = convertirImgString(bitmap);
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        // Obtener la hora actual
                        String str = formatter.format(curDate);

                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("id_incidente", incidente);
                        parametros.put("fecha_solicitud", date);
                        parametros.put("hora_solicitud", str);
                        parametros.put("latitud_solicitud", latitud);
                        parametros.put("longitud_solicitud", longitud);
                        parametros.put("id_usuario", usuario);
                        parametros.put("foto_solicitud", imagen);


                        return parametros;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        }, 2000);

    }


    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void cargarIncidentes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String ip = getString(R.string.ip);
        String url = ip + "/obtenerTipoIncidente.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        obtenerIncidentes(jsonArray);
                    } catch (JSONException jsnex1) {
                        Toast.makeText(getApplicationContext(), jsnex1.toString()
                                , Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Sin Conexión", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    public void obtenerIncidentes(JSONArray jsonArray) {
        listaIdIncidentes = new ArrayList<String>();
        listaIncidentes = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //display
                String idincidente = jsonObject.getString("id_incidente");
                String nombre_incidente = jsonObject.getString("tipo_incidente");
                listaIdIncidentes.add(idincidente);
                listaIncidentes.add(nombre_incidente);


            } catch (JSONException jsnEx2) {

            }
        }
        ArrayAdapter<String> adapterIncidentes = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, listaIncidentes);
        spinnerIncidentes.setAdapter(adapterIncidentes);
    }


    ///////////////
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Ubicación")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void toggleGPSUpdates(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals(getResources().getString(R.string.pause))) {
            locationManager.removeUpdates(locationListenerGPS);
            button.setText(R.string.resume);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
            button.setText(R.string.pause);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public void agradecimiento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¡Gracias por avisarnos del incidente!... En instantes estaremos en ese lugar! ")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Se ha reportado su denuncia", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DenunciasActivity.this, NavigationDrawer.class);
                        startActivity(i);
                    }
                });
        builder.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }

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


}

