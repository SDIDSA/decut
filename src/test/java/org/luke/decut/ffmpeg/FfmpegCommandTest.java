package org.luke.decut.ffmpeg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luke.decut.ffmpeg.codec.VideoCodec;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.ffmpeg.preset.Preset;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FfmpegCommandTest {
    private static File h264;
    private static File h265;

    private File out;

    @BeforeAll
    static void inputs() throws URISyntaxException {
        URL h264Res = FfmpegCommandTest.class.getResource("/h264.mp4");
        assertNotNull(h264Res);
        h264 = Paths.get(h264Res.toURI()).toFile();

        URL h265Res = FfmpegCommandTest.class.getResource("/h265.mp4");
        assertNotNull(h265Res);
        h265 = Paths.get(h265Res.toURI()).toFile();
    }

    @BeforeEach
    void setUp() throws IOException {
        out = File.createTempFile("decut_test_", ".mp4");
    }

    @AfterEach
    void tearDown() {
        if (out.exists()) {
            assertTrue(out.delete());
        }
    }

    @Test
    void h265ToH264() {
        assertTrue(h265.exists());
        new FfmpegCommand()
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            assertTrue(pi.getProgress() >= 0);
                            assertTrue(pi.getProgress() <= 1);
                        }))
                .setPreset(Preset.ULTRAFAST)
                .addInput(h265)
                .setCodec(VideoCodec.H264)
                .setOutput(out)
                .execute()
                .waitFor();
        assertTrue(out.exists());
        assertNotEquals(0, out.length());
    }

    @Test
    void h265ToH264Nvenc() {
        assertTrue(h265.exists());
        new FfmpegCommand()
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            assertTrue(pi.getProgress() >= 0);
                            assertTrue(pi.getProgress() <= 1);
                        }))
                .setPreset(Preset.ULTRAFAST)
                .addInput(h265)
                .setCodec(VideoCodec.H264_NVENC)
                .setOutput(out)
                .execute()
                .waitFor();
        assertTrue(out.exists());
        assertNotEquals(0, out.length());
    }

    @Test
    void h264ToH265() {
        assertTrue(h264.exists());
        new FfmpegCommand()
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            assertTrue(pi.getProgress() >= 0);
                            assertTrue(pi.getProgress() <= 1);
                        }))
                .setPreset(Preset.ULTRAFAST)
                .addInput(h264)
                .setCodec(VideoCodec.H265)
                .setOutput(out)
                .execute()
                .waitFor();
        assertTrue(out.exists());
        assertNotEquals(0, out.length());
    }

    @Test
    void h264ToH265Nvenc() {
        assertTrue(h264.exists());
        new FfmpegCommand()
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            assertTrue(pi.getProgress() >= 0);
                            assertTrue(pi.getProgress() <= 1);
                        }))
                .setPreset(Preset.ULTRAFAST)
                .addInput(h264)
                .setCodec(VideoCodec.HEVC_NVENC)
                .setOutput(out)
                .execute()
                .waitFor();
        assertTrue(out.exists());
        assertNotEquals(0, out.length());
    }
}