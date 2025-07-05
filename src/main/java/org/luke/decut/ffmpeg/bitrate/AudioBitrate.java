package org.luke.decut.ffmpeg.bitrate;

import org.luke.decut.ffmpeg.core.StreamType;

public class AudioBitrate extends Bitrate {
    public AudioBitrate(String bitrate) {
        super(StreamType.AUDIO, bitrate);
    }
}
