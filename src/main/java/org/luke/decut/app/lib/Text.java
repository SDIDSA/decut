package org.luke.decut.app.lib;

import org.luke.decut.app.home.Home;
import org.luke.gui.controls.text.keyed.ColoredKeyedText;
import org.luke.gui.style.Style;

public class Text extends LibraryContent {
    public Text(Home owner) {
        super(owner);

        getChildren().add(
                new ColoredKeyedText(owner.getWindow(), "this is the text tab", Style::getTextNormal)
        );
    }
}
