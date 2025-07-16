package org.luke.decut.ffprobe;

import org.luke.decut.cmd.Command;
import org.luke.decut.file.FileDealer;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.FfprobeManager;
import org.luke.decut.local.managers.FfprobeManager;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.gui.exception.ErrorHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FfprobeCommand {
    private File input;
    private final List<String> args = new ArrayList<>();
    private Consumer<String> onOutput;
    private Consumer<String> onError;
    private Process running;

    public FfprobeCommand setInput(File file) {
        this.input = file;
        return this;
    }

    public FfprobeCommand addArgument(String arg) {
        args.add(arg);
        return this;
    }

    public FfprobeCommand onOutput(Consumer<String> consumer) {
        this.onOutput = consumer;
        return this;
    }

    public FfprobeCommand onError(Consumer<String> consumer) {
        this.onError = consumer;
        return this;
    }

    public FfprobeCommand execute() {
        try {
            List<String> command = new ArrayList<>();
            command.add(getFfprobeBinary());
            command.addAll(args);
            command.add("-i");
            command.add(input.getAbsolutePath());

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);

            running = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(running.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (onOutput != null) onOutput.accept(line);
            }

            running.waitFor();

        } catch (Exception e) {
            ErrorHandler.handle(e, "executing ffprobe");
        }

        return this;
    }

    public String executeAndGetSingleLineOutput() {
        try {
            List<String> command = new ArrayList<>();
            command.add(getFfprobeBinary());
            command.addAll(args);
            command.add(input.getAbsolutePath());

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);

            running = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(running.getInputStream()));
            return reader.readLine();

        } catch (Exception e) {
            ErrorHandler.handle(e, "executing ffprobe");
            return null;
        }
    }

    public static String getFfprobeBinary() {
        String defStr = LocalStore.getDefaultFfprobe();
        if(defStr == null) {
            if(systemFfprobe()) {
                return "ffprobe";
            } else {
                return null;
            }
        }

        File ffprobeRoot = new File(defStr);
        if(!ffprobeRoot.exists() || !ffprobeRoot.isDirectory()) {
            if(systemFfprobe()) {
                return "ffprobe";
            } else {
                return null;
            }
        }

        LocalInstall ffprobe = FfprobeManager.versionOf(ffprobeRoot.getAbsolutePath());
        if(ffprobe == null) {
            return null;
        }
        return "\"" + ffprobe.getBinary().getAbsolutePath() + "\"";
    }

    public static boolean systemFfprobe() {
        return FfprobeManager.getFfprobeVersion("ffprobe") != null;
    }
}
