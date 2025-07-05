package org.luke.decut.ffmpeg.filters.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

import java.util.ArrayList;

/**
 * Represents a chain of FFmpeg filters that process a media stream in sequence.
 * Filters in a chain are applied in order, with the output of one filter
 * becoming the input of the next.
 * <p>
 * Example usage:
 * <pre>
 * FilterChain chain = new FilterChain()
 *     .addFilter(new ScaleFilter(1280, 720))
 *     .addFilter(new CropFilter(640, 480));
 * </pre>
 */
public class FilterChain implements CommandPart {
    public ArrayList<Filter> filters;

    /**
     * Creates a new empty filter chain.
     */
    public FilterChain() {
        filters = new ArrayList<>();
    }

    /**
     * Adds a filter to the end of this chain.
     *
     * @param filter the filter to add
     * @return this FilterChain instance for method chaining
     */
    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Generates the FFmpeg filter chain syntax.
     * The format is: filter1,filter2,filter3
     * where each filter is in the format: name=option1:option2:option3
     *
     * @return the FFmpeg filter chain syntax string
     */
    @Override
    public String apply(FfmpegCommand command) {
        return String.join(",",
                filters.stream().map(f -> f.apply(command)).toList().toArray(new String[0]));
    }
}
