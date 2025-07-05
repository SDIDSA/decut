package org.luke.decut.ffmpeg;

/**
 * Interface for handling lines of output from a command execution.
 * Implementations of this interface can be used to process and respond to
 * specific patterns in command output.
 */
public interface LineHandler {
    boolean match(FfmpegCommand command, String line);
    void handle(FfmpegCommand command, String line);
}
