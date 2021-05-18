package com.example.mascotasproject;

public class locationMascota {
    String direccion, latitude,longitud,nombreMas,codigoDueno,codigoMascota,fecha;

    public locationMascota(){}


    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public locationMascota(String direccion, String latitude, String longitud, String nombreMas, String codigoDueno, String codigoMascota, String fecha) {
        this.fecha=fecha;
        this.direccion=direccion;
        this.latitude=latitude;
        this.longitud=longitud;
        this.nombreMas=nombreMas;
        this.codigoDueno=codigoDueno;
        this.codigoMascota=codigoMascota;
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
