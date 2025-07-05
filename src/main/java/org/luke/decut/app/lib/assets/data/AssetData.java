package org.luke.decut.app.lib.assets.data;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.app.lib.assets.display.ExtensionFilters;

import java.io.File;
import java.util.Date;

public class AssetData {
    protected final File file;
    protected final AssetType type;
    protected long updated;
    protected long duration = -1;
    protected Dimension2D resolution;
    protected Image thumb;

    public static AssetData getData(File file) {
        return switch (ExtensionFilters.typeOf(file)) {
            case VIDEO -> VideoAssetData.getData(file);
            case AUDIO -> AudioAssetData.getData(file);
            default -> ImageAssetData.getData(file);
        };
    }

    public AssetData(File file, AssetType type) {
        this.file = file;
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public AssetType getType() {
        return type;
    }

    public void fetch() {
        updated = new Date().getTime();
    }

    public long getUpdated() {
        return updated;
    }

    public Image getThumb() {
        return thumb;
    }

    public long getDuration() {
        return duration;
    }

    public double getDurationSeconds() {
        return duration / 1000.0;
    }

    public Dimension2D getResolution() {
        return resolution;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isThumbColored() {
        return this instanceof AudioAssetData;
    }
}
