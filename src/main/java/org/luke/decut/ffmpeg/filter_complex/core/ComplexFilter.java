package org.luke.decut.ffmpeg.filter_complex.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

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
public abstract class ComplexFilter implements CommandPart {
    private final String name;
    private final FilterOption enable;
    private final ArrayList<FilterOption> options;

    /**
     * Creates a new filter with the specified name and type.
     *
     * @param name the name of the filter (e.g., "scale", "crop")
     */
    public ComplexFilter(String name) {
        this.name = name;
        options = new ArrayList<>();

        enable = new FilterOption("enable");
        addOption(enable);
    }

    /**
     * Adds a single option to the filter.
     *
     * @param option the filter option to add
     * @return this Filter instance for method chaining
     */
    public ComplexFilter addOption(FilterOption option) {
        options.add(option);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ComplexFilter> T setEnable(String value) {
        enable.setValue(value);
        return (T) this;
    }

    /**
     * Adds multiple options to the filter.
     *
     * @param options the filter options to add
     * @return this Filter instance for method chaining
     */
    public ComplexFilter addOptions(FilterOption...options) {
        for(FilterOption option : options) {
            addOption(option);
        }
        return this;
    }

    /**
     * Generates the FFmpeg filter syntax for this filter and its options.
     * The format is: name=option1:option2:option3
     *
     * @return the FFmpeg filter syntax string
     */
    @Override
    public String apply(FfmpegCommand command) {
        List<String> filtered = options.stream().filter(FilterOption::isChanged).map(fo -> fo.apply(command)).toList();
        return name.concat(filtered.isEmpty() ? "" : "=").concat(options.isEmpty() ? "" :
            String.join(":", filtered)
        );
    }
}
