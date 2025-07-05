package org.luke.decut.lang;

import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.KeyedMenuItem;
import org.luke.gui.locale.Locale;
import org.luke.decut.local.LocalStore;

public class LanguageItem extends KeyedMenuItem {
    public LanguageItem(ContextMenu menu, Locale locale) {
        super(menu, locale.getName().toLowerCase(), locale.getName().toLowerCase());
        setColored(false);
        setAction(() -> {
            menu.getOwner().setLocale(locale);
            LocalStore.setLanguage(locale);
        });
    }
}
