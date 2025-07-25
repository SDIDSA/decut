package org.luke.gui.controls.popup.context.items;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.controls.text.unkeyed.Text;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Custom implementation of a menu item, extending {@link HBox} and implementing
 * {@link Styleable}. Represents an item within a {@link ContextMenu} that can
 * trigger an action when selected. Manages the appearance, behavior, and
 * interaction of the menu item.
 *
 * @author SDIDSA
 */
public class MenuItem extends HBox implements Styleable {
	protected Text lab;
	protected BooleanProperty active;

	private final ContextMenu menu;

	private Runnable action;

	private final Color fill;

	private boolean hideOnAction = true;

	private KeyCombination accelerator;
	private Text acceleratorLabel;

	private final ColorIcon icon;

	/**
	 * Constructs a menu item with the specified key, fill color, and whether it is
	 * keyed.
	 *
	 * @param menu  The parent {@link ContextMenu} to which this menu item belongs.
	 * @param key   The key or text associated with the menu item.
	 * @param fill  The fill color of the menu item.
	 * @param keyed Indicates whether the menu item is localized.
	 */
	public MenuItem(ContextMenu menu, String key, String iconName, Color fill, boolean keyed) {
		this.menu = menu;
		this.fill = fill;
		setAlignment(Pos.CENTER_LEFT);
		setPadding(new Insets(8));
		setCursor(Cursor.HAND);
		setSpacing(10);

		Font f = new Font(Font.DEFAULT_FAMILY_MEDIUM, 12);
		lab = keyed ? new KeyedText(menu.getOwner(), key, f) : new Text(key, f);

		icon = new ColorIcon(iconName, 14);

		getChildren().addAll(icon);
		if(key != null && !key.isBlank()) {
			getChildren().addAll(lab, new ExpandingHSpace());
		}

		ColorAdjust ca = new ColorAdjust();

		setEffect(ca);

		ca.brightnessProperty().bind(Bindings.when(pressedProperty()).then(-.2).otherwise(0));

		active = new SimpleBooleanProperty(false);

		applyStyle(menu.getOwner().getStyl());
	}

	public MenuItem removeIcon() {
		getChildren().remove(icon);
		return this;
	}

	/**
	 * Sets the text of the menu item.
	 *
	 * @param text The text to set for the menu item.
	 */
	public void setText(String text) {
		lab.set(text);
	}

	/**
	 * Sets whether the menu item should hide after the action is performed.
	 *
	 * @param hideOnAction {@code true} if the menu item should hide, {@code false}
	 *                     otherwise.
	 */
	public void setHideOnAction(boolean hideOnAction) {
		this.hideOnAction = hideOnAction;
	}

	/**
	 * Checks if the menu item should hide after the action is performed.
	 *
	 * @return {@code true} if the menu item should hide, {@code false} otherwise.
	 */
	public boolean isHideOnAction() {
		return hideOnAction;
	}

	/**
	 * Gets the accelerator key combination associated with the menu item.
	 *
	 * @return The accelerator key combination.
	 */
	public KeyCombination getAccelerator() {
		return accelerator;
	}

	/**
	 * Sets the accelerator key combination for the menu item.
	 *
	 * @param accelerator The accelerator key combination.
	 */
	public void setAccelerator(KeyCombination accelerator) {
		this.accelerator = accelerator;
	}

	/**
	 * Sets the accelerator key combination for the menu item using a string
	 * representation. Also updates the display of the accelerator in the menu item.
	 *
	 * @param keyComb The string representation of the accelerator key combination.
	 */
	public void setAccelerator(String keyComb) {
		setAccelerator(KeyCombination.keyCombination(keyComb));
		String text = accelerator.getName();
		if (acceleratorLabel == null) {
			acceleratorLabel = new Text(text, new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));
			acceleratorLabel.fillProperty().bind(lab.fillProperty());
			getChildren().add(acceleratorLabel);
		} else {
			acceleratorLabel.set(text);
		}
	}

	/**
	 * Constructs a menu item with the specified parent context menu, key, and fill
	 * color.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param text The text associated with the menu item.
	 * @param fill The fill color of the menu item.
	 */
	public MenuItem(ContextMenu menu, String text, String icon, Color fill) {
		this(menu, text, icon, fill, false);
	}

	public MenuItem(ContextMenu menu, String icon, Color fill) {
		this(menu, "", icon, fill, false);
	}

	/**
	 * Constructs a menu item with the specified parent context menu and key.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param text The text associated with the menu item.
	 */
	public MenuItem(ContextMenu menu, String text, String icon) {
		this(menu, text, icon, null, false);
	}

	public MenuItem(ContextMenu menu, String icon) {
		this(menu, "", icon, null, false);
	}

	/**
	 * Constructs a menu item with the specified parent context menu, text, and
	 * boolean indicator.
	 *
	 * @param menu The parent {@link ContextMenu} to which this menu item belongs.
	 * @param text The text associated with the menu item.
	 * @param keyed Indicates whether the menu item is localized.
	 */
	public MenuItem(ContextMenu menu, String text, String icon, boolean keyed) {
		this(menu, text, icon, null, keyed);
	}

	/**
	 * Sets the action to be performed when the menu item is selected.
	 *
	 * @param action The action to be performed.
	 */
	public void setAction(Runnable action) {
		this.action = action;
	}

	public MenuItem setColored(boolean colored) {
		icon.setColored(colored);
		return this;
	}

	/**
	 * Executes the action associated with the menu item and hides the context menu
	 * if specified.
	 */
	public void fire() {
		if (action != null) {
			try {
				action.run();
			} catch (Exception x) {
				ErrorHandler.handle(x, "fire action for menu item [" + lab.getText() + "]");
			}
		}
		if (hideOnAction && !(this instanceof MenuMenuItem)) {
			menu.hide();
			ContextMenu parent = menu.getParent();
			while (parent != null) {
				parent.hide();
				parent = parent.getParent();
			}
		}
	}

	/**
	 * Sets the active state of the menu item.
	 *
	 * @param active {@code true} to set the menu item as active, {@code false}
	 *               otherwise.
	 */
	public void setActive(boolean active) {
		this.active.set(active);
	}

	@Override
	public void applyStyle(Style style) {
		lab.fillProperty().bind(Bindings.when(active).then(style.getTextOnAccent())
				.otherwise(fill == null ? style.getInteractiveNormal() : fill));
		icon.fillProperty().bind(Bindings.when(active).then(style.getTextOnAccent())
				.otherwise(fill == null ? style.getInteractiveNormal() : fill));
		backgroundProperty().bind(Bindings.when(active)
				.then(Backgrounds.make(fill == null ? style.getAccent() : fill, 7)).otherwise(Background.EMPTY));
	}
}
