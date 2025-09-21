package com.techne.ChronoFlow.domain.job;

import com.techne.ChronoFlow.domain.job.enums.Empresas;
import com.techne.ChronoFlow.domain.job.enums.JobStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JOB")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cronExpression;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Empresas empresas;

    private LocalDateTime ultimaExecucao;

    private LocalDateTime proximaExecucao;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Empresas getEmpresa() {
        return empresas;
    }

    public void setEmpresa(Empresas empresas) {
        this.empresas = empresas;
    }

    public LocalDateTime getUltimaExecucao() {
        return ultimaExecucao;
    }

    public void setUltimaExecucao(LocalDateTime ultimaExecucao) {
        this.ultimaExecucao = ultimaExecucao;
    }

    public LocalDateTime getProximaExecucao() {
        return proximaExecucao;
    }

    public void setProximaExecucao(LocalDateTime proximaExecucao) {
        this.proximaExecucao = proximaExecucao;
    }
}
