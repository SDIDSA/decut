package org.luke.gui.controls.recycle.table;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.text.keyed.ColoredKeyedLabel;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.style.StyledColor;

public class HeaderCell<S> extends HBox implements Styleable {

    private final ColoredIcon sortDir;

    private final ColoredKeyedLabel headerLabel;

    public HeaderCell(TableView<S> table, ColumnRenderer<S> column) {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(8));
        setMaxHeight(Double.MAX_VALUE);
        setCursor(Cursor.HAND);

        setMinWidth(0);
        setMaxWidth(USE_PREF_SIZE);

        headerLabel = new ColoredKeyedLabel(table.getWindow(),
                column.getHeaderText(), Style::getTextNormal);
        headerLabel.setFont(new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));

        sortDir = new ColoredIcon(table.getWindow(), "empty", 14, Style::getTextNormal);

        getChildren().addAll(headerLabel, new ExpandingHSpace());

        applyStyle(table.getWindow().getStyl());
    }

    public ColoredIcon getSortDir() {
        return sortDir;
    }

    public void setIcon(String icon) {
        if(icon.equals("empty")) {
            getChildren().remove(sortDir);
        } else if(sortDir.getParent() == null){
            getChildren().add(sortDir);
        }
        sortDir.setImage(icon);
    }

    public void setFill(StyledColor fill) {
        headerLabel.setFill(fill);
    }

    @Override
    public void applyStyle(Style style) {
        backgroundProperty().bind(Bindings.when(pressedProperty()).then(
                Backgrounds.make(style.getBackgroundModifierActive(), 5, 4)
        ).otherwise(Bindings.when(hoverProperty()).then(
                Backgrounds.make(style.getBackgroundModifierHover(), 5, 4)
        ).otherwise(Background.EMPTY)));
    }
}
