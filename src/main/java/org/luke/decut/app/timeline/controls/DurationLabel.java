package org.luke.decut.app.timeline.controls;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.unkeyed.Text;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;

import java.time.Duration;
import java.util.ArrayList;

public class DurationLabel extends HBox implements Styleable {

    private final ArrayList<Text> nodes;

    public DurationLabel(Window window) {
        super();
        setSpacing(2);
        setAlignment(Pos.CENTER);
        Font font = new Font("monospace", 14);

        nodes = new ArrayList<>();

        nodes.add(new Text("", font));
        nodes.add(new Text(":", font));
        nodes.add(new Text("", font));
        nodes.add(new Text(":", font));
        nodes.add(new Text("", font));

        getChildren().addAll(nodes);

        applyStyle(window.getStyl());
    }

    public void setDuration(Duration duration) {
        nodes.get(0).setText(pad(duration.toHours()));
        nodes.get(2).setText(pad(duration.toMinutesPart()));
        nodes.get(4).setText(pad(duration.toSecondsPart()));
    }

    private String pad(long val) {
        return (val < 10 ? "0" : "") + val;
    }

    @Override
    public void applyStyle(Style style) {
        for (Text node : nodes) {
            node.setFill(style.getTextNormal());
        }
    }
}
