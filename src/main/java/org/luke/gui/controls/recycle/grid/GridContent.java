package org.luke.gui.controls.recycle.grid;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.luke.gui.exception.MyObservableList;

import java.util.*;
import java.util.function.Function;

/**
 * The content manager for GridView that handles virtualization and recycling of grid cells.
 * This class manages positioning, recycling, and rendering of grid cells in a 2D layout.
 *
 * @param <T> The type of items in the grid
 */
public class GridContent<T> extends Pane {
    private final ObservableList<T> data;
    private final SortedList<T> sortedData;

    private final ObjectProperty<Function<GridView<T>, ? extends GridCell<T>>> cellFactory;
    private final DoubleProperty cellWidth;
    private final DoubleProperty cellHeight;
    private final DoubleProperty horizontalSpacing;
    private final DoubleProperty verticalSpacing;

    private final Map<Integer, GridCell<T>> activeCells = new HashMap<>();
    private final Deque<GridCell<T>> recycledCells = new ArrayDeque<>();

    private final GridView<T> gridView;

    public GridContent(GridView<T> gridView) {
        this.gridView = gridView;
        data = MyObservableList.createList();
        sortedData = new SortedList<>(data);
        cellFactory = new SimpleObjectProperty<>();
        cellWidth = new SimpleDoubleProperty(100);
        cellHeight = new SimpleDoubleProperty(100);
        horizontalSpacing = new SimpleDoubleProperty(5);
        verticalSpacing = new SimpleDoubleProperty(5);

        StackPane.setAlignment(this, Pos.TOP_CENTER);

        Runnable refresh = () ->
                applyScroll(gridView.getHeight(), gridView.getScrollY());

        cellWidth.addListener((_,_,_) -> refresh.run());
        cellHeight.addListener((_,_,_) -> refresh.run());
        verticalSpacing.addListener((_,_,_) -> refresh.run());
        horizontalSpacing.addListener((_,_,_) -> refresh.run());
        widthProperty().addListener((_,_,_) -> refresh.run());
        heightProperty().addListener((_,_,_) -> refresh.run());
        gridView.getScrollBar().positionProperty().addListener((_,_,_) -> refresh.run());
        gridView.heightProperty().addListener((_,_,_) -> refresh.run());
        sortedData.addListener((ListChangeListener<? super T>) _ -> refresh.run());

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    if (data.isEmpty()) return 0.0;

                    int columns = calculateColumnsPerRow();
                    if (columns <= 0) return 0.0;

                    int totalRows = (int) Math.ceil((double) data.size() / columns);
                    if (totalRows == 0) return 0.0;

                    return (cellHeight.get() * totalRows) +
                            (verticalSpacing.get() * Math.max(0, totalRows - 1));
                },
                sortedData, cellWidth, cellHeight, verticalSpacing, horizontalSpacing, widthProperty()));
    }

    /**
     * Calculates the number of columns that can fit in the current width
     * @return number of columns per row
     */
    private int calculateColumnsPerRow() {
        if (getWidth() <= 0 || cellWidth.get() <= 0) {
            return 1;
        }

        double availableWidth = getWidth();
        double totalCellWidth = cellWidth.get();
        double spacing = horizontalSpacing.get();

        int columns = (int) Math.floor((availableWidth + spacing) / (totalCellWidth + spacing));

        return Math.max(1, columns);
    }

    public void setSorting(Comparator<T> comparator) {
        sortedData.setComparator(comparator);
    }

    private void applyScroll(double viewportHeight, double viewportY) {
        if (sortedData.isEmpty() || cellFactory.get() == null) {
            return;
        }

        int columns = calculateColumnsPerRow();
        if (columns <= 0) {
            return;
        }

        double totalRowHeight = cellHeight.get() + verticalSpacing.get();

        int firstVisibleRow = Math.max(0, (int) Math.floor((viewportY + verticalSpacing.get()) / totalRowHeight));
        int lastVisibleRow = Math.max(0, (int) Math.floor((viewportY + viewportHeight) / totalRowHeight));

        int totalRows = (int) Math.ceil((double) sortedData.size() / columns);
        lastVisibleRow = Math.min(totalRows - 1, lastVisibleRow);

        int firstVisibleIndex = firstVisibleRow * columns;
        int lastVisibleIndex = Math.min(sortedData.size() - 1, (lastVisibleRow + 1) * columns - 1);

        List<Integer> cellsToRemove = new ArrayList<>(activeCells.keySet());

        for (Integer index : cellsToRemove) {
            GridCell<T> row = activeCells.remove(index);
            getChildren().remove(row);
            row.unload();
            recycledCells.add(row);
        }

        double hSpace = columns >= 2 ?(gridView.getWidth() - (columns * cellWidth.get())) / (columns - 1) :
                horizontalSpacing.get();

        for (int i = firstVisibleIndex; i <= lastVisibleIndex; i++) {
            if (i >= 0 && i < sortedData.size() && !activeCells.containsKey(i)) {
                T item = sortedData.get(i);
                GridCell<T> cell;

                if (!recycledCells.isEmpty()) {
                    cell = recycledCells.removeFirst();
                } else {
                    cell = cellFactory.get().apply(gridView);
                }

                int row = i / columns;
                int col = i % columns;

                double x = col * (cellWidth.get() + hSpace);
                double y = row * (cellHeight.get() + verticalSpacing.get());

                cell.setLayoutX(x);
                cell.setLayoutY(y);
                cell.load(item);

                getChildren().add(cell);
                activeCells.put(i, cell);
            }
        }

        updateSelection();
    }

    public void updateSelection() {
        activeCells.values().forEach(cell -> {
            cell.setSelected(gridView.getSelectionModel().isSelected(cell.getItem()));
        });
    }

    /**
     * Gets the current number of columns per row based on available width
     * @return current columns per row
     */
    public int getColumnsPerRow() {
        return calculateColumnsPerRow();
    }

    // Getters and setters
    public ObservableList<T> getData() {
        return data;
    }

    public SortedList<T> getSortedData() {
        return sortedData;
    }

    public Function<GridView<T>, ? extends GridCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    public void setCellFactory(Function<GridView<T>, ? extends GridCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public ObjectProperty<Function<GridView<T>, ? extends GridCell<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    public double getCellWidth() {
        return cellWidth.get();
    }

    public void setCellWidth(double cellWidth) {
        this.cellWidth.set(cellWidth);
    }

    public DoubleProperty cellWidthProperty() {
        return cellWidth;
    }

    public double getCellHeight() {
        return cellHeight.get();
    }

    public void setCellHeight(double cellHeight) {
        this.cellHeight.set(cellHeight);
    }

    public DoubleProperty cellHeightProperty() {
        return cellHeight;
    }

    public double getHorizontalSpacing() {
        return horizontalSpacing.get();
    }

    public void setHorizontalSpacing(double horizontalSpacing) {
        this.horizontalSpacing.set(horizontalSpacing);
    }

    public DoubleProperty horizontalSpacingProperty() {
        return horizontalSpacing;
    }

    public double getVerticalSpacing() {
        return verticalSpacing.get();
    }

    public void setVerticalSpacing(double verticalSpacing) {
        this.verticalSpacing.set(verticalSpacing);
    }

    public DoubleProperty verticalSpacingProperty() {
        return verticalSpacing;
    }
}