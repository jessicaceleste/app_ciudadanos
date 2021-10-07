package com.estilo.app_ciudadanos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class ContenedorInstruccionesActivity extends AppCompatActivity {


    TextView politica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_introduccion);
        politica = (TextView) findViewById(R.id.PoliticadePrivacidad);

        politica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPolitica();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void mostrarPolitica() {
        Intent i = new Intent(this, PoliticaActivity.class);
        startActivity(i);
        finish();
    }
}