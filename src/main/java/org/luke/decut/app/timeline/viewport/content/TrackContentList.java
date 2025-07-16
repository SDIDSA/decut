package org.luke.decut.app.timeline.viewport.content;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.controls.scroll.VerticalScrollable;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

public class TrackContentList extends VerticalScrollable implements Styleable {
    private final Home owner;

    private final VBox root;

    public TrackContentList(Home owner) {
        this.owner = owner;

        root = new VBox();
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        StackPane.setAlignment(root, Pos.TOP_CENTER);

        Platform.runLater(() -> {
            owner.getTracks().getTracks().addListener((ListChangeListener<? super Track>) _ -> {
                root.getChildren().setAll(owner.getTracks().getTracks().stream().map(Track::getContent).toList());
            });
            prefHeightProperty().bind(owner.getTracks().getTrackList().heightProperty());
        });

        setContent(root);

        VBox.setVgrow(this, Priority.ALWAYS);

        applyStyle(owner.getWindow().getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        getScrollBar().setThumbFill(style.getTextMuted());
    }

    @Override
    protected void updateBounds() {
        try {
            super.updateBounds();
        } catch (Exception x) {
            Platform.runLater(() -> getChildren().clear());
            System.out.println("suspect : TrackContentList");
        }
    }
}
