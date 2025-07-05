package org.luke.gui.controls.scroll;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class VerticalScrollable extends StackPane {
    private final StackPane scrollBarCont;
    private final VerticalScrollBar sb;

    private Region content;

    public VerticalScrollable() {
        setAlignment(Pos.TOP_LEFT);
        setMinHeight(0);

        scrollBarCont = new StackPane();
        scrollBarCont.setAlignment(Pos.CENTER_RIGHT);

        sb = new VerticalScrollBar(15, 5);
        scrollBarCont.setPickOnBounds(false);
        scrollBarCont.getChildren().add(sb);

        sb.opacityProperty().bind(Bindings.when(hoverProperty().or(sb.pressedProperty())).then(1).otherwise(.4));
        getChildren().addAll(scrollBarCont);
    }

    public VerticalScrollBar getScrollBar() {
        return sb;
    }

    public void setContent(Region content) {
        this.content = content;
        getChildren().setAll(content, scrollBarCont);
        sb.install(this, content);

        content.addEventFilter(ScrollEvent.ANY, e -> {
            if(e.isControlDown()) return;
            setScrollY(getScrollY() - e.getDeltaY());
        });

        content.translateYProperty().bind(Bindings.createIntegerBinding(() ->
                content.getHeight() > getHeight() ? (int) (-sb.positionProperty().get() * (content.getHeight() - getHeight())) : 0,
                sb.positionProperty(), content.heightProperty(), heightProperty()));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        clip.yProperty().bind(content.translateYProperty().negate());

        content.setClip(clip);
    }

    public double getScrollY() {
        if(content == null) return -1;

        return -content.getTranslateY();
    }

    public void setScrollY(double y) {
        if (content == null) {
            return;
        }

        double contentHeight = content.getHeight();
        double viewportHeight = getHeight();

        if (contentHeight <= viewportHeight) {
            sb.positionProperty().set(0);
            return;
        }

        double maxScroll = contentHeight - viewportHeight;

        double clampedY = Math.max(0, Math.min(maxScroll, y));

        double newPosition = clampedY / maxScroll;

        sb.positionProperty().set(newPosition);
    }
}
