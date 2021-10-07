package com.estilo.app_ciudadanos.model;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: Solicitud***/
public class Solicitud {
    private int idSolicitud;
    private int idIncidente;
    private String Incidente;
    private String fecha;
    private String hora;
    private String estado;

    public Solicitud() {
    }

    public Solicitud(int idSolicitud, int idIncidente, String incidente, String fecha, String hora, String estado) {
        this.idSolicitud = idSolicitud;
        this.idIncidente = idIncidente;
        Incidente = incidente;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public String getIncidente() {
        return Incidente;
    }

    public void setIncidente(String incidente) {
        Incidente = incidente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
