package org.luke.gui.file;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static void unzipWithProgress(File zipFilePath, File destDir,
                                         Consumer<Double> onProgress) throws IOException {
        if (!destDir.exists()) {
            Files.createDirectories(destDir.toPath());
        }

        int totalEntries = 0;
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        while (zipIn.getNextEntry() != null) {
            totalEntries++;
            zipIn.closeEntry();
        }

        int processedEntries = 0;
        long lastUpdate = System.currentTimeMillis();

        onProgress.accept(0.0);

        zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();

            if (!entryName.isEmpty()) {
                String filePath = destDir.getAbsolutePath() + File.separator + entryName;
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    Files.createDirectories(new File(filePath).toPath());
                }
            }

            zipIn.closeEntry();
            processedEntries++;

            long now = System.currentTimeMillis();
            if (now - lastUpdate > 200 || processedEntries == totalEntries) {
                onProgress.accept((double) processedEntries / totalEntries);
                lastUpdate = now;
            }

            entry = zipIn.getNextEntry();
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File newFile = new File(filePath);
        File parent = newFile.getParentFile();
        if (parent != null && !parent.exists()) {
            Files.createDirectories(parent.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = zipIn.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
}