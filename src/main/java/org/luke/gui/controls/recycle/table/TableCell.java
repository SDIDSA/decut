package org.luke.gui.controls.recycle.table;

import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

public interface TableCell<S> {
    void apply(S value);
    void setFill(Paint fill);

    default Region getNode() {
        if(this instanceof Region region) {
            return region;
        } else {
            throw new TypeNotPresentException(Region.class.getName(), null);
        }
    }
}
