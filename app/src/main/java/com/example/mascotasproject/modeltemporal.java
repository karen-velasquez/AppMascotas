package com.example.mascotasproject;

public class modeltemporal {

    String codigo, url;

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

    public modeltemporal(String codigo, String url) {
        this.codigo = codigo;
        this.url=url;

    }

}
