package org.luke.gui.controls.scroll;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class HorizontalScrollable extends StackPane {
    private final StackPane scrollBarCont;
    private final HorizontalScrollBar sb;

    private Region content;

    public HorizontalScrollable() {
        setAlignment(Pos.TOP_LEFT);
        setMinWidth(0);

        scrollBarCont = new StackPane();
        scrollBarCont.setAlignment(Pos.BOTTOM_CENTER);

        sb = new HorizontalScrollBar(15, 5);
        scrollBarCont.setPickOnBounds(false);
        scrollBarCont.getChildren().add(sb);

        sb.opacityProperty().bind(Bindings.when(hoverProperty().or(sb.pressedProperty())).then(1).otherwise(.4));
        getChildren().addAll(scrollBarCont);
    }

    public HorizontalScrollBar getScrollBar() {
        return sb;
    }

    private DoubleBinding scrollXProperty;
    public void setContent(Region content) {
        this.content = content;
        getChildren().setAll(content, scrollBarCont);
        sb.install(this, content);

        scrollXProperty = content.translateXProperty().negate();

        content.addEventFilter(ScrollEvent.ANY, e -> {
            if(e.isControlDown()) return;
            setScrollX(getScrollX() - e.getDeltaX());
        });

        content.translateXProperty().bind(Bindings.createIntegerBinding(() ->
                        content.getWidth() > getWidth() ? (int) (-sb.positionProperty().get() * (content.getWidth() - getWidth())) : 0,
                sb.positionProperty(), content.widthProperty(), widthProperty()));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        clip.xProperty().bind(content.translateXProperty().negate());

        content.setClip(clip);
    }

    public double getScrollX() {
        if(content == null) return -1;

        return -content.getTranslateX();
    }

    public DoubleBinding scrollXProperty() {
        if(scrollXProperty == null) throw new IllegalStateException();
        return scrollXProperty;
    }

    public void setScrollX(double x) {
        setScrollX(x, content.getWidth());
    }

    public void setScrollX(double x, double contentWidth) {
        if (content == null) {
            return;
        }

        double viewportWidth = getWidth();

        if (contentWidth <= viewportWidth) {
            sb.positionProperty().set(0);
            return;
        }

        double maxScroll = contentWidth - viewportWidth;

        double clampedX = Math.max(0, Math.min(maxScroll, x));

        double newPosition = clampedX / maxScroll;

        sb.positionProperty().set(newPosition);
    }
}