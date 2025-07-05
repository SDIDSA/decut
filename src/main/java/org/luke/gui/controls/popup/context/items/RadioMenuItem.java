package org.luke.gui.controls.popup.context.items;

import javafx.beans.property.BooleanProperty;
import javafx.scene.paint.Color;
import org.luke.gui.controls.input.check.Check;
import org.luke.gui.controls.input.radio.Radio;
import org.luke.gui.controls.popup.context.ContextMenu;

/**
 * Custom implementation of a check menu item, extending {@link KeyedMenuItem}.
 * Represents a menu item with an associated check mark that can be toggled on
 * or off. Manages the appearance, behavior, and interaction of the check menu
 * item.
 *
 * @author SDIDSA
 */
public class RadioMenuItem extends KeyedMenuItem {

	private final Radio radio;

	/**
	 * Constructs a check menu item with the specified parent context menu, key, and
	 * fill color.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param key  The key or text associated with the menu item.
	 * @param fill The fill color of the menu item.
	 */
	public RadioMenuItem(ContextMenu menu, String key, String icon, Color fill) {
		super(menu, key, icon, fill);

		radio = new Radio(menu.getOwner(), 14);

		radio.invertedProperty().bind(active);

		radio.setMouseTransparent(true);

		getChildren().add(radio);

		setAction(radio::flip);

		setHideOnAction(false);
	}

	public RadioMenuItem(ContextMenu menu, String key, Color fill) {
		this(menu, key, "empty", fill);
	}

	public RadioMenuItem(ContextMenu menu, String key, String icon) {
		this(menu, key, icon, null);
	}

	public RadioMenuItem(ContextMenu menu, String key) {
		this(menu, key, "empty", null);
	}

	/**
	 * Sets whether the check mark of the menu item is checked.
	 *
	 * @param checked {@code true} to set the check mark as checked, {@code false}
	 *                otherwise.
	 */
	public void setChecked(boolean checked) {
		radio.setChecked(checked);
	}

	/**
	 * Gets the boolean property representing the checked state of the check mark.
	 *
	 * @return The boolean property representing the checked state.
	 */
	public BooleanProperty checkedProperty() {
		return radio.checkedProperty();
	}

	public Radio getRadio() {
		return radio;
	}
}
