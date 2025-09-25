package com.techne.ChronoFlow.application.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private static final Logger logger = LoggerFactory.getLogger(SseService.class);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        this.emitters.add(emitter);
        logger.info("Novo emitter adicionado. Total de emitters: {}", emitters.size());
    }

    public void removeEmitter(SseEmitter emitter) {
        this.emitters.remove(emitter);
        logger.info("Emitter removido. Total de emitters: {}", emitters.size());
    }

    public void sendJobUpdate(Object jobData) {
        logger.info("Enviando atualização de job para {} emitters.", emitters.size());

        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("job-update")
                        .data(jobData));

            } catch (IOException e) {
                logger.error("Erro ao enviar evento para emitter. Removendo-o da lista.", e);
                removeEmitter(emitter);
            }
        }
    }
}
