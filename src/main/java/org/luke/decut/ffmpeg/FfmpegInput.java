package org.luke.decut.ffmpeg;

import org.luke.decut.ffmpeg.options.FfmpegOption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    public List<String> apply(FfmpegCommand command) {
        ArrayList<String> out = new ArrayList<>();
        options.forEach(option -> out.addAll(option.apply(command)));
        out.add("-i");
        out.add(source.getAbsolutePath());
        return out;
    }
}
