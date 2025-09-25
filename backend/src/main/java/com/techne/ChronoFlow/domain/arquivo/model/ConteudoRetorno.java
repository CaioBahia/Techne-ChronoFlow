package com.techne.ChronoFlow.domain.arquivo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConteudoRetorno {

    private String nomeEmpresa;
    private String lote;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataGeracao;


    private List<TransacaoRetorno> transacoes;


    private String erro;


    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public void setTransacoes(List<TransacaoRetorno> transacoes) {
        this.transacoes = transacoes;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
