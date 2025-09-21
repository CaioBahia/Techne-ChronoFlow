package com.techne.ChronoFlow.domain.arquivo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConteudoRetorno {
    // --- Dados do Cabeçalho ---
    private String nomeEmpresa;
    private String lote;
    private LocalDate dataGeracao;

    // --- Dados das Transações ---
    private List<TransacaoRetorno> transacoes;

    // --- Campo de Erro ---
    private String erro;

    // Getters e Setters

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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
