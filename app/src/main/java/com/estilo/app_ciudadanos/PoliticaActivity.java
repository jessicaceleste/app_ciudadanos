package com.estilo.app_ciudadanos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/
public class PoliticaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}