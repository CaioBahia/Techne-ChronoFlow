package com.techne.ChronoFlow.domain.job;

import com.techne.ChronoFlow.domain.job.enums.Empresa;
import com.techne.ChronoFlow.domain.job.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "JOB")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Empresa empresa;

    private LocalDateTime ultimaExecucao;

    private LocalDateTime proximaExecucao;
}
