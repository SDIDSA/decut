package org.luke.gui.controls.input.slider;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

/**
 * SliderControl is a JavaFX control that represents a stylized slider. It
 * consists of a track and a thumb that can be dragged to select a value within a range.
 *
 * @author SDIDSA
 */
public class Slider extends Pane implements Styleable {
    protected final DoubleProperty value;
    protected final DoubleProperty minValue;
    protected final DoubleProperty maxValue;

    protected double initX;
    protected double initV;

    private final BooleanProperty inverted;

    private final Rectangle track;
    private final Rectangle trackFill;
    protected final Circle thumb;

    protected final double size;

    /**
     * Constructs a SliderControl instance with the specified window, size, min value, and max value.
     * The 'size' parameter here will primarily dictate the thickness of the slider track and the thumb's radius.
     *
     * @param window   The associated Window for styling.
     * @param size     The visual size factor of the slider elements (e.g., track thickness, thumb radius).
     * @param minValue The minimum value of the slider.
     * @param maxValue The maximum value of the slider.
     */
    public Slider(Window window, double size, double minValue, double maxValue) {
        this.size = size;
        this.minValue = new SimpleDoubleProperty(minValue);
        this.maxValue = new SimpleDoubleProperty(maxValue);
        this.value = new SimpleDoubleProperty(minValue);
        inverted = new SimpleBooleanProperty(false);

        double trackHeight = size / 3;

        setPadding(new Insets(3, 0, 3, 0));

        track = new Rectangle();
        track.setHeight(trackHeight);
        track.setOpacity(0.4);
        track.widthProperty().bind(widthProperty());
        track.setArcHeight(trackHeight);
        track.setArcWidth(trackHeight);
        track.setStrokeWidth(0);

        trackFill = new Rectangle();
        trackFill.setHeight(trackHeight);
        trackFill.setArcHeight(trackHeight);
        trackFill.setArcWidth(trackHeight);
        trackFill.setStrokeWidth(0);

        thumb = new Circle(size / 2);
        trackFill.widthProperty().bind(thumb.layoutXProperty());

        thumb.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double width = getWidth() - size;
            if (width == 0) return size / 2;

            double range = this.maxValue.get() - this.minValue.get();
            if (range == 0) return size / 2;

            double normalizedValue = (this.value.get() - this.minValue.get()) / range;
            return (normalizedValue * width) + size / 2;
        }, widthProperty(), this.value, this.minValue, this.maxValue));

        setOnMousePressed(e -> {
            initX = e.getX();
            initV = value.get();
        });

        setOnMouseDragged(e -> {
            double mouseX = e.getX();
            double width = getWidth() - size;
            if (width == 0) return;

            double dx = mouseX - initX;

            double dv = (dx / width) * (maxValue - minValue);
            if(e.isShiftDown()) {
                dv /= 10.0;
            }

            double nv = initV + dv;

            double normalizedPosition = Math.max(minValue, Math.min(maxValue, nv));

            setValue(normalizedPosition);
        });

        getChildren().addAll(track
                , trackFill
                , thumb);

        applyStyle(window.getStyl());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        track.setLayoutY(getHeight() / 2 - track.getHeight() / 2);
        trackFill.setLayoutY(getHeight() / 2 - trackFill.getHeight() / 2);
        thumb.setLayoutY(getHeight() / 2);
    }

    /**
     * Gets the DoubleProperty for the value property.
     *
     * @return The DoubleProperty for the value property.
     */
    public DoubleProperty valueProperty() {
        return value;
    }

    /**
     * Gets the current value of the slider.
     *
     * @return The current value.
     */
    public double getValue() {
        return value.get();
    }

    /**
     * Sets the value of the slider.
     *
     * @param val The value to set.
     */
    public void setValue(double val) {
        this.value.set(Math.min(Math.max(val, minValue.get()), maxValue.get()));
    }

    /**
     * Gets the DoubleProperty for the minimum value.
     *
     * @return The DoubleProperty for the minimum value.
     */
    public DoubleProperty minValueProperty() {
        return minValue;
    }

    /**
     * Gets the DoubleProperty for the maximum value.
     *
     * @return The DoubleProperty for the maximum value.
     */
    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    /**
     * Gets the BooleanProperty for the inverted state.
     *
     * @return The BooleanProperty for the inverted state.
     */
    public BooleanProperty invertedProperty() {
        return inverted;
    }

    private ChangeListener<Boolean> listener;

    @Override
    public void applyStyle(Style style) {
        Runnable restyle = () -> {
            boolean invertedVal = this.inverted.get();

            track.setFill(
                    invertedVal ? style.getTextOnAccent() : style.getTextNormal()
            );
            trackFill.setFill(
                    invertedVal ? style.getTextOnAccent() : style.getTextNormal()
            );
            thumb.setFill(
                    invertedVal ? style.getTextOnAccent() : style.getTextNormal()
            );
        };

        restyle.run();

        if (listener != null) {
            inverted.removeListener(listener);
        }

        listener = (_, _, _) -> restyle.run();
        inverted.addListener(listener);
    }
}