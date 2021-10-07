package com.estilo.app_ciudadanos.model;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: Incidente***/
public class Incidente {

    private int idIncidente;
    private String tipIncidente;


    public Incidente() {


    }

    public Incidente(int idIncidente, String tipIncidente) {
        this.idIncidente = idIncidente;
        this.tipIncidente = tipIncidente;
    }

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public String getTipIncidente() {
        return tipIncidente;
    }

    public void setTipIncidente(String tipIncidente) {
        this.tipIncidente = tipIncidente;
    }


    @Override
    public String toString() {
        return tipIncidente;
    }

    private int toInt() {
        return idIncidente;
    }
}
