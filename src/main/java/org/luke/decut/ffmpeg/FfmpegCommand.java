package org.luke.decut.ffmpeg;

import org.luke.decut.cmd.Command;
import org.luke.decut.ffmpeg.bitrate.Bitrate;
import org.luke.decut.ffmpeg.codec.Codec;
import org.luke.decut.ffmpeg.codec.VideoCodec;
import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilterGraph;
import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilterNode;
import org.luke.decut.ffmpeg.filters.core.Filter;
import org.luke.decut.ffmpeg.filters.core.FilterGraph;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.ffmpeg.options.FfmpegOption;
import org.luke.decut.ffmpeg.preset.Preset;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.FfmpegManager;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Consumer;

public class FfmpegCommand implements CommandPart {
    private final ArrayList<FfmpegInput> inputs;
    private final ArrayList<LineHandler> handlers;
    private final ArrayList<FilterGraph> graphs;
    private final ArrayList<FfmpegOption> options;
    private final ArrayList<Codec> codecs;
    private final ArrayList<Bitrate> bitrates;
    private final ComplexFilterGraph complexFilterGraph;
    private File output;
    private Consumer<File> onOutput;
    private boolean outputHandled = false;
    private Process running;
    private Preset preset;

    private long duration = -1;

    private boolean progress = false;

    public FfmpegCommand() {
        inputs = new ArrayList<>();
        handlers = new ArrayList<>();
        graphs = new ArrayList<>();
        options = new ArrayList<>();
        codecs = new ArrayList<>();
        bitrates = new ArrayList<>();
        complexFilterGraph = new ComplexFilterGraph();
    }

    private FfmpegCommand execute(String ffmpegBinary) {
        String com = apply(this, ffmpegBinary);
        System.out.println(com);
        running = new Command(this::handleLine, this::handleLine, com).addOnExit(_ -> {
            if (output != null && output.exists() && output.length() > 0) {
                if (onOutput != null) {
                    onOutput.accept(output);
                }
                outputHandled = true;
            }
        }).execute(new File("/"));
        return this;
    }

    public static String getFfmpegBinary() {
        File ffmpegRoot = new File(LocalStore.getDefaultFfmpeg());
        if(!ffmpegRoot.exists() || !ffmpegRoot.isDirectory()) {
            if(systemFfmpeg()) {
                return "ffmpeg";
            } else {
                return null;
            }
        }
        LocalInstall ffmpeg = FfmpegManager.versionFromDir(ffmpegRoot);
        if(ffmpeg == null) {
            return null;
        }
        return "\"" + ffmpeg.getBinary().getAbsolutePath() + "\"";
    }

    public FfmpegCommand execute() {
        String ffmpegBinary = getFfmpegBinary();
        if(ffmpegBinary == null) return this;
        return execute(ffmpegBinary);
    }

    public FfmpegCommand waitFor() {
        if (running != null && running.isAlive()) {
            try {
                running.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.waitWhile(() -> output != null && !outputHandled, 1000);
        }
        return this;
    }

    public FfmpegCommand addHandler(LineHandler handler) {
        if (handler instanceof ProgressHandler) {
            progress = true;
        }
        handlers.add(handler);
        return this;
    }

    public FfmpegCommand setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public FfmpegCommand addFilterGraph(FilterGraph graph) {
        graphs.add(graph);
        return this;
    }

    public FfmpegCommand addFilter(Filter filter) {
        FilterGraph addTo = null;
        for (FilterGraph graph : graphs) {
            if (graph.getType() == filter.getType()) {
                addTo = graph;
                break;
            }
        }

        if (addTo == null) {
            addTo = new FilterGraph(filter.getType());
            graphs.add(addTo);
        }

        addTo.addFilter(filter);

        return this;
    }

    public FfmpegCommand addComplexFilterNode(ComplexFilterNode node) {
        complexFilterGraph.addNode(node);
        return this;
    }

    public FfmpegCommand addComplexFilter(String input, ComplexFilter filter, String output) {
        complexFilterGraph.addSimpleNode(input, filter, output);
        return this;
    }

    public FfmpegCommand addComplexFilter(ComplexFilter filter) {
        complexFilterGraph.addSimpleNode(filter);
        return this;
    }

    public FfmpegCommand addOption(FfmpegOption option) {
        options.add(option);
        return this;
    }

    public FfmpegCommand setPreset(Preset preset) {
        this.preset = preset;
        return this;
    }

    public FfmpegCommand setCodec(Codec codec) {
        codecs.removeIf(c -> c.getType() == codec.getType());
        codecs.add(codec);
        return this;
    }

    public ArrayList<Codec> getCodecs() {
        return codecs;
    }

    public VideoCodec getVideoCodec() {
        for (Codec codec : codecs) {
            if (codec instanceof VideoCodec videoCodec) {
                return videoCodec;
            }
        }
        return null;
    }

    public FfmpegCommand setBitrate(Bitrate bitrate) {
        bitrates.removeIf(b -> b.getType() == bitrate.getType());
        bitrates.add(bitrate);
        return this;
    }

    public FfmpegCommand addInput(File file) {
        inputs.add(new FfmpegInput(file));
        return this;
    }

    public FfmpegCommand addInput(FfmpegInput input) {
        inputs.add(input);
        return this;
    }

    public FfmpegCommand setOutput(File file) {
        this.output = file;
        return this;
    }

    private void handleLine(String line) {
        handlers.forEach(handler -> {
            if (handler.match(this, line)) {
                handler.handle(this, line);
            }
        });
    }

    public FfmpegCommand setOnOutput(Consumer<File> onOutput, String extension) {
        try {
            setOutput(File.createTempFile("decut_", extension));
            this.onOutput = onOutput;
        } catch (IOException e) {
            ErrorHandler.handle(e, "set output handler");
        }
        return this;
    }

    public FfmpegCommand setOnOutputStream(Consumer<InputStream> onOutput, String extension) {
        try {
            setOutput(File.createTempFile("decut_", extension));
            this.onOutput = file -> {
                try (InputStream fis = new FileInputStream(file)) {
                    onOutput.accept(fis);
                    //Files.delete(file.toPath());
                } catch (IOException ex) {
                    ErrorHandler.handle(ex, "handle ffmpeg output");
                }
            };
        } catch (IOException e) {
            ErrorHandler.handle(e, "set output handler");
        }
        return this;
    }

    public String apply(FfmpegCommand command, String ffmpegBinary) {
        ArrayList<CommandPart> parts = new ArrayList<>();

        StringBuilder commandBuilder = new StringBuilder(ffmpegBinary)
                .append(progress ? " -progress pipe:1" : "")
                .append(" -y");

        parts.addAll(inputs);
        parts.addAll(graphs);
        if (!complexFilterGraph.isEmpty()) parts.add(complexFilterGraph);
        if (preset != null) parts.add(preset);
        parts.addAll(codecs);
        parts.addAll(bitrates);
        parts.addAll(options);

        for (CommandPart part : parts) {
            commandBuilder.append(" ").append(part.apply(command));
        }

        if (output != null) {
            commandBuilder.append(" ").append(output.getAbsolutePath());
        }
        return commandBuilder.toString().trim();
    }

    @Override
    public String apply(FfmpegCommand command) {
        return apply(command, "ffmpeg");
    }

    public static boolean systemFfmpeg() {
        String version = FfmpegManager.getFFmpegVersion("ffmpeg");
        return version != null;
    }
}
