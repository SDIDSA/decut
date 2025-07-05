package org.luke.decut.app.lib.assets.display;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.LibraryContent;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.text.keyed.ColoredKeyedText;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class Drop extends VBox implements Styleable {
    private final Home owner;
    private final ColoredKeyedText importLab;

    public Drop(Home owner) {
        this.owner = owner;
        setAlignment(Pos.CENTER);
        setSpacing(15);

        ColoredIcon importIcon = new ColoredIcon(owner.getWindow(), "assets", 54, Style::getTextMuted);
        importLab = new ColoredKeyedText(owner.getWindow(), "",
                new Font(14), Style::getTextMuted);
        importLab.setTextAlignment(TextAlignment.CENTER);

        setCursor(Cursor.HAND);

        setOnMouseClicked(_ -> {
            LibraryContent.getInstance(owner, Assets.class).getGrid().showOpenDialogue();
        });

        getChildren().addAll(importIcon, importLab);

        applyStyle(owner.getWindow().getStyl());
    }

    public void setCount(int count) {
        importLab.setKey("Drop to import "+count+" files");
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundSecondary(), 5));
    }
}
