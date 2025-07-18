package org.luke.decut.ffmpeg;

import java.text.DecimalFormat;
import java.util.List;

public interface CommandPart {
    DecimalFormat DECIMAL = new DecimalFormat("#.####");

    List<String> apply(FfmpegCommand command);
}
