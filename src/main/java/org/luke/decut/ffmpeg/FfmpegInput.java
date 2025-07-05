package org.luke.decut.ffmpeg;

import org.luke.decut.ffmpeg.options.FfmpegOption;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FfmpegInput implements CommandPart {
    private final File source;

    private final ArrayList<FfmpegOption> options;


    public FfmpegInput(File source) {
        this.source = source;
        options = new ArrayList<>();
    }

    public FfmpegInput addOption(FfmpegOption option) {
        options.add(option);
        return this;
    }


    @Override
    public String apply(FfmpegCommand command) {
        return (options.isEmpty() ? "" :
                options.stream().map(fo -> fo.apply(command))
                        .collect(Collectors.joining(" ", "", " "))).concat("-i \"" + source + "\"");
    }
}
