package org.luke.decut.app.home.menubar.edit;

import java.util.ArrayList;

public class HistoryManager {
    private final ArrayList<DecutAction> actions;
    private int currentIndex; // Points to the next action to undo
    private final int maxSize;

    public HistoryManager(int maxSize) {
        this.actions = new ArrayList<>();
        this.currentIndex = 0;
        this.maxSize = maxSize;
    }

    public void executeAndRecord(DecutAction action) {
        action.perform();

        while (actions.size() > currentIndex) {
            actions.removeLast();
        }

        actions.add(action);
        currentIndex++;

        if (actions.size() > maxSize) {
            actions.removeFirst();
            currentIndex--;
        }
    }

    public boolean hasUndo() {
        return currentIndex > 0;
    }

    public boolean hasRedo() {
        return currentIndex < actions.size();
    }

    public String getUndoActionName() {
        if (!hasUndo()) return null;
        return actions.get(currentIndex - 1).getName();
    }

    public String getRedoActionName() {
        if (!hasRedo()) return null;
        return actions.get(currentIndex).getName();
    }

    public void undo() {
        if (!hasUndo()) return;
        currentIndex--;
        actions.get(currentIndex).undo();
    }

    public void redo() {
        if (!hasRedo()) return;
        actions.get(currentIndex).perform();
        currentIndex++;
    }
}