package org.luke.decut.app.home.menubar;

import javafx.stage.FileChooser;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.file.DecutProject;
import org.luke.decut.file.FileDealer;
import org.luke.gui.controls.popup.context.meta.MetaMenuItem;
import org.luke.gui.controls.popup.context.meta.MetaMenuMenu;
import org.luke.gui.threading.Platform;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileMenu extends HomeMenuButton {
    public FileMenu(Home owner) {
        super(owner, "File");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Decut Project", "*.dcp"));

        addItem(new MetaMenuMenu("New", "new", null)
                    .add("Project", "file", null)
        );
        addItem(new MetaMenuItem("Open...", "folder", () -> {
            File open = fc.showOpenDialog(owner.getWindow());
            if(open != null) {
                DecutProject proj = new DecutProject();
                proj.deserialize(new JSONObject(FileDealer.read(open)));
                owner.load(proj);
            }
        }));
        separate();
        addItem(new MetaMenuItem("Import media", "import", null));
        separate();
        addItem(new MetaMenuItem("Save", "save", null));
        addItem(new MetaMenuItem("Save as...", () -> {
            File saveTo = fc.showSaveDialog(owner.getWindow());
            if(saveTo != null) {
                FileDealer.write(owner.save().serialize().toString(), saveTo);
            }
        }));

        FileChooser exportAs = new FileChooser();
        addItem(new MetaMenuItem("Export", "export", () -> {
            File saveTo = exportAs.showSaveDialog(owner.getWindow());
            if(saveTo != null) {
                FfmpegCommand com = owner.render(saveTo);
                com.addHandler(new ProgressHandler()
                        .addHandler(pi -> {
                            System.out.println(pi.getProgress());
                        }));
                Platform.runBack(() -> {
                    com.execute().waitFor();
                    try {
                        Desktop.getDesktop().open(saveTo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }));
        separate();
        addItem(new MetaMenuItem("Exit", "exit", null));
    }
}
