package org.luke.decut.app.timeline.controls;

import javafx.animation.Interpolator;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.luke.decut.app.home.Home;
import org.luke.gui.controls.SplineInterpolator;
import org.luke.gui.controls.input.slider.EasedSlider;

public class ZoomControls extends HBox {
    private final EasedSlider slider;
    public ZoomControls(Home owner) {
        setAlignment(Pos.CENTER);
        setSpacing(5);


        TimelineButton zoomIn = new TimelineButton(owner.getWindow(),
                "plus", "Zoom In");
        TimelineButton zoomOut = new TimelineButton(owner.getWindow(),
                "minus", "Zoom Out");

        slider = new EasedSlider(owner.getWindow(), 12, 0, 1, SplineInterpolator.EASE_IN);
        slider.setPrefWidth(120);

        getChildren().addAll(zoomOut, slider, zoomIn);
    }

    public DoubleProperty timeScale() {
        return slider.valueProperty();
    }

    public DoubleProperty source() {
        return slider.source();
    }
}
