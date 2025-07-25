package org.luke.gui.controls.input.check;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;

/**
 * a control that combines a stylized checkbox (Check) with a label (Label) in
 * an HBox.
 *
 * @author SDIDSA
 */
public class KeyedCheck extends HBox implements Styleable {
	private final Check check;
	private final KeyedText label;

	private boolean unset = true;

	/**
	 * Constructs a KeyedCheck instance with the specified window, key, and size.
	 *
	 * @param window The associated Window for styling.
	 * @param key    The text to display in the label.
	 * @param size   The size of the check mark.
	 */
	public KeyedCheck(Window window, String key, double size) {
		super(8);
		setAlignment(Pos.CENTER_LEFT);

		check = new Check(window, size);
		label = new KeyedText(window, key);

		check.setMouseTransparent(true);
		label.setMouseTransparent(true);

		setOnMouseClicked(_ -> check.flip());
		check.checkedProperty().addListener((_, _, _) -> unset = false);

		setCursor(Cursor.HAND);

		getChildren().addAll(check, label);

		unset = true;
		applyStyle(window.getStyl());
	}

	public void set(boolean val) {
		check.setChecked(val);
		unset = false;
	}

	public boolean get() {
		return check.isChecked();
	}

	public boolean isUnset() {
		return unset;
	}

	public void unset() {
		unset = true;
	}

	/**
	 * Gets the BooleanProperty for the checked property of the internal Check.
	 *
	 * @return The BooleanProperty for the checked property.
	 */
	public BooleanProperty property() {
		return check.checkedProperty();
	}

	/**
	 * Sets the font for the label.
	 *
	 * @param font The font to set.
	 */
	public void setFont(Font font) {
		label.setFont(font);
	}

	/**
	 * Sets the key text for the label.
	 *
	 * @param key The key text to set.
	 */
	public void setKey(String key) {
		label.setKey(key);
	}

	@Override
	public void applyStyle(Style style) {
		label.setFill(style.getTextNormal());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
