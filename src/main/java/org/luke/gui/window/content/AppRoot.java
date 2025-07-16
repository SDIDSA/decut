package org.luke.gui.window.content;

import javafx.scene.layout.HBox;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;
import org.luke.gui.window.content.app_bar.AppBar;
import org.luke.gui.window.content.app_bar.AppBarButton;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;

public class AppRoot extends BorderPane implements Styleable {

	private final AppBar bar;

	public AppRoot(Window window) {
		bar = new AppBar(window);
		setTop(bar);
		
		applyStyle(window.getStyl());
	}

	public HBox getMenuBar() {
		return bar.getMenuBar();
	}
	
	public void addBarButton(AppBarButton button) {
		addBarButton(0, button);
	}
	
	public void addBarButton(int index, AppBarButton button) {
		bar.addButton(index, button);
	}

	public void setOnInfo(Runnable runnable) {
		bar.setOnInfo(runnable);
	}
	
	public AppBarButton getInfo() {
		return bar.getInfo();
	} 
	
	public void setFill(Paint fill) {
		setBackground(Backgrounds.make(fill));
	}

	private Page old = null;
	public void setContent(Page page) {
		if (old != null) {
			old.destroy();
		}

		page.setup();
		setCenter(page);
		
		old = page;
	}

	public AppBar getAppBar() {
		return bar;
	}

	public void setIcon(String image) {
		bar.setIcon(image);
	}

	@Override
	public void applyStyle(Style style) {
		setFill(style.getBackgroundSecondary());
	}

}
