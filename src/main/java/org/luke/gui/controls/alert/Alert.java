package org.luke.gui.controls.alert;

import java.util.EnumMap;
import java.util.function.Function;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.text.MultiText;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.FixedVSpace;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * represents a customizable overlay dialog for displaying informative messages
 * and interacting with the user. It extends the Overlay class and implements
 * the Styleable interface for consistent styling. Alerts can have a header,
 * body with text or links, customizable buttons, and an optional loading state.
 *
 * @author SDIDSA
 */
public class Alert extends Overlay implements Styleable {
	private final StackPane preRoot;
	private final HBox bottom;

	protected ColorIcon closeIcon;
	protected VBox root;

	private final MultiText head;
	private final MultiText body;

	private Function<Style, Color> bodyFill;

	private final EnumMap<ButtonType, Runnable> actions;
	private final EnumMap<ButtonType, AlertButton> buttons;

	public Alert(Pane owner, Window window, AlertType type, double width) {
		super(owner, window);
		preRoot = new StackPane();
		preRoot.setAlignment(Pos.TOP_RIGHT);
		preRoot.setMaxWidth(width);

		closeIcon = new ColorIcon("close", 16, true);
		closeIcon.setPadding(8);
		closeIcon.setAction(this::hide);
		closeIcon.setCursor(Cursor.HAND);
		closeIcon.applyStyle(window.getStyl());
		StackPane.setMargin(closeIcon, new Insets(10));

		root = new VBox();
		root.setPadding(new Insets(16));
		root.setPickOnBounds(false);

		head = new MultiText(window, "", new Font(Font.DEFAULT_FAMILY_MEDIUM, 16));
		head.setMouseTransparent(true);
		VBox.setMargin(head, new Insets(0, 0, 16, 0));

		body = new MultiText(window);
		body.setLineSpacing(5);

		root.getChildren().addAll(head, body, new FixedVSpace(10));

		preRoot.getChildren().addAll(closeIcon, root);

		bottom = new HBox(8);
		bottom.setMaxWidth(width);
		bottom.setPadding(new Insets(16));

		bottom.getChildren().add(new ExpandingHSpace());

		actions = new EnumMap<>(ButtonType.class);
		buttons = new EnumMap<>(ButtonType.class);

		for (ButtonType buttonType : type.getButtons()) {
			AlertButton button = new AlertButton(this, buttonType);
			button.setAction(() -> {
				Runnable action = actions.get(buttonType);
				if (action == null) {
					hide();
				} else {
					action.run();
				}
			});
			bottom.getChildren().add(button);
			buttons.put(buttonType, button);
		}

		if(!type.getIcon().isBlank()) {
			ColoredIcon icon = new ColoredIcon(window, type.getIcon(), 32, Style::getAccent);
			bottom.getChildren().addFirst(icon);
		}

		bodyFill = Style::getTextNormal;

		setContent(preRoot, bottom);
		applyStyle(window.getStyl());
	}

	public void setButtonTypes(ButtonType... types) {
		bottom.getChildren().removeAll(buttons.values());
		actions.clear();
		buttons.clear();
		for (ButtonType buttonType : types) {
			AlertButton button = new AlertButton(this, buttonType);
			button.setAction(() -> {
				Runnable action = actions.get(buttonType);
				if (action == null) {
					hide();
				} else {
					action.run();
				}
			});
			bottom.getChildren().add(button);
			buttons.put(buttonType, button);
		}

		applyStyle(getWindow().getStyl());
	}

	public BooleanProperty disableProperty(ButtonType type) {
		AlertButton button = buttons.get(type);
		if (button != null) {
			return button.disableProperty();
		}

		return null;
	}

	public void startLoading(ButtonType type) {
		AlertButton button = buttons.get(type);
		if (button != null) {
			button.startLoading();
		}
	}

	public void stopLoading(ButtonType type) {
		AlertButton button = buttons.get(type);
		if (button != null) {
			button.stopLoading();
		}
	}

	public void addToBody(Node... node) {
		root.getChildren().addAll(node);
	}

	public Alert(Page page, AlertType type, double width) {
		this(page, page.getWindow(), type, width);
	}

	public Alert(Pane owner, Window window, AlertType type) {
		this(owner, window, type, 440);
	}

	public Alert(Page page, AlertType type) {
		this(page, type, 440);
	}

	public Alert(Page page) {
		this(page, AlertType.INFO, 440);
	}

	public void addAction(ButtonType type, Runnable action) {
		actions.put(type, action);
	}

	public void setHead(String key) {
		head.setKey(key);
	}

	public void addLabel(String key) {
		addLabel(key, new Font(14));
	}

	public void addLabel(String key, Font font) {
		body.addLabel(key, font);
	}

	public void addUnkeyedLabel(String key) {
		addUnkeyedLabel(key, new Font(14));
	}

	public void addUnkeyedLabel(String key, Font font) {
		body.addLabel(key, font);
	}

	public void addLink(String key) {
		body.addLink(key, new Font(14));
	}

	public void setBodyAction(int index, Runnable action) {
		body.setAction(index, action);
	}

	public void clearBody() {
		body.clear();
	}

	public void centerBody() {
		body.center();
	}

	public void setBodyFill(Function<Style, Color> bodyFill, Style style) {
		this.bodyFill = bodyFill;
		body.setFill(bodyFill.apply(style));
	}

	@Override
	public void applyStyle(Style style) {
		preRoot.setBackground(Backgrounds.make(style.getBackgroundSecondary(), new CornerRadii(8, 8, 0, 0, false)));
		bottom.setBackground(Backgrounds.make(style.getBackgroundFloating(), new CornerRadii(0, 0, 8, 8, false)));

		closeIcon.fillProperty().bind(Bindings.when(closeIcon.hoverProperty()).then(style.getHeaderPrimary())
				.otherwise(style.getHeaderSecondary()));

		head.setFill(style.getHeaderPrimary());
		body.setFill(bodyFill.apply(style));
	}

}
