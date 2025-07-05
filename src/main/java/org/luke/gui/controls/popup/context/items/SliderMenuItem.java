package org.luke.gui.controls.popup.context.items;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.luke.gui.controls.input.slider.Slider;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.space.FixedHSpace;

/**
 * Custom implementation of a check menu item, extending {@link KeyedMenuItem}.
 * Represents a menu item with an associated check mark that can be toggled on
 * or off. Manages the appearance, behavior, and interaction of the check menu
 * item.
 *
 * @author SDIDSA
 */
public class SliderMenuItem extends MenuItem {

	private final Slider slider;

	/**
	 * Constructs a check menu item with the specified parent context menu, key, and
	 * fill color.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param fill The fill color of the menu item.
	 */
	public SliderMenuItem(ContextMenu menu, String key, String icon, Color fill, double min, double max) {
		super(menu, key, icon, fill);

		slider = new Slider(menu.getOwner(), 12, min, max);
		slider.invertedProperty().bind(active);

		HBox.setHgrow(slider, Priority.ALWAYS);

		getChildren().addAll(slider, new FixedHSpace(0));

		setHideOnAction(false);
	}

	public SliderMenuItem(ContextMenu menu, Color fill, double min, double max) {
		this(menu, null, "empty", fill, min, max);
	}

	public SliderMenuItem(ContextMenu menu, String icon, double min, double max) {
		this(menu, null, icon, null, min, max);
	}

	public SliderMenuItem(ContextMenu menu, String key, String icon, double min, double max) {
		this(menu, key, icon, null, min, max);
	}

	public void setValue(double value) {
		slider.valueProperty().set(value);
	}

	public DoubleProperty valueProperty() {
		return slider.valueProperty();
	}
}
