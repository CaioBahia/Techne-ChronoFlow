package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FileParser {

    package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.transacao.Transacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

    @Component
    public class FileParser {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");
        private static final Logger log = LoggerFactory.getLogger(com.techne.ChronoFlow.application.arquivo.FileParser.class);

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

                arquivo.setDataArquivo(dataArquivo);
                arquivo.setHorarioArquivo(horarioArquivo);
                arquivo.setNomeEmpresa(nomeEmpresa.trim());
                arquivo.setNumeroLote(numeroLote.trim());

            } catch (Exception e) {
                throw new ParsingException("Falha ao extrair dados do cabeçalho: " + e.getMessage(), e);
            }
        }

        public Transacao parseLine(String line) throws ParsingException {
            if (line == null) {
                throw new ParsingException("Linha de transação é nula.", null);
            }
            String trimmedLine = line.trim();
            if (trimmedLine.length() < 53) {
                String errorMsg = String.format("Linha de transação é muito curta. Esperado: 53, Encontrado: %d. Conteúdo: '%s'", trimmedLine.length(), trimmedLine);
                log.error(errorMsg);
                throw new ParsingException(errorMsg, null);
            }

            try {
                String tipoTransacao = trimmedLine.substring(0, 1);
                String valorString = trimmedLine.substring(1, 12);
                String dataString = trimmedLine.substring(12, 20);
                String descricao = trimmedLine.substring(20, 50);
                String ocorrenciaString = trimmedLine.substring(50, 53);

                BigDecimal valor = new BigDecimal(valorString).divide(new BigDecimal("100"));
                LocalDate dataPagamento = LocalDate.parse(dataString, DATE_FORMATTER);
                Integer codigoOcorrencia = Integer.parseInt(ocorrenciaString);

                Transacao transacao = new Transacao();
                transacao.setTipoTransacao(tipoTransacao);
                transacao.setValor(valor);
                transacao.setDataPagamento(dataPagamento);
                transacao.setDescricao(descricao.trim());
                transacao.setCodigoOcorrencia(codigoOcorrencia);

                return transacao;

            } catch (Exception e) {
                throw new ParsingException("Falha ao extrair dados da linha de transação: " + e.getMessage(), e);
            }
        }
    }

}
