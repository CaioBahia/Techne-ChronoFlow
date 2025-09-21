package com.techne.ChronoFlow.domain.arquivo.model;

import java.time.LocalDate;
import java.util.List;

public class ConteudoRetorno {
    // --- Dados do Cabeçalho ---
    private String nomeBanco;
    private String codigoConvenio;
    private LocalDate dataGeracao;

    // --- Dados das Transações ---
    private List<TransacaoRetorno> transacoes;

    // --- Campo de Erro ---
    private String erro;

    // Getters e Setters

    public String getNomeBanco() {
        return nomeBanco;
    }

    public void setNomeBanco(String nomeBanco) {
        this.nomeBanco = nomeBanco;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public List<TransacaoRetorno> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoRetorno> transacoes) {
        this.transacoes = transacoes;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
