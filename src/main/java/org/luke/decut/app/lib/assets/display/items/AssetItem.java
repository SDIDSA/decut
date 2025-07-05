package org.luke.decut.app.lib.assets.display.items;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;

public interface AssetItem {
    DoubleProperty SIZE = new SimpleDoubleProperty(64);

    default Node getNode() {
        if(this instanceof Node node) {
            return node;
        }
        return null;
    }
}
