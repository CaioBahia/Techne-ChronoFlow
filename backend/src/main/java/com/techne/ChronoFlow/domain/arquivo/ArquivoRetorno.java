package com.techne.ChronoFlow.domain.arquivo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.transacao.Transacao;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARQUIVO_RETORNO")
public class ArquivoRetorno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId", nullable = false)
    @JsonIgnore // Evita serialização recursiva
    private Job job;

    @OneToMany(mappedBy = "arquivoRetorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transacao> transacoes = new ArrayList<>();

    // --- Campos do Cabeçalho ---
    private LocalDate dataArquivo;
    private LocalTime horarioArquivo;
    @Column(length = 9)
    private String nomeEmpresa;
    @Column(length = 10)
    private String numeroLote;
    // --------------------------

    @Column(nullable = false)
    private String nomeArquivo;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataProcessamento;

    @Column(nullable = false)
    private String status;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }

    public LocalDate getDataArquivo() {
        return dataArquivo;
    }

    public void setDataArquivo(LocalDate dataArquivo) {
        this.dataArquivo = dataArquivo;
    }

    public LocalTime getHorarioArquivo() {
        return horarioArquivo;
    }

    public void setHorarioArquivo(LocalTime horarioArquivo) {
        this.horarioArquivo = horarioArquivo;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
