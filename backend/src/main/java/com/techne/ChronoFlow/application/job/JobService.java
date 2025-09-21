package com.techne.ChronoFlow.application.job;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.arquivo.ArquivoRetornoRepository;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.JobRepository;
import com.techne.ChronoFlow.domain.job.enums.JobStatus;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final ArquivoRetornoRepository arquivoRetornoRepository;
    private final Scheduler scheduler;

    public JobService(JobRepository jobRepository, ArquivoRetornoRepository arquivoRetornoRepository, Scheduler scheduler) {
        this.jobRepository = jobRepository;
        this.arquivoRetornoRepository = arquivoRetornoRepository;
        this.scheduler = scheduler;
    }

    @PostConstruct
    @Transactional
    public void initializeAndScheduleJobs() {
        // Reagenda todos os jobs que estão no banco de dados.
        log.info("Iniciando o agendamento de todos os jobs existentes...");
        jobRepository.findAll().forEach(this::scheduleJob);
        log.info("Agendamento de jobs concluído.");
    }

    @Transactional
    public Job createJob(Job job) {
        job.setStatus(JobStatus.AGENDADO);
        Job savedJob = jobRepository.save(job);
        scheduleJob(savedJob);
        return savedJob;
    }

    @Transactional(readOnly = true)
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    @Transactional
    public Job updateJob(Long id, Job jobDetails) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        job.setNome(jobDetails.getNome());
        job.setCronExpression(jobDetails.getCronExpression());

        Job updatedJob = jobRepository.save(job);
        rescheduleJob(updatedJob);
        return updatedJob;
    }

    @Transactional
    public void deleteJob(Long id) {
        jobRepository.findById(id).ifPresent(job -> {
            try {
                scheduler.deleteJob(new JobKey(String.valueOf(job.getId())));
            } catch (SchedulerException e) {
                log.error("Erro ao remover job {} do agendador.", job.getId(), e);
                throw new RuntimeException("Erro ao remover job do agendador.", e);
            }
        });
        jobRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ArquivoRetorno> getArquivosByJobId(Long id) {
        return arquivoRetornoRepository.findByJobId(id);
    }

    private void scheduleJob(Job job) {
        try {
            JobDetail jobDetail = buildJobDetail(job);
            Trigger trigger = buildTrigger(job, jobDetail);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Erro ao agendar o job {}: {}", job.getId(), e.getMessage());
            throw new RuntimeException("Erro ao agendar o job.", e);
        }
    }

    private void rescheduleJob(Job job) {
        try {
            TriggerKey triggerKey = new TriggerKey(String.valueOf(job.getId()));
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);

            if (oldTrigger != null) {
                JobKey jobKey = oldTrigger.getJobKey();
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                if (jobDetail != null) {
                    Trigger newTrigger = buildTrigger(job, jobDetail);
                    scheduler.rescheduleJob(triggerKey, newTrigger);
                } else {
                    log.error("JobDetail não encontrado para a chave {} ao tentar reagendar.", jobKey);
                }
            } else {
                scheduleJob(job);
            }
        } catch (SchedulerException e) {
            log.error("Erro ao reagendar o job {}: {}", job.getId(), e.getMessage());
            throw new RuntimeException("Erro ao reagendar o job.", e);
        }
    }

    private JobDetail buildJobDetail(Job job) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobId", job.getId());

        return JobBuilder.newJob(ProcessFileJob.class)
                .withIdentity(String.valueOf(job.getId()))
                .withDescription(job.getNome())
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(Job job, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(String.valueOf(job.getId()))
                .withDescription("Trigger para " + job.getNome())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                .build();
    }
}
