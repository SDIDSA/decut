package org.luke.decut.app.timeline;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.home.Resizer;
import org.luke.decut.app.timeline.tracks.Tracks;
import org.luke.decut.app.timeline.viewport.Viewport;
import org.luke.gui.style.Style;

public class Timeline extends HBox {
    private final Tracks tracks;
    private final Viewport viewPort;

    private double initHSplit;
    private final DoubleProperty hSplit;
    public Timeline(Home owner) {

        hSplit = new SimpleDoubleProperty(0.2);

        double thickness = 7;

        tracks = new Tracks(owner);

        viewPort = new Viewport(owner);

        tracks.setMinWidth(USE_PREF_SIZE);
        tracks.setMaxWidth(USE_PREF_SIZE);
        tracks.prefWidthProperty().bind(widthProperty().subtract(thickness).multiply(hSplit));
        tracks.setMinHeight(USE_PREF_SIZE);
        tracks.setMaxHeight(USE_PREF_SIZE);
        tracks.prefHeightProperty().bind(heightProperty());

        viewPort.setMinWidth(USE_PREF_SIZE);
        viewPort.setMaxWidth(USE_PREF_SIZE);
        viewPort.prefWidthProperty().bind(widthProperty().subtract(thickness).multiply(hSplit.negate().add(1)));
        viewPort.setMinHeight(USE_PREF_SIZE);
        viewPort.setMaxHeight(USE_PREF_SIZE);
        viewPort.prefHeightProperty().bind(heightProperty());

        Resizer hResizer = new Resizer(owner, Orientation.VERTICAL, thickness);
        hResizer.setFill(Style::getNothing);
        hResizer.setOnInit(() -> initHSplit = hSplit.get());
        hResizer.setOnDrag((byPx) -> {
            double newVal = initHSplit + byPx / getWidth();
            hSplit.set(Math.min(0.4, Math.max(0.1, newVal)));
        });

        getChildren().addAll(tracks, hResizer ,viewPort);

    }

    public Viewport getViewPort() {
        return viewPort;
    }

    public DoubleProperty ppsProperty() {
        return viewPort.ppsProperty();
    }

    public DoubleProperty atProperty() {
        return viewPort.atProperty();
    }

    public Tracks getTracks() {
        return tracks;
    }
}
