package com.techne.ChronoFlow.application.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessFileJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ProcessFileJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");
        log.info("Executando Job com ID: {}", jobId);
    }
}
