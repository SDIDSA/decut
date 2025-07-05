package org.luke.decut.ui;

import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class ProgressBar extends javafx.scene.control.ProgressBar implements Styleable {
	private final Window stage;
	
	public ProgressBar(Window stage) {
		this.stage = stage;
		
		applyStyle(stage.getStyl());
	}

	@Override
	public void applyStyle(Style style) {
		getChildren().addListener((Change<? extends Node> c) -> {
			c.next();
			StackPane stack1 = (StackPane) c.getAddedSubList().get(0);
			Styler.styleRegion(style, stack1);
		});
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
