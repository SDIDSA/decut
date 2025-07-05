package org.luke.gui.controls.image;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.luke.gui.style.ColorItem;

public class ColorImageView extends StackPane implements ColorItem {
    private final ImageView view;
    private final Rectangle overlay;
    private final Rectangle clip;

    public ColorImageView(Image image) {
        view = new ImageView(image);
        overlay = new Rectangle();
        overlay.setClip(view);

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        view.setPreserveRatio(false);
        view.fitWidthProperty().bind(widthProperty());
        view.fitHeightProperty().bind(heightProperty());

        overlay.widthProperty().bind(widthProperty());
        overlay.heightProperty().bind(heightProperty());

        clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        getChildren().addAll(overlay);
    }

    public void setColored(boolean colored) {
        getChildren().clear();
        overlay.setClip(null);
        overlay.setClip(colored ? view : null);
        getChildren().setAll(colored ? overlay : view);
    }

    public void round(double radius) {
        if(radius == -1) {
            clip.arcHeightProperty().bind(this.widthProperty());
            clip.arcWidthProperty().bind(this.heightProperty());
        } else {
            clip.setArcHeight(radius * 2);
            clip.setArcWidth(radius * 2);
        }
    }

    public void setImage(Image image) {
        view.setImage(image);
    }

    @Override
    public void setFill(Paint fill) {
        overlay.setFill(fill);
    }

    @Override
    public Node getNode() {
        return this;
    }
}
