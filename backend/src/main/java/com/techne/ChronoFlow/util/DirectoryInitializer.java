package com.techne.ChronoFlow.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class DirectoryInitializer {

    @Value("${file.path.source}")
    private String sourcePath;

    @Value("${file.path.pending}")
    private String pendingPath;

    @Value("${file.path.processed}")
    private String processedPath;

    @Value("${file.path.error}")
    private String errorPath;

    @PostConstruct
    public void initializeDirectories() {
        createDirectory(sourcePath);
        createDirectory(pendingPath);
        createDirectory(processedPath);
        createDirectory(errorPath);
    }

    private void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
