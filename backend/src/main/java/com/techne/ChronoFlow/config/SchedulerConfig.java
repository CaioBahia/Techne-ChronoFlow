package com.techne.ChronoFlow.config;

import com.techne.ChronoFlow.application.dto.JobStatusUpdateDTO;
import com.techne.ChronoFlow.application.sse.SseService;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.JobRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final SseService sseService;
    private final JobRepository jobRepository;

    public SchedulerConfig(SseService sseService, JobRepository jobRepository) {
        this.sseService = sseService;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional(readOnly = true)
    public void broadcastAllJobStatuses() {
        List<Job> allJobs = jobRepository.findAll();

        for (Job job : allJobs) {
            JobStatusUpdateDTO updateDTO = new JobStatusUpdateDTO(job);
            sseService.sendJobUpdate(updateDTO);
        }
    }
}
