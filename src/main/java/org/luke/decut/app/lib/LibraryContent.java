package org.luke.decut.app.lib;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.Home;
import org.luke.gui.UiCache;
import org.luke.gui.exception.ErrorHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryContent extends VBox {
    private static final ConcurrentHashMap<Class<? extends LibraryContent>, LibraryContent> cache =
            new ConcurrentHashMap<>();

    static {
        UiCache.register(LibraryContent::clearCache);
    }

    private final Home owner;

    public LibraryContent(Home owner) {
        this.owner = owner;
    }

    public Home getOwner() {
        return owner;
    }

    public void setup() {

    }

    public void destroy() {

    }

    public synchronized static <T extends LibraryContent> T getInstance(Home owner, Class<T> type) {
        LibraryContent found = cache.get(type);
        if (found == null || found.getOwner() != owner) {
            try {
                found = type.getConstructor(Home.class).newInstance(owner);
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

    public static boolean hasInstance(Class<? extends LibraryContent> type) {
        return cache.containsKey(type);
    }

    public static void clearCache() {
        cache.clear();
    }
}
