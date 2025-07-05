package org.luke.decut.local.zip;

import org.luke.gui.file.ZipUtils;

import java.io.File;
import java.io.IOException;

public class ZipTest {
    public static void main(String[] args) throws IOException {
        File zipFile = new File("C:\\Users\\zinou\\Downloads\\ffmpeg-6.1-win-64.zip");
        File dest = new File("C:\\Users\\zinou\\Downloads\\ffmpeg-6.1-win-64");
        ZipUtils.unzipWithProgress(zipFile, dest, p -> {
            System.out.println("progress : " + p);
        });
    }
}
