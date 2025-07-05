package org.luke.gui.controls.input.radio;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.luke.gui.controls.popup.context.items.RadioMenuItem;

/**
 * a utility class for grouping Radio controls. It provides a way to manage a
 * group of Radio controls where only one radio button within the group can be
 * selected at a time. It can be initialized with either Radio or KeyedRadio
 * controls.
 *
 * @author SDIDSA
 */
public class RadioGroup {
	private final ArrayList<Radio> radios;

	private final ObjectProperty<Radio> value;

	public RadioGroup() {
		radios = new ArrayList<>();

		value = new SimpleObjectProperty<>();
	}

	/**
	 * Constructs a RadioGroup with the specified Radio controls.
	 *
	 * @param items The Radio controls to include in the group.
	 */
	public RadioGroup(Radio... items) {
		radios = new ArrayList<>();

		value = new SimpleObjectProperty<>();

		for (Radio radio : items) {
			add(radio);
		}
	}

	/**
	 * Constructs a RadioGroup with the specified KeyedRadio controls.
	 *
	 * @param items The KeyedRadio controls to include in the group.
	 */
	public RadioGroup(KeyedRadio... items) {
		radios = new ArrayList<>();

		value = new SimpleObjectProperty<>();

		for (KeyedRadio radio : items) {
			add(radio.getCheck());
		}
	}

	public RadioGroup(RadioMenuItem... items) {
		radios = new ArrayList<>();

		value = new SimpleObjectProperty<>();

		for (RadioMenuItem radio : items) {
			add(radio.getRadio());
		}
	}

	public RadioGroup(List<RadioMenuItem> items) {
		this(items.toArray(new RadioMenuItem[0]));
	}

	/**
	 * Adds a Radio control to the group.
	 *
	 * @param radio The Radio control to add.
	 */
	public void add(Radio radio) {
		radio.checkedProperty().addListener((_, _, nv) -> {
			if (nv) {
				value.set(radio);
				radios.forEach(e -> {
					if (e != radio) {
						e.setChecked(false);
					}
				});
			}
		});

		radios.add(radio);
	}

	/**
	 * Gets the currently selected Radio control in the group.
	 *
	 * @return The currently selected Radio control.
	 */
	public Radio getValue() {
		return value.get();
	}

	/**
	 * Gets the ObjectProperty for the currently selected Radio control.
	 *
	 * @return The ObjectProperty for the currently selected Radio control.
	 */
	public ObjectProperty<Radio> valueProperty() {
		return value;
	}
}
