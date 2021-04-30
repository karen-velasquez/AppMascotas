package com.example.mascotasproject;

public class model {
    String Caracteristicas, CodigoDueno, CodigoMascota, Imagen, NombreMas, UbicacionPerdida,Vigencia;

    public model(){}

    public model( String NombreMas, String UbicacionPerdida,  String Imagen, String Caracteristicas, String CodigoDueno, String CodigoMascota,  String Vigencia) {
        this.Caracteristicas = Caracteristicas;
        this.CodigoDueno=CodigoDueno;
        this.CodigoMascota=CodigoMascota;
        this.Imagen=Imagen;
        this.NombreMas=NombreMas;
        this.UbicacionPerdida=UbicacionPerdida;
        this.Vigencia=Vigencia;
    }

    public String getCaracteristicas() {
        return Caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        Caracteristicas = caracteristicas;
    }

    public String getCodigoDueno() {
        return CodigoDueno;
    }

    public void setCodigoDueno(String codigoDueno) {
        CodigoDueno = codigoDueno;
    }

    public String getCodigoMascota() {
        return CodigoMascota;
    }

    public void setCodigoMascota(String codigoMascota) {
        CodigoMascota = codigoMascota;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public String getNombreMas() {
        return NombreMas;
    }

    public void setNombreMas(String nombreMas) {
        NombreMas = nombreMas;
    }

    public String getUbicacionPerdida() {
        return UbicacionPerdida;
    }

    public void setUbicacionPerdida(String ubicacionPerdida) {
        UbicacionPerdida = ubicacionPerdida;
    }

    public String getVigencia() {
        return Vigencia;
    }

    public void setVigencia(String vigencia) {
        Vigencia = vigencia;
    }



}
