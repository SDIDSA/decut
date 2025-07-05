package org.luke.gui.controls.popup.context.items;

import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.controls.popup.context.ContextMenu;

import javafx.scene.paint.Color;

/**
 * Custom implementation of a keyed menu item, extending {@link MenuItem}.
 * Represents a menu item associated with a key, typically used for localized
 * text. Manages the appearance, behavior, and interaction of the keyed menu
 * item.
 *
 * @author SDIDSA
 */
public class KeyedMenuItem extends MenuItem {

	/**
	 * Constructs a keyed menu item with the specified parent context menu, key, and
	 * fill color.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param key  The key or text associated with the menu item.
	 * @param fill The fill color of the menu item.
	 */
	public KeyedMenuItem(ContextMenu menu, String key, String icon, Color fill) {
		super(menu, key, icon, fill, true);
	}

	public KeyedMenuItem(ContextMenu menu, String key, Color fill) {
		super(menu, key, "empty", fill, true);
	}

	/**
	 * Constructs a keyed menu item with the specified parent context menu and key.
	 * Uses a default fill color for the menu item.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param key  The key or text associated with the menu item.
	 */
	public KeyedMenuItem(ContextMenu menu, String key, String icon) {
		this(menu, key, icon, null);
	}

	public KeyedMenuItem(ContextMenu menu, String key) {
		this(menu, key, "empty", null);
	}

	/**
	 * Gets the key associated with the menu item.
	 *
	 * @return The key associated with the menu item.
	 */
	public String getKey() {
		return ((KeyedText) lab).getKey();
	}

	/**
	 * Sets the key associated with the menu item.
	 * <p>
	 * @param key  The key or text associated with the menu item.
	 */
    public void setKey(String key) {
		((KeyedText) lab).setKey(key);
	}
}
