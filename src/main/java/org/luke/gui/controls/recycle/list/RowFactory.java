package org.luke.gui.controls.recycle.list;

import javafx.scene.paint.Color;
import org.luke.gui.style.Style;
import org.luke.gui.style.StyledColor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class RowFactory<T> implements Function<ListView<T>, ListRow<T>> {
    protected StyledColor backgroundFill;
    protected StyledColor backgroundSelectedFill;
    protected StyledColor fill;
    protected StyledColor selectedFill;

    private final Set<ListRow<T>> allRows;

    public RowFactory() {
        allRows = new HashSet<>();

        backgroundFill = s -> Color.TRANSPARENT;
        backgroundSelectedFill = Style::getAccent;

        fill = Style::getHeaderSecondary;
        selectedFill = Style::getTextOnAccent;
    }

    @Override
    public ListRow<T> apply(ListView<T> listView) {
        ListRow<T> row = make(listView);
        row.setBackgroundFill(backgroundFill);
        row.setBackgroundSelectedFill(backgroundSelectedFill);
        row.setFill(fill);
        row.setSelectedFill(selectedFill);
        allRows.add(row);
        return row;
    }

    public abstract ListRow<T> make(ListView<T> listView);

    public StyledColor getBackgroundFill() {
        return backgroundFill;
    }

    public StyledColor getBackgroundSelectedFill() {
        return backgroundSelectedFill;
    }

    public StyledColor getFill() {
        return fill;
    }

    public StyledColor getSelectedFill() {
        return selectedFill;
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        this.backgroundFill = backgroundFill;
        allRows.forEach(row -> row.setBackgroundFill(backgroundFill));
    }

    public void setBackgroundSelectedFill(StyledColor backgroundSelectedFill) {
        this.backgroundSelectedFill = backgroundSelectedFill;
        allRows.forEach(row -> row.setBackgroundSelectedFill(backgroundSelectedFill));
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        allRows.forEach(row -> row.setFill(fill));
    }

    public void setSelectedFill(StyledColor selectedFill) {
        this.selectedFill = selectedFill;
        allRows.forEach(row -> row.setSelectedFill(selectedFill));
    }
}
