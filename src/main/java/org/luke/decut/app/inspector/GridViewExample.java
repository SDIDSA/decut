package org.luke.decut.app.inspector;

import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.recycle.grid.GridView;
import org.luke.gui.controls.recycle.grid.GridCell;
import org.luke.gui.controls.recycle.selection.SelectionMode;
import org.luke.gui.controls.text.unkeyed.ColoredLabel;
import org.luke.gui.controls.text.unkeyed.Label;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

/**
 * Example usage of GridView with a custom GridCell implementation.
 * This example shows how to create a simple grid of colored items.
 */
public class GridViewExample {

    /**
     * Custom GridCell implementation for displaying colored items
     */
    public static class NumberGridCell extends GridCell<NumberItem> {
        private final Label label;

        public NumberGridCell(GridView<NumberItem> gridView) {
            super(gridView);

            label = new ColoredLabel(gridView.getOwner(), "", Style::getTextNormal);
            label.setFont(new Font(14));
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(TextAlignment.CENTER);

            label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            label.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            label.prefHeightProperty().bind(heightProperty());
            label.prefWidthProperty().bind(widthProperty());

            setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

            prefWidthProperty().bind(gridView.cellWidthProperty());
            prefHeightProperty().bind(gridView.cellHeightProperty());


            getChildren().add(label);
        }

        @Override
        protected void updateContent(NumberItem item) {
            label.setText(String.valueOf(item.number()));
        }

        @Override
        protected void clearContent() {
            label.setText("");
        }

        @Override
        public void applyStyle(Style style, boolean selected) {
            setBackground(Backgrounds.make(selected ? style.getBackgroundModifierHover() : style.getBackgroundModifierSelected(), 5));
        }
    }

    /**
         * Simple data class for grid items
         */
        public record NumberItem(int number) {
    }

    /**
     * Creates and configures a GridView with sample data
     */
    public static GridView<NumberItem> createSampleGridView(Window owner) {
        GridView<NumberItem> gridView = new GridView<>(owner);
        gridView.setCellSize(30, 30);
        gridView.setSpacing(10);
        gridView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Set the cell factory
        gridView.setCellFactory(NumberGridCell::new);

        // Add sample data
        for(int i = 1; i <= 1000; i++) {
            gridView.getItems().add(new NumberItem(i));
        }

        return gridView;
    }
}