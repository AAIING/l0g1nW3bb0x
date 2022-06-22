package com.opencode.webboxdespacho.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viajesd {

    @SerializedName("Nroviaje")
    public int Nroviaje ;

    @SerializedName("Pedido")
    public int Pedido ;

    @SerializedName("Cajas")
    public int Cajas ;

    @SerializedName("Bolsas")
    public int Bolsas ;

    @SerializedName("Cajascargadas")
    public int Cajascargadas ;

    @SerializedName("Bolsascargadas")
    public int Bolsascargadas ;

    @SerializedName("Cajasentregadas")
    public int Cajasentregadas ;

    @SerializedName("Bolsasentregadas")
    public int Bolsasentregadas ;

    public int Iddespachod;

    @SerializedName("Pedidos")
    @Expose
    private Pedidos pedidos;

    public Pedidos getPedidos() {
        return pedidos;
    }

    public void setPedidos(Pedidos pedidos) {
        this.pedidos = pedidos;
    }


    public int getIddespachod() {
        return Iddespachod;
    }

    public void setIddespachod(int iddespachod) {
        Iddespachod = iddespachod;
    }

    public int getNroviaje() {
        return Nroviaje;
    }

    public void setNroviaje(int nroviaje) {
        Nroviaje = nroviaje;
    }

    public int getPedido() {
        return Pedido;
    }

    public void setPedido(int pedido) {
        Pedido = pedido;
    }

    public int getCajas() {
        return Cajas;
    }

    public void setCajas(int cajas) {
        Cajas = cajas;
    }

    public int getBolsas() {
        return Bolsas;
    }

    public void setBolsas(int bolsas) {
        Bolsas = bolsas;
    }

    public int getCajascargadas() {
        return Cajascargadas;
    }

    public void setCajascargadas(int cajascargadas) {
        Cajascargadas = cajascargadas;
    }

    public int getBolsascargadas() {
        return Bolsascargadas;
    }

    public void setBolsascargadas(int bolsascargadas) {
        Bolsascargadas = bolsascargadas;
    }

    public int getCajasentregadas() {
        return Cajasentregadas;
    }

    public void setCajasentregadas(int cajasentregadas) {
        Cajasentregadas = cajasentregadas;
    }

    public int getBolsasentregadas() {
        return Bolsasentregadas;
    }

    public void setBolsasentregadas(int bolsasentregadas) {
        Bolsasentregadas = bolsasentregadas;
    }
}
