package org.luke.gui;

import java.util.ArrayList;

public interface UiCache {
    void clearCache();

    ArrayList<UiCache> allCaches = new ArrayList<>();

    static void register(UiCache item) {
        allCaches.add(item);
    }

    static void clearAll() {
        for(UiCache item : allCaches) {
            item.clearCache();
        }
    }
}
