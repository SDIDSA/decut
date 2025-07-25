package org.luke.gui.window;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.geometry.NodeOrientation;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import org.luke.gui.controls.image.ImageProxy;
import org.luke.gui.locale.Locale;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;
import org.luke.gui.window.content.AppPreRoot;
import org.luke.gui.window.content.TransparentScene;
import org.luke.gui.window.content.app_bar.AppBar;
import org.luke.gui.window.content.app_bar.AppBarButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * custom JavaFX Stage with additional functionality.
 *
 * @author SDIDSA
 */
public class Window extends Stage {
    // Data associated with the window
    private final HashMap<String, Object> data = new HashMap<>();

    // Listeners to be executed on window close
    private final ArrayList<Runnable> onClose = new ArrayList<>();

    // Object properties for style and locale
    private final ObjectProperty<Style> style;
    private final ObjectProperty<Locale> locale;

    // Root content of the window
    private final AppPreRoot root;

    // Reference to the JavaFX application
    private final Application app;
    // Load a page into the window
    private Page loadedPage;

    // Constructor to initialize the window with a specified style and locale
    public Window(Application app, Style style, Locale locale) {
        super();
        this.app = app;
        this.style = new SimpleObjectProperty<>(style);
        this.locale = new SimpleObjectProperty<>(locale);

        initStyle(StageStyle.EXTENDED);

        root = new AppPreRoot(this);

        setStyle(style);
        setLocale(locale);

        TransparentScene scene = new TransparentScene(root, 500, 500);

        setScene(scene);

        // Request focus when the window is shown
        setOnShown(e -> root.requestFocus());

        // Handle close request to execute custom close actions
        setOnCloseRequest(e -> {
            e.consume();
            close();
        });
    }

    // Getter for the JavaFX application
    public Application getApp() {
        return app;
    }

    // Add a button to the app bar
    public void addBarButton(AppBarButton button) {
        addBarButton(0, button);
    }

    // Add a button to the app bar at a specified index
    public void addBarButton(int index, AppBarButton button) {
        root.addBarButton(index, button);
    }

    // Set a custom action for the info button
    public void setOnInfo(Runnable runnable) {
        root.setOnInfo(runnable);
    }

    // Get the info button from the app bar
    public AppBarButton getInfo() {
        return root.getInfo();
    }

    // Open a link in the default browser
    public void openLink(String link) {
        app.getHostServices().showDocument(link);
    }

    // Add a custom action to be executed on window close
    public void addOnClose(Runnable runnable) {
        onClose.add(runnable);
    }

    // Get the operating system name
    public String getOsName() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("win") == 0 ? "Windows" : ""; // Will handle other operating systems when targeting them
    }

    // Get the app bar from the window
    public AppBar getAppBar() {
        return root.getAppBar();
    }

    // Load a page asynchronously
    public void loadPage(Class<? extends Page> type, Runnable onFinish) {
        //new code
        Platform.runBack(
                () -> Page.getInstance(this, type),
                page -> {
                    loadPage(page);
                    if (onFinish != null) {
                        onFinish.run();
                    }
                });
    }

    // Load a page asynchronously
    public void loadPage(Class<? extends Page> type) {
        loadPage(type, null);
    }

    private void loadPage(Page page) {
        loadedPage = page;
        root.setContent(page);
        centerOnScreen();
    }

    // Get the currently loaded page
    public Page getLoadedPage() {
        return loadedPage;
    }

    // Set the fill color of the window
    public void setFill(Paint fill) {
        root.setFill(fill);
    }

    // Get the style property of the window
    public ObjectProperty<Style> getStyl() {
        return style;
    }

    // Get the locale property of the window
    public ObjectProperty<Locale> getLocale() {
        return locale;
    }

    // Set the locale of the window
    public void setLocale(Locale locale) {
        this.locale.set(locale);
        root.setNodeOrientation(locale.isRtl() ?
                NodeOrientation.RIGHT_TO_LEFT :
                NodeOrientation.LEFT_TO_RIGHT);
    }

    // Set the style of the window
    public void setStyle(Style style) {
        this.style.set(style);
    }

    // Get the root content of the window
    public AppPreRoot getRoot() {
        return root;
    }

    public HBox getMenuBar() {
        return root.getMenuBar();
    }

    // Set the minimum size of the window
    public void setMinSize(Dimension d) {
        setMinWidth(d.getWidth());
        setMinHeight(d.getHeight());
    }

    // Close the window
    @Override
    public void close() {
        onClose.forEach(Runnable::run);
        super.close();
    }

    // Put data into the window data map
    public void putData(String key, Object value) {
        data.put(key, value);
    }

    // Get JSON data from the window data map
    public JSONObject getJsonData(String key) throws IllegalStateException {
        return getOfType(key, JSONObject.class);
    }

    // Get data of a specific type from the window data map
    private <T> T getOfType(String key, Class<? extends T> type) {
        Object obj = data.get(key);

        if (type.isInstance(obj)) {
            return type.cast(obj);
        } else {
            throw new IllegalStateException("no " + type.getSimpleName() + " was found at key " + key);
        }
    }

    // Set the taskbar icon of the window
    public void setTaskIcon(String image) {
        Image m = ImageProxy.load(image, 256);
        for (int i = 16; i <= 128; i *= 2) {
            getIcons().add(ImageProxy.resize(m, i));
        }
    }

    // Set the window icon
    public void setWindowIcon(String image) {
        root.setIcon(image);
    }
}