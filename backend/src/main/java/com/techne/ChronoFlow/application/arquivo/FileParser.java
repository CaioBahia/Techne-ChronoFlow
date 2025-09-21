package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
import com.techne.ChronoFlow.domain.arquivo.model.TransacaoRetorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final Logger log = LoggerFactory.getLogger(FileParser.class);

    /**
     * This method is kept for now to avoid breaking potential other usages,
     * but the main parsing logic is now in the {@link #parse(BufferedReader)} method.
     * @deprecated Use {@link #parse(BufferedReader)} instead.
     */
    @Deprecated
    public void parseHeader(String line, ArquivoRetorno arquivo) throws ParsingException {
        if (line == null) {
            throw new ParsingException("Linha de cabeçalho é nula.", null);
        }
        String trimmedLine = line.trim();
        if (trimmedLine.length() < 33) {
            throw new ParsingException(String.format("Linha de cabeçalho é muito curta. Esperado: 33, Encontrado: %d", trimmedLine.length()), null);
        }
        try {
            LocalDate.parse(trimmedLine.substring(0, 8), DATE_FORMATTER);
            LocalTime.parse(trimmedLine.substring(8, 14), DateTimeFormatter.ofPattern("HHmmss"));
        } catch (Exception e) {
            throw new ParsingException("Falha ao extrair dados do cabeçalho: " + e.getMessage(), e);
        }
    }

    public ConteudoRetorno parse(BufferedReader reader) throws IOException, ParsingException {
        ConteudoRetorno conteudo = new ConteudoRetorno();
        List<TransacaoRetorno> transacoes = new ArrayList<>();

        String headerLine = reader.readLine();
        if (headerLine == null || headerLine.trim().isEmpty()) {
            throw new ParsingException("Arquivo está vazio ou não contém um cabeçalho válido.", null);
        }
        parseHeaderLine(headerLine, conteudo);

        String detailLine;
        while ((detailLine = reader.readLine()) != null) {
            if (!detailLine.trim().isEmpty()) {
                transacoes.add(parseDetailLine(detailLine));
            }
        }
        conteudo.setTransacoes(transacoes);

        return conteudo;
    }

    private void parseHeaderLine(String line, ConteudoRetorno conteudo) throws ParsingException {
        if (line.length() < 33) {
            throw new ParsingException(String.format("Linha de cabeçalho é muito curta. Esperado: 33, Encontrado: %d", line.length()), null);
        }
        try {
            conteudo.setDataGeracao(LocalDate.parse(line.substring(0, 8), DATE_FORMATTER));
            conteudo.setNomeBanco(line.substring(14, 23).trim());
            conteudo.setCodigoConvenio(line.substring(23, 33).trim());
        } catch (Exception e) {
            throw new ParsingException("Falha ao extrair dados do cabeçalho: " + e.getMessage(), e);
        }
    }

    private TransacaoRetorno parseDetailLine(String line) throws ParsingException {
        if (line.length() < 53) {
            throw new ParsingException(String.format("Linha de detalhe é muito curta. Esperado no mínimo: 53, Encontrado: %d", line.length()), null);
        }
        try {
            TransacaoRetorno transacao = new TransacaoRetorno();

            String tipoStr = line.substring(0, 1);
            if ("P".equals(tipoStr)) {
                transacao.setTipo("DEBITO");
            } else if ("R".equals(tipoStr)) {
                transacao.setTipo("CREDITO");
            } else {
                transacao.setTipo("DESCONHECIDO");
            }

            String valorStr = line.substring(1, 12);
            BigDecimal valor = new BigDecimal(valorStr).divide(new BigDecimal("100"));
            transacao.setValor(valor.doubleValue());

            String descricao = line.substring(20, 50).trim();
            transacao.setIdTransacao(descricao);

            return transacao;
        } catch (Exception e) {
            throw new ParsingException("Falha ao extrair dados da linha de detalhe: '" + line + "'. Erro: " + e.getMessage(), e);
        }
    }
}