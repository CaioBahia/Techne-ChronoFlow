package com.techne.ChronoFlow.domain.arquivo.model;

import lombok.Setter;

public class TransacaoRetorno {
    @Setter
    private String idTransacao;
    @Setter
    private double valor;
    private String tipo; // Ex: "pagamento", "recebimento"
    // Outros campos relevantes da transação...

    // Getters e Setters

    public String getIdTransacao() {
        return idTransacao;
    }

    public double getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo.toUpperCase();
    }
}
