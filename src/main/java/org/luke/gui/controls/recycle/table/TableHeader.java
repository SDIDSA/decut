package org.luke.gui.controls.recycle.table;

import javafx.geometry.Pos;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import org.luke.decut.app.lib.assets.filter.SortDirection;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class TableHeader<S> extends HBox implements Styleable {
    private final TableSorting<S> sorting;
    private final TableView<S> table;
    private final HashMap<ColumnRenderer<S>, HeaderCell<S>> headerCells;
    private HeaderCell<S> sorter;
    private StyledColor backgroundFill;

    public TableHeader(TableView<S> table, List<ColumnRenderer<S>> columns) {
        this.table = table;
        setAlignment(Pos.CENTER);
        sorting = new TableSorting<>();
        setMinWidth(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        prefWidthProperty().bind(table.widthProperty());
        headerCells = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            ColumnRenderer<S> column = columns.get(i);
            HeaderCell<S> headerCell = new HeaderCell<>(table, column);
            headerCell.prefWidthProperty().bind(table.widthProperty()
                    .subtract((columns.size() - 1) * HeaderResizer.THICKNESS)
                    .multiply(column.widthProperty()));

            headerCell.setOnMouseClicked(_ -> {
                sorting.setSortBy(column);
                Comparator<S> comp = sorting.getComparator();
                table.setSorting(comp);
                if (sorting.getSortBy() == column) {
                    headerCell.setIcon(sorting.getDirection().getIcon());
                    if (sorter != null && sorter != headerCell) {
                        sorter.setIcon("empty");
                    }
                    sorter = headerCell;
                } else {
                    sorter = null;
                    headerCell.setIcon("empty");
                }
            });

            headerCells.put(column, headerCell);

            getChildren().add(headerCell);

            if (i < columns.size() - 1) {
                HeaderResizer<S> resizer = new HeaderResizer<>(table, column, columns.get(i + 1));
                resizer.prefHeightProperty().bind(heightProperty().subtract(8));
                getChildren().add(resizer);
            }
        }

        backgroundFill = Style::getBackgroundTertiaryOr;

        applyStyle(table.getWindow().getStyl());
    }

    public void setSorting(ColumnRenderer<S> column, SortDirection direction) {
        HeaderCell<S> headerCell = headerCells.get(column);
        sorting.setSorting(column, direction);
        Comparator<S> comp = sorting.getComparator();
        table.setSorting(comp);
        if (sorting.getSortBy() == column) {
            if (sorter != null && sorter != headerCell) {
                sorter.setIcon("empty");
            }
            if (headerCell != null) {
                headerCell.setIcon(sorting.getDirection().getIcon());
                sorter = headerCell;
            }
        } else {
            sorter = null;
            if (headerCell != null) headerCell.setIcon("empty");
        }
    }

    public void setBackgroundFill(StyledColor backgroundFill) {
        this.backgroundFill = backgroundFill;
        applyStyle(table.getWindow().getStyl().get());
    }

    public void setFill(StyledColor fill) {
        headerCells.values().forEach(hc -> hc.setFill(fill));
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(backgroundFill.apply(style),
                new CornerRadii(5, 5, 0, 0, false)));
    }
}