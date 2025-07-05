package org.luke.gui.controls.text.unkeyed;

import org.luke.gui.controls.Font;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

public class ColoredLabel extends Label implements Styleable {
    private final Window window;
    private StyledColor fill;

    public ColoredLabel(Window window, String key, Font font, StyledColor fill) {
        super(key, font);
        this.window = window;
        this.fill = fill;
        applyStyle(window.getStyl());
    }

    public ColoredLabel(Window window, String key, StyledColor fill) {
        this(window, key, new Font(Font.DEFAULT_SIZE), fill);
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(window.getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        setFill(fill.apply(style));
    }
}
