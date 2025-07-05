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
import org.luke.gui.style.Style;

public class EmptyLib extends VBox {
    private final Home owner;

    public EmptyLib(Home owner) {
        this.owner = owner;
        setAlignment(Pos.CENTER);
        setSpacing(15);

        ColoredIcon importIcon = new ColoredIcon(owner.getWindow(), "import", 54, Style::getTextMuted);
        ColoredKeyedText importLab = new ColoredKeyedText(owner.getWindow(), "Click here\nor\ndrop files to import",
                new Font(14), Style::getTextMuted);
        importLab.setTextAlignment(TextAlignment.CENTER);

        setCursor(Cursor.HAND);

        setOnMouseClicked(_ -> {
            LibraryContent.getInstance(owner, Assets.class).getGrid().showOpenDialogue();
        });

        getChildren().addAll(importIcon, importLab);
    }
}
