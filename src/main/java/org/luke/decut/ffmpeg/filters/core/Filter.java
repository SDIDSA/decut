package org.luke.decut.ffmpeg.filters.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.core.StreamType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for FFmpeg filters.
 * This class provides the foundation for implementing specific FFmpeg filters
 * with support for filter options and type categorization.
 * <p>
 * Example usage:
 * <pre>
 * Filter filter = new VideoFilter("scale")
 *     .addOption(new FilterOption("width", "1280"))
 *     .addOption(new FilterOption("height", "720"));
 * </pre>
 */
public abstract class Filter {
    private final StreamType type;
    private final String name;
    private final ArrayList<FilterOption> options;

    /**
     * Creates a new filter with the specified name and type.
     *
     * @param name the name of the filter (e.g., "scale", "crop")
     * @param type the type of filter (video, audio, etc.)
     */
    public Filter(String name, StreamType type) {
        this.name = name;
        this.type = type;
        options = new ArrayList<>();
    }

    /**
     * Adds a single option to the filter.
     *
     * @param option the filter option to add
     * @return this Filter instance for method chaining
     */
    public Filter addOption(FilterOption option) {
        options.add(option);
        return this;
    }

    /**
     * Adds multiple options to the filter.
     *
     * @param options the filter options to add
     * @return this Filter instance for method chaining
     */
    public Filter addOptions(FilterOption...options) {
        for(FilterOption option : options) {
            addOption(option);
        }
        return this;
    }

    /**
     * Gets the type of this filter.
     *
     * @return the filter type
     */
    public StreamType getType() {
        return type;
    }

    /**
     * Generates the FFmpeg filter syntax for this filter and its options.
     * The format is: name=option1:option2:option3
     *
     * @return the FFmpeg filter syntax string
     */
    public String apply(FfmpegCommand command) {
        List<String> filtered = options.stream().filter(FilterOption::isChanged).map(fo -> fo.apply(command)).toList();
        return name.concat(filtered.isEmpty() ? "" : "=").concat(options.isEmpty() ? "" :
                String.join(":", filtered)
        );
    }
}
