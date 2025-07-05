package org.luke.decut.ffmpeg.bitrate;

import org.luke.decut.ffmpeg.core.StreamType;

public class VideoBitrate extends Bitrate {
    public VideoBitrate(String bitrate) {
        super(StreamType.VIDEO, bitrate);
    }
}
