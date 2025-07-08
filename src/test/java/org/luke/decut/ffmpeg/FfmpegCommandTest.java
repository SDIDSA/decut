package org.luke.decut.ffmpeg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.luke.decut.ffmpeg.codec.Codec;
import org.luke.decut.ffmpeg.codec.VideoCodec;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.ffmpeg.options.FfmpegOption;
import org.luke.decut.ffmpeg.preset.Preset;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FfmpegCommandTest {
    private static File src;

    private File out;

    @BeforeAll
    static void inputs() throws URISyntaxException {
        URL srcRes = FfmpegCommandTest.class.getResource("/src.mp4");
        assertNotNull(srcRes);
        src = Paths.get(srcRes.toURI()).toFile();
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

    @ParameterizedTest
    @MethodSource("getCodecs")
    void codecs(VideoCodec codec) {
        assertTrue(src.exists());
        new FfmpegCommand()
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            assertTrue(pi.getProgress() >= 0);
                            assertTrue(pi.getProgress() <= 1);
                        }))
                .setPreset(Preset.ULTRAFAST)
                .addInput(src)
                .setCodec(codec)
                .setOutput(out)
                .execute()
                .waitFor();
        assertTrue(out.exists());
        assertNotEquals(0, out.length());
    }

    static List<VideoCodec> getCodecs() {
        return VideoCodec.getNeededCodecs();
    }
}