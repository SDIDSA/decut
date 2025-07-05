package org.luke.gui.controls.text.unkeyed;

import javafx.beans.binding.Bindings;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.TextTransform;

/**
 * The {@code Text} class represents a text node that supports color, font, and
 * text transformation. It extends {@link javafx.scene.text.Text} and implements
 * the {@link TextNode} interface.
 *
 * @author SDIDSA
 */
public class Label extends javafx.scene.control.Label implements TextNode {

    private TextTransform transform = TextTransform.NONE;

    /**
     * Constructs a Text node with the specified text and font.
     *
     * @param val  The initial text value.
     * @param font The font to use for the text.
     */
    public Label(String val, Font font) {
        if (val != null) {
            setText(val);
        }
        setFont(font);

        setCache(true);
        setCacheHint(CacheHint.SPEED);

        opacityProperty().bind(Bindings.when(disabledProperty()).then(0.5).otherwise(1));
    }

    /**
     * Constructs a Text node with the specified text using the default font.
     *
     * @param val The initial text value.
     */
    public Label(String val) {
        this(val, Font.DEFAULT);
    }

    /**
     * Sets the text value, applying the current text transformation.
     *
     * @param text The text to set.
     */
    public void set(String text) {
        setText(transform.apply(text));
    }

    @Override
    public void setTransform(TextTransform transform) {
        this.transform = transform;
        setText(transform.apply(getText()));
    }

    @Override
    public void setFont(Font font) {
        setFont(font.getFont());
    }

    @Override
    public void setFill(Paint fill) {
        setTextFill(fill);
    }

    @Override
    public Node getNode() {
        return this;
    }

}
