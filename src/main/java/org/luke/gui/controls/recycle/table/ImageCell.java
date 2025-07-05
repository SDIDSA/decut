package org.luke.gui.controls.recycle.table;

import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.luke.gui.controls.image.ColorIcon;

import java.util.function.Function;

public class ImageCell<S> extends StackPane implements TypedTableCell<S, Image>{
    private final Function<S, Image> extractor;
    private final ColorIcon icon;
    private final TableView<S> tableView;

    public ImageCell(TableView<S> tableView, Function<S, Image> extractor) {
        super();
        this.tableView = tableView;
        icon = new ColorIcon(null, 0);
        this.extractor = extractor;

        icon.setColored(false);

        getChildren().add(icon);
    }

    @Override
    public void apply(S item) {
        TypedTableCell.super.apply(item);
    }

    @Override
    public void applyTyped(Image extracted) {
        icon.setImage(extracted, tableView.rowHeightProperty().subtract(10));
    }

    @Override
    public void setFill(Paint fill) {
        //IGNORE
    }

    @Override
    public Region getNode() {
        return this;
    }

    @Override
    public Image extract(S item) {
        return extractor.apply(item);
    }
}
