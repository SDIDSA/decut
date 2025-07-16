package org.luke.decut.app.home.menubar;

import javafx.stage.FileChooser;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.file.project.DecutProject;
import org.luke.decut.file.FileDealer;
import org.luke.gui.controls.popup.context.meta.MetaMenuItem;
import org.luke.gui.controls.popup.context.meta.MetaMenuMenu;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.file.ZipUtils;
import org.luke.gui.threading.Platform;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMenu extends HomeMenuButton {
    public FileMenu(Home owner) {
        super(owner, "File");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Decut Project", "*.dcx"));

        FileChooser zfc = new FileChooser();
        zfc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zipped Decut Project", "*.zdc"));

        addItem(new MetaMenuMenu("New", "new", null)
                    .add("Project", "file", null)
        );

        addItem(new MetaMenuItem("Open...", "folder", () -> {
            File open = fc.showOpenDialog(owner.getWindow());
            if(open != null) {
                owner.load(open);
            }
        }));

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
        addItem(new MetaMenuItem("Zip project", "zip", () -> {
            File saveTo = zfc.showSaveDialog(owner.getWindow());
            if(saveTo != null) {
                try {
                    File root = Files.createTempDirectory("decut_zip").toFile();
                    owner.zip(root);
                    File projFile = new File(root, "project.dcx");
                    FileDealer.write(owner.save().serialize().toString(), projFile);
                    ZipUtils.zipFolder(root, saveTo);
                } catch (IOException e) {
                    ErrorHandler.handle(e, "zip project");
                }
            }
        }));
        addItem(new MetaMenuItem("Open Zipped project", "open-zip", () -> {
            File open = zfc.showOpenDialog(owner.getWindow());
            if(open != null) {
                try {
                    File root = Files.createTempDirectory("decut_zip").toFile();
                    ZipUtils.unzipWithProgress(open, root, p -> {
                        System.out.println("unzipping : " + p);
                    });
                    File proj = new File(root, "project.dcx");
                    owner.load(proj);
                } catch (IOException e) {
                    ErrorHandler.handle(e, "unzip project");
                }
            }
        }));
        separate();
        addItem(new MetaMenuItem("Exit", "exit", null));
    }
}
