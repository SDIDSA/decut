package org.luke.gui.controls.recycle.table;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.luke.gui.controls.recycle.list.ListRow;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;

import java.util.ArrayList;
import java.util.List;

public class TableRow<S> extends ListRow<S> {
    private final List<ColumnRenderer<S>> columns;
    private final List<TableCell<S>> cells = new ArrayList<>();

    public TableRow(TableView<S> table, List<ColumnRenderer<S>> columns) {
        super(table.getListView());
        this.columns = columns;

        HBox root = new HBox();

        for (ColumnRenderer<S> column : columns) {
            TableCell<S> cell = column.createCell(table);
            Region cellNode = cell.getNode();
            cellNode.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            cellNode.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            cellNode.prefWidthProperty().bind(widthProperty().multiply(column.widthProperty()));
            cellNode.prefHeightProperty().bind(heightProperty());
            cellNode.setPadding(new Insets(5));
            cells.add(cell);
            root.getChildren().add(cellNode);
        }

        getChildren().add(root);

        applyStyle(table.getWindow().getStyl().get());
    }

    @Override
    protected void updateContent(S item) {
        for (int i = 0; i < columns.size(); i++) {
            cells.get(i).apply(item);
        }
    }

    @Override
    public void applyStyle(Style style, boolean selected) {
        if(cells == null) return;
        for(TableCell<S> cell : cells) {
            cell.setFill(isSelected() ? selectedFill.apply(style) : fill.apply(style));
            setBackground(Backgrounds.make(selected ?
                    backgroundSelectedFill.apply(style) : backgroundFill.apply(style)));
        }
    }
}