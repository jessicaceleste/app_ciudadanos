package com.estilo.app_ciudadanos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.estilo.app_ciudadanos.adaptadores.SolicitudAdaptador;
import com.estilo.app_ciudadanos.model.Solicitud;
import com.estilo.app_ciudadanos.model.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class ListaReportesActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    RecyclerView recyclerReportes;
    ArrayList<Solicitud> listaSolicitud;


    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reportes);

        listaSolicitud = new ArrayList<>();

        recyclerReportes = (RecyclerView) findViewById(R.id.idRecyclerReporte);
        recyclerReportes.setLayoutManager(new LinearLayoutManager(ListaReportesActivity.this));
        recyclerReportes.setHasFixedSize(true);
        cargarWebService();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void cargarWebService() {

        SharedPreferences preferences = getSharedPreferences("preferenciasIdUsuario", Context.MODE_PRIVATE);
        String idusuario = preferences.getString("id_usuario", "1");
        String ip = getString(R.string.ip);

        String url = ip + "/ConsultarListaReporte.php?id_usuario=" +idusuario;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getIntanciaVolley(ListaReportesActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(ListaReportesActivity.this, "No tiene reportes", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Solicitud solicitud = null;

        JSONArray json = response.optJSONArray("solicitudes");
        try {
            if (json != null) {
                int len = json.length();
                for (int i = 0; i < len; i++) {
                    solicitud = new Solicitud();
                    JSONObject jsonObject = null;
                    jsonObject = json.getJSONObject(i);
                    solicitud.setIncidente(jsonObject.optString("tipo_incidente"));
                    solicitud.setFecha(jsonObject.optString("fecha_solicitud"));
                    solicitud.setHora(jsonObject.optString("hora_solicitud"));
                    solicitud.setEstado(jsonObject.optString("estado_solicitud"));


                    listaSolicitud.add(solicitud);
                }
                SolicitudAdaptador adapter = new SolicitudAdaptador(listaSolicitud);
                recyclerReportes.setAdapter(adapter);
            } else {
                Toast.makeText(ListaReportesActivity.this, "No hay datos que mostrar" +
                        " " + response, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ListaReportesActivity.this, "No se ha podido establecer conexión con el servidor", Toast.LENGTH_LONG).show();
        }

    }
}