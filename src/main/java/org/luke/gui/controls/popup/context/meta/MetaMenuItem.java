package org.luke.gui.controls.popup.context.meta;

import java.util.function.Supplier;

public class MetaMenuItem {
    private final String text;
    private final String icon;
    private final Runnable action;
    private final Supplier<Boolean> enabled;

    public MetaMenuItem(String text, Runnable action) {
        this(text, "empty", action, null);
    }

    public MetaMenuItem(String text, Runnable action, Supplier<Boolean> enabled) {
        this(text, "empty", action, enabled);
    }

    public MetaMenuItem(String text, String icon, Runnable action) {
        this(text, icon, action, null);
    }

    public MetaMenuItem(String text, String icon, Runnable action, Supplier<Boolean> enabled) {
        this.text = text;
        this.action = action;
        this.icon = icon;
        this.enabled = enabled;
    }

    public String getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public Runnable getAction() {
        return action;
    }

    public Supplier<Boolean> getEnabled() {
        return enabled;
    }
}
