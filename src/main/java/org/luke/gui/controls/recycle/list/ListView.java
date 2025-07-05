package org.luke.gui.controls.recycle.list;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.CornerRadii;
import org.luke.gui.controls.recycle.VirtualFlow;
import org.luke.gui.controls.recycle.selection.MultipleSelectionModel;
import org.luke.gui.controls.recycle.selection.SelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.luke.gui.controls.scroll.VerticalScrollable;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;
import org.luke.gui.window.Window;

import java.util.Comparator;

/**
 * A ListView implementation that combines VerticalScrollable with VRecyclerContent
 * to provide efficient scrolling and rendering of large lists.
 * <p>
 * This ListView uses view recycling to maintain performance with large datasets
 * by only rendering visible items and reusing row components.
 *
 * @param <T> The type of items in the list
 * @author SDIDSA
 */
public class ListView<T> extends VerticalScrollable implements VirtualFlow<T>, Styleable {
    private final Window owner;

    private final ListContent<T> recyclerContent;
    private final ObjectProperty<SelectionModel<T>> selectionModel;

    private StyledColor backgroundFill;

    /**
     * Constructs a new ListView with default settings.
     */
    public ListView(Window owner) {
        super();
        this.owner = owner;

        recyclerContent = new ListContent<>(this);
        selectionModel = new SimpleObjectProperty<>(this, "selectionModel");
        selectionModel.set(new MultipleSelectionModel<>(this));

        selectionModel.get().getSelectedIndices().addListener((ListChangeListener<? super Integer>) _ -> {
            recyclerContent.updateSelection();
        });

        setContent(recyclerContent);

        backgroundFill = Style::getBackgroundFloating;

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

    public ObservableList<T> getSortedItems() {
        return recyclerContent.getSortedData();
    }

    public void setRowFactory(RowFactory<T> factory) {
        recyclerContent.setRowFactory(factory);
    }

    public void setItemHeight(double height) {
        recyclerContent.setRowHeight(height);
    }

    public DoubleProperty itemHeightProperty() {
        return recyclerContent.rowHeightProperty();
    }

    public void setItemSpacing(double spacing) {
        recyclerContent.setSpacing(spacing);
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        this.backgroundFill = backgroundFill;
        applyStyle(owner.getStyl().get());
    }

    public void setRowBackgroundFill(StyledColor backgroundFill) {
        recyclerContent.setBackgroundFill(backgroundFill);
    }

    public void setRowBackgroundSelectedFill(StyledColor backgroundSelectedFill) {
        recyclerContent.setBackgroundSelectedFill(backgroundSelectedFill);
    }

    public void setRowFill(StyledColor fill) {
        recyclerContent.setFill(fill);
    }

    public void setRowSelectedFill(StyledColor selectedFill) {
        recyclerContent.setSelectedFill(selectedFill);
    }

    @Override
    public void applyStyle(Style style) {
        getScrollBar().setThumbFill(style.getTextMuted());
        setBackground(Backgrounds.make(backgroundFill.apply(style),
                new CornerRadii(0,0,5,5, false)));
    }
}