package com.techne.ChronoFlow.application.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/sse") // Mapeamento base para o controller
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/jobs") // O endpoint completo será /api/sse/jobs
    public SseEmitter subscribeToJobUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> sseService.removeEmitter(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            sseService.removeEmitter(emitter);
        });
        emitter.onError(e -> {
            emitter.complete();
            sseService.removeEmitter(emitter);
        });
        sseService.addEmitter(emitter);

        try {
            emitter.send(SseEmitter.event().name("connection-established").data("Conexão SSE estabelecida com sucesso!"));
        } catch (IOException e) {
            sseService.removeEmitter(emitter);
        }

        return emitter;
    }
}
