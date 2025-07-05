package org.luke.gui.controls.recycle;

import javafx.collections.ObservableList;

public interface VirtualFlow<T> {
    ObservableList<T> getItems();
    ObservableList<T> getSortedItems();
}
