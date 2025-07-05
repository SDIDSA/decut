package org.luke.gui.style;

public interface SelectableStyleable extends Styleable {
    boolean isSelected();
    void applyStyle(Style style, boolean selected);
    default void applyStyle(Style style) {
        applyStyle(style, isSelected());
    }
}
