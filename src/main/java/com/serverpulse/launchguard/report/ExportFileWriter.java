package com.serverpulse.launchguard.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

public class ExportFileWriter {

    private final Path exportsDir;
    private final Logger logger;

    public ExportFileWriter(Path exportsDir, Logger logger) {
        this.exportsDir = exportsDir;
        this.logger = logger;
    }

    public Path save(String content, String source, String extension) {
        try {
            Files.createDirectories(exportsDir);
        } catch (IOException e) {
            logger.warning("Failed to create exports directory: " + e.getMessage());
            return null;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
        String filename = timestamp + "_" + source + "." + extension;
        Path filePath = exportsDir.resolve(filename);

        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warning("Failed to write export file: " + e.getMessage());
            return null;
        }

        return filePath;
    }

    public void prune(int maxToKeep) {
        if (maxToKeep < 1) return;

        File dir = exportsDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] exportFiles = dir.listFiles((d, name) -> name.endsWith(".json") || name.endsWith(".html"));
        if (exportFiles == null || exportFiles.length <= maxToKeep) return;

        Arrays.sort(exportFiles, Comparator.comparingLong(File::lastModified));

        int toDelete = exportFiles.length - maxToKeep;
        for (int i = 0; i < toDelete; i++) {
            if (!exportFiles[i].delete()) {
                logger.warning("Failed to delete old export file: " + exportFiles[i].getName());
            }
        }
    }
}
