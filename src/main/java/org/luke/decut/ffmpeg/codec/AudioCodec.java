package org.luke.decut.ffmpeg.codec;

import org.luke.decut.ffmpeg.core.StreamType;

public class AudioCodec extends Codec {
    public AudioCodec(String codecName) {
        super(StreamType.AUDIO, codecName);
    }

    public static final AudioCodec COPY = new AudioCodec("copy");
}