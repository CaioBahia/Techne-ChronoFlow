package com.techne.ChronoFlow.application.dto;

import com.techne.ChronoFlow.domain.job.Job;
import java.time.LocalDateTime;

public class JobStatusUpdateDTO {
    private Long id;
    private String status;
    private LocalDateTime proximaExecucao;
    private LocalDateTime ultimaExecucao;

    // Construtor para facilitar a conversão da entidade para DTO
    public JobStatusUpdateDTO(Job job) {
        this.id = job.getId();
        this.status = job.getStatus();
        this.proximaExecucao = job.getProximaExecucao();
        this.ultimaExecucao = job.getUltimaExecucao();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getProximaExecucao() { return proximaExecucao; }
    public void setProximaExecucao(LocalDateTime proximaExecucao) { this.proximaExecucao = proximaExecucao; }
    public LocalDateTime getUltimaExecucao() { return ultimaExecucao; }
    public void setUltimaExecucao(LocalDateTime ultimaExecucao) { this.ultimaExecucao = ultimaExecucao; }
}