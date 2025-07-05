package org.luke.gui.controls.recycle.table;

import javafx.scene.layout.Region;
import org.luke.gui.controls.text.keyed.KeyedLabel;

import java.util.function.Function;

public class TextCell<S, T> extends KeyedLabel implements TypedTableCell<S, T>{
    private final Function<S, T> extractor;
    private final Function<T, String> renderer;

    public TextCell(TableView<S> tableView, Function<S, T> extractor, Function<T, String> renderer) {
        super(tableView.getWindow(), "");
        this.extractor = extractor;
        this.renderer = renderer;
    }

    @Override
    public void apply(S item) {
        TypedTableCell.super.apply(item);
    }

    @Override
    public void applyTyped(T extracted) {
        setKey(renderer.apply(extracted));
    }

    @Override
    public Region getNode() {
        return this;
    }

    @Override
    public T extract(S item) {
        return extractor.apply(item);
    }
}
