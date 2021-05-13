package com.example.mascotasproject;

public class modeltemporal {

    String codigo, url, vigencia;

    public modeltemporal(){}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public modeltemporal(String codigo, String url, String vigencia) {
        this.codigo = codigo;
        this.url=url;
        this.vigencia=vigencia;

    }

}
