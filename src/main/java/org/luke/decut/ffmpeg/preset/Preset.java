package org.luke.decut.ffmpeg.preset;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.codec.VideoCodec;

public enum Preset implements CommandPart {
    ULTRAFAST("ultrafast"),
    SUPERFAST("superfast"),
    VERYFAST("veryfast"),
    FASTER("faster"),
    FAST("fast"),
    MEDIUM("medium"),
    SLOW("slow"),
    SLOWER("slower"),
    VERYSLOW("veryslow"),
    PLACEBO("placebo");

    private final String x264Value;

    Preset(String x264Value) {
        this.x264Value = x264Value;
    }

    public String getValueForCodec(String codec) {
        return switch (codec) {
            case "libx264", "libx264rgb", "libx265" -> x264Value;
            case "h264_nvenc", "hevc_nvenc" -> mapToNvencPreset();
            case "h264_qsv", "hevc_qsv" -> mapToQsvPreset();
            case "h264_amf", "hevc_amf" -> mapToAmfPreset();
            default -> null;
        };
    }

    public String getX264Value() {
        return x264Value;
    }

    private String mapToNvencPreset() {
        return switch (this) {
            case ULTRAFAST -> "llhp";
            case SUPERFAST -> "hp";
            case VERYFAST -> "fast";
            case FASTER, FAST -> "medium";
            case SLOW, SLOWER -> "hq";
            case VERYSLOW, PLACEBO -> "slow";
            default -> null;
        };
    }

    private String mapToQsvPreset() {
        return switch (this) {
            case ULTRAFAST, SUPERFAST -> "fast";
            case MEDIUM -> "slow";
            case SLOW, SLOWER -> "slower";
            case VERYSLOW, PLACEBO -> "veryslow";
            default -> null;
        };
    }

    private String mapToAmfPreset() {
        return switch (this) {
            case ULTRAFAST, SUPERFAST, VERYFAST -> "speed";
            case SLOW, SLOWER, VERYSLOW, PLACEBO -> "quality";
            default -> null;
        };
    }

    public String applyForCodec(String codec) {
        String mapped = getValueForCodec(codec);
        return mapped == null ? "" : "-preset " + mapped;
    }

    @Override
    public String apply(FfmpegCommand command) {
        VideoCodec codec = command.getVideoCodec();
        return codec == null ? "" : applyForCodec(codec.getCodecName());
    }
}
