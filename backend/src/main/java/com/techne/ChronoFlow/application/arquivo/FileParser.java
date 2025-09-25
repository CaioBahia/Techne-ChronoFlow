package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
import com.techne.ChronoFlow.domain.arquivo.model.TransacaoRetorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final Logger log = LoggerFactory.getLogger(FileParser.class);

    public String getEmpresaFromHeader(Path filePath) throws IOException, ParsingException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new ParsingException("Arquivo está vazio ou não contém um cabeçalho válido.", null);
            }
            if (headerLine.length() < 23) {
                throw new ParsingException("Linha de cabeçalho muito curta para extrair o nome da empresa.", null);
            }
            return headerLine.substring(14, 23).trim().replace(' ', '_');
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
            String nomeEmpresa = line.substring(14, 23).trim().replace(' ', '_');
            conteudo.setNomeEmpresa(nomeEmpresa);
            conteudo.setLote(line.substring(23, 33).trim());
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
                transacao.setTipo("pagamento");
            } else if ("R".equals(tipoStr)) {
                transacao.setTipo("recebimento");
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
