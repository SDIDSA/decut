package org.luke.gui.exception;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MyObservableList {
    public static <T> ObservableList<T> createList() {
        ObservableList<T> res = FXCollections.observableArrayList();


        return res;
    }
}
