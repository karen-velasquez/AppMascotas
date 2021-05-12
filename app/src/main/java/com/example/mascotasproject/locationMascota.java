package com.example.mascotasproject;

public class locationMascota {
    String caracteristica, latitude,longitud,nombreMas,codigoDueno,codigoMascota;

    public locationMascota(){}


    public locationMascota(String caracteristica, String latitude, String longitud, String nombreMas, String codigoDueno, String codigoMascota) {
        this.caracteristica=caracteristica;
        this.latitude=latitude;
        this.longitud=longitud;
        this.nombreMas=nombreMas;
        this.codigoDueno=codigoDueno;
        this.codigoMascota=codigoMascota;
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

    public String getCodigoDueno() {
        return codigoDueno;
    }

    public void setCodigoDueno(String codigoDueno) {
        this.codigoDueno = codigoDueno;
    }

    public String getCodigoMascota() {
        return codigoMascota;
    }

    public void setCodigoMascota(String codigoMascota) {
        this.codigoMascota = codigoMascota;
    }

}
