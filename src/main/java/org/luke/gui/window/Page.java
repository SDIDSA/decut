package org.luke.gui.window;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.luke.gui.UiCache;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.style.Styleable;

import javafx.beans.binding.DoubleExpression;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class Page extends StackPane implements Styleable {
	public static final Dimension DEFAULT_WINDOW_MINSIZE = new Dimension(1280,720);
	private static final ConcurrentHashMap<Class<? extends Page>, Page> cache = new ConcurrentHashMap<>();

	static {
		UiCache.register(Page::clearCache);
	}
	
	protected Window window;
	protected Dimension minSize;

	public Page(Window window, Dimension minSize) {
		this.window = window;
		this.minSize = minSize;

		Rectangle clipBottom = new Rectangle();
		double arc = 13;
		clipBottom.setArcHeight(arc);
		clipBottom.setArcWidth(arc);

		Rectangle clipTop = new Rectangle();
		
		widthProperty().addListener((_, _, nv) -> {
			clipBottom.setWidth(nv.doubleValue());
			clipTop.setWidth(nv.doubleValue());

			setClip(Shape.union(clipBottom, clipTop));
		});

		heightProperty().addListener((_, _, nv) -> {
			clipTop.setHeight(nv.doubleValue() / 2 + arc);
			clipBottom.setHeight(nv.doubleValue() / 2);
			clipBottom.setY(nv.doubleValue() / 2);

			setClip(Shape.union(clipBottom, clipTop));
		});


		clipBottom.setArcHeight(arc);
		clipBottom.setArcWidth(arc);

		DoubleExpression height = window.heightProperty()
				.subtract(window.getAppBar().heightProperty());

		DoubleExpression width = window.widthProperty();

		setMinHeight(0);
		maxHeightProperty().bind(height);
		setMinWidth(0);
		maxWidthProperty().bind(width);
	}

	protected Page(Window window) {
		this(window, DEFAULT_WINDOW_MINSIZE);
	}

	public Window getWindow() {
		return window;
	}

	public JSONObject getJsonData(String key) {
		return window.getJsonData(key);
	}

	public void setup() {
		window.setMinSize(minSize);

	}

	public void destroy() {

	}

	public static boolean hasInstance(Class<? extends Page> type) {
		return cache.containsKey(type);
	}

	public synchronized static <T extends Page> T getInstance(Window owner, Class<T> type) {
		Page found = cache.get(type);
		if (found == null || found.getWindow() != owner) {
			try {
				found = type.getConstructor(Window.class).newInstance(owner);
				cache.put(type, found);
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				ErrorHandler.handle(e, "creating page instance of type " + type.getName());
			}
		}
		if (!type.isInstance(found)) {
			ErrorHandler.handle(new RuntimeException("incorrect page type"),
					"loading page of type " + type.getName());
		}
		return type.cast(found);
	}

	public static void clearCache() {
		cache.clear();
	}
}
