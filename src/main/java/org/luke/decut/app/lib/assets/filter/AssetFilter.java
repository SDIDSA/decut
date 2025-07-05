package org.luke.decut.app.lib.assets.filter;

import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.display.items.AssetItem;
import org.luke.gui.controls.button.ColoredIconButton;
import org.luke.gui.controls.input.radio.RadioGroup;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.CheckMenuItem;
import org.luke.gui.controls.popup.context.items.RadioMenuItem;
import org.luke.gui.controls.popup.context.items.SliderMenuItem;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.style.Style;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AssetFilter extends ColoredIconButton {
    private final ContextMenu menu;

    private final ArrayList<CheckMenuItem> typeItems;

    private final FilterState preAll;

    private final FilterState present;
    private Consumer<FilterState> onFilterChange;

    private final SortState sortState;
    private Consumer<SortState> onSortChange;

    private final DisplayModeState modeState;
    private Consumer<DisplayModeState> onModeChange;

    private final AtomicBoolean skip;

    public AssetFilter(Home owner) {
        super(owner.getWindow(), 5, 40, 40,
                "filter", 18,
                Style::getBackgroundTertiaryOr, Style::getTextNormal);
        TextTooltip.install(this, Direction.UP, "Filter / Sort", 0, 15);

        menu = new ContextMenu(owner.getWindow());

        typeItems = new ArrayList<>();
        ArrayList<RadioMenuItem> sortBy = new ArrayList<>();
        ArrayList<RadioMenuItem> sortDir = new ArrayList<>();

        preAll = new FilterState();
        present = new FilterState();
        skip = new AtomicBoolean(false);

        sortState = new SortState();

        modeState = new DisplayModeState(AssetDisplayMode.GRID);

        ArrayList<RadioMenuItem> displayModes = new ArrayList<>();
        for(AssetDisplayMode mode : AssetDisplayMode.values()) {
            RadioMenuItem modeItem = new RadioMenuItem(menu, mode.getName(), mode.getIcon());
            displayModes.add(modeItem);
            final AssetDisplayMode fm = mode;
            modeItem.checkedProperty().addListener((_,_,nv) -> {
                if(nv) {
                    modeState.setMode(fm);
                    if(onModeChange != null) onModeChange.accept(modeState);
                }
            });
        }
        new RadioGroup(displayModes);

        SliderMenuItem scale = new SliderMenuItem(menu, "Display","scale", 42, 128);
        scale.setValue(AssetItem.SIZE.getValue());
        scale.valueProperty().addListener((_,_,nv) ->
                AssetItem.SIZE.set(nv.intValue()));

        for (SortBy by : SortBy.values()) {
            RadioMenuItem byItem = new RadioMenuItem(menu, by.getName(), by.getIcon());
            sortBy.add(byItem);
            final SortBy fb = by;
            byItem.checkedProperty().addListener((_, _, nv) -> {
                if(nv) {
                    sortState.setSortBy(fb);
                    if(onSortChange != null) onSortChange.accept(sortState);
                }
            });
        }
        new RadioGroup(sortBy);

        for (SortDirection dir : SortDirection.values()) {
            RadioMenuItem dirItem = new RadioMenuItem(menu, dir.getName(), dir.getIcon());
            sortDir.add(dirItem);
            final SortDirection fd = dir;
            dirItem.checkedProperty().addListener((_, _, nv) -> {
                if(nv) {
                    sortState.setDirection(fd);
                    if(onSortChange != null) onSortChange.accept(sortState);
                }
            });
        }
        new RadioGroup(sortDir);

        sortBy.getFirst().setChecked(true);
        sortDir.getFirst().setChecked(true);
        displayModes.get(1).setChecked(true);

        menu.addText("Sort by");
        menu.addCombined(120, sortBy.get(0), sortBy.get(1));
        menu.addCombined(120, sortBy.get(2), sortBy.get(3));
        menu.addCombined(120, sortBy.get(4), sortBy.get(5));
        menu.separate();
        menu.addCombined(120, sortDir.get(0), sortDir.get(1));
        menu.separate();
        menu.addCombined(120, displayModes.get(0), displayModes.get(1));
        menu.addMenuItem(scale);
        menu.separate();
        menu.addText("Filter");

        CheckMenuItem all = new CheckMenuItem(menu, "All");

        for (AssetType type : AssetType.values()) {
            CheckMenuItem filterType = new CheckMenuItem(menu, type.getName(), type.getIcon());
            typeItems.add(filterType);
            present.add(type, filterType);
            final AssetType ft = type;
            final CheckMenuItem fft = filterType;
            filterType.checkedProperty().addListener((_, _, nv) -> {
                if(nv) present.add(ft, fft);
                else present.remove(ft);
                if(onFilterChange != null && !skip.get()) {
                    onFilterChange.accept(present);
                }
            });
        }
        menu.addCombined(120, all, typeItems.getFirst());
        menu.addCombined(120, typeItems.get(1), typeItems.get(2));

        all.checkedProperty().addListener((_, _, nv) -> {
            skip.set(true);
            if(nv) {
                preAll.setAll(present.getMap());
                typeItems.forEach(item -> {
                    item.setChecked(true);
                    item.setDisable(true);
                });
            } else {
                present.setAll(preAll.getMap());
                typeItems.forEach(item -> {
                    item.setChecked(present.isPresent(item));
                    item.setDisable(false);
                });
            }
            if(onFilterChange != null) onFilterChange.accept(present);
            skip.set(false);
        });

        all.setChecked(true);

        setAction(() -> menu.showPop(this, Direction.RIGHT_DOWN, 10, 0));
    }

    public void setOnFilterChange(Consumer<FilterState> onFilterChange) {
        this.onFilterChange = onFilterChange;
        onFilterChange.accept(present);
    }

    public void setOnSortChange(Consumer<SortState> onSortChange) {
        this.onSortChange = onSortChange;
        onSortChange.accept(sortState);
    }

    public void setOnModeChange(Consumer<DisplayModeState> onModeChange) {
        this.onModeChange = onModeChange;
        onModeChange.accept(modeState);
    }
}
