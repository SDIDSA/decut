package org.luke.decut.app.lib.assets.filter;

public enum AssetType {
    VIDEO("Video", "video"),
    AUDIO("Audio", "audio"),
    IMAGE("Image", "image");

    private final String name;
    private final String icon;

    AssetType(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
