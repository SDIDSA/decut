package org.luke.decut.app.lib.assets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.LibraryContent;
import org.luke.decut.app.lib.assets.display.AssetsDisplay;
import org.luke.decut.app.lib.assets.filter.AssetFilter;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.button.ColoredIconButton;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.input.text.ModernTextInput;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.controls.space.FixedHSpace;
import org.luke.gui.style.Style;

public class Assets extends LibraryContent {
    private final AssetsDisplay grid;
    private final AssetFilter filter;

    private final ColoredIconButton imp;

    public Assets(Home owner) {
        super(owner);
        setSpacing(10);

        HBox top = new HBox(10);

        ModernTextInput input = new ModernTextInput(owner.getWindow(),
                new Font(13),
                "search_input",
                false);

        input.setPrompt("Type to search...");

        HBox.setHgrow(input, Priority.ALWAYS);

        filter = new AssetFilter(owner);

        imp = new ColoredIconButton(owner.getWindow(), 5, 40, 40,
                "import", 18,
                Style::getBackgroundTertiaryOr, Style::getTextNormal);
        TextTooltip.install(imp, Direction.UP, "Import", 0, 15);
        imp.setLoadingSize(5);
        imp.setAction(() -> LibraryContent.getInstance(owner, Assets.class).getGrid().showOpenDialogue());

        grid = new AssetsDisplay(owner);
        VBox.setVgrow(grid, Priority.ALWAYS);

        top.getChildren().addAll(input, imp, filter);


        grid.setMinWidth(USE_PREF_SIZE);
        grid.setMaxWidth(USE_PREF_SIZE);
        grid.prefWidthProperty().bind(
                widthProperty()
        );

        getChildren().addAll(top, grid);
    }

    public void startLoading() {
        imp.startLoading();
    }

    public void stopLoading() {
        imp.stopLoading();
    }

    public AssetsDisplay getGrid() {
        return grid;
    }

    public AssetFilter getFilter() {
        return filter;
    }
}
