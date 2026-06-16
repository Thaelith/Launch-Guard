package com.serverpulse.launchguard.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ReportFileWriter {

    private final Path reportsDir;
    private final Logger logger;

    public ReportFileWriter(Path reportsDir, Logger logger) {
        this.reportsDir = reportsDir;
        this.logger = logger;
    }

    public Path save(String reportContent, String source) {
        try {
            Files.createDirectories(reportsDir);
        } catch (IOException e) {
            logger.warning("Failed to create reports directory: " + e.getMessage());
            return null;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = timestamp + "_" + source + ".txt";
        Path filePath = reportsDir.resolve(filename);

        try {
            Files.writeString(filePath, reportContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warning("Failed to write report file: " + e.getMessage());
            return null;
        }

        return filePath;
    }

    public void prune(int maxToKeep) {
        if (maxToKeep < 1) return;

        File dir = reportsDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] txtFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (txtFiles == null || txtFiles.length <= maxToKeep) return;

        Arrays.sort(txtFiles, Comparator.comparingLong(File::lastModified));

        int toDelete = txtFiles.length - maxToKeep;
        for (int i = 0; i < toDelete; i++) {
            if (!txtFiles[i].delete()) {
                logger.warning("Failed to delete old report file: " + txtFiles[i].getName());
            }
        }
    }

    public List<ReportFileInfo> listRecent(int limit) {
        List<ReportFileInfo> result = new ArrayList<>();

        File dir = reportsDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return result;

        File[] txtFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) return result;

        Arrays.sort(txtFiles, Comparator.comparingLong(File::lastModified).reversed());

        int count = Math.min(limit, txtFiles.length);
        for (int i = 0; i < count; i++) {
            File file = txtFiles[i];
            result.add(new ReportFileInfo(
                    file.getName(),
                    file.lastModified(),
                    file.length()
            ));
        }

        return result;
    }

    public Path getLatestReport() {
        File dir = reportsDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return null;

        File[] txtFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) return null;

        File latest = null;
        long latestTime = 0;
        for (File file : txtFiles) {
            if (file.lastModified() > latestTime) {
                latestTime = file.lastModified();
                latest = file;
            }
        }

        return latest != null ? latest.toPath() : null;
    }

    public record ReportFileInfo(String name, long lastModified, long size) {
    }
}
