package org.luke.decut.app.home.menubar.edit;

import org.luke.decut.app.Decut;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.FfmpegManager;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.gui.controls.alert.Alert;
import org.luke.gui.controls.alert.AlertType;
import org.luke.gui.controls.alert.ButtonType;
import org.luke.gui.threading.Platform;

import java.io.File;

public class DecutAction {
    private final String name;
    private final Runnable perform;
    private final Runnable undo;
    private final boolean ffmpeg;

    public DecutAction(String name, Runnable perform, Runnable undo, boolean ffmpeg) {
        this.name = name;
        this.perform = perform;
        this.undo = undo;
        this.ffmpeg = ffmpeg;
    }

    public DecutAction(String name, Runnable perform, Runnable undo) {
        this(name, perform, undo, false);
    }

    public String getName() {
        return name;
    }

    public boolean isFfmpeg() {
        return ffmpeg;
    }

    public void perform() {
        if(isFfmpeg() && FfmpegCommand.getFfmpegBinary() == null) {
            Platform.runAfter(() -> {
                Alert alert = new Alert(Decut.instance.getHome(), AlertType.CONFIRM);
                alert.setHead("Ffmpeg not configured");
                alert.addLabel("Decut relies on ffmpeg for media processing and couldn't detect it on your system, " +
                        "do you want to configure it now ?");
                alert.addAction(ButtonType.YES, () -> {
                    Decut.instance.openFfmpegConfig();
                    alert.hide();
                });
                alert.showAndWait();
                LocalInstall version = FfmpegManager.versionFromDir(new File(LocalStore.getDefaultFfmpeg()));
                if(version != null) {
                    if(perform != null) perform.run();
                }
            }, 100);
        } else {
            if(perform != null) perform.run();
        }
    }

    public void undo() {
        if(undo != null) undo.run();
    }
}
