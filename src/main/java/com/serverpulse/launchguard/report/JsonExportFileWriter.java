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

public class JsonExportFileWriter {

    private final Path exportsDir;
    private final Logger logger;

    public JsonExportFileWriter(Path exportsDir, Logger logger) {
        this.exportsDir = exportsDir;
        this.logger = logger;
    }

    public Path save(String jsonContent, String source) {
        try {
            Files.createDirectories(exportsDir);
        } catch (IOException e) {
            logger.warning("Failed to create exports directory: " + e.getMessage());
            return null;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
        String filename = timestamp + "_" + source + ".json";
        Path filePath = exportsDir.resolve(filename);

        try {
            Files.writeString(filePath, jsonContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warning("Failed to write JSON export file: " + e.getMessage());
            return null;
        }

        return filePath;
    }

    public void prune(int maxToKeep) {
        if (maxToKeep < 1) return;

        File dir = exportsDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length <= maxToKeep) return;

        Arrays.sort(jsonFiles, Comparator.comparingLong(File::lastModified));

        int toDelete = jsonFiles.length - maxToKeep;
        for (int i = 0; i < toDelete; i++) {
            if (!jsonFiles[i].delete()) {
                logger.warning("Failed to delete old JSON export file: " + jsonFiles[i].getName());
            }
        }
    }
}
