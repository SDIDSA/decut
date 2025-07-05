package org.luke.decut.app.lib;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import org.luke.decut.app.home.Home;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.SplineInterpolator;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.controls.shape.Back;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class LibraryTab extends StackPane implements Styleable {
    private final Home owner;

    private final Class<? extends LibraryContent> content;

    private final BooleanProperty selected;

    private final ColorIcon icon;
    private final KeyedText title;

    protected Back back;

    public LibraryTab(Home owner, String titleStr, String iconFile, Class<? extends LibraryContent> content) {
        this.owner = owner;
        this.content = content;
        selected = new SimpleBooleanProperty(false);

        setCursor(Cursor.HAND);

        VBox root = new VBox();
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);
        root.setMouseTransparent(true);

        back = new Back();
        back.setFill(Color.TRANSPARENT);
        back.setStrokeType(StrokeType.INSIDE);

        back.radiusProperty().set(new CornerRadii(5));
        back.wProp().bind(widthProperty());
        back.hProp().bind(heightProperty());

        icon = new ColorIcon(iconFile, 24);
        title = new KeyedText(owner.getWindow(), titleStr, new Font(13));
        title.opacityProperty().unbind();

        icon.setTranslateY(10);
        title.setTranslateY(-10);
        title.setOpacity(0);

        root.setPadding(new Insets(10,5,10,5));
        root.getChildren().addAll(icon, title);

        setOnMouseEntered(_ -> {
            if(selected.get()) return;
            if(enter != null) enter.playFromStart();
        });

        setOnMouseExited(_ -> {
            if(selected.get()) return;
            if(exit != null) exit.playFromStart();
        });

        setOnMouseClicked(_ -> select());

        getChildren().addAll(back, root);

        TextTooltip.install(this, Direction.RIGHT, titleStr, 15, 0);

        applyStyle(owner.getWindow().getStyl());
    }

    public Class<? extends LibraryContent> getContent() {
        return content;
    }

    private static LibraryTab current;

    public void select() {
        if(selected.get()) return;
        selected.set(true);
        if(select != null) select.playFromStart();
        if(current != null) current.unselect();
        current = this;
        owner.loadLibraryContent(this);
    }

    private void unselect() {
        selected.set(false);
        if(exit != null) exit.playFromStart();
    }

    private Timeline enter;
    private Timeline exit;

    private Timeline select;

    @Override
    public void applyStyle(Style style) {
        double duration = 0.25;
        Interpolator interpolator = SplineInterpolator.OVERSHOOT;

        enter = new Timeline(
                new KeyFrame(Duration.seconds(duration),
                        new KeyValue(back.fillProperty(), style.getBackgroundTertiaryOr(), interpolator),
                        new KeyValue(icon.fillProperty(), style.getTextNormal(), interpolator),
                        new KeyValue(title.fillProperty(), style.getTextNormal(), interpolator)
                )
        );

        exit = new Timeline(
                new KeyFrame(Duration.seconds(duration),
                        new KeyValue(back.fillProperty(), style.getBackgroundTertiary(), interpolator),
                        new KeyValue(icon.fillProperty(), style.getTextMuted(), interpolator),
                        new KeyValue(title.fillProperty(), style.getTextMuted(), interpolator),
                        new KeyValue(icon.translateYProperty(), 10, interpolator),
                        new KeyValue(title.translateYProperty(), -10, interpolator),
                        new KeyValue(title.opacityProperty(), 0, interpolator)
                )
        );

        select = new Timeline(
                new KeyFrame(Duration.seconds(duration),
                        new KeyValue(back.fillProperty(), style.getTextNormal(), interpolator),
                        new KeyValue(icon.fillProperty(), style.getBackgroundTertiaryOr(), interpolator),
                        new KeyValue(title.fillProperty(), style.getBackgroundTertiaryOr(), interpolator),
                        new KeyValue(icon.translateYProperty(), 0, interpolator),
                        new KeyValue(title.translateYProperty(), 0, interpolator),
                        new KeyValue(title.opacityProperty(), 1, interpolator)
                )
        );

        back.setFill(selected.get() ? style.getTextNormal() : style.getBackgroundTertiary());
        icon.setFill(selected.get() ? style.getBackgroundTertiaryOr() : style.getTextMuted());
        title.setFill(selected.get() ? style.getBackgroundTertiaryOr() : style.getTextMuted());
    }
}
