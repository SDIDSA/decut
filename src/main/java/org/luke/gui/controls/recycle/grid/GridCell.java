package org.luke.gui.controls.recycle.grid;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import org.luke.gui.style.SelectableStyleable;

/**
 * An enhanced GridCell that provides better item management and lifecycle methods.
 * This class extends Pane to provide functionality for GridView items.
 *
 * @param <T> The type of item this cell represents
 * @author SDIDSA
 */
public abstract class GridCell<T> extends Pane implements SelectableStyleable {
    private final ObjectProperty<T> item;
    private final BooleanProperty selected;
    private boolean isLoaded = false;

    public GridCell(GridView<T> gridView) {
        super();
        item = new SimpleObjectProperty<>();
        selected = new SimpleBooleanProperty(false);

        item.addListener((_, _, newItem) -> {
            if (newItem != null) {
                updateContent(newItem);
                isLoaded = true;
            } else {
                clearContent();
                isLoaded = false;
            }
        });

        setOnMouseClicked(event -> {
            if (gridView != null && getItem() != null) {
                int index = gridView.getItems().indexOf(item.get());
                if (index >= 0) {
                    if (event.isControlDown() || event.isMetaDown()) {
                        gridView.getSelectionModel().toggle(index);
                    } else if (event.isShiftDown()) {
                        int old = gridView.getSelectionModel().getSelectedIndex();
                        if(old >= 0) {
                            gridView.getSelectionModel().selectRange(old, index);
                        }
                    } else {
                        gridView.getSelectionModel().clearSelection();
                        gridView.getSelectionModel().select(index);
                    }
                }
            }
        });

        selected.addListener((_,_,_) ->
                applyStyle(gridView.getOwner().getStyl().get()));

        applyStyle(gridView.getOwner().getStyl());
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Updates the cell content with the given item.
     * Subclasses must implement this method to define how items are displayed.
     *
     * @param item the item to display
     */
    protected abstract void updateContent(T item);

    /**
     * Clear the cell content when no item is assigned.
     * Override this method to provide custom cleanup logic.
     */
    protected void clearContent() {
        // Default implementation - subclasses can override
    }

    /**
     * Called when the cell is about to be recycled.
     * Override this method to perform cleanup before reuse.
     */
    protected void onRecycle() {
        // Default implementation - subclasses can override
    }

    /**
     * Loads an item into this cell.
     *
     * @param item the item to load
     */
    public final void load(T item) {
        if (isLoaded) {
            onRecycle();
        }
        this.item.set(item);
    }

    /**
     * Gets the current item displayed in this cell.
     *
     * @return the current item, or null if no item is loaded
     */
    public T getItem() {
        return item.get();
    }

    /**
     * Gets the item property for this cell.
     *
     * @return the item property
     */
    public ObjectProperty<T> itemProperty() {
        return item;
    }

    /**
     * Checks if this cell currently has an item loaded.
     *
     * @return true if an item is loaded, false otherwise
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Unloads the current item from this cell.
     */
    public void unload() {
        if (isLoaded) {
            onRecycle();
        }
        item.set(null);
    }
}