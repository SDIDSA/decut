package org.luke.decut.ffmpeg.filter_complex.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a single node in a complex filter graph.
 * Each node can have multiple inputs, a chain of filters, and multiple outputs.
 * 
 * Example usage:
 * <pre>
 * ComplexFilterNode node = new ComplexFilterNode()
 *     .setInputs("[0:v]", "[1:v]")
 *     .addFilter(new OverlayFilter())
 *     .setOutput("[result]");
 * </pre>
 */
public class ComplexFilterNode implements CommandPart {
    private final List<String> inputs;
    private final List<ComplexFilter> filters;
    private final List<String> outputs;
    
    /**
     * Creates a new empty complex filter node.
     */
    public ComplexFilterNode() {
        this.inputs = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
    
    /**
     * Sets a single input stream for this node.
     * 
     * @param input the input stream label (e.g., "[0:v]", "[audio1]")
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode setInput(String input) {
        inputs.clear();
        inputs.add(input);
        return this;
    }
    
    /**
     * Sets multiple input streams for this node.
     * 
     * @param inputs the input stream labels
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode setInputs(String... inputs) {
        this.inputs.clear();
        this.inputs.addAll(Arrays.asList(inputs));
        return this;
    }
    
    /**
     * Adds an input stream to this node.
     * 
     * @param input the input stream label to add
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode addInput(String input) {
        inputs.add(input);
        return this;
    }
    
    /**
     * Sets a single output stream for this node.
     * 
     * @param output the output stream label (e.g., "[scaled]", "[v]")
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode setOutput(String output) {
        outputs.clear();
        outputs.add(output);
        return this;
    }
    
    /**
     * Sets multiple output streams for this node.
     * 
     * @param outputs the output stream labels
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode setOutputs(String... outputs) {
        this.outputs.clear();
        this.outputs.addAll(Arrays.asList(outputs));
        return this;
    }
    
    /**
     * Adds an output stream to this node.
     * 
     * @param output the output stream label to add
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode addOutput(String output) {
        outputs.add(output);
        return this;
    }
    
    /**
     * Adds a filter to this node's filter chain.
     * 
     * @param filter the filter to add
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode addFilter(ComplexFilter filter) {
        filters.add(filter);
        return this;
    }
    
    /**
     * Adds multiple filters to this node's filter chain.
     * 
     * @param filters the filters to add
     * @return this ComplexFilterNode instance for method chaining
     */
    public ComplexFilterNode addFilters(ComplexFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }
    
    /**
     * Generates the FFmpeg syntax for this filter node.
     * The format is: [input1][input2]filter1,filter2[output1][output2]
     * 
     * @return the FFmpeg filter node syntax string
     */
    @Override
    public String apply(FfmpegCommand command) {
        StringBuilder sb = new StringBuilder();
        
        // Add inputs
        if (!inputs.isEmpty()) {
            sb.append(String.join("", inputs));
        }
        
        // Add filters
        if (!filters.isEmpty()) {
            String filterChain = filters.stream()
                .map(cf -> cf.apply(command))
                .collect(Collectors.joining(","));
            sb.append(filterChain);
        }
        
        // Add outputs
        if (!outputs.isEmpty()) {
            sb.append(String.join("", outputs));
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the input stream labels for this node.
     * 
     * @return a copy of the input list
     */
    public List<String> getInputs() {
        return new ArrayList<>(inputs);
    }
    
    /**
     * Gets the output stream labels for this node.
     * 
     * @return a copy of the output list
     */
    public List<String> getOutputs() {
        return new ArrayList<>(outputs);
    }
    
    /**
     * Gets the filters in this node.
     * 
     * @return a copy of the filter list
     */
    public List<ComplexFilter> getFilters() {
        return new ArrayList<>(filters);
    }
}