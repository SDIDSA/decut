package org.luke.gui.controls.recycle.table;

import org.luke.decut.app.lib.assets.filter.SortDirection;

import java.util.Comparator;

public class TableSorting<S> {
    private ColumnRenderer<S> sortBy;
    private SortDirection direction = SortDirection.DESCENDING;

    public void setSortBy(ColumnRenderer<S> sortBy) {
        if(!sortBy.isSortable()) return;
        if(this.sortBy == sortBy) {
            if(direction == SortDirection.DESCENDING) {
                direction = SortDirection.ASCENDING;
            } else {
                this.sortBy = null;
                direction = SortDirection.DESCENDING;
            }
        } else {
            this.sortBy = sortBy;
            direction = SortDirection.DESCENDING;
        }
    }

    public void setSorting(ColumnRenderer<S> by, SortDirection direction) {
        this.sortBy = by;
        this.direction = direction;
    }

    public ColumnRenderer<S> getSortBy() {
        return sortBy;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public Comparator<S> getComparator() {
        if(sortBy == null) return null;

        Comparator<S> res = sortBy.getComparator();
        if(res == null) return null;

        if(direction == SortDirection.DESCENDING)
            res = res.reversed();

        return res;
    }
}
