package com.example.mascotasproject;

public class model {
    String caracteristicas, cod_dueno, cod_mascota, imagen, nombre_mascota, ubicacion_perdida,vigencia;

    public model(String caracteristicas, String cod_dueno, String cod_mascota, String imagen, String nombre_mascota, String ubicacion_perdida, String vigencia) {
        this.caracteristicas = caracteristicas;
        this.cod_dueno=cod_dueno;
        this.cod_mascota=cod_mascota;
        this.imagen=imagen;
        this.nombre_mascota=nombre_mascota;
        this.ubicacion_perdida=ubicacion_perdida;
        this.vigencia=vigencia;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getCod_dueno() {
        return cod_dueno;
    }

    public void setCod_dueno(String cod_dueno) {
        this.cod_dueno = cod_dueno;
    }

    public String getCod_mascota() {
        return cod_mascota;
    }

    public void setCod_mascota(String cod_mascota) {
        this.cod_mascota = cod_mascota;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre_mascota() {
        return nombre_mascota;
    }

    public void setNombre_mascota(String nombre_mascota) {
        this.nombre_mascota = nombre_mascota;
    }

    public String getUbicacion_perdida() {
        return ubicacion_perdida;
    }

    public void setUbicacion_perdida(String ubicacion_perdida) {
        this.ubicacion_perdida = ubicacion_perdida;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }
}
