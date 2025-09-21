package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class FileParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");
    private static final Logger log = LoggerFactory.getLogger(FileParser.class);

    public void parseHeader(String line, ArquivoRetorno arquivo) throws ParsingException {
        if (line == null) {
            throw new ParsingException("Linha de cabeçalho é nula.", null);
        }
        String trimmedLine = line.trim();
        if (trimmedLine.length() < 33) {
            throw new ParsingException(String.format("Linha de cabeçalho é muito curta. Esperado: 33, Encontrado: %d", trimmedLine.length()), null);
        }
        try {
            String dataString = trimmedLine.substring(0, 8);
            String horarioString = trimmedLine.substring(8, 14);
            String nomeEmpresa = trimmedLine.substring(14, 23);
            String numeroLote = trimmedLine.substring(23, 33);

            LocalDate dataArquivo = LocalDate.parse(dataString, DATE_FORMATTER);
            LocalTime horarioArquivo = LocalTime.parse(horarioString, TIME_FORMATTER);

            // Campos removidos de ArquivoRetorno para simplificação
            // arquivo.setDataArquivo(dataArquivo);
            // arquivo.setHorarioArquivo(horarioArquivo);
            // arquivo.setNomeEmpresa(nomeEmpresa.trim());
            // arquivo.setNumeroLote(numeroLote.trim());

        } catch (Exception e) {
            throw new ParsingException("Falha ao extrair dados do cabeçalho: " + e.getMessage(), e);
        }
    }
}
