package org.luke.decut.ffmpeg.preset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PresetTest {

    @Test
    void testX264X265Presets() {
        assertEquals("ultrafast", Preset.ULTRAFAST.getValueForCodec("libx264"));
        assertEquals("superfast", Preset.SUPERFAST.getValueForCodec("libx264"));
        assertEquals("veryfast", Preset.VERYFAST.getValueForCodec("libx264"));
        assertEquals("faster", Preset.FASTER.getValueForCodec("libx264"));
        assertEquals("fast", Preset.FAST.getValueForCodec("libx264"));
        assertEquals("medium", Preset.MEDIUM.getValueForCodec("libx264"));
        assertEquals("slow", Preset.SLOW.getValueForCodec("libx264"));
        assertEquals("slower", Preset.SLOWER.getValueForCodec("libx264"));
        assertEquals("veryslow", Preset.VERYSLOW.getValueForCodec("libx264"));
        assertEquals("placebo", Preset.PLACEBO.getValueForCodec("libx264"));

        assertEquals("medium", Preset.MEDIUM.getValueForCodec("libx264rgb"));
        assertEquals("fast", Preset.FAST.getValueForCodec("libx264rgb"));

        assertEquals("slow", Preset.SLOW.getValueForCodec("libx265"));
        assertEquals("veryslow", Preset.VERYSLOW.getValueForCodec("libx265"));
    }

    @Test
    void testNvencPresetMappings() {
        assertEquals("llhp", Preset.ULTRAFAST.getValueForCodec("h264_nvenc"));
        assertEquals("hp", Preset.SUPERFAST.getValueForCodec("h264_nvenc"));
        assertEquals("fast", Preset.VERYFAST.getValueForCodec("h264_nvenc"));
        assertEquals("medium", Preset.FASTER.getValueForCodec("h264_nvenc"));
        assertEquals("medium", Preset.FAST.getValueForCodec("h264_nvenc"));
        assertNull(Preset.MEDIUM.getValueForCodec("h264_nvenc"));
        assertEquals("hq", Preset.SLOW.getValueForCodec("h264_nvenc"));
        assertEquals("hq", Preset.SLOWER.getValueForCodec("h264_nvenc"));
        assertEquals("slow", Preset.VERYSLOW.getValueForCodec("h264_nvenc"));
        assertEquals("slow", Preset.PLACEBO.getValueForCodec("h264_nvenc"));

        assertEquals("llhp", Preset.ULTRAFAST.getValueForCodec("hevc_nvenc"));
        assertEquals("hp", Preset.SUPERFAST.getValueForCodec("hevc_nvenc"));
        assertEquals("fast", Preset.VERYFAST.getValueForCodec("hevc_nvenc"));
    }

    @Test
    void testQsvPresetMappings() {
        assertEquals("fast", Preset.ULTRAFAST.getValueForCodec("h264_qsv"));
        assertEquals("fast", Preset.SUPERFAST.getValueForCodec("h264_qsv"));
        assertNull(Preset.VERYFAST.getValueForCodec("h264_qsv"));
        assertNull(Preset.FASTER.getValueForCodec("h264_qsv"));
        assertNull(Preset.FAST.getValueForCodec("h264_qsv"));
        assertEquals("slow", Preset.MEDIUM.getValueForCodec("h264_qsv"));
        assertEquals("slower", Preset.SLOW.getValueForCodec("h264_qsv"));
        assertEquals("slower", Preset.SLOWER.getValueForCodec("h264_qsv"));
        assertEquals("veryslow", Preset.VERYSLOW.getValueForCodec("h264_qsv"));
        assertEquals("veryslow", Preset.PLACEBO.getValueForCodec("h264_qsv"));

        assertEquals("fast", Preset.ULTRAFAST.getValueForCodec("hevc_qsv"));
        assertEquals("slow", Preset.MEDIUM.getValueForCodec("hevc_qsv"));
    }

    @Test
    void testAmfPresetMappings() {
        assertEquals("speed", Preset.ULTRAFAST.getValueForCodec("h264_amf"));
        assertEquals("speed", Preset.SUPERFAST.getValueForCodec("h264_amf"));
        assertEquals("speed", Preset.VERYFAST.getValueForCodec("h264_amf"));
        assertNull(Preset.FASTER.getValueForCodec("h264_amf"));
        assertNull(Preset.FAST.getValueForCodec("h264_amf"));
        assertNull(Preset.MEDIUM.getValueForCodec("h264_amf"));
        assertEquals("quality", Preset.SLOW.getValueForCodec("h264_amf"));
        assertEquals("quality", Preset.SLOWER.getValueForCodec("h264_amf"));
        assertEquals("quality", Preset.VERYSLOW.getValueForCodec("h264_amf"));
        assertEquals("quality", Preset.PLACEBO.getValueForCodec("h264_amf"));

        // Test hevc_amf (should be same as h264_amf)
        assertEquals("speed", Preset.ULTRAFAST.getValueForCodec("hevc_amf"));
        assertEquals("quality", Preset.SLOW.getValueForCodec("hevc_amf"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown_codec", "libvpx", "av1", ""})
    void testUnknownCodecs(String codec) {
        assertNull(Preset.MEDIUM.getValueForCodec(codec));
        assertNull(Preset.FAST.getValueForCodec(codec));
        assertNull(Preset.SLOW.getValueForCodec(codec));
    }

    @Test
    void testApplyForCodec() {
        assertEquals("medium", Preset.MEDIUM.applyForCodec("libx264").get(1));
        assertEquals("fast", Preset.VERYFAST.applyForCodec("h264_nvenc").get(1));
        assertEquals("slow", Preset.MEDIUM.applyForCodec("h264_qsv").get(1));
        assertEquals("speed", Preset.ULTRAFAST.applyForCodec("h264_amf").get(1));

        assertTrue(Preset.MEDIUM.applyForCodec("unknown_codec").isEmpty());
        assertTrue(Preset.MEDIUM.applyForCodec("h264_nvenc").isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Preset.class)
    void testAllPresetsConsistency(Preset preset) {
        String x264Result = preset.getValueForCodec("libx264");
        assertNotNull(x264Result);
        assertFalse(x264Result.isEmpty());

        String applied = preset.applyForCodec("libx264").get(1);
        assertEquals(x264Result, applied);

        assertDoesNotThrow(() -> {
            preset.getValueForCodec("h264_nvenc");
            preset.getValueForCodec("h264_qsv");
            preset.getValueForCodec("h264_amf");
        });
    }

    @Test
    void testPresetEnumValues() {
        Preset[] presets = Preset.values();
        assertEquals(10, presets.length);

        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.ULTRAFAST));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.SUPERFAST));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.VERYFAST));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.FASTER));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.FAST));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.MEDIUM));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.SLOW));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.SLOWER));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.VERYSLOW));
        assertTrue(java.util.Arrays.stream(presets).anyMatch(p -> p == Preset.PLACEBO));
    }

    @Test
    void testEdgeCases() {
        assertNull(Preset.MEDIUM.getValueForCodec("LIBX264"));
        assertNull(Preset.MEDIUM.getValueForCodec("LibX264"));

        assertNull(Preset.MEDIUM.getValueForCodec(""));

        assertNull(Preset.MEDIUM.getValueForCodec(" libx264 "));
    }
}