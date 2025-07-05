package org.luke.decut.app.home.menubar;

import org.luke.decut.app.home.Home;
import org.luke.gui.controls.button.MenuBarButton;

public class HomeMenuButton extends MenuBarButton {
    public HomeMenuButton(Home home, String key) {
        super(home.getWindow(), key);
    }
}
