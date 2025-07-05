package org.luke.decut.app.preview;

import javafx.geometry.Pos;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.keyed.ColoredKeyedText;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class Preview extends VBox implements Styleable {
    public Preview(Home owner) {

        setAlignment(Pos.CENTER);

        getChildren().add(new ColoredKeyedText(owner.getWindow(), "Preview Here...", new Font(18), Style::getTextNormal));



        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        applyStyle(owner.getWindow().getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), new CornerRadii(2, 2,
                2, 2, false)));
    }
}
