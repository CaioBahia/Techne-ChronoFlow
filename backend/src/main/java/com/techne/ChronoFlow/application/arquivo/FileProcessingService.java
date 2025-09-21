package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.arquivo.ArquivoRetornoRepository;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.JobRepository;
import com.techne.ChronoFlow.domain.job.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileProcessingService {
    package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.arquivo.ArquivoRetornoRepository;
import com.techne.ChronoFlow.domain.job.Job;
import com.techne.ChronoFlow.domain.job.JobRepository;
import com.techne.ChronoFlow.domain.job.JobStatus;
import com.techne.ChronoFlow.domain.transacao.Transacao;
import com.techne.ChronoFlow.domain.transacao.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

    @Service
    public class FileProcessingService {

        private static final Logger log = LoggerFactory.getLogger(com.techne.ChronoFlow.application.arquivo.FileProcessingService.class);

        private final JobRepository jobRepository;
        private final ArquivoRetornoRepository arquivoRetornoRepository;
        private final TransacaoRepository transacaoRepository;
        private final FileParser fileParser;

        private final Path sourceDirectory;
        private final Path pendingDirectory;
        private final Path processedDirectory;
        private final Path errorDirectory;

        public FileProcessingService(JobRepository jobRepository,
                                     ArquivoRetornoRepository arquivoRetornoRepository,
                                     TransacaoRepository transacaoRepository,
                                     FileParser fileParser,
                                     @Value("${file.path.source:C:/ChronoFlow_Files/in}") String sourcePath,
                                     @Value("${file.path.pending:C:/ChronoFlow_Files/out/pendentes}") String pendingPath,
                                     @Value("${file.path.processed:C:/ChronoFlow_Files/out/processados}") String processedPath,
                                     @Value("${file.path.error:C:/ChronoFlow_Files/out/erros}") String errorPath) throws IOException {
            this.jobRepository = jobRepository;
            this.arquivoRetornoRepository = arquivoRetornoRepository;
            this.transacaoRepository = transacaoRepository;
            this.fileParser = fileParser;

            this.sourceDirectory = Paths.get(sourcePath);
            this.pendingDirectory = Paths.get(pendingPath);
            this.processedDirectory = Paths.get(processedPath);
            this.errorDirectory = Paths.get(errorPath);

            Files.createDirectories(this.sourceDirectory);
            Files.createDirectories(this.pendingDirectory);
            Files.createDirectories(this.processedDirectory);
            Files.createDirectories(this.errorDirectory);
        }

        @Transactional
        public void collectAndRegisterFiles(Long jobId) {
            Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
            if (job.getStatus() == JobStatus.PROCESSANDO) {
                log.warn("Job {} já está em PROCESSANDO. Pulando coleta.", jobId);
                return;
            }

            log.info("Iniciando coleta de arquivos para o Job ID: {}", jobId);
            job.setStatus(JobStatus.PROCESSANDO);
            job.setUltimaExecucao(LocalDateTime.now());
            jobRepository.save(job);

            try (Stream<Path> paths = Files.walk(sourceDirectory, 1)) {
                List<Path> filesToProcess = paths.filter(Files::isRegularFile).toList();
                if (filesToProcess.isEmpty()) {
                    log.warn("Nenhum arquivo encontrado na pasta de entrada. Job concluído.");
                    job.setStatus(JobStatus.CONCLUIDO);
                    jobRepository.save(job);
                    return;
                }

                filesToProcess.forEach(sourceFile -> {
                    try {
                        String newFileName = "job-" + jobId + "_" + UUID.randomUUID() + "_" + sourceFile.getFileName().toString();
                        Path pendingFile = pendingDirectory.resolve(newFileName);
                        Files.move(sourceFile, pendingFile, StandardCopyOption.REPLACE_EXISTING);

                        ArquivoRetorno arquivoRetorno = new ArquivoRetorno();
                        arquivoRetorno.setJob(job);
                        arquivoRetorno.setNomeArquivo(newFileName);
                        arquivoRetorno.setDataProcessamento(LocalDateTime.now());
                        arquivoRetorno.setStatus("PENDENTE");
                        arquivoRetornoRepository.save(arquivoRetorno);
                        log.info("Arquivo {} movido para pendentes e registrado com status PENDENTE.", sourceFile.getFileName());
                    } catch (IOException e) {
                        log.error("Erro ao mover ou registrar o arquivo {}: {}", sourceFile.getFileName(), e.getMessage());
                    }
                });
            } catch (IOException e) {
                log.error("Erro crítico ao ler o diretório de origem {}: {}", sourceDirectory, e.getMessage());
                job.setStatus(JobStatus.FALHA);
                jobRepository.save(job);
            }
        }

        @Scheduled(fixedRate = 60000)
        @Transactional
        public void processPendingFiles() {
            List<ArquivoRetorno> pendingFiles = arquivoRetornoRepository.findByStatus("PENDENTE");
            if (pendingFiles.isEmpty()) {
                return;
            }
            log.info("Encontrados {} arquivos pendentes para processar.", pendingFiles.size());
            for (ArquivoRetorno arquivo : pendingFiles) {
                processSingleFile(arquivo.getId());
            }
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void processSingleFile(Long arquivoRetornoId) {
            ArquivoRetorno arquivo = arquivoRetornoRepository.findById(arquivoRetornoId)
                    .orElseThrow(() -> new IllegalStateException("ArquivoRetorno não encontrado: " + arquivoRetornoId));

            Path sourcePath = pendingDirectory.resolve(arquivo.getNomeArquivo());
            if (!Files.exists(sourcePath)) {
                arquivo.setStatus("ERRO");
                arquivo.setConteudo("Falha: Arquivo físico não foi encontrado em 'pendentes'.");
                arquivoRetornoRepository.save(arquivo);
                log.error("Arquivo {} não encontrado na pasta de pendentes. Marcado como ERRO.", arquivo.getNomeArquivo());
                return;
            }

            String finalStatus;
            try {
                readFileAndPopulateContent(arquivo, sourcePath);
                finalStatus = "PROCESSADO";
                log.info("Leitura do arquivo {} concluída com sucesso.", arquivo.getNomeArquivo());
            } catch (IOException | ParsingException e) {
                finalStatus = "ERROR";
                log.error("Falha ao ler e processar o arquivo {}. Causa: {}", arquivo.getNomeArquivo(), e.getMessage());
            }

            arquivo.setStatus(finalStatus);
            arquivo.setDataProcessamento(LocalDateTime.now());
            arquivoRetornoRepository.save(arquivo);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        Path destinationDir = "PROCESSADO".equals(arquivo.getStatus()) ? processedDirectory : errorDirectory;
                        Files.move(sourcePath, destinationDir.resolve(arquivo.getNomeArquivo()), StandardCopyOption.REPLACE_EXISTING);
                        log.info("Ação pós-commit: Arquivo {} movido para a pasta '{}'.", arquivo.getNomeArquivo(), destinationDir.getFileName());
                    } catch (IOException e) {
                        log.error("CRÍTICO: O status do arquivo {} foi salvo como '{}', mas falhou ao movê-lo. Intervenção manual necessária.", arquivo.getNomeArquivo(), arquivo.getStatus(), e);
                    }
                }
            });
        }

        private void readFileAndPopulateContent(ArquivoRetorno arquivo, Path sourcePath) throws IOException, ParsingException {
            StringBuilder fileContentBuilder = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(sourcePath)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    fileContentBuilder.append(line).append(System.lineSeparator());
                    if (line.trim().isEmpty()) continue;

                    try {
                        if (lineNumber == 1) {
                            fileParser.parseHeader(line, arquivo);
                        } else {
                            Transacao transacao = fileParser.parseLine(line);
                            transacao.setArquivoRetorno(arquivo);
                            arquivo.getTransacoes().add(transacao);
                        }
                    } catch (ParsingException e) {
                        throw new ParsingException("Erro na linha " + lineNumber + ": " + e.getMessage(), e);
                    }
                }
                arquivo.setConteudo(fileContentBuilder.toString());
            } catch (IOException e) {
                throw new IOException("Erro de I/O ao ler o arquivo " + sourcePath.getFileName() + ": " + e.getMessage(), e);
            }
        }

        @Scheduled(fixedRate = 90000)
        @Transactional
        public void updateJobStatus() {
            List<Job> processingJobs = jobRepository.findByStatus(JobStatus.PROCESSANDO);
            for (Job job : processingJobs) {
                if (job.getArquivos().stream().anyMatch(a -> "PENDENTE".equals(a.getStatus()))) {
                    continue; // Job ainda tem arquivos sendo processados
                }

                long errorFiles = job.getArquivos().stream().filter(a -> "ERRO".equals(a.getStatus())).count();
                if (errorFiles > 0) {
                    job.setStatus(JobStatus.FALHA);
                    log.warn("Job {} finalizado como FALHA pois {} arquivo(s) tiveram erro.", job.getId(), errorFiles);
                } else {
                    job.setStatus(JobStatus.CONCLUIDO);
                    log.info("Job {} finalizado como CONCLUÍDO.", job.getId());
                }
                jobRepository.save(job);
            }
        }
    }

}
