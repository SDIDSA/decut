package org.luke.gui.file;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

    public static void zipFolder(File sourceFolder, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zos = new ZipOutputStream(bos)) {
            for (File child : sourceFolder.listFiles()) {
                zipRecursive(child, sourceFolder.getAbsolutePath(), zos);
            }
        }
    }

    private static void zipRecursive(File fileToZip, String basePath, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        String substring = fileToZip.getAbsolutePath().substring(basePath.length() + 1);
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children == null || children.length == 0) {
                String entryName = substring + "/";
                zos.putNextEntry(new ZipEntry(entryName));
                zos.closeEntry();
            } else {
                for (File childFile : children) {
                    zipRecursive(childFile, basePath, zos);
                }
            }
        } else {
            String entryName = substring.replace(File.separatorChar, '/');
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(entryName);
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
            }
        }
    }

}