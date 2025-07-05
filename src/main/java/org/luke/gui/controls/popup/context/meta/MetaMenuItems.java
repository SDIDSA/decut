package org.luke.gui.controls.popup.context.meta;

import java.util.ArrayList;

public class MetaMenuItems {
    private final ArrayList<MetaMenuItem> items;

    public MetaMenuItems() {
        items = new ArrayList<>();
    }

    public MetaMenuItems add(String text, Runnable action) {
        return add(text, "empty", action);
    }

    public MetaMenuItems add(String text, String icon, Runnable action) {
        items.add(new MetaMenuItem(text, icon, action));
        return this;
    }

    public ArrayList<MetaMenuItem> getItems() {
        return items;
    }
}
