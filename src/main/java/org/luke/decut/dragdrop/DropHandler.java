package org.luke.decut.dragdrop;

import javafx.scene.input.DragEvent;

public interface DropHandler {
    void onDragEntered(DragEvent event);
    void onDragExited(DragEvent event);
    void onDragOver(DragEvent event);
    void onDragDropped(DragEvent event);
}
