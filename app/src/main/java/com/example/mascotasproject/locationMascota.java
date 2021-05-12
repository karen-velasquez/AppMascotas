package com.example.mascotasproject;

public class locationMascota {
    String caracteristica, latitude,longitud,nombreMas,codigo;

    public locationMascota(){}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public locationMascota(String caracteristica, String latitude, String longitud, String nombreMas, String codigo) {
        this.caracteristica=caracteristica;
        this.latitude=latitude;
        this.longitud=longitud;
        this.nombreMas=nombreMas;
        this.codigo=codigo;
    }

    public String getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(String caracteristica) {
        this.caracteristica = caracteristica;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getNombreMas() {
        return nombreMas;
    }

    public void setNombreMas(String nombreMas) {
        this.nombreMas = nombreMas;
    }


}
