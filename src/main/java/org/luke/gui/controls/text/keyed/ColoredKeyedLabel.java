package org.luke.gui.controls.text.keyed;

import org.luke.gui.controls.Font;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

public class ColoredKeyedLabel extends KeyedLabel implements Styleable {
    private StyledColor fill;

    public ColoredKeyedLabel(Window window, String key, Font font, StyledColor fill) {
        super(window, key, font);
        this.fill = fill;
        applyStyle(window.getStyl());
    }

    public ColoredKeyedLabel(Window window, String key, StyledColor fill) {
        this(window, key, new Font(Font.DEFAULT_SIZE), fill);
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(getWindow().getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        setFill(fill.apply(style));
    }
}
