package org.luke.decut.app.lib;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

public class Library extends HBox implements Styleable {
    private final Home owner;
    private final VBox tabs;
    private final StackPane content;
    private LibraryContent current;

    public Library(Home owner) {
        this.owner = owner;

        tabs = new VBox();
        tabs.setPadding(new Insets(10,10,10,10));
        tabs.setSpacing(5);

        content = new StackPane();
        content.setPadding(new Insets(10,0,10,0));
        content.setAlignment(Pos.TOP_LEFT);

        content.setMinWidth(USE_PREF_SIZE);
        content.setMaxWidth(USE_PREF_SIZE);
        content.prefWidthProperty().bind(
                widthProperty().subtract(tabs.widthProperty())
        );

        getChildren().addAll(tabs, content);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        applyStyle(owner.getWindow().getStyl());
    }

    public StackPane getContentPane() {
        return content;
    }

    public void loadContent(LibraryTab tab) {
        loadContent(tab, null);
    }

    public void loadContent(LibraryTab tab, Runnable onFinish) {
        content.getChildren().clear();
        Platform.runBack(
                () -> LibraryContent.getInstance(owner, tab.getContent()),
                content -> {
                    if (current != null) current.destroy();
                    this.content.getChildren().add(content);
                    current = content;

                    current.setMinWidth(USE_PREF_SIZE);
                    current.setMaxWidth(USE_PREF_SIZE);
                    current.prefWidthProperty().bind(
                            this.content.widthProperty().subtract(10)
                    );

                    current.setup();
                    if (onFinish != null) {
                        onFinish.run();
                    }
                });
    }

    public Library addTab(LibraryTab tab) {
        tabs.getChildren().add(tab);
        return this;
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(),
                new CornerRadii(8, 2, 2, 2, false)));
    }
}
