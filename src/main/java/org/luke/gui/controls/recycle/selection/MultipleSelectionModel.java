package org.luke.gui.controls.recycle.selection;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.luke.gui.controls.recycle.VirtualFlow;
import org.luke.gui.exception.MyObservableList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MultipleSelectionModel<T> implements SelectionModel<T> {
    private final VirtualFlow<T> flow;

    private final ReadOnlyIntegerWrapper selectedIndex;
    private final ReadOnlyObjectWrapper<T> selectedItem;
    private final ReadOnlyBooleanWrapper empty;

    private final ObservableList<Integer> indices;
    private final ObservableList<T> items;

    private final ObjectProperty<SelectionMode> selectionMode;

    public MultipleSelectionModel(VirtualFlow<T> flow) {
        this.flow = flow;

        selectionMode = new SimpleObjectProperty<>(SelectionMode.SINGLE);

        selectedIndex = new ReadOnlyIntegerWrapper(this, "selectedIndex", -1);
        selectedItem = new ReadOnlyObjectWrapper<>(this, "selectedItem");
        empty = new ReadOnlyBooleanWrapper(this, "empty", true);

        indices = MyObservableList.createList();

        selectedItem.bind(Bindings.createObjectBinding(() -> {
            int index = selectedIndex.get();
            return (index >= 0 && index < flow.getItems().size())
                    ? flow.getItems().get(index)
                    : null;
        }, selectedIndex, flow.getItems()));

        items = MyObservableList.createList();

        indices.addListener((ListChangeListener<? super Integer>) _ ->
                items.setAll(
                        indices.stream()
                                .map(i -> flow.getItems().get(i))
                                .collect(Collectors.toList())
                ));

        empty.bind(Bindings.createBooleanBinding(
                indices::isEmpty,
                indices
        ));

        selectionMode.addListener((_,_,nv) -> {
            if(nv == SelectionMode.SINGLE && indices.size() > 1) {
                select(indices.getFirst());
            }
        });

        flow.getItems().addListener((ListChangeListener<? super T>)
                _ -> clearSelection());
    }

    @Override
    public void select(int index) {
        if(selectionMode.get() == SelectionMode.SINGLE) {
            clearSelection();
        }
        if (isValidIndex(index) && !indices.contains(index)) {
            indices.add(index);
            selectedIndex.set(index);
        }
    }

    @Override
    public void select(T item) {
        if (item == null) return;
        int index = flow.getItems().indexOf(item);
        if (isValidIndex(index)) {
            select(index);
        }
    }

    @Override
    public void selectIndices(int... indices) {
        for (int index : indices) {
            if (isValidIndex(index)) {
                select(index);
            }
        }
    }

    @Override
    public void selectItems(List<T> items) {
        clearSelection();
        for (T item : items) {
            select(item);
        }
    }

    @Override
    public void selectRange(int from, int to) {
        if (from == to) return;
        final int ff = flow.getSortedItems().indexOf(flow.getItems().get(from));
        final int ft = flow.getSortedItems().indexOf(flow.getItems().get(to));
        int dir = ff > ft ? -1 : 1;
        Predicate<Integer> stop = ff > ft ? i -> i>=ft : i -> i<= ft;

        for (int i = ff; stop.test(i) && i >= 0 && i < flow.getItems().size(); i += dir) {
            select(flow.getSortedItems().get(i));
        }
    }

    @Override
    public void toggle(int index) {
        if (indices.contains(index)) {
            clearSelection(index);
        } else {
            select(index);
        }
    }

    @Override
    public void toggle(T item) {
        if (item == null) return;
        int index = flow.getItems().indexOf(item);
        if (isValidIndex(index)) {
            toggle(index);
        }
    }

    @Override
    public void selectAll() {
        indices.clear();
        for (int i = 0; i < flow.getItems().size(); i++) {
            select(i);
        }
    }

    @Override
    public void clearSelection() {
        indices.clear();
        selectedIndex.set(-1);
    }

    @Override
    public void clearSelection(int index) {
        indices.remove(Integer.valueOf(index));
        if (selectedIndex.get() == index) {
            selectedIndex.set(indices.isEmpty() ? -1 :
                    indices.getLast());
        }
    }

    @Override
    public boolean isSelected(int index) {
        return indices.contains(index);
    }

    @Override
    public boolean isSelected(T item) {
        if (item == null) return false;
        int index = flow.getItems().indexOf(item);
        return isValidIndex(index) && indices.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return indices.isEmpty();
    }

    @Override
    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    @Override
    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndex.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return selectedItem.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyBooleanProperty emptyProperty() {
        return empty.getReadOnlyProperty();
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return indices;
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return items;
    }

    @Override
    public final void setSelectionMode(SelectionMode mode) {
        if (mode == null) mode = SelectionMode.SINGLE;
        this.selectionMode.set(mode);
    }

    public final SelectionMode getSelectionMode() {
        return this.selectionMode.get();
    }

    public final ObjectProperty<SelectionMode> selectionModeProperty() {
        return this.selectionMode;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < flow.getItems().size();
    }
}