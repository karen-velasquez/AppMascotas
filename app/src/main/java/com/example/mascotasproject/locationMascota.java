package com.example.mascotasproject;

public class locationMascota {
    String caracteristica, latitude,longitud,nombreMas,codigoDueno;

    public locationMascota(){}


    public String getCodigoDueno() {
        return codigoDueno;
    }

    public void setCodigoDueno(String codigoDueno) {
        this.codigoDueno = codigoDueno;
    }

    public locationMascota(String caracteristica, String latitude, String longitud, String nombreMas, String codigoDueno) {
        this.caracteristica=caracteristica;
        this.latitude=latitude;
        this.longitud=longitud;
        this.nombreMas=nombreMas;
        this.codigoDueno=codigoDueno;
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
