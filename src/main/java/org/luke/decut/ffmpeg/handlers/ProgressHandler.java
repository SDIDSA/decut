package org.luke.decut.ffmpeg.handlers;

import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.LineHandler;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A line handler that processes FFmpeg progress output and provides real-time progress information.
 * This handler parses FFmpeg's progress output format and maintains state about the current
 * processing status, including frame count, FPS, bitrate, file size, processing time,
 * total duration, and processing speed.
 * <p>
 * Example usage:
 * <pre>
 * ProgressHandler handler = new ProgressHandler()
 *     .addHandler(progress -> {
 *         System.out.println("Progress: " + progress.getTime() + " / " + progress.getDuration());
 *     });
 * </pre>
 */
public class ProgressHandler implements LineHandler {
    private final ArrayList<Consumer<ProgressInfo>> handlers;

    private int frame;
    private float fps;
    private String bitRate;
    private long size;
    private long time;
    private long duration = -1;
    private float speed;

    /**
     * Creates a new progress handler with an empty list of progress consumers.
     */
    public ProgressHandler() {
        handlers = new ArrayList<>();
    }

    /**
     * Adds a consumer that will be notified of progress updates.
     * The consumer receives a ProgressInfo object containing the current state
     * of the FFmpeg processing.
     *
     * @param handler the consumer to add
     * @return this ProgressHandler instance for method chaining
     */
    public ProgressHandler addHandler(Consumer<ProgressInfo> handler) {
        handlers.add(handler);
        return this;
    }

    /**
     * Determines if a line of FFmpeg output should be processed by this handler.
     * This handler processes lines that:
     * 1. Match "progress=continue"
     * 2. Contain duration information in the format "duration:HH:MM:SS.mmm"
     * 3. Contain progress information in key=value format
     *
     * @param line the line of FFmpeg output to check
     * @return true if the line should be handled, false otherwise
     */
    @Override
    public boolean match(FfmpegCommand command, String line) {
        if (line.equalsIgnoreCase("progress=continue")) {
            return true;
        }

        String[] parts = line.split(":");
        if (command.getDuration() == -1 && duration == -1) {
            if (parts.length == 4 && parts[0].trim().equalsIgnoreCase("duration")) {
                int hours = Integer.parseInt(parts[1].trim());
                int minutes = Integer.parseInt(parts[2].trim());
                String[] subParts = parts[3].split("\\.");
                int seconds = Integer.parseInt(subParts[0].trim());
                int milliseconds = (int) (Float.parseFloat("0." + subParts[1].trim()) * 1000);

                duration =
                        hours * 3600_000L +
                                minutes * 60_000L +
                                seconds * 1_000L +
                                milliseconds;
                return false;
            }

            parts = line.split(",");
            if (parts.length == 3 && line.trim().startsWith("Duration:")) {
                String[] subParts = parts[0].split(":");
                int hours = Integer.parseInt(subParts[1].trim());
                int minutes = Integer.parseInt(subParts[2].trim());
                String[] subSubParts = subParts[3].split("\\.");
                int seconds = Integer.parseInt(subSubParts[0].trim());
                StringBuilder millis = new StringBuilder(subSubParts[1]);
                while (millis.length() < 3) {
                    millis.append("0");
                }
                int milliseconds = (int) (Float.parseFloat("0." + millis) * 1000);
                duration =
                        hours * 3600_000L +
                                minutes * 60_000L +
                                seconds * 1_000L +
                                milliseconds;
                return false;
            }
        }

        if(command.getDuration() != -1 && duration == -1) {
            duration = command.getDuration();
        }

        parts = line.split("=");
        if (parts.length == 2) {
            try {
                switch (parts[0]) {
                    case "frame":
                        frame = Integer.parseInt(parts[1]);
                        break;
                    case "fps":
                        fps = Float.parseFloat(parts[1]);
                        break;
                    case "bitrate":
                        bitRate = parts[1];
                        break;
                    case "total_size":
                        size = Long.parseLong(parts[1]);
                        break;
                    case "out_time_ms":
                        time = Long.parseLong(parts[1]) / 1000;
                        break;
                    case "speed":
                        speed = Float.parseFloat(parts[1].replace("x", ""));
                        break;
                }
            } catch (Exception x) {
                //ErrorHandler.handle(x, "parsing progress");
            }
        }
        handle(command, "");
        return false;
    }

    /**
     * Processes a line of FFmpeg output and notifies all registered handlers
     * with the current progress information.
     * This method is called for each line that matches the criteria in {@link #match(FfmpegCommand, String)}.
     *
     * @param line the line of FFmpeg output to process
     */
    @Override
    public void handle(FfmpegCommand command, String line) {
        ProgressInfo state = new ProgressInfo(frame, fps, bitRate, size, time, duration, speed);
        handlers.forEach(handler -> handler.accept(state));
    }
}
