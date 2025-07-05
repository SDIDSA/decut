package org.luke.gui.controls.recycle.table;

import java.util.Comparator;
import java.util.function.Function;

public class TypedColumnRenderer<S, T> extends ColumnRenderer<S> {
    private final Function<TableView<S>, TypedTableCell<S, T>> creator;

    public TypedColumnRenderer(String headerText,
                               Function<TableView<S>, TypedTableCell<S, T>> creator,
                               double initW,
                               Comparator<S> comparator) {
        super(headerText, initW, comparator);
        this.creator = creator;
    }

    @Override
    public TypedColumnRenderer<S, T> setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public TypedTableCell<S, T> createCell(TableView<S> tableView) {
        return creator.apply(tableView);
    }
}