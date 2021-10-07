package com.estilo.app_ciudadanos.model;

import android.os.Parcel;
import android.os.Parcelable;

/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: Usuario***/
public class Usuario implements Parcelable {
    private int id;
    private String email;
    private String pass;
    private String nombre;
    private String foto;


    public Usuario() {
    }

    public Usuario(int id, String email,  String nombre, String foto) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.foto = foto;

    }

    public Usuario(int id, String correo,String pass, String nombre, String foto) {
        this.id = id;
        this.email = correo;
        this.pass = pass;
        this.nombre = nombre;
        this.foto = foto;


    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String setId(int id) {
        this.id = id;
        return null;
    }

    public String setEmail(String email) {
        this.email = email;
        return email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getNombre() {
        return nombre;
    }

    public String setNombre(String nombre) {
        this.nombre = nombre;
        return nombre;
    }

    protected Usuario(Parcel in) {
        id = in.readInt();
        email = in.readString();
        pass = in.readString();
        nombre = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(pass);
        dest.writeString(nombre);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };
}