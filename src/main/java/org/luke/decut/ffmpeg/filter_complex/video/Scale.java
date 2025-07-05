package org.luke.decut.ffmpeg.filter_complex.video;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class Scale extends ComplexFilter {
    private final FilterOption width;
    private final FilterOption height;
    private final FilterOption eval;
    private final FilterOption interl;
    private final FilterOption flags;
    private final FilterOption param0;
    private final FilterOption param1;
    private final FilterOption intent;
    private final FilterOption size;
    private final FilterOption in_color_matrix;
    private final FilterOption out_color_matrix;
    private final FilterOption in_range;
    private final FilterOption out_range;
    private final FilterOption in_chroma_loc;
    private final FilterOption out_chroma_loc;
    private final FilterOption in_primaries;
    private final FilterOption out_primaries;
    private final FilterOption in_transfer;
    private final FilterOption out_transfer;
    private final FilterOption force_original_aspect_ratio;
    private final FilterOption force_divisible_by;
    private final FilterOption reset_sar;

    public Scale() {
        super("scale");

        width = new FilterOption("width");
        height = new FilterOption("height");
        eval = new FilterOption("eval");
        interl = new FilterOption("interl");
        flags = new FilterOption("flags");
        param0 = new FilterOption("param0");
        param1 = new FilterOption("param1");
        intent = new FilterOption("intent");
        size = new FilterOption("size");
        in_color_matrix = new FilterOption("in_color_matrix");
        out_color_matrix = new FilterOption("out_color_matrix");
        in_range = new FilterOption("in_range");
        out_range = new FilterOption("out_range");
        in_chroma_loc = new FilterOption("in_chroma_loc");
        out_chroma_loc = new FilterOption("out_chroma_loc");
        in_primaries = new FilterOption("in_primaries");
        out_primaries = new FilterOption("out_primaries");
        in_transfer = new FilterOption("in_transfer");
        out_transfer = new FilterOption("out_transfer");
        force_original_aspect_ratio = new FilterOption("force_original_aspect_ratio");
        force_divisible_by = new FilterOption("force_divisible_by");
        reset_sar = new FilterOption("reset_sar");

        addOptions(width, height, eval, interl, flags, param0, param1, intent, size, in_color_matrix,
                out_color_matrix, in_range, out_range, in_chroma_loc, out_chroma_loc, in_primaries, out_primaries,
                in_transfer, out_transfer, force_original_aspect_ratio, force_divisible_by, reset_sar);
    }

    public Scale contain(int width, int height) {
        setWidth(String.valueOf(width));
        setHeight(String.valueOf(height));
        setForceOriginalAspectRatio("decrease");
        return this;
    }

    public Scale cover(int width, int height) {
        setWidth(String.valueOf(width));
        setHeight(String.valueOf(height));
        setForceOriginalAspectRatio("increase");
        return this;
    }

    public Scale setWidth(String value) {
        width.setValue(value);
        return this;
    }

    public Scale setHeight(String value) {
        height.setValue(value);
        return this;
    }

    public Scale setEval(String value) {
        eval.setValue(value);
        return this;
    }

    public Scale setInterl(String value) {
        interl.setValue(value);
        return this;
    }

    public Scale setFlags(String value) {
        flags.setValue(value);
        return this;
    }

    public Scale setParam0(String value) {
        param0.setValue(value);
        return this;
    }

    public Scale setParam1(String value) {
        param1.setValue(value);
        return this;
    }

    public Scale setIntent(String value) {
        intent.setValue(value);
        return this;
    }

    public Scale setSize(String value) {
        size.setValue(value);
        return this;
    }

    public Scale setInColorMatrix(String value) {
        in_color_matrix.setValue(value);
        return this;
    }

    public Scale setOutColorMatrix(String value) {
        out_color_matrix.setValue(value);
        return this;
    }

    public Scale setInRange(String value) {
        in_range.setValue(value);
        return this;
    }

    public Scale setOutRange(String value) {
        out_range.setValue(value);
        return this;
    }

    public Scale setInChromaLoc(String value) {
        in_chroma_loc.setValue(value);
        return this;
    }

    public Scale setOutChromaLoc(String value) {
        out_chroma_loc.setValue(value);
        return this;
    }

    public Scale setInPrimaries(String value) {
        in_primaries.setValue(value);
        return this;
    }

    public Scale setOutPrimaries(String value) {
        out_primaries.setValue(value);
        return this;
    }

    public Scale setInTransfer(String value) {
        in_transfer.setValue(value);
        return this;
    }

    public Scale setOutTransfer(String value) {
        out_transfer.setValue(value);
        return this;
    }

    public Scale setForceOriginalAspectRatio(String value) {
        force_original_aspect_ratio.setValue(value);
        return this;
    }

    public Scale setForceDivisibleBy(String value) {
        force_divisible_by.setValue(value);
        return this;
    }

    public Scale setResetSar(String value) {
        reset_sar.setValue(value);
        return this;
    }
}
