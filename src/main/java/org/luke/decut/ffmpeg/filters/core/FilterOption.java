package org.luke.decut.ffmpeg.filters.core;

import org.luke.decut.ffmpeg.FfmpegCommand;

/**
 * Represents a single option for an FFmpeg filter.
 * Each option consists of a name-value pair that configures the behavior of a filter.
 * <p>
 * Example usage:
 * <pre>
 * FilterOption option = new FilterOption("width", "1280");
 * </pre>
 */
public class FilterOption {
    private final String name;
    private String value;
    private boolean changed;

    /**
     * Creates a new filter option with the specified name and value.
     *
     * @param name the name of the option (e.g., "width", "height")
     */
    public FilterOption(String name) {
        this.name = name;
        changed = false;
    }

    /**
     * Updates the value of this option.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    /**
     * Gets the name of this option.
     *
     * @return the option name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of this option.
     *
     * @return the option value
     */
    public String getValue() {
        return value;
    }

    /**
     * Generates the FFmpeg option syntax for this option.
     * The format is: name=value
     *
     * @return the FFmpeg option syntax string
     */
    public String apply(FfmpegCommand command) {
        return name.concat("=").concat(value);
    }
}
