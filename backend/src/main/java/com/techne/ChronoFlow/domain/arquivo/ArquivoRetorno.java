package com.techne.ChronoFlow.domain.arquivo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techne.ChronoFlow.domain.arquivo.converter.ConteudoRetornoConverter;
import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
import com.techne.ChronoFlow.domain.job.Job;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String nomeArquivo;

    @Lob
    @Convert(converter = ConteudoRetornoConverter.class)
    @Column(columnDefinition = "TEXT")
    private ConteudoRetorno conteudo;

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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public ConteudoRetorno getConteudo() {
        return conteudo;
    }

    public void setConteudo(ConteudoRetorno conteudo) {
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
