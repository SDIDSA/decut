package org.luke.gui.controls.popup.context.meta;

public class MetaMenuMenu extends MetaMenuItem {
    private final MetaMenuItems items;
    public MetaMenuMenu(String text, Runnable action) {
        this(text, "empty", action);
    }
    public MetaMenuMenu(String text, String icon, Runnable action) {
        super(text, icon, action);
        items = new MetaMenuItems();
    }

    public MetaMenuMenu add(String text, Runnable action) {
        return add(text, "empty", action);
    }

    public MetaMenuMenu add(String text, String icon, Runnable action) {
        items.add(text, icon, action);
        return this;
    }

    public MetaMenuItems getItems() {
        return items;
    }
}
