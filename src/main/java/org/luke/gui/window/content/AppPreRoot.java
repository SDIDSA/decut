package org.luke.gui.window.content;

import javafx.scene.layout.HBox;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;
import org.luke.gui.window.content.app_bar.AppBar;
import org.luke.gui.window.content.app_bar.AppBarButton;

import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class AppPreRoot extends StackPane {
	public static final double DEFAULT_PADDING = 15;
	
	private final AppRoot root;
	
	public AppPreRoot(Window window) {
		setBackground(Background.EMPTY);
		
		root = new AppRoot(window);
		getChildren().setAll(root);
	}

	public HBox getMenuBar() {
		return root.getMenuBar();
	}
	
	public void addBarButton(AppBarButton button) {
		addBarButton(0, button);
	}
	
	public void addBarButton(int index, AppBarButton button) {
		root.addBarButton(index, button);
	}

	public void setOnInfo(Runnable runnable) {
		root.setOnInfo(runnable);
	}
	
	public AppBarButton getInfo() {
		return root.getInfo();
	}
	
	public void setFill(Paint fill) {
		root.setFill(fill);
	}
	
	public void setBorder(Paint fill, double width) {
		root.setBorderFill(fill, width);
	}
	
	public void setContent(Page page) {
		root.setContent(page);
	}

	public AppBar getAppBar() {
		return root.getAppBar();
	}

	public void setIcon(String image) {
		root.setIcon(image);
	}
}
