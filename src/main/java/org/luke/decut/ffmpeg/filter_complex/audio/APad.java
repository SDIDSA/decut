package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class APad extends ComplexFilter {
    private final FilterOption packet_size;
    private final FilterOption pad_len;
    private final FilterOption whole_len;
    private final FilterOption pad_dur;
    private final FilterOption whole_dur;
    public APad() {
        super("apad");

        packet_size = new FilterOption("packet_size");
        pad_len = new FilterOption("pad_len");
        whole_len = new FilterOption("whole_len");
        pad_dur = new FilterOption("pad_dur");
        whole_dur = new FilterOption("whole_dur");

        addOptions(packet_size, pad_len, whole_len, pad_dur, whole_dur);
    }

    public APad setPacketSize(String value) {
        packet_size.setValue(value);
        return this;
    }

    public APad setPadLen(String value) {
        pad_len.setValue(value);
        return this;
    }

    public APad setWholeLen(String value) {
        whole_len.setValue(value);
        return this;
    }

    public APad setPadDur(String value) {
        pad_dur.setValue(value);
        return this;
    }

    public APad setWholeDur(double value) {
        whole_dur.setValue(DECIMAL.format(value));
        return this;
    }
}
