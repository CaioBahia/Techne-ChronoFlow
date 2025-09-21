package com.techne.ChronoFlow.application.arquivo;

import com.techne.ChronoFlow.domain.arquivo.ArquivoRetorno;
import com.techne.ChronoFlow.domain.arquivo.ArquivoRetornoRepository;
import com.techne.ChronoFlow.domain.arquivo.model.ConteudoRetorno;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileProcessingService {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingService.class);

    private final JobRepository jobRepository;
    private final ArquivoRetornoRepository arquivoRetornoRepository;
    private final FileParser fileParser;

    private final Path sourceDirectory;
    private final Path pendingDirectory;
    private final Path processedDirectory;
    private final Path errorDirectory;

    public FileProcessingService(JobRepository jobRepository,
                               ArquivoRetornoRepository arquivoRetornoRepository,
                               FileParser fileParser,
                               @Value("${file.path.source:C:/ChronoFlow_Files/in}") String sourcePath,
                               @Value("${file.path.pending:C:/ChronoFlow_Files/out/pendentes}") String pendingPath,
                               @Value("${file.path.processed:C:/ChronoFlow_Files/out/processados}") String processedPath,
                               @Value("${file.path.error:C:/ChronoFlow_Files/out/erros}") String errorPath) throws IOException {
        this.jobRepository = jobRepository;
        this.arquivoRetornoRepository = arquivoRetornoRepository;
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
                    String formattedDateTime = LocalDateTime.now().format(formatter);
                    String originalFileName = sourceFile.getFileName().toString();
                    String tempFileName = formattedDateTime + "_job_" + jobId + "_" + originalFileName;

                    Path pendingFile = pendingDirectory.resolve(tempFileName);
                    Files.move(sourceFile, pendingFile, StandardCopyOption.REPLACE_EXISTING);

                    ArquivoRetorno arquivoRetorno = new ArquivoRetorno();
                    arquivoRetorno.setJob(job);
                    arquivoRetorno.setNomeArquivo(tempFileName);
                    arquivoRetorno.setDataProcessamento(LocalDateTime.now());
                    arquivoRetorno.setStatus("PENDENTE");
                    arquivoRetornoRepository.save(arquivoRetorno);
                    log.info("Arquivo {} movido para pendentes como {} e registrado com status PENDENTE.", originalFileName, tempFileName);
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

        String tempFileName = arquivo.getNomeArquivo();
        Path sourcePath = pendingDirectory.resolve(tempFileName);
        if (!Files.exists(sourcePath)) {
            arquivo.setStatus("ERRO");
            ConteudoRetorno conteudo = new ConteudoRetorno();
            conteudo.setErro("Falha: Arquivo físico não foi encontrado em 'pendentes'.");
            arquivo.setConteudo(conteudo);
            arquivoRetornoRepository.save(arquivo);
            log.error("Arquivo {} não encontrado na pasta de pendentes. Marcado como ERRO.", tempFileName);
            return;
        }

        String finalStatus;
        String finalFileName = tempFileName;
        try {
            readFileAndPopulateContent(arquivo, sourcePath);
            finalStatus = "PROCESSADO";

            ConteudoRetorno conteudo = arquivo.getConteudo();
            if (conteudo != null && conteudo.getNomeEmpresa() != null && !conteudo.getNomeEmpresa().isEmpty()) {
                String nomeEmpresa = conteudo.getNomeEmpresa().replaceAll("\s+", "_");
                finalFileName = nomeEmpresa + "_" + tempFileName;
                arquivo.setNomeArquivo(finalFileName);
            }
            log.info("Leitura do arquivo {} concluída com sucesso.", tempFileName);
        } catch (IOException | ParsingException e) {
            finalStatus = "ERRO";
            ConteudoRetorno conteudo = new ConteudoRetorno();
            conteudo.setErro("Falha ao ler e processar o arquivo: " + e.getMessage());
            arquivo.setConteudo(conteudo);
            log.error("Falha ao ler e processar o arquivo {}. Causa: {}", tempFileName, e.getMessage());
        }

        arquivo.setStatus(finalStatus);
        arquivo.setDataProcessamento(LocalDateTime.now());
        arquivoRetornoRepository.save(arquivo);

        final String finalNameToMove = finalFileName;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    Path destinationDir = "PROCESSADO".equals(arquivo.getStatus()) ? processedDirectory : errorDirectory;
                    Files.move(sourcePath, destinationDir.resolve(finalNameToMove), StandardCopyOption.REPLACE_EXISTING);
                    log.info("Ação pós-commit: Arquivo {} movido para a pasta '{}' como {}.", tempFileName, destinationDir.getFileName(), finalNameToMove);
                } catch (IOException e) {
                    log.error("CRÍTICO: O status do arquivo {} foi salvo como '{}', mas falhou ao movê-lo para {}. Intervenção manual necessária.", tempFileName, arquivo.getStatus(), finalNameToMove, e);
                }
            }
        });
    }

    private void readFileAndPopulateContent(ArquivoRetorno arquivo, Path sourcePath) throws IOException, ParsingException {
        try (BufferedReader reader = Files.newBufferedReader(sourcePath)) {
            ConteudoRetorno conteudo = fileParser.parse(reader);
            arquivo.setConteudo(conteudo);
        } catch (IOException e) {
            throw new IOException("Erro de I/O ao ler o arquivo " + sourcePath.getFileName() + ": " + e.getMessage(), e);
        } catch (ParsingException e) {
            // Log the parsing error with more details if possible
            log.error("Erro de parsing no arquivo {}: {}", sourcePath.getFileName(), e.getMessage());
            throw e; // Re-throw to be handled by processSingleFile
        }
    }

    @Scheduled(fixedRate = 90000)
    @Transactional
    public void updateJobStatus() {
        List<Job> processingJobs = jobRepository.findByStatus(JobStatus.PROCESSANDO);
        for (Job job : processingJobs) {
            List<ArquivoRetorno> arquivos = arquivoRetornoRepository.findByJobId(job.getId());

            if (arquivos.stream().anyMatch(a -> "PENDENTE".equals(a.getStatus()))) {
                continue; // Job ainda tem arquivos sendo processados
            }

            long errorFiles = arquivos.stream().filter(a -> "ERRO".equals(a.getStatus())).count();
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
