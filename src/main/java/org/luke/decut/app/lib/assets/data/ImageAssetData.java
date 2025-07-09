package org.luke.decut.app.lib.assets.data;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.options.VFrames;
import org.luke.gui.controls.image.ImageProxy;
import org.luke.gui.exception.ErrorHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ImageAssetData extends AssetData {
    private static final HashMap<File, ImageAssetData> cache = new HashMap<>();
    private Image image;

    ImageAssetData(File file) {
        super(file, AssetType.IMAGE);
        fetch();
    }

    public static ImageAssetData getData(File file) {
        ImageAssetData found = cache.get(file);
        if (found == null) {
            found = new ImageAssetData(file);
            cache.put(file, found);
        }

        if (file.lastModified() > found.getUpdated()) {
            found.fetch();
        }

        return found;
    }

    public void fetch() {
        super.fetch();
        new FfmpegCommand()
                .addInput(getFile())
                .addOption(new VFrames(1))
                .setOnOutput(file -> {
                            try (InputStream fis = new FileInputStream(file)) {
                                image = new Image(fis);
                                resolution = new Dimension2D(image.getWidth(), image.getHeight());
                                thumb = ImageProxy.resize(ImageUtils.cropCenter(image), 128);
                                //Files.delete(file.toPath());
                            } catch (IOException x) {
                                ErrorHandler.handle(x, "generate video thumbnail");
                            }
                        },
                        ".png")
                .execute()
                .waitFor();
    }

    public String getName() {
        return getFile().getName();
    }

    public Image getImage() {
        return image;
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
