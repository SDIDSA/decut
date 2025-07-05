package org.luke.decut.app.home.menubar.edit;

import org.luke.decut.app.home.Home;
import org.luke.decut.app.home.menubar.HomeMenuButton;
import org.luke.gui.controls.popup.context.items.KeyedMenuItem;
import org.luke.gui.controls.popup.context.items.MenuItem;
import org.luke.gui.controls.popup.context.meta.MetaMenuItem;

public class EditMenu extends HomeMenuButton {
    private final HistoryManager history;

    private final KeyedMenuItem undo;
    private final KeyedMenuItem redo;

    public EditMenu(Home owner) {
        super(owner, "Edit");

        history = new HistoryManager(15);

        undo = addItem(new MetaMenuItem("Undo", "undo", history::undo, history::hasUndo));
        redo = addItem(new MetaMenuItem("Redo", "redo", history::redo, history::hasRedo));

        menu.addOnShowing(() -> {
            if(history.hasRedo()) {
                redo.setKey("Redo " + history.getRedoActionName());
            } else {
                redo.setKey("Nothing to redo...");
            }
            if(history.hasUndo()) {
                undo.setKey("Undo " + history.getUndoActionName());
            } else {
                undo.setKey("Nothing to undo...");
            }
        });

        separate();
    }

    public void perform(String name, Runnable action, Runnable inverse, boolean ffmpeg) {
        history.executeAndRecord(new DecutAction(name, action, inverse, ffmpeg));
    }

    public void perform(String name, Runnable action, Runnable inverse) {
        perform(name, action, inverse, false);
    }
}
