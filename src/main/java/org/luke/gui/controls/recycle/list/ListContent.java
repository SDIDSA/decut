package org.luke.gui.controls.recycle.list;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.luke.gui.exception.MyObservableList;
import org.luke.gui.style.StyledColor;

import java.util.*;
import java.util.function.Function;

public class ListContent<T> extends Pane {
    private final ObservableList<T> data;
    private final SortedList<T> sortedData;

    private final ObjectProperty<RowFactory<T>> rowFactory;
    private final DoubleProperty rowHeight;
    private final DoubleProperty spacing;

    private final Map<Integer, ListRow<T>> activeRows = new HashMap<>();
    private final Deque<ListRow<T>> recycledRows = new ArrayDeque<>();

    private final ListView<T> listView;

    public ListContent(ListView<T> listView) {
        this.listView = listView;
        data = MyObservableList.createList();
        sortedData = new SortedList<>(data);
        rowFactory = new SimpleObjectProperty<>();
        rowHeight = new SimpleDoubleProperty(30);
        spacing = new SimpleDoubleProperty(0);

        StackPane.setAlignment(this, Pos.TOP_CENTER);

        InvalidationListener refresh = _ ->
                applyScroll(listView.getHeight(), listView.getScrollY());

        rowHeight.addListener(refresh);
        spacing.addListener(refresh);
        heightProperty().addListener(refresh);
        listView.getScrollBar().positionProperty().addListener(refresh);
        listView.heightProperty().addListener(refresh);
        sortedData.addListener(refresh);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    if (data.isEmpty()) return 0.0;
                    return (rowHeight.get() * data.size()) + (spacing.get() * Math.max(0, data.size() - 1));
                }, sortedData, rowHeight, spacing));
    }

    public void setSorting(Comparator<T> comparator) {
        sortedData.setComparator(comparator);
    }

    private void applyScroll(double viewportHeight, double viewportY) {
        if (sortedData.isEmpty() || rowFactory.get() == null) {
            return;
        }

        double totalRowHeight = rowHeight.get() + spacing.get();

        int firstVisibleIndex = Math.max(0, (int) Math.floor((viewportY + spacing.get()) / totalRowHeight));
        int lastVisibleIndex = Math.max(0, (int) Math.floor((viewportY + viewportHeight) / totalRowHeight));

        lastVisibleIndex = Math.min(sortedData.size() - 1, lastVisibleIndex);

        List<Integer> rowsToRemove = new ArrayList<>(activeRows.keySet());

        for (Integer index : rowsToRemove) {
            ListRow<T> row = activeRows.remove(index);
            getChildren().remove(row);
            row.unload();
            recycledRows.add(row);
        }

        for (int i = firstVisibleIndex; i <= lastVisibleIndex; i++) {
            if (i >= 0 && i < sortedData.size() && !activeRows.containsKey(i)) {
                T item = sortedData.get(i);
                ListRow<T> row;

                if (!recycledRows.isEmpty()) {
                    row = recycledRows.removeFirst();
                } else {
                    row = rowFactory.get().apply(listView);
                    row.setMinHeight(USE_PREF_SIZE);
                    row.setMaxHeight(USE_PREF_SIZE);
                    row.prefHeightProperty().bind(rowHeight);
                    row.prefWidthProperty().bind(widthProperty());
                }

                row.setLayoutY(i * totalRowHeight);
                row.load(item);
                getChildren().add(row);
                activeRows.put(i, row);
            }
        }

        updateSelection();
    }

    public void updateSelection() {
        activeRows.values().forEach(cell -> {
            cell.setSelected(listView.getSelectionModel().isSelected(cell.getItem()));
        });
    }

    public ObservableList<T> getData() {
        return data;
    }

    public SortedList<T> getSortedData() {
        return sortedData;
    }

    public RowFactory<T> getRowFactory() {
        return rowFactory.get();
    }

    public void setRowFactory(RowFactory<T> rowFactory) {
        this.rowFactory.set(rowFactory);
    }

    public ObjectProperty<RowFactory<T>> rowFactoryProperty() {
        return rowFactory;
    }

    public double getSpacing() {
        return spacing.get();
    }

    public void setSpacing(double spacing) {
        this.spacing.set(spacing);
    }

    public DoubleProperty spacingProperty() {
        return spacing;
    }

    public double getRowHeight() {
        return rowHeight.get();
    }

    public void setRowHeight(double rowHeight) {
        this.rowHeight.set(rowHeight);
    }

    public DoubleProperty rowHeightProperty() {
        return rowHeight;
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        rowFactory.get().setBackgroundFill(backgroundFill);
    }

    public void setBackgroundSelectedFill(StyledColor backgroundSelectedFill) {
        rowFactory.get().setBackgroundSelectedFill(backgroundSelectedFill);
    }

    public void setFill(StyledColor fill) {
        rowFactory.get().setFill(fill);
    }

    public void setSelectedFill(StyledColor selectedFill) {
        rowFactory.get().setSelectedFill(selectedFill);
    }
}