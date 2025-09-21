package com.techne.ChronoFlow.application.job.listener;

import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.JobRepository;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;

@Component
public class JobCompletionListener implements TriggerListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    @Autowired
    private JobRepository jobRepository;

    @Override
    public String getName() {
        return "JobCompletionListener";
    }

    @Override
    @Transactional
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstruction) {
        // Recupera o jobId como 'long' para evitar ClassCastException.
        // O valor é provavelmente armazenado como um número, não uma String.
        long jobId = context.getJobDetail().getJobDataMap().getLongValue("jobId");

        try {
            Job job = jobRepository.findById(jobId).orElse(null);

            if (job != null) {
                Date nextFireTime = trigger.getFireTimeAfter(context.getFireTime());
                if (nextFireTime != null) {
                    job.setProximaExecucao(nextFireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    log.info("Atualizando próxima execução do Job ID {} para: {}", jobId, job.getProximaExecucao());
                } else {
                    // Se não há próxima execução (ex: cron que só roda uma vez), pode-se definir como nulo ou manter.
                    job.setProximaExecucao(null);
                    log.info("Não há próxima execução agendada para o Job ID {}.", jobId);
                }
                jobRepository.save(job);
            } else {
                log.warn("Job com ID {} não foi encontrado no repositório.", jobId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar a conclusão do trigger para o Job ID {}.", jobId, e);
        }
    }

    // Métodos não utilizados da interface, podem ficar vazios
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {}

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {}
}
