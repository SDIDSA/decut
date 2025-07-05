package org.luke.gui.controls.image;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import org.luke.gui.NodeUtils;
import org.luke.gui.style.ColorItem;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * a custom control that represents an icon with customizable colors. It
 * consists of an ImageView displaying an image and an optional Rectangle
 * overlay for color tinting. ColorIcon can be configured with an image name,
 * load size, display size, and an optional focus capability. It provides an
 * action callback that can be triggered on mouse click or space key press.
 *
 * @author SDIDSA
 */
public class ColorIcon extends StackPane implements Styleable, ColorItem {
	private final ImageView view;
	private final Rectangle overlay;

	private String name;
	private Image image;
	private double loadSize;
	private double displaySize;

	private Runnable action;

	private boolean crop = false;

	/**
	 * Constructs a ColorIcon with the specified image name, load size, display
	 * size, and focusable flag.
	 *
	 * @param name        The name of the image file.
	 * @param loadSize    The size used for loading the image.
	 * @param displaySize The size used for displaying the image.
	 * @param focusable   Flag indicating whether the control is focusable.
	 */
	public ColorIcon(String name, double loadSize, double displaySize, boolean focusable) {
		view = new ImageView();
		overlay = new Rectangle();
		overlay.setClip(view);

		if (name != null && !name.isBlank())
			setImage(name, loadSize, displaySize);

		if (focusable) {
			setFocusTraversable(true);
		}

		setOnMouseClicked(this::fire);
		setOnKeyPressed(this::fire);

		getChildren().addAll(overlay);
	}

	public void setColored(boolean colored) {
		getChildren().clear();
		overlay.setClip(null);
		overlay.setClip(colored ? view : null);
		getChildren().setAll(colored ? overlay : view);
	}

	public void round(double radius) {
		Rectangle clip = new Rectangle();
		if(radius == -1) {
			clip.arcHeightProperty().bind(this.widthProperty());
			clip.arcWidthProperty().bind(this.heightProperty());
		} else {
			clip.setArcHeight(radius * 2);
			clip.setArcWidth(radius * 2);
		}
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());
		setClip(clip);
	}

	/**
	 * Constructs a ColorIcon with the specified image name, load size, and display
	 * size.
	 *
	 * @param name        The name of the image file.
	 * @param loadSize    The size used for loading the image.
	 * @param displaySize The size used for displaying the image.
	 */
	public ColorIcon(String name, double loadSize, double displaySize) {
		this(name, loadSize, displaySize, false);
	}

	public ColorIcon(String name, double size, boolean focusable) {
		this(name, size, size, focusable);
	}

	private void fire(MouseEvent dismiss) {
		fire();
	}

	private void fire(KeyEvent e) {
		if (e.getCode().equals(KeyCode.SPACE)) {
			fire();
			e.consume();
		}
	}

	public void fire() {
		if (action != null) {
			action.run();
		}
	}

	/**
	 * Sets the action callback to be executed on icon click or key press.
	 *
	 * @param action The action to be executed.
	 */
	public void setAction(Runnable action) {
		this.action = action;
	}

	public ColorIcon(String name, double size) {
		this(name, size, size, false);
	}

	public void setName(String name) {
		setImage(name, loadSize, displaySize);
	}

	public void setSize(int size) {
		setImage(name, size);
	}

	public void setSize(int loadSize, int displaySize) {
		setImage(name, loadSize, displaySize);
	}

	public void setImage(String name, double size) {
		setImage(name, size, size);
	}

	public void setImage(String name) {
		view.setImage(ImageProxy.loadResize(name, loadSize, displaySize));
	}

	public void setCrop(boolean crop) {
		this.crop = crop;
		setImage(name, displaySize);
	}

	/**
	 * Sets the image for the ColorIcon based on the specified image name and sizes.
	 *
	 * @param name        The name of the image file.
	 * @param loadSize    The size used for loading the image.
	 * @param displaySize The size used for displaying the image.
	 */
	public void setImage(String name, double loadSize, double displaySize) {
		this.name = name;
		this.loadSize = loadSize;
		this.displaySize = displaySize;

		setMinSize(displaySize, displaySize);
		setMaxSize(displaySize, displaySize);

		if(name.contains("http")) {
			ImageProxy.asyncLoad(name, loadSize, img -> {
				double w = crop ? displaySize : img.getWidth();
				double h = crop ? displaySize : img.getHeight();

				view.setImage(img);
				setAlignment(Pos.CENTER);

				setMinSize(w, h);
				setMaxSize(w, h);
				setPrefSize(w, h);

				overlay.setWidth(w);
				overlay.setHeight(h);
			});
		} else {
			Image img = loadSize == displaySize ? ImageProxy.load(name, loadSize)
					: ImageProxy.loadResize(name, loadSize, displaySize);
			double w = img.getWidth();
			double h = img.getHeight();

			view.setImage(img);

			setMinSize(w, h);
			setMaxSize(w, h);

			overlay.setWidth(w);
			overlay.setHeight(h);
		}
	}

	public void setImage(Image image, ObservableDoubleValue size) {
		this.image = image;
		unbindSize();
		view.setImage(image);
		bindSize(size);
	}

	public void bindSize(ObservableDoubleValue size) {
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

		prefHeightProperty().bind(size);
		prefWidthProperty().bind(size);

		overlay.widthProperty().bind(size);
		overlay.heightProperty().bind(size);
		view.fitWidthProperty().bind(size);
		view.fitHeightProperty().bind(size);
	}

	public void unbindSize() {
		overlay.widthProperty().unbind();
		overlay.heightProperty().unbind();
		view.fitWidthProperty().unbind();
		view.fitHeightProperty().unbind();
	}

	public void setPadding(double val) {
		setMinSize(displaySize + val * 2, displaySize + val * 2);
		setMaxSize(displaySize + val * 2, displaySize + val * 2);
	}

	/**
	 * Gets the fill property of this color icon.
	 *
	 * @return The fill property of the color icon.
	 */
	public ObjectProperty<Paint> fillProperty() {
		return overlay.fillProperty();
	}

	/**
	 * Sets the fill color for the color icon.
	 *
	 * @param fill The fill color to be set.
	 */
	public void setFill(Paint fill) {
		overlay.setFill(fill);
	}

	@Override
	public Node getNode() {
		return this;
	}

    @Override
	public void applyStyle(Style style) {
		NodeUtils.focusBorder(this, style.getTextLink());
	}
}
