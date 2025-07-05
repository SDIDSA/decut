package org.luke.decut.dragdrop;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public interface DragHandler {
    void onDragDetected(MouseEvent event);
    void onDragDone(DragEvent event);
}
