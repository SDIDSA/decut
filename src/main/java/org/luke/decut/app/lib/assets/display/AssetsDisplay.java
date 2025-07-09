package org.luke.decut.app.lib.assets.display;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.LibraryContent;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.decut.app.lib.assets.display.items.AssetItem;
import org.luke.decut.app.lib.assets.display.items.GridAssetItem;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.lib.assets.filter.*;
import org.luke.gui.controls.recycle.grid.GridView;
import org.luke.gui.controls.recycle.selection.SelectionMode;
import org.luke.gui.controls.recycle.table.ColumnRenderer;
import org.luke.gui.controls.recycle.table.TableView;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class AssetsDisplay extends StackPane {
    private final Home owner;

    private final ObservableList<AssetData> data;
    private final FilteredList<AssetData> filteredData;

    private final GridView<AssetData> grid;
    private final TableView<AssetData> list;
    private final EmptyLib emptyLib;
    private final Drop drop;

    private FileChooser chooser;

    private FilterState currentFilter;
    private SortState currentSort;
    private DisplayModeState currentMode;

    public AssetsDisplay(Home owner) {
        this.owner = owner;
        currentFilter = new FilterState();
        currentSort = new SortState();
        currentMode = new DisplayModeState(AssetDisplayMode.GRID);

        emptyLib = new EmptyLib(owner);
        drop = new Drop(owner);

        grid = new GridView<>(owner.getWindow());
        grid.setCellFactory(gv -> new GridAssetItem(owner, gv));
        grid.cellWidthProperty().bind(AssetItem.SIZE);
        grid.cellHeightProperty().bind(AssetItem.SIZE);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        grid.setSpacing(10);
        grid.getScrollBar().setTranslateX(13);

        List<ColumnRenderer<AssetData>> columns = Stream.of(
                SortBy.NAME,
                SortBy.RESOLUTION,
                SortBy.SIZE,
                SortBy.TYPE
        ).map(SortBy::getColumn).toList();

        list = new TableView<>(owner.getWindow(), columns);
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.rowHeightProperty().bind(AssetItem.SIZE.divide(2));
        list.setMinWidth(USE_PREF_SIZE);
        list.setMaxWidth(USE_PREF_SIZE);
        list.prefWidthProperty().bind(
                widthProperty()
        );
        list.setHeaderBackground(Style::getBackgroundTertiaryOr);
        list.setHeaderFill(Style::getTextNormal);
        list.setRowSelectedFill(Style::getAccent);
        list.setRowBackgroundSelectedFill(Style::getBackgroundModifierSelected);
        list.setBackgroundFill(Style::getNothing);

        getChildren().add(emptyLib);

        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, ad -> currentFilter.isPresent(ad.getType()));

        filteredData.addListener((InvalidationListener) _ -> {
            grid.getItems().setAll(filteredData);
            list.getItems().setAll(filteredData);
            refreshDisplay();
        });

        Platform.runLater(() -> {
            LibraryContent.getInstance(owner, Assets.class).getFilter().setOnFilterChange(state -> {
                currentFilter = state;
                filteredData.setPredicate(ad -> currentFilter.isPresent(ad.getType()));
            });

            LibraryContent.getInstance(owner, Assets.class).getFilter()
                    .setOnSortChange(state -> {
                        currentSort = state;
                        grid.setSorting(createComparator());
                        list.setSorting(state.getSortBy().getColumn(), state.getDirection());
                    });

            LibraryContent.getInstance(owner, Assets.class).getFilter()
                    .setOnModeChange(state -> {
                        currentMode = state;
                        refreshDisplay();
                    });
        });

        setOnDragOver(de -> {
            List<File> files = de.getDragboard().getFiles();
            List<File> valid = ExtensionFilters.validate(files);
            if(!valid.isEmpty()) {
                drop.setCount(valid.size());
                getChildren().setAll(drop);
                de.acceptTransferModes(TransferMode.ANY);
            }
            de.consume();
        });

        setOnDragDropped(de -> {
            List<File> files = de.getDragboard().getFiles();
            List<File> valid = ExtensionFilters.validate(files);
            if(!valid.isEmpty()) {
                importFiles(valid);
                de.setDropCompleted(true);
            } else {
                de.setDropCompleted(false);
            }
            de.consume();
        });

        setOnDragExited(de -> {
            refreshDisplay();
        });

    }

    private void refreshDisplay() {
        if (filteredData.isEmpty()) {
            getChildren().setAll(emptyLib);
        } else {
            if (currentMode.getMode() == AssetDisplayMode.LIST) {
                list.getSelectionModel().selectItems(grid.getSelectionModel().getSelectedItems());
            } else {
                grid.getSelectionModel().selectItems(list.getSelectionModel().getSelectedItems());
            }
            getChildren().setAll(currentMode.getMode() == AssetDisplayMode.LIST ? list : grid);
        }
    }

    private Comparator<AssetData> createComparator() {
        Comparator<AssetData> comparator = switch (currentSort.getSortBy()) {
            case SIZE -> Comparator.comparing(item -> item.getFile().length());
            case DATE -> Comparator.comparing(item -> item.getFile().lastModified());
            case DURATION -> Comparator.comparing(AssetData::getDuration);
            default -> Comparator.comparing(item -> item.getFile().getName().toLowerCase());
        };

        if (currentSort.getDirection() == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }

        return comparator;
    }


    public void showOpenDialogue() {
        if (chooser == null) {
            chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(ExtensionFilters.getFilters());
        }

        List<File> files = chooser.showOpenMultipleDialog(owner.getWindow());
        if (files != null && !files.isEmpty()) {
            importFiles(ExtensionFilters.validate(files));
        }
    }

    public void importFiles(List<File> files) {

        Runnable action = () -> {
            LibraryContent.getInstance(owner, Assets.class).startLoading();
            Platform.runBack(files, AssetData::getData, is -> {
                data.addAll(is.stream().filter(i -> !data.contains(i)).toList());
                LibraryContent.getInstance(owner, Assets.class).stopLoading();
            });
            refreshDisplay();
        };

        Runnable undo = () -> {
            data.removeIf(i -> files.contains(i.getFile()));
            refreshDisplay();
        };

        owner.perform("import assets", action, undo, true);
    }

    public ObservableList<AssetData> getData() {
        return data;
    }
}
