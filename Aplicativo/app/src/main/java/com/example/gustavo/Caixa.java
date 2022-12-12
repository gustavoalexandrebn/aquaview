package com.example.gustavo;

public class Caixa {
    private String cpf;
    private float capacidade;
    private float volumeatual;
    private String datacoleta;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public float getVolumeatual() {
        return volumeatual;
    }

    public void setVolumeatual(float volumeatual) {
        this.volumeatual = volumeatual;
    }

    public String getDatacoleta() {
        return datacoleta;
    }

    public void setDatacoleta(String datacoleta) {
        this.datacoleta = datacoleta;
    }

    public float getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(float capacidade) {
        this.capacidade = capacidade;
    }
}
