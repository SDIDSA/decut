package org.luke.decut.app.lib.assets.data;

import java.io.File;
import java.util.HashMap;

public class SubVideoAssetData extends VideoAssetData {
    private static final HashMap<File, SubVideoAssetData> cache = new HashMap<>();

    public static SubVideoAssetData getData(File file, VideoAssetData parent) {
        SubVideoAssetData found = cache.get(file);
        if(found == null) {
            found = new SubVideoAssetData(file, parent);
            cache.put(file, found);
        }

        if(file.lastModified() > found.getUpdated()) {
            found.fetch();
        }

        return found;
    }

    private final VideoAssetData parent;

    SubVideoAssetData(File file, VideoAssetData parent) {
        super(file);
        this.parent = parent;
        fetch();
    }

    public void fetch() {
        if(parent == null) return;
        super.fetch(false);
        duration = parent.duration;
        resolution = parent.resolution;
        thumb = parent.thumb;
    }

    @Override
    public AssetData getParent() {
        return parent;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

}
