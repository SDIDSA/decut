package org.luke.decut.app.lib.assets.filter;

public enum AssetDisplayMode {
    LIST("List", "list"),
    GRID("Grid", "grid");

    private final String name;
    private final String icon;

    AssetDisplayMode(String name, String icon) {
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
