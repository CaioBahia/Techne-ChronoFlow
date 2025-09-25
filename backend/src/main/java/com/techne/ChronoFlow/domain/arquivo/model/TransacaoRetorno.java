package com.techne.ChronoFlow.domain.arquivo.model;

public class TransacaoRetorno {
    private String idTransacao;
    private double valor;
    private String tipo; // Ex: "pagamento", "recebimento"
    // Outros campos relevantes da transação...

    // Getters e Setters

    public String getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(String idTransacao) {
        this.idTransacao = idTransacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo.toUpperCase();
    }
}
