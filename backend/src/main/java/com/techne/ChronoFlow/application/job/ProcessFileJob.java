package com.techne.ChronoFlow.application.job;

import com.techne.ChronoFlow.application.arquivo.FileProcessingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessFileJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ProcessFileJob.class);

    private final FileProcessingService fileProcessingService;

    public ProcessFileJob(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");
        log.info("Disparando execução do Job com ID: {}", jobId);

        try {
            fileProcessingService.collectAndRegisterFiles(jobId);
        } catch (Exception e) {
            log.error("Falha na execução do Job ID: {}. Erro: {}", jobId, e.getMessage());
            throw new JobExecutionException(e);
        }
    }
}
