package org.luke.gui.controls.button;

import javafx.scene.layout.CornerRadii;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

public class ColoredButton extends Button implements Styleable {
    private StyledColor background;
    private StyledColor textFill;

    public ColoredButton(Window window, String key, double radius, double width, double height,
                         StyledColor background, StyledColor textFill) {
        super(window, key, radius, width, height);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
    }

    public ColoredButton(Window window, String key, CornerRadii radius, double width, double height,
                         StyledColor background, StyledColor textFill) {
        super(window, key, radius, width, height);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
    }

    public ColoredButton(Window window, String key,
                         StyledColor background, StyledColor textFill) {
        super(window, key);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
    }

    public ColoredButton(Window window, String key, double width,
                         StyledColor background, StyledColor textFill) {
        super(window, key, width);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
    }

    public ColoredButton(Window window, String string, double radius, double width,
                         StyledColor background, StyledColor textFill) {
        super(window, string, radius, width);
        this.background = background;
        this.textFill = textFill;
        applyStyle(window.getStyl());
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
