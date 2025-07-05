package org.luke.gui.controls.recycle.list;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.luke.gui.style.SelectableStyleable;
import org.luke.gui.style.Style;
import org.luke.gui.style.StyledColor;

/**
 * An enhanced RecyclerRow that provides better item management and lifecycle methods.
 * This class extends the base RecyclerRow to provide more functionality for ListView items.
 *
 * @param <T> The type of item this row represents
 * @author SDIDSA
 */
public abstract class ListRow<T> extends Pane implements SelectableStyleable {
    private final ObjectProperty<T> item;
    private final BooleanProperty selected;
    private boolean isLoaded = false;

    private final ListView<T> listView;

    protected StyledColor backgroundFill;
    protected StyledColor backgroundSelectedFill;
    protected StyledColor fill;
    protected StyledColor selectedFill;

    public ListRow(ListView<T> listView) {
        super();
        this.listView = listView;
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
            if (listView != null && getItem() != null) {
                int index = listView.getItems().indexOf(item.get());
                if (index >= 0) {
                    if (event.isControlDown() || event.isMetaDown()) {
                        listView.getSelectionModel().toggle(index);
                    } else if (event.isShiftDown()) {
                        int old = listView.getSelectionModel().getSelectedIndex();
                        if(old >= 0) {
                            listView.getSelectionModel().selectRange(old, index);
                        }
                    } else {
                        listView.getSelectionModel().clearSelection();
                        listView.getSelectionModel().select(index);
                    }
                }
            }
        });

        selected.addListener((_,_,_) ->
                applyStyle(listView.getOwner().getStyl().get()));

        backgroundFill = s -> Color.TRANSPARENT;
        backgroundSelectedFill = Style::getAccent;

        fill = Style::getHeaderSecondary;
        selectedFill = Style::getTextOnAccent;

        applyStyle(listView.getOwner().getStyl());
    }

    public ListView<T> getListView() {
        return listView;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    protected abstract void updateContent(T item);

    /**
     * Clear the row content when no item is assigned.
     * Override this method to provide custom cleanup logic.
     */
    protected void clearContent() {
        // Default implementation - subclasses can override
    }

    /**
     * Called when the row is about to be recycled.
     * Override this method to perform cleanup before reuse.
     */
    protected void onRecycle() {
        // Default implementation - subclasses can override
    }

    public final void load(T item) {
        if (isLoaded) {
            onRecycle();
        }

        this.item.set(item);
    }

    /**
     * Gets the current item displayed in this row.
     *
     * @return the current item, or null if no item is loaded
     */
    public T getItem() {
        return item.get();
    }

    /**
     * Gets the item property for this row.
     *
     * @return the item property
     */
    public ObjectProperty<T> itemProperty() {
        return item;
    }

    /**
     * Checks if this row currently has an item loaded.
     *
     * @return true if an item is loaded, false otherwise
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Unloads the current item from this row.
     */
    public void unload() {
        if (isLoaded) {
            onRecycle();
        }
        item.set(null);
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        this.backgroundFill = backgroundFill;
        applyStyle(getListView().getOwner().getStyl().get());
    }

    public void setBackgroundSelectedFill(StyledColor backgroundSelectedFill) {
        this.backgroundSelectedFill = backgroundSelectedFill;
        applyStyle(getListView().getOwner().getStyl().get());
    }

    public void setFill(StyledColor fill) {
        this.fill = fill;
        applyStyle(getListView().getOwner().getStyl().get());
    }

    public void setSelectedFill(StyledColor selectedFill) {
        this.selectedFill = selectedFill;
        applyStyle(getListView().getOwner().getStyl().get());
    }
}