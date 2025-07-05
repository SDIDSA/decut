package org.luke.gui.controls.loading;

import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

public class ColoredLoading extends Loading implements Styleable {
    private final Window owner;
    private StyledColor fill;

    public ColoredLoading(Window owner, double size, StyledColor fill) {
        super(size);
        this.owner = owner;
        this.fill = fill;
        applyStyle(owner.getStyl());
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(owner.getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        setFill(fill.apply(style));
    }
}
