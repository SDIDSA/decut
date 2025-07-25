package org.luke.decut.ffprobe;

import org.luke.decut.cmd.Command;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.FfprobeManager;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FfprobeCommand {
    private static String binary;

    private final List<String> args = new ArrayList<>();
    private File input;
    private Consumer<String> onOutput;
    private Consumer<String> onError;
    private Process running;

    public static void resetBinary() {
        binary = getFfprobeBinary();
    }

    public static String getFfprobeBinary() {
        String defStr = LocalStore.getDefaultFfprobe();
        if (defStr == null) {
            if (systemFfprobe()) {
                return "ffprobe";
            } else {
                return null;
            }
        }

        File ffprobeRoot = new File(defStr);
        if (!ffprobeRoot.exists() || !ffprobeRoot.isDirectory()) {
            if (systemFfprobe()) {
                return "ffprobe";
            } else {
                return null;
            }
        }

        LocalInstall ffprobe = FfprobeManager.versionOf(ffprobeRoot.getAbsolutePath());
        if (ffprobe == null) {
            return null;
        }
        return ffprobe.getBinary().getAbsolutePath();
    }

    public static boolean systemFfprobe() {
        return FfprobeManager.getFfprobeVersion("ffprobe") != null;
    }

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
        List<String> command = new ArrayList<>();
        command.add(binary);
        command.addAll(args);
        command.add("-i");
        command.add(input.getAbsolutePath());

        Command com = new Command(onOutput, onError, command.toArray(new String[0]));
        running = com.execute();
        return this;
    }

    public int getExitCode() {
        return running.exitValue();
    }

    public FfprobeCommand waitFor() {
        Platform.waitWhile(() -> running == null, 10000);
        if(running == null) {
            System.out.println(binary + " " + String.join(" ", args) + " -i " + input);
            System.out.println("\texecution failed, retrying...");
            return execute().waitFor();
        }
        if (running.isAlive()) {
            try {
                running.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
}
