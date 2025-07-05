package org.luke.gui.controls.recycle.table;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Comparator;

public abstract class ColumnRenderer<S> {
    protected final String headerText;
    private final boolean resizable;
    private final DoubleProperty width;
    private boolean sortable;
    private Comparator<S> comparator;

    public ColumnRenderer(String headerText, double initW, Comparator<S> comparator) {
        this.headerText = headerText;
        width = new SimpleDoubleProperty(initW);
        this.resizable = initW == 0;
        sortable = comparator != null;
        this.comparator = comparator;
    }

    public double getWidth() {
        return width.get();
    }

    public ColumnRenderer<S> setWidth(double width) {
        this.width.set(width);
        return this;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public boolean isResizable() {
        return resizable;
    }

    public ColumnRenderer<S> setSortable(Comparator<S> comparator) {
        this.sortable = true;
        this.comparator = comparator;
        return this;
    }

    public Comparator<S> getComparator() {
        return comparator;
    }

    public boolean isSortable() {
        return sortable;
    }

    public abstract TableCell<S> createCell(TableView<S> tableView);

    public String getHeaderText() { return headerText; }
}