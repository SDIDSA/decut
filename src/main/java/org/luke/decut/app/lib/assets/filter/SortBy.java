package org.luke.decut.app.lib.assets.filter;

import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.gui.controls.image.ImageProxy;
import org.luke.gui.controls.recycle.table.ColumnRenderer;
import org.luke.gui.controls.recycle.table.IconCell;

import org.luke.gui.controls.recycle.table.TableColumns;

import java.util.Comparator;

public enum SortBy {
    NAME("Name", "sort-name", TableColumns.stringColumn("Name", ad -> ad.getFile().getName())),
    DURATION("Length", "sort-duration", TableColumns.durationMillisColumn("Length", AssetData::getDuration)),
    RESOLUTION("Res", "resolution", TableColumns.resolutionColumn("Resolution", AssetData::getResolution,0)),
    SIZE("Size", "sort-size", TableColumns.byteSizeColumn("Size", ad -> ad.getFile().length())),
    DATE("Date", "sort-date", TableColumns.epochColumn("Modified", ad -> ad.getFile().lastModified())),
    TYPE("Type", "file", TableColumns.createCustomColumn("",
            tv -> new IconCell<>(tv, s -> ImageProxy.load(s.getType().getIcon(), 64)),
            0.1, Comparator.comparing(d -> d.getType().getName())));

    private final String name;
    private final String icon;
    private final ColumnRenderer<AssetData> column;

    SortBy(String name, String icon, ColumnRenderer<AssetData> column) {
        this.name = name;
        this.icon = icon;
        this.column = column;
    }

    public ColumnRenderer<AssetData> getColumn() {
        return column;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public SortBy byName(String name) {
        for (SortBy value : values()) {
            if(value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}