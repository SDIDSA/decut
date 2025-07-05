package org.luke.decut.ffmpeg.options;

import org.luke.decut.ffmpeg.core.StreamType;

public class Skip extends FfmpegOption {

    public Skip(StreamType type) {
        super(type.getVal().concat("n"));
    }
    public static final Skip AUDIO = new Skip(StreamType.AUDIO);
    public static final Skip VIDEO = new Skip(StreamType.VIDEO);
}
