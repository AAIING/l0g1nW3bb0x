package com.opencode.webboxdespacho.models;

public class Fotos {

    public int idFoto;
    public int idPedido;
    public String rutaUrl;

    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getRutaUrl() {
        return rutaUrl;
    }

    public void setRutaUrl(String rutaUrl) {
        this.rutaUrl = rutaUrl;
    }
}
