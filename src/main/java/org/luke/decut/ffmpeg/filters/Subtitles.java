package org.luke.decut.ffmpeg.filters;

import org.luke.decut.ffmpeg.filters.core.Filter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;
import org.luke.decut.ffmpeg.core.StreamType;

public class Subtitles extends Filter {
    private final FilterOption filename;
    private final FilterOption original_size;
    private final FilterOption fontsdir;
    private final FilterOption alpha;
    private final FilterOption charenc;
    private final FilterOption stream_index;
    private final FilterOption force_style;
    private final FilterOption wrap_unicode;

    public Subtitles() {
        super("subtitles", StreamType.VIDEO);

        filename = new FilterOption("filename");
        original_size = new FilterOption("original_size");
        fontsdir = new FilterOption("fontsdir");
        alpha = new FilterOption("alpha");
        charenc = new FilterOption("charenc");
        stream_index = new FilterOption("stream_index");
        force_style = new FilterOption("force_style");
        wrap_unicode = new FilterOption("wrap_unicode");

        addOptions(
                filename, original_size, fontsdir, alpha,
                charenc, stream_index, force_style, wrap_unicode
        );
    }

    public Subtitles setFilename(String value) {
        filename.setValue(value);
        return this;
    }

    public Subtitles setOriginalSize(String value) {
        original_size.setValue(value);
        return this;
    }

    public Subtitles setFontsDir(String value) {
        fontsdir.setValue(value);
        return this;
    }

    public Subtitles setAlpha(String value) {
        alpha.setValue(value);
        return this;
    }

    public Subtitles setCharEnc(String value) {
        charenc.setValue(value);
        return this;
    }

    public Subtitles setStreamIndex(String value) {
        stream_index.setValue(value);
        return this;
    }

    public Subtitles setForceStyle(String value) {
        force_style.setValue(value);
        return this;
    }

    public Subtitles setWrapUnicode(String value) {
        wrap_unicode.setValue(value);
        return this;
    }
}
