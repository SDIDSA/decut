package org.luke.decut.ffmpeg.filters.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.core.StreamType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an FFmpeg filter graph for a specific stream type (audio or video).
 * A filter graph can contain multiple filter chains, which are sequences of filters
 * that process the media stream in order.
 *
 * Example usage:
 * <pre>
 * FilterGraph graph = new FilterGraph(FilterType.VIDEO)
 *     .addFilter(new ScaleFilter(1280, 720))
 *     .addFilter(new CropFilter(640, 480));
 * </pre>
 */
public class FilterGraph implements CommandPart {
    private final StreamType type;
    private final ArrayList<FilterChain> chains;

    /**
     * Creates a new filter graph for the specified stream type.
     *
     * @param type the type of stream this graph will process (audio or video)
     */
    public FilterGraph(StreamType type) {
        this.type = type;
        chains = new ArrayList<>();
    }

    /**
     * Adds a complete filter chain to this graph.
     *
     * @param chain the filter chain to add
     * @return this FilterGraph instance for method chaining
     */
    public FilterGraph addFilterChain(FilterChain chain) {
        chains.add(chain);
        return this;
    }

    /**
     * Adds a single filter to this graph.
     * If no filter chain exists, a new one is created.
     * The filter must match the type of this graph.
     *
     * @param filter the filter to add
     * @return this FilterGraph instance for method chaining
     * @throws IllegalArgumentException if the filter type doesn't match the graph type
     */
    public FilterGraph addFilter(Filter filter) {
        if(type != filter.getType()) {
            throw new IllegalArgumentException("filter type mismatch " +
                    type.getVal() + " vs " +
                    filter.getType().getVal());
        }
        if(chains.isEmpty()) {
            chains.add(new FilterChain());
        }
        chains.getLast().addFilter(filter);
        return this;
    }

    /**
     * Gets the type of stream this graph processes.
     *
     * @return the filter type (audio or video)
     */
    public StreamType getType() {
        return type;
    }

    @Override
    public List<String> apply(FfmpegCommand command) {
        return List.of("-filter:".concat(type.apply(command)),
                chains.stream().map(fc -> fc.apply(command)).collect(Collectors.joining(";", " ", "")));
    }
}
