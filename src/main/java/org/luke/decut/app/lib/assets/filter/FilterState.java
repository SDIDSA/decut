package org.luke.decut.app.lib.assets.filter;

import org.luke.decut.app.lib.assets.display.ExtensionFilters;
import org.luke.gui.controls.popup.context.items.CheckMenuItem;

import java.io.File;
import java.util.*;

public class FilterState {
    private final HashMap<AssetType, CheckMenuItem> types;

    public FilterState(Map<AssetType, CheckMenuItem> types) {
        this.types = new HashMap<>();
        this.types.putAll(types);
    }

    public FilterState() {
        this.types = new HashMap<>();
    }

    public Collection<AssetType> getTypes() {
        return types.keySet();
    }

    public Map<AssetType, CheckMenuItem> getMap() {
        return types;
    }

    public boolean isPresent(AssetType type) {
        return types.containsKey(type);
    }

    public boolean isPresent(File file) {
        return types.containsKey(ExtensionFilters.typeOf(file));
    }

    public boolean isPresent(CheckMenuItem check) {
        return types.containsValue(check);
    }

    public void add(AssetType type, CheckMenuItem check) {
        types.put(type, check);
    }

    public void addAll(Map<AssetType, CheckMenuItem> types) {
        this.types.putAll(types);
    }

    public void setAll(Map<AssetType, CheckMenuItem> types) {
        this.types.clear();
        this.types.putAll(types);
    }

    public void remove(AssetType type) {
        types.remove(type);
    }
}
