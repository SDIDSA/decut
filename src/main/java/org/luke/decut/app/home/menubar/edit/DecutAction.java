package org.luke.decut.app.home.menubar.edit;

import org.luke.decut.app.Decut;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffprobe.FfprobeCommand;
import org.luke.gui.controls.alert.Alert;
import org.luke.gui.controls.alert.AlertType;
import org.luke.gui.controls.alert.ButtonType;
import org.luke.gui.threading.Platform;

import java.util.function.BooleanSupplier;

public class DecutAction {
    private final String name;
    private final Runnable perform;
    private final Runnable undo;
    private final boolean ffmpeg;
    private final boolean ffprobe;

    public DecutAction(String name, Runnable perform, Runnable undo, boolean ffmpeg, boolean ffprobe) {
        this.name = name;
        this.perform = perform;
        this.undo = undo;
        this.ffmpeg = ffmpeg;
        this.ffprobe = ffprobe;
    }

    public DecutAction(String name, Runnable perform, Runnable undo) {
        this(name, perform, undo, false, false);
    }

    public String getName() {
        return name;
    }

    public boolean isFfmpeg() {
        return ffmpeg;
    }

    public boolean isFfprobe() {
        return ffprobe;
    }

    public void perform() {
        if (isFfmpeg() && FfmpegCommand.getFfmpegBinary() == null) {
            configure(
                    "Ffmpeg",
                    Decut.instance::openFfmpegConfig,
                    () -> FfmpegCommand.getFfmpegBinary() != null);
        } else if(isFfprobe() && FfprobeCommand.getFfprobeBinary() == null) {
            configure(
                    "Ffprobe",
                    Decut.instance::openFfprobeConfig,
                    () -> FfprobeCommand.getFfprobeBinary() != null);
        } else {
            if (perform != null) perform.run();
        }
    }

    private void configure(String tool, Runnable openConfig, BooleanSupplier check) {
        Platform.runAfter(() -> {
            Alert alert = new Alert(Decut.instance.getHome(), AlertType.CONFIRM);
            alert.setHead(tool + " not configured");
            alert.addLabel("Decut relies on " + tool.toLowerCase() + " for media processing and couldn't detect it on your system, " +
                    "do you want to configure it now ?");
            alert.addAction(ButtonType.YES, () -> {
                openConfig.run();
                alert.hide();
            });
            alert.showAndWait();
            perform();
        }, 100);

    }

    public void undo() {
        if (undo != null) undo.run();
    }
}
