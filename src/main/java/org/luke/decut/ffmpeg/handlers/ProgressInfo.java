package org.luke.decut.ffmpeg.handlers;

public record ProgressInfo(int frame, float fps, String bitRate, long size, long time, long duration, float speed) {
    public String getFormattedTime() {
        return formatTime(time);
    }

    public String getFormattedDuration() {
        return formatTime(duration);
    }

    private String formatTime(long ms) {
        long seconds = ms / 1_000;
        return String.format("%02d:%02d:%02d",
                seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public long getEstimatedRemainingTime() {
        if (speed <= 0 || duration <= 0) return -1;

        long remainingDuration = duration - time;
        return (long) (remainingDuration / speed);
    }

    public String getFormattedRemainingTime() {
        return formatTime(getEstimatedRemainingTime());
    }

    public float getProgress() {
        return (float) time / duration;
    }
}
