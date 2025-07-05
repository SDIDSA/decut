package org.luke.decut.ui;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.input.text.DeprecatedTextInput;
import org.luke.gui.window.Window;

public class TextField extends DeprecatedTextInput {
	public TextField(Window window) {
		super(window, Font.DEFAULT, "");
		setFont(new Font(12));
		applyStyle(window.getStyl());
	}
}
