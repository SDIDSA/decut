package org.luke.gui.controls.recycle.grid;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import org.luke.gui.controls.recycle.VirtualFlow;
import org.luke.gui.controls.recycle.selection.MultipleSelectionModel;
import org.luke.gui.controls.recycle.selection.SelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.luke.gui.controls.scroll.VerticalScrollable;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import java.util.Comparator;
import java.util.function.Function;

/**
 * A GridView implementation that combines VerticalScrollable with GridRecyclerContent
 * to provide efficient scrolling and rendering of large grids.
 * <p>
 * This GridView uses view recycling to maintain performance with large datasets
 * by only rendering visible items and reusing cell components.
 * <p>
 * The number of columns per row is automatically calculated based on the cell width
 * and available container width.
 *
 * @param <T> The type of items in the grid
 * @author SDIDSA
 */
public class GridView<T> extends VerticalScrollable implements VirtualFlow<T>, Styleable {
    private final Window owner;

    private final GridContent<T> recyclerContent;
    private final ObjectProperty<SelectionModel<T>> selectionModel;

    /**
     * Constructs a new GridView with default settings.
     */
    public GridView(Window owner) {
        super();
        this.owner = owner;

        recyclerContent = new GridContent<>(this);
        selectionModel = new SimpleObjectProperty<>(this, "selectionModel");
        selectionModel.set(new MultipleSelectionModel<>(this));

        selectionModel.get().getSelectedIndices().addListener((ListChangeListener<? super Integer>) _ -> {
            recyclerContent.updateSelection();
        });

        setContent(recyclerContent);

        applyStyle(owner.getStyl());
    }

    public void setSorting(Comparator<T> comparator) {
        recyclerContent.setSorting(comparator);
    }

    public SelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    public void setSelectionModel(SelectionModel<T> model) {
        selectionModel.set(model);
    }

    public ObjectProperty<SelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public T getSelectedItem() {
        return selectionModel.get().selectedItemProperty().get();
    }

    public int getSelectedIndex() {
        return selectionModel.get().selectedIndexProperty().get();
    }

    public Window getOwner() {
        return owner;
    }

    public ObservableList<T> getItems() {
        return recyclerContent.getData();
    }

    @Override
    public ObservableList<T> getSortedItems() {
        return recyclerContent.getSortedData();
    }

    public void setCellFactory(Function<GridView<T>, ? extends GridCell<T>> factory) {
        recyclerContent.setCellFactory(factory);
    }

    public void setCellSize(double width, double height) {
        recyclerContent.setCellWidth(width);
        recyclerContent.setCellHeight(height);
    }

    public void setCellWidth(double width) {
        recyclerContent.setCellWidth(width);
    }

    public DoubleProperty cellWidthProperty() {
        return recyclerContent.cellWidthProperty();
    }

    public DoubleProperty cellHeightProperty() {
        return recyclerContent.cellHeightProperty();
    }

    public void setCellHeight(double height) {
        recyclerContent.setCellHeight(height);
    }

    public void setHorizontalSpacing(double spacing) {
        recyclerContent.setHorizontalSpacing(spacing);
    }

    public void setVerticalSpacing(double spacing) {
        recyclerContent.setVerticalSpacing(spacing);
    }

    public void setSpacing(double spacing) {
        setHorizontalSpacing(spacing);
        setVerticalSpacing(spacing);
    }

    @Override
    public void applyStyle(Style style) {
        getScrollBar().setThumbFill(style.getTextMuted());
    }
}