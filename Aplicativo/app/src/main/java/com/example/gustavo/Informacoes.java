package com.example.gustavo;

import android.media.Image;

public class Informacoes {

    private float consumo;
    private float consumomedio;
    private String ulrfig;
    private float entrada;
    private String data;

    public float getConsumo() {
        return consumo;
    }

    public void setConsumo(float consumo) {
        this.consumo = consumo;
    }

    public float getEntrada() {
        return entrada;
    }

    public void setEntrada(float entrada) {
        this.entrada = entrada;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getConsumomedio() {
        return consumomedio;
    }

    public void setConsumomedio(float consumomedio) {
        this.consumomedio = consumomedio;
    }

    public String getUlrfig() {
        return ulrfig;
    }

    public void setUlrfig(String ulrfig) {
        this.ulrfig = ulrfig;
    }
}
