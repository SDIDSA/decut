package org.luke.decut.settings.abs;

import java.util.ArrayList;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.controls.space.FixedVSpace;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsContent extends VBox implements Styleable {
	protected final Settings settings;
	
	private final ArrayList<KeyedText> headers = new ArrayList<>();

	public SettingsContent(Settings settings) {
		this.settings = settings;
		setPadding(new Insets(30, 40, 80, 40));
		
		setMinWidth(0);
		maxWidthProperty().bind(settings.contentWidth());
	}
	
	public void addHeader(Window win, String s, Node...nodes) {
		KeyedText lab = new KeyedText(win, s, new Font(Font.DEFAULT_FAMILY_MEDIUM, 16));
		headers.add(lab);
		
		HBox hbox = new HBox(20, nodes);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.getChildren().addFirst(lab);
		
		getChildren().addAll(new FixedVSpace(15), hbox, new FixedVSpace(15));
	}

	public void separate(Window win, double margin) {
		Separator sep = new Separator(win, Orientation.HORIZONTAL);
		setMargin(sep, new Insets(margin, 0, margin, 0));
		getChildren().add(sep);
	}

	@Override
	public void applyStyle(Style style) {
		headers.forEach(h -> h.setFill(style.getHeaderPrimary()));
	}

}
