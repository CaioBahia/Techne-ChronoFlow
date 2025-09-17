package com.techne.ChronoFlow.domain.job;

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

    @Column(nullable = false)
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
