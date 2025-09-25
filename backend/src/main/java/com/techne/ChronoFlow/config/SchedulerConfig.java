package com.techne.ChronoFlow.config;

import com.techne.ChronoFlow.application.sse.SseService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final SseService sseService;

    public SchedulerConfig(SseService sseService) {
        this.sseService = sseService;
    }

    @Scheduled(fixedRate = 5000)
    public void sendSseEvents() {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("timestamp", LocalTime.now().toString());
        eventData.put("message", "This is a test event.");
        sseService.sendJobUpdate(eventData);
    }
}
