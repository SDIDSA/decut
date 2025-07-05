package org.luke.decut.ffmpeg;

import java.text.DecimalFormat;

public interface CommandPart {
    DecimalFormat DECIMAL = new DecimalFormat("#.####");

    String apply(FfmpegCommand command);
}
