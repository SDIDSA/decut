package org.luke.gui.controls.button;

import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.window.Window;

public class IconButton extends AbstractButton {
    private final ColorIcon icon;

    public IconButton(Window window, CornerRadii radius, double height, double width, String iconFile, double size) {
        super(window, radius, height, width);

        icon = new ColorIcon(iconFile, size);
        icon.opacityProperty().bind(back.opacityProperty());

        add(icon);

        applyStyle(window.getStyl());
    }

    public void setIcon(String icon) {
        this.icon.setImage(icon);
    }

    public IconButton(Window window, double radius, double height, double width, String iconFile, double size) {
        this(window, new CornerRadii(radius), height, width, iconFile, size);
    }

    public IconButton(Window window, double radius, double height, String iconFile, double size) {
        this(window, new CornerRadii(radius), height, DEFAULT_WIDTH, iconFile, size);
    }

    public IconButton(Window window, CornerRadii radius, double height, String iconFile, double size) {
        this(window, radius, height, DEFAULT_WIDTH, iconFile, size);
    }

    public IconButton(Window window, String iconFile, double size) {
        this(window, new CornerRadii(DEFAULT_RADIUS), DEFAULT_HEIGHT, DEFAULT_WIDTH, iconFile, size);
    }

    public IconButton(Window window, double radius, String iconFile, double size) {
        this(window, new CornerRadii(radius), DEFAULT_HEIGHT, DEFAULT_WIDTH, iconFile, size);
    }

    @Override
    public void setTextFill(Paint fill) {
        super.setTextFill(fill);
        icon.setFill(fill);
    }
}
