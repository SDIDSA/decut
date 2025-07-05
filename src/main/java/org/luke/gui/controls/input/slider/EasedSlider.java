package org.luke.gui.controls.input.slider;

import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.luke.gui.window.Window;

public class EasedSlider extends Slider {

    private final DoubleProperty easedValue;

    public EasedSlider(Window window, double size, double minValue, double maxValue, Interpolator interpolator) {
        super(window, size, minValue, maxValue);

        easedValue = new SimpleDoubleProperty();

        easedValue.bind(Bindings.createDoubleBinding(() -> {
            double minV = this.minValue.get();
            double maxV = this.maxValueProperty().get();
            double v = value.get();

            double per = (v - minV) / (maxV - minV);
            return interpolator.interpolate(minV, maxV, per);
        }, value, this.minValue, this.maxValue));
    }

    @Override
    public DoubleProperty valueProperty() {
        return easedValue;
    }

    public DoubleProperty source() {
        return value;
    }

    @Override
    public double getValue() {
        return easedValue.get();
    }
}