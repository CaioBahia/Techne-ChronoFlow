package com.techne.ChronoFlow.domain.arquivo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techne.ChronoFlow.domain.arquivo.converter.ConteudoRetornoConverter;
import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
import com.techne.ChronoFlow.domain.job.Job;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "ARQUIVO_RETORNO")
public class ArquivoRetorno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobId", nullable = false)
    @JsonIgnore
    private Job job;

    @Column(nullable = false)
    private String nomeArquivo;

    @Lob
    @Convert(converter = ConteudoRetornoConverter.class)
    @Column(columnDefinition = "TEXT")
    private ConteudoRetorno conteudo;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataProcessamento;

    @Column(nullable = false)
    private String status;


    public void setId(Long id) {
        this.id = id;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setConteudo(ConteudoRetorno conteudo) {
        this.conteudo = conteudo;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
