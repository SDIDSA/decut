package org.luke.gui.controls.button;

import javafx.scene.layout.CornerRadii;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

public class ColoredIconButton extends IconButton implements Styleable {
    private StyledColor background;
    private StyledColor textFill;

    public ColoredIconButton(Window window, CornerRadii radius, double height, double width,
                             String icon, double size,
                             StyledColor background, StyledColor textFill) {
        super(window, radius, height, width, icon, size);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
    }

    public ColoredIconButton(Window window, double radius, double height, double width, String iconFile,
                             double size, StyledColor background, StyledColor textFill) {
        this(window, new CornerRadii(radius), height, width, iconFile, size, background, textFill);
    }

    public ColoredIconButton(Window window, double radius, double height, String iconFile,
                             double size, StyledColor background, StyledColor textFill) {
        this(window, new CornerRadii(radius), height, DEFAULT_WIDTH, iconFile, size, background, textFill);
    }

    public ColoredIconButton(Window window, CornerRadii radius, double height, String iconFile,
                             double size, StyledColor background, StyledColor textFill) {
        this(window, radius, height, DEFAULT_WIDTH, iconFile, size, background, textFill);
    }

    public ColoredIconButton(Window window, String iconFile, double size, StyledColor background, StyledColor textFill) {
        this(window, new CornerRadii(DEFAULT_RADIUS), DEFAULT_HEIGHT, DEFAULT_WIDTH, iconFile, size, background, textFill);
    }

    public ColoredIconButton(Window window, double radius, String iconFile, double size, StyledColor background, StyledColor textFill) {
        this(window, new CornerRadii(radius), DEFAULT_HEIGHT, DEFAULT_WIDTH, iconFile, size, background, textFill);
    }

    public void setBackground(StyledColor background) {
        this.background = background;
        applyStyle(getWindow().getStyl().get());
    }

    public void setTextFill(StyledColor textFill) {
        this.textFill = textFill;
        applyStyle(getWindow().getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        if(background == null || textFill == null) return;
        setFill(background.apply(style));
        setTextFill(textFill.apply(style));
        super.applyStyle(style);
    }
}
