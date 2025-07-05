package org.luke.decut.app.lib.assets.data;

import java.io.File;
import java.util.HashMap;

public class SubAudioAssetData extends AudioAssetData {
    private static final HashMap<File, SubAudioAssetData> cache = new HashMap<>();

    public static SubAudioAssetData getData(File file, VideoAssetData parent) {
        SubAudioAssetData found = cache.get(file);
        if(found == null) {
            found = new SubAudioAssetData(file, parent);
            cache.put(file, found);
        }

        if(file.lastModified() > found.getUpdated()) {
            found.fetch();
        }

        return found;
    }

    private final VideoAssetData parent;

    SubAudioAssetData(File file, VideoAssetData parent) {
        super(file);
        this.parent = parent;
        fetch();
    }

    public VideoAssetData getParent() {
        return parent;
    }

    @Override
    public void fetch() {
        if(parent == null) return;
        super.fetch(false);
        duration = parent.duration;
    }
}
