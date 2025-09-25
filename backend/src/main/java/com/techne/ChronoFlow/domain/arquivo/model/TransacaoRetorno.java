package com.techne.ChronoFlow.domain.arquivo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TransacaoRetorno {
    @Setter
    private String idTransacao;
    @Setter
    private double valor;
    private String tipo;


    public void setTipo(String tipo) {
        this.tipo = tipo.toUpperCase();
    }
}
