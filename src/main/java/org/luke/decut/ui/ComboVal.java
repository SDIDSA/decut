package org.luke.decut.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.input.combo.ComboInput;
import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

public class ComboVal extends VBox implements Styleable {
	private final ComboInput field;

	private final KeyedText lab;
	private final HBox bottom;

	public ComboVal(Window window, String name) {
		super(7);
		field = new ComboInput(window, new Font(12), name);

		field.setMinWidth(0);

		bottom = new HBox(10, field);

		HBox.setHgrow(this, Priority.ALWAYS);
		HBox.setHgrow(field, Priority.ALWAYS);

		lab = new KeyedText(window, name, new Font(14));
		getChildren().addAll(lab, bottom);
		
		applyStyle(window.getStyl());
	}

	public ComboInput getField() {
		return field;
	}

	public void setInputFont(Font font) {
		field.setFont(font);
	}

	public void addToBottom(Node node) {
		bottom.getChildren().add(node);
	}

	public String getValue() {
		return field.getValue();
	}

	public void setValue(String value) {
		field.setValue(value);
	}

	@Override
	public void applyStyle(Style style) {
		lab.setFill(style.getTextNormal());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}