package org.luke.decut.app.timeline.tracks;

import org.luke.decut.app.lib.assets.filter.AssetType;

import java.util.ArrayList;
import java.util.Collections;

public enum TrackType {
    AUDIO("speaker", AssetType.AUDIO),
    VIDEO("video-clip", AssetType.VIDEO),
    EFFECT("effect");

    private final String icon;
    private final ArrayList<AssetType> valid;

    TrackType(String icon, AssetType...types) {
        this.icon = icon;
        valid = new ArrayList<>();
        Collections.addAll(valid, types);
    }

    public boolean isValidType(AssetType type) {
        return valid.contains(type);
    }

    public String getIcon() {
        return icon;
    }
}
