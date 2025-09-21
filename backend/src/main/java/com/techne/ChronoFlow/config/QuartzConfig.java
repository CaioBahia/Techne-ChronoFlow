package com.techne.ChronoFlow.config;

import com.techne.ChronoFlow.application.job.listener.JobCompletionListener;
import jakarta.annotation.PostConstruct;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobCompletionListener jobCompletionListener;

    @PostConstruct
    public void addJobCompletionListener() {
        try {
            scheduler.getListenerManager().addTriggerListener(jobCompletionListener);
            log.info("JobCompletionListener registrado com sucesso no Quartz Scheduler.");
        } catch (SchedulerException e) {
            log.error("Erro ao registrar o JobCompletionListener no Quartz Scheduler.", e);
        }
    }
}
