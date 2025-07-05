package org.luke.gui.controls.recycle.table;

import javafx.scene.Cursor;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import org.luke.gui.factory.Borders;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class HeaderResizer<S> extends Pane implements Styleable {
    public static final int THICKNESS = 4;

    private double initX;
    private double initLeft, initRight;

    public HeaderResizer(TableView<S> table, ColumnRenderer<S> left, ColumnRenderer<S> right) {

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefWidth(THICKNESS);
        setCursor(Cursor.H_RESIZE);

        setTranslateX((double) -THICKNESS / 2);

        setOpacity(0.2);

        setOnMousePressed(e -> {
            initX = e.getSceneX();
            initLeft = left.getWidth();
            initRight = right.getWidth();
        });

        setOnMouseDragged(e -> {
            double dx = e.getSceneX() - initX;
            double perc = dx / table.getWidth();
            double newLeft = initLeft + perc;
            double newRight = initRight - perc;

            if(newLeft < 0.1) {
                newLeft = 0.1;
                newRight = (initRight + initLeft) - 0.1;
            }

            if(newRight < 0.1) {
                newRight = 0.1;
                newLeft = (initRight + initLeft) - 0.1;
            }

            right.setWidth(newRight);
            left.setWidth(newLeft);
        });

        applyStyle(table.getWindow().getStyl());
    }

    @Override
    public void applyStyle(Style style) {
        setBorder(Borders.make(style.getTextNormal(), new BorderWidths(0,1,0,0)));
    }
}
