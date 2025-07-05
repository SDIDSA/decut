package org.luke.decut.ffmpeg.filter_complex.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an FFmpeg complex filter graph that can handle multiple inputs and outputs
 * with stream labeling and complex routing between filters.
 * <p>
 * Complex filter graphs allow for advanced operations like:
 * - Mixing multiple video/audio streams
 * - Picture-in-picture effects
 * - Audio mixing with multiple sources
 * - Complex transitions and overlays
 * <p>
 * Example usage:
 * <pre>
 * ComplexFilterGraph complex = new ComplexFilterGraph()
 *     .addNode(new ComplexFilterNode()
 *         .setInputs("[0:v]", "[1:v]")
 *         .addFilter(new OverlayFilter())
 *         .setOutput("[overlaid]"))
 *     .addNode(new ComplexFilterNode()
 *         .setInput("[overlaid]")
 *         .addFilter(new ScaleFilter(1920, 1080))
 *         .setOutput("[v]"));
 * </pre>
 */
public class ComplexFilterGraph implements CommandPart {
    private final List<ComplexFilterNode> nodes;

    /**
     * Creates a new empty complex filter graph.
     */
    public ComplexFilterGraph() {
        this.nodes = new ArrayList<>();
    }

    /**
     * Adds a filter node to the complex graph.
     *
     * @param node the complex filter node to add
     * @return this ComplexFilterGraph instance for method chaining
     */
    public ComplexFilterGraph addNode(ComplexFilterNode node) {
        nodes.add(node);
        return this;
    }

    /**
     * Creates and adds a simple filter node with automatic stream labeling.
     * This is a convenience method for simple cases.
     *
     * @param input  the input stream label (e.g., "[0:v]" or "[audio1]")
     * @param filter the filter to apply
     * @param output the output stream label (e.g., "[scaled]" or "[v]")
     * @return this ComplexFilterGraph instance for method chaining
     */
    public ComplexFilterGraph addSimpleNode(String input, ComplexFilter filter, String output) {
        return addNode(new ComplexFilterNode()
                .setInput(input)
                .addFilter(filter)
                .setOutput(output));
    }

    public ComplexFilterGraph addSimpleNode(ComplexFilter filter) {
        if (nodes.isEmpty()) addNode(new ComplexFilterNode());
        nodes.getFirst().addFilter(filter);
        return this;
    }

    /**
     * Creates a filter chain node for multiple filters in sequence.
     *
     * @param input   the input stream label
     * @param filters the filters to chain together
     * @param output  the output stream label
     * @return this ComplexFilterGraph instance for method chaining
     */
    public ComplexFilterGraph addChainNode(String input, List<ComplexFilter> filters, String output) {
        ComplexFilterNode node = new ComplexFilterNode().setInput(input).setOutput(output);
        filters.forEach(node::addFilter);
        return addNode(node);
    }

    /**
     * Generates the FFmpeg filter_complex syntax.
     * The format is: -filter_complex "node1;node2;node3"
     *
     * @return the FFmpeg filter_complex command string
     */
    @Override
    public String apply(FfmpegCommand command) {
        if (nodes.isEmpty()) {
            return "";
        }

        String filterString = nodes.stream()
                .map(cfn -> cfn.apply(command))
                .collect(Collectors.joining(";"));

        return "-filter_complex \"" + filterString + "\"";
    }

    /**
     * Checks if this complex filter graph is empty.
     *
     * @return true if no nodes have been added, false otherwise
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}