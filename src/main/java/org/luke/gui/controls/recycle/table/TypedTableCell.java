package org.luke.gui.controls.recycle.table;

public interface TypedTableCell<S, T> extends TableCell<S> {

    default void apply(S item) {
        applyTyped(extract(item));
    }

    void applyTyped(T extracted);

    T extract(S item);
}
