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

    /**
     * Endpoint para clientes (front-end) se inscreverem para receber atualizações de status dos jobs.
     * A conexão ficará aberta para que o servidor possa enviar eventos em tempo real.
     *
     * @return SseEmitter que gerencia a conexão.
     */
    @GetMapping("/jobs") // O endpoint completo será /api/sse/jobs
    public SseEmitter subscribeToJobUpdates() {
        // Cria um emitter com um timeout longo. O Spring cuida de manter a conexão aberta.
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Callback para quando a conexão for completada (ex: cliente fechou a página)
        emitter.onCompletion(() -> sseService.removeEmitter(emitter));
        // Callback para quando ocorrer um timeout
        emitter.onTimeout(() -> {
            emitter.complete();
            sseService.removeEmitter(emitter);
        });
        // Callback para quando ocorrer um erro
        emitter.onError(e -> {
            emitter.complete();
            sseService.removeEmitter(emitter);
        });

        // Adiciona o emitter à lista de emitters ativos para que possamos enviar eventos para ele mais tarde.
        sseService.addEmitter(emitter);

        try {
            // Envia um evento inicial para o cliente confirmar que a conexão foi estabelecida com sucesso.
            // O 'eventName' ("connection-established") é importante para o front-end saber que tipo de evento é este.
            emitter.send(SseEmitter.event().name("connection-established").data("Conexão SSE estabelecida com sucesso!"));
        } catch (IOException e) {
            // Se não conseguirmos enviar o evento inicial, removemos o emitter.
            sseService.removeEmitter(emitter);
        }

        return emitter;
    }
}
