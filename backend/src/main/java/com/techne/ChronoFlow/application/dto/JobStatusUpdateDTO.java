package com.techne.ChronoFlow.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobStatusUpdateDTO {
    private Long id;
    private JobStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime proximaExecucao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ultimaExecucao;

    public JobStatusUpdateDTO(Job job) {
        this.id = job.getId();
        this.status = job.getStatus();
        this.proximaExecucao = job.getProximaExecucao();
        this.ultimaExecucao = job.getUltimaExecucao();
    }
}
