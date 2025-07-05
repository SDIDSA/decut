package org.luke.gui.file;

import org.luke.gui.exception.ErrorHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class DirUtils {
    public static long deleteDir(File dir) {
        AtomicLong size = new AtomicLong(0);
        try (Stream<Path> stream = Files.walk(dir.toPath())) {
            stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
                long l = file.length();
                if(file.delete()) {
                    size.addAndGet(l);
                }
            });
        } catch (IOException e1) {
            ErrorHandler.handle(e1, "delete dir " + dir.getName());
        }
        return size.get();
    }
}
