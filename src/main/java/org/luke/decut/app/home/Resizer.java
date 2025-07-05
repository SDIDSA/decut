package org.luke.decut.app.home;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;

import java.util.function.Consumer;

public class Resizer extends Pane implements Styleable {

    private StyledColor fill = Style::getBackgroundSecondary;

    private double initVal;

    private Runnable onInit;
    private Consumer<Double> onDrag;

    private final Home owner;

    public Resizer(Home home, Orientation orientation, double thickness) {
        this.owner = home;
        if(orientation == Orientation.HORIZONTAL) {
            setMinHeight(USE_PREF_SIZE);
            setMaxHeight(USE_PREF_SIZE);
            setPrefHeight(thickness);

            setPrefWidth(200000);
        } else {
            setMinWidth(USE_PREF_SIZE);
            setMaxWidth(USE_PREF_SIZE);
            setPrefWidth(thickness);

            setPrefHeight(200000);
        }

        setPickOnBounds(true);
        setCursor(orientation == Orientation.HORIZONTAL ? Cursor.V_RESIZE : Cursor.H_RESIZE);

        setOnMousePressed(mev -> {
            initVal = orientation == Orientation.HORIZONTAL ? mev.getSceneY() : mev.getSceneX();
            if(onInit != null) onInit.run();
            mev.consume();
        });

        setOnMouseDragged(mev -> {
            double curVal = orientation == Orientation.HORIZONTAL ? mev.getSceneY() : mev.getSceneX();
            if(onDrag != null) onDrag.accept(curVal - initVal);
            mev.consume();
        });

        applyStyle(home.getWindow().getStyl());
    }

    public void setOnInit(Runnable onInit) {
        this.onInit = onInit;
    }

    public void setOnDrag(Consumer<Double> onDrag) {
        this.onDrag = onDrag;
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(owner.getWindow().getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(fill.apply(style)));
    }
}
