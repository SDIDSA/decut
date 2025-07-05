package org.luke.gui.controls.image;

import javafx.scene.Cursor;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.luke.gui.controls.shape.Back;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

/**
 * a specialized ColorIcon that allows setting a {@link StyledColor} fill color.
 *
 * @author SDIDSA
 */
public class ColoredIcon extends ColorIcon implements Styleable {
    private final Window owner;
    private final Back back;
    private StyledColor fill;
    private StyledColor background;

    /**
     * Constructs a ColoredIcon with the specified window, image name, size, and styled fill color.
     *
     * @param owner The window associated with the ColoredIcon.
     * @param name  The name of the image file.
     * @param fill  The styled fill color applied to the icon.
     */
    public ColoredIcon(Window owner, String name, double readSize, double displaySize, StyledColor background, StyledColor fill) {
        super(name, readSize, displaySize);
        this.owner = owner;
        this.fill = fill;
        this.background = background;

        back = new Back();
        back.setFill(Color.TRANSPARENT);
        back.setStrokeType(StrokeType.INSIDE);

        back.wProp().bind(widthProperty());
        back.hProp().bind(heightProperty());

        getChildren().addFirst(back);

        applyStyle(owner.getStyl());
    }

    public ColoredIcon(Window window, String name, double size, StyledColor fill) {
        this(window, name, size, size, s -> Color.TRANSPARENT, fill);
    }

    public ColoredIcon(Window window, String name, double size, StyledColor background, StyledColor fill) {
        this(window, name, size, size, background, fill);
    }

    public ColoredIcon setOnAction(Runnable action) {
        super.setAction(action);
        setCursor(Cursor.HAND);
        return this;
    }

    public void setRadius(CornerRadii radius) {
        back.radiusProperty().set(radius);
    }

    public void setRadius(double radius) {
        back.radiusProperty().set(new CornerRadii(radius));
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(owner.getStyl().get());
    }

    public void setBackground(StyledColor background) {
        this.background = background;
        applyStyle(owner.getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        super.setFill(fill.apply(style));
        back.setFill(background.apply(style));
        super.applyStyle(style);
    }
}
