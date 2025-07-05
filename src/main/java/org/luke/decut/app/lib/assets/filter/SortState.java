package org.luke.decut.app.lib.assets.filter;

public class SortState {
    private SortBy sortBy;
    private SortDirection direction;

    public SortState() {
        this.sortBy = SortBy.NAME;
        this.direction = SortDirection.ASCENDING;
    }

    public SortState(SortBy sortBy, SortDirection direction) {
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public boolean isAscending() {
        return direction == SortDirection.ASCENDING;
    }

    public boolean isDescending() {
        return direction == SortDirection.DESCENDING;
    }

    public boolean isSortedBy(SortBy sortBy) {
        return this.sortBy == sortBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SortState sortState = (SortState) obj;
        return sortBy == sortState.sortBy && direction == sortState.direction;
    }

    @Override
    public int hashCode() {
        return sortBy.hashCode() * 31 + direction.hashCode();
    }

    @Override
    public String toString() {
        return String.format("SortState{sortBy=%s, direction=%s}",
                sortBy.getName(), direction.getName());
    }

    public SortState copy() {
        return new SortState(this.sortBy, this.direction);
    }
}
