package org.luke.gui.controls.button;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.*;
import org.luke.gui.controls.popup.context.meta.MetaMenuItem;
import org.luke.gui.controls.popup.context.meta.MetaMenuMenu;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

public class MenuBarButton extends ColoredButton {
    protected final ContextMenu menu;

    public MenuBarButton(Window window, String key) {
        super(window, key, -1, _ -> Color.TRANSPARENT, Style::getTextNormal);
        setOpacity(.8);
        setFont(new Font("", 14));
        setAlignment(Pos.CENTER_LEFT);
        setUlOnHover(true);
        setFocusTraversable(false);

        menu = new ContextMenu(window, 150);

        setAction(() -> {
            menu.showPop(this, Direction.DOWN_RIGHT, -8, 8);
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends MenuItem> T addItem(MetaMenuItem metaItem) {
        if(metaItem instanceof MetaMenuMenu metaMenu) {
            MenuMenuItem item = new MenuMenuItem(menu, metaItem.getText(), metaItem.getIcon());
            metaMenu.getItems().getItems().forEach(metaSubItem -> {
                item.addMenuItem(metaSubItem.getText(), metaSubItem.getIcon(), metaSubItem.getAction());
            });
            menu.addMenuItem(item);
            return (T) item;
        } else {
            KeyedMenuItem item = new KeyedMenuItem(menu, metaItem.getText(), metaItem.getIcon());
            item.setAction(metaItem.getAction());
            if(metaItem.getEnabled() != null) {
                menu.addOnShowing(() -> {
                    item.setDisable(!metaItem.getEnabled().get());
                });
            }
            menu.addMenuItem(item);
            return (T) item;
        }
    }

    public MenuBarButton separate() {
        menu.separate();
        return this;
    }
}
