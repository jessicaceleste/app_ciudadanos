package com.estilo.app_ciudadanos.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estilo.app_ciudadanos.R;
import com.estilo.app_ciudadanos.model.Solicitud;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
/*Auxilio K66, Version 1
* Applicación Móvil para Denuncias de Incidentes
* Desarrollado por: @jessicaCoronel y @miliceSanabria
* Fecha de Inicio: Mayo 2021
* Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: Solicitudes Adaptador
 * FUNCIÓN: - Está clase creará la lista de solucitudes o denuncias que realizó el usuario.
 *          - Crea un array con los datos del Modelo Solicitud donde se llamará al layaout del reportes_list para mostrar los datos ***/
public class SolicitudAdaptador extends RecyclerView.Adapter<SolicitudAdaptador.SolicitudHolder> {

    List<Solicitud> listaSolicitud;

    public SolicitudAdaptador(List<Solicitud> listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
    }


    @Override
    public SolicitudAdaptador.SolicitudHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportes_list, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new SolicitudAdaptador.SolicitudHolder(vista);
    }

    @Override
    public void onBindViewHolder(SolicitudAdaptador.SolicitudHolder holder, int position) {
        holder.Incidente.setText(listaSolicitud.get(position).getIncidente().toString());
        holder.fecha.setText(String.valueOf(listaSolicitud.get(position).getFecha()));
        holder.hora.setText(String.valueOf(listaSolicitud.get(position).getHora()));
        holder.estado.setText(String.valueOf(listaSolicitud.get(position).getEstado()));


    }

    @Override
    public int getItemCount() {
        return listaSolicitud.size();
    }

    public class SolicitudHolder extends RecyclerView.ViewHolder {

        TextView  Incidente, fecha, hora, estado;

        public SolicitudHolder(View itemView) {
            super(itemView);
            Incidente = itemView.findViewById(R.id.tipoIncidente);
            fecha = itemView.findViewById(R.id.fechaReporte);
            hora = itemView.findViewById(R.id.horaReporte);
            estado = itemView.findViewById(R.id.estadoReporte);


        }
    }

}