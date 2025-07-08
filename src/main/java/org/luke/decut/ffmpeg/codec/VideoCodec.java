package org.luke.decut.ffmpeg.codec;

import org.luke.decut.ffmpeg.core.StreamType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoCodec extends Codec {
    private static final ArrayList<VideoCodec> all = new ArrayList<>();

    private VideoCodec(String codecName) {
        super(StreamType.VIDEO, codecName);
        all.add(this);
    }

    @Override
    public String toString() {
        return getCodecName();
    }

    public static List<VideoCodec> getAllSupported() {
        return Collections.unmodifiableList(all);
    }

    public static List<VideoCodec> getNeededCodecs() {
        return List.of(H264, H265, H264_NVENC, HEVC_NVENC);
    }

    // Special codec
    public static final VideoCodec COPY = new VideoCodec("copy");

    // H.264 (AVC) codecs
    public static final VideoCodec H264 = new VideoCodec("libx264");
    public static final VideoCodec H264_NVENC = new VideoCodec("h264_nvenc");
    public static final VideoCodec H264_QSV = new VideoCodec("h264_qsv");
    public static final VideoCodec H264_AMF = new VideoCodec("h264_amf");
    public static final VideoCodec H264_VIDEOTOOLBOX = new VideoCodec("h264_videotoolbox");
    public static final VideoCodec H264_V4L2M2M = new VideoCodec("h264_v4l2m2m");
    public static final VideoCodec H264_VAAPI = new VideoCodec("h264_vaapi");
    public static final VideoCodec H264_OMX = new VideoCodec("h264_omx");

    // H.265 (HEVC) codecs
    public static final VideoCodec H265 = new VideoCodec("libx265");
    public static final VideoCodec HEVC_NVENC = new VideoCodec("hevc_nvenc");
    public static final VideoCodec HEVC_QSV = new VideoCodec("hevc_qsv");
    public static final VideoCodec HEVC_AMF = new VideoCodec("hevc_amf");
    public static final VideoCodec HEVC_VIDEOTOOLBOX = new VideoCodec("hevc_videotoolbox");
    public static final VideoCodec HEVC_V4L2M2M = new VideoCodec("hevc_v4l2m2m");
    public static final VideoCodec HEVC_VAAPI = new VideoCodec("hevc_vaapi");

    // AV1 codecs
    public static final VideoCodec AV1 = new VideoCodec("libaom-av1");
    public static final VideoCodec AV1_SVT = new VideoCodec("libsvtav1");
    public static final VideoCodec AV1_NVENC = new VideoCodec("av1_nvenc");
    public static final VideoCodec AV1_QSV = new VideoCodec("av1_qsv");
    public static final VideoCodec AV1_AMF = new VideoCodec("av1_amf");
    public static final VideoCodec AV1_VAAPI = new VideoCodec("av1_vaapi");

    // VP8/VP9 codecs
    public static final VideoCodec VP8 = new VideoCodec("libvpx");
    public static final VideoCodec VP9 = new VideoCodec("libvpx-vp9");
    public static final VideoCodec VP9_VAAPI = new VideoCodec("vp9_vaapi");
    public static final VideoCodec VP9_QSV = new VideoCodec("vp9_qsv");

    // Legacy codecs
    public static final VideoCodec MPEG2 = new VideoCodec("mpeg2video");
    public static final VideoCodec MPEG4 = new VideoCodec("libxvid");
    public static final VideoCodec MPEG4_PART2 = new VideoCodec("mpeg4");
    public static final VideoCodec H263 = new VideoCodec("h263");
    public static final VideoCodec H263P = new VideoCodec("h263p");

    // Other popular codecs
    public static final VideoCodec THEORA = new VideoCodec("libtheora");
    public static final VideoCodec DNXHD = new VideoCodec("dnxhd");
    public static final VideoCodec PRORES = new VideoCodec("prores");
    public static final VideoCodec PRORES_KS = new VideoCodec("prores_ks");
    public static final VideoCodec MJPEG = new VideoCodec("mjpeg");
    public static final VideoCodec HUFFYUV = new VideoCodec("huffyuv");
    public static final VideoCodec FFVHUFF = new VideoCodec("ffvhuff");
    public static final VideoCodec UTVIDEO = new VideoCodec("libutvideo");

    // Raw/Uncompressed
    public static final VideoCodec RAWVIDEO = new VideoCodec("rawvideo");
    public static final VideoCodec V210 = new VideoCodec("v210");
    public static final VideoCodec V410 = new VideoCodec("v410");

    // Animation/Screen capture
    public static final VideoCodec LIBX264RGB = new VideoCodec("libx264rgb");
    public static final VideoCodec QTRLE = new VideoCodec("qtrle");
    public static final VideoCodec PNG = new VideoCodec("png");
    public static final VideoCodec APNG = new VideoCodec("apng");
    public static final VideoCodec GIF = new VideoCodec("gif");
}