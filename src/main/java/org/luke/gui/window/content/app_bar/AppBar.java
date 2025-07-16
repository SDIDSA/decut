package org.luke.gui.window.content.app_bar;

import javafx.geometry.NodeOrientation;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HeaderDragType;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class AppBar extends HeaderBar implements Styleable {
	private final Window window;
	private final AppBarButton info;
	private final ColorIcon icon;

	private final HBox buttons;
	private final HBox menuBar;
	
	public AppBar(Window window) {
		this.window = window;
		setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		icon = new ColorIcon(null, 20);
		icon.setMouseTransparent(true);

		menuBar = new HBox(15);
		menuBar.setAlignment(Pos.CENTER_LEFT);

		buttons = new HBox(4);
		buttons.setAlignment(Pos.CENTER);

		info = new AppBarButton(window, "info");
		HBox.setMargin(info, new Insets(0, 8, 0, 0));
		
		buttons.getChildren().addAll(info);

		HBox leading = new HBox(10, icon, menuBar);
		leading.setPadding(new Insets(0,0,0,7));
		leading.setAlignment(Pos.CENTER_LEFT);
		setLeading(leading);
		setTrailing(buttons);
		//getChildren().addAll(icon, menuBar, new ExpandingHSpace(), buttons);
		
		applyStyle(window.getStyl());
	}

	public void setOnInfo(Runnable action) {
		info.setAction(action);
	}
	
	public void addButton(int index, AppBarButton button) {
		HBox.setMargin(button, new Insets(0, 8, 0, 0));

		buttons.getChildren().add(index, button);
	}

	public HBox getMenuBar() {
		return menuBar;
	}

	public void addButton(AppBarButton button) {
		addButton(0, button);
	}
	
	public AppBarButton getInfo() {
		return info;
	}
	
	@Override
	public void applyStyle(Style style) {
		icon.setFill(style.getAccent());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

	public void setIcon(String image) {
		icon.setImage(image, 20);
	}
}
