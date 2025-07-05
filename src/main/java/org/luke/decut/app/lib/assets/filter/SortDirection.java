package org.luke.decut.app.lib.assets.filter;

public enum SortDirection {
    ASCENDING("Asc", "sort-asc"),
    DESCENDING("Desc", "sort-desc");

    private final String name;
    private final String icon;

    SortDirection(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public SortDirection reverse() {
        return this == ASCENDING ? DESCENDING : ASCENDING;
    }

    public SortDirection byName(String name) {
        for (SortDirection value : values()) {
            if(value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}