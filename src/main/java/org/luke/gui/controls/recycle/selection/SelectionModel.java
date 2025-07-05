package org.luke.gui.controls.recycle.selection;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

public interface SelectionModel<T> {
    void select(int index);
    void select(T item);

    void toggle(int index);
    void toggle(T item);

    void selectRange(int from, int to);

    void selectIndices(int... indices);
    void selectItems(List<T> items);
    void selectAll();

    void clearSelection();
    void clearSelection(int index);

    boolean isSelected(int index);
    boolean isSelected(T item);
    boolean isEmpty();

    int getSelectedIndex();

    void setSelectionMode(SelectionMode mode);

    ReadOnlyIntegerProperty selectedIndexProperty();
    ReadOnlyObjectProperty<T> selectedItemProperty();
    ReadOnlyBooleanProperty emptyProperty();

    ObservableList<Integer> getSelectedIndices();
    ObservableList<T> getSelectedItems();
}