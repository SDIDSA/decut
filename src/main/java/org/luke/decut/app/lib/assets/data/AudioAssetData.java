package org.luke.decut.app.lib.assets.data;

import javafx.scene.image.Image;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.filter_complex.ShowWavesPic;
import org.luke.decut.ffmpeg.filter_complex.audio.AFormat;
import org.luke.decut.ffmpeg.filter_complex.audio.ATrimC;
import org.luke.decut.ffmpeg.filter_complex.audio.Compand;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AudioAssetData extends AssetData {
    private static final HashMap<File, AudioAssetData> cache = new HashMap<>();

    AudioAssetData(File file) {
        super(file, AssetType.AUDIO);
        fetch();
    }

    public static AudioAssetData byName(File file) {
        for(Map.Entry<File, AudioAssetData> entry : cache.entrySet()) {
            if(file.getName().equals(entry.getKey().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static AudioAssetData getData(File file) {
        AudioAssetData found = file.exists() ? cache.get(file) : byName(file);
        if (found == null) {
            found = new AudioAssetData(file);
            cache.put(file, found);
        }

        if (file.lastModified() > found.getUpdated()) {
            found.fetch();
        }

        return found;
    }

    public void fetch() {
        fetch(true);
    }

    public void fetch(boolean parent) {
        super.fetch();
        if (parent)
            thumb = generateWaveform(128, 128);
    }

    public Image generateWaveform(int width, int height) {
        AtomicReference<Image> res = new AtomicReference<>();
        new FfmpegCommand()
                .addInput(getFile())
                .addComplexFilter(new AFormat()
                        .setMono())
                .addComplexFilter(new Compand())
                .addComplexFilter(new ShowWavesPic()
                        .setSize(width, height))
                .addHandler(new ProgressHandler()
                        .addHandler(pi -> setDuration(pi.duration())))
                .setOnOutput(file -> {
                    try (InputStream fis = new FileInputStream(file)) {
                        res.set(new Image(fis));
                        //Files.delete(file.toPath());
                    } catch (IOException x) {
                        ErrorHandler.handle(x, "generate video thumbnail");
                    }
                }, ".png")
                .execute();
        Platform.waitWhile(() -> res.get() == null);
        return res.get();
    }

    public Image generateWaveform(int width, int height, double start, double duration) {
        AtomicReference<Image> result = new AtomicReference<>();

        new FfmpegCommand()
                .addInput(getFile())
                .addComplexFilter(new AFormat().setMono())
                .addComplexFilter(new Compand())
                .addComplexFilter(new ATrimC(start, duration))
                .addComplexFilter(new ShowWavesPic().setSize(width, height))
                .setOnOutputStream(fis -> result.set(new Image(fis)), ".png")
                .execute();

        Platform.waitWhile(() -> result.get() == null);
        return result.get();
    }

    public String getName() {
        return getFile().getName();
    }

    @Override
    public AssetData getParent() {
        return null;
    }

    @Override
    public boolean hasParent() {
        return false;
    }
}
