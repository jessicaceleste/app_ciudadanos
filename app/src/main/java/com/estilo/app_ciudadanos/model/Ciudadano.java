package com.estilo.app_ciudadanos.model;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: Ciudadano***/
public class Ciudadano {
    private int IdCiudadano;
    private String nombreCiudadano;
    private String direccionCiudadano;
    private String telefonoCiudadano;
    private int idUsuario;
    private String foto;

    public Ciudadano() {
    }

    public Ciudadano(int idCiudadano, String nombreCiudadano, String direccionCiudadano, String telefonoCiudadano, int idUsuario, String foto) {
        IdCiudadano = idCiudadano;
        this.nombreCiudadano = nombreCiudadano;
        this.direccionCiudadano = direccionCiudadano;
        this.telefonoCiudadano = telefonoCiudadano;
        this.idUsuario = idUsuario;
        this.foto = foto;

    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getIdCiudadano() {
        return IdCiudadano;
    }

    public void setIdCiudadano(int idCiudadano) {
        IdCiudadano = idCiudadano;
    }

    public String getNombreCiudadano() {
        return nombreCiudadano;
    }

    public void setNombreCiudadano(String nombreCiudadano) {
        this.nombreCiudadano = nombreCiudadano;
    }

    public String getDireccionCiudadano() {
        return direccionCiudadano;
    }

    public void setDireccionCiudadano(String direccionCiudadano) {
        this.direccionCiudadano = direccionCiudadano;
    }

    public String getTelefonoCiudadano() {
        return telefonoCiudadano;
    }

    public void setTelefonoCiudadano(String telefonoCiudadano) {
        this.telefonoCiudadano = telefonoCiudadano;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
