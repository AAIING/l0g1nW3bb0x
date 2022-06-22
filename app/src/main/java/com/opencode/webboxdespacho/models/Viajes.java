package com.opencode.webboxdespacho.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Viajes {

    @SerializedName("Fecha")
    public String Fecha ;

    @SerializedName("Patente")
    public String Patente ;

    @SerializedName("Chofer")
    public String Chofer ;

    @SerializedName("Nroviaje")
    public int Nroviaje ;

    @SerializedName("Viajesd")
    @Expose
    public List<Viajesd> Viajesd ;

    public List<Viajesd> getViajesd() {
        return Viajesd;
    }

    public void setViajesd(List<Viajesd> viajesd) {
        Viajesd = viajesd;
    }

    public int idViaje ;

    public int getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    /// <summary>
    /// 0:cargando  0:en viaje  5: cerrado
    /// </summary>
    @SerializedName("Estado")
    public short Estado ;

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getPatente() {
        return Patente;
    }

    public void setPatente(String patente) {
        Patente = patente;
    }

    public String getChofer() {
        return Chofer;
    }

    public void setChofer(String chofer) {
        Chofer = chofer;
    }

    public int getNroviaje() {
        return Nroviaje;
    }

    public void setNroviaje(int nroviaje) {
        Nroviaje = nroviaje;
    }

    public short getEstado() {
        return Estado;
    }

    public void setEstado(short estado) {
        Estado = estado;
    }


}
