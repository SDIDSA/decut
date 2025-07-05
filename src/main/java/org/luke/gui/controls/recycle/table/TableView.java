package org.luke.gui.controls.recycle.table;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.lib.assets.filter.SortDirection;
import org.luke.gui.controls.recycle.list.ListRow;
import org.luke.gui.controls.recycle.list.ListView;
import org.luke.gui.controls.recycle.list.RowFactory;
import org.luke.gui.controls.recycle.selection.SelectionModel;
import org.luke.gui.controls.scroll.VerticalScrollBar;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A custom TableView that displays data in columns with a fixed header.
 * This implementation uses composition, containing a ListView for the scrollable rows
 * and a separate TableHeader, to provide a better structural layout.
 *
 * @param <T> The type of items in the table.
 */
public class TableView<T> extends VBox {

    private final List<ColumnRenderer<T>> columns;
    private final ListView<T> listView;
    private final TableHeader<T> header;

    public TableView(Window owner, List<ColumnRenderer<T>> columns) {
        super();
        this.columns = columns;
        this.listView = new ListView<>(owner);
        listView.setItemSpacing(1);
        this.listView.setRowFactory(new RowFactory<>() {
            @Override
            public ListRow<T> make(ListView<T> lv) {
                return new TableRow<>(TableView.this, columns);
            }
        });

        header = new TableHeader<>(this, columns);

        VBox.setVgrow(this.listView, Priority.ALWAYS);

        this.getChildren().addAll(header, this.listView);

        double occupiedWidth = 0;
        ArrayList<ColumnRenderer<T>> toCalc = new ArrayList<>();
        for (ColumnRenderer<T> column : columns) {
            if(column.getWidth() == 0) {
                toCalc.add(column);
            } else {
                occupiedWidth += column.getWidth();
            }
        }
        double remaining = 1 - occupiedWidth;

        double w = remaining / toCalc.size();
        toCalc.forEach(c -> {
            c.setWidth(w);
        });

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        setClip(clip);
    }

    public ListView<T> getListView() {
        return listView;
    }

    public int columnCount() {
        return columns.size();
    }

    @SafeVarargs
    public TableView(Window window, ColumnRenderer<T>... columns) {
        this(window, Arrays.asList(columns));
    }

    // --- Facade Methods ---
    // These methods expose the internal ListView's functionality so you can
    // still easily access items and selection from your TableView instance.

    public void setSorting(Comparator<T> comparator) {
        listView.setSorting(comparator);
    }

    public void setSorting(ColumnRenderer<T> column, SortDirection dir) {
        header.setSorting(column, dir);
    }

    public Window getWindow() {
        return listView.getOwner();
    }

    /**
     * Returns the list of items in the table.
     * @return The ObservableList of items.
     */
    public ObservableList<T> getItems() {
        return this.listView.getItems();
    }

    /**
     * Returns the selection model for the table.
     * @return The SelectionModel.
     */
    public SelectionModel<T> getSelectionModel() {
        return this.listView.getSelectionModel();
    }

    public DoubleProperty rowHeightProperty() {
        return listView.itemHeightProperty();
    }

    /**
     * Gets the currently selected item.
     * @return The selected item.
     */
    public T getSelectedItem() {
        return this.listView.getSelectedItem();
    }

    /**
     * Gets the index of the currently selected item.
     * @return The selected index.
     */
    public int getSelectedIndex() {
        return this.listView.getSelectedIndex();
    }

    public void setHeaderBackground(StyledColor backgroundFill) {
        header.setBackgroundFill(backgroundFill);
    }

    public void setHeaderFill(StyledColor fill) {
        header.setFill(fill);
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        listView.setBackgroundFill(backgroundFill);
    }

    public void setRowBackgroundFill(StyledColor backgroundFill) {
        listView.setRowBackgroundFill(backgroundFill);
    }

    public void setRowBackgroundSelectedFill(StyledColor backgroundSelectedFill) {
        listView.setRowBackgroundSelectedFill(backgroundSelectedFill);
    }

    public void setRowFill(StyledColor fill) {
        listView.setRowFill(fill);
    }

    public void setRowSelectedFill(StyledColor selectedFill) {
        listView.setRowSelectedFill(selectedFill);
    }

    public VerticalScrollBar getScrollBar() {
        return listView.getScrollBar();
    }
}