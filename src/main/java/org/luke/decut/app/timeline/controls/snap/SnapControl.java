package org.luke.decut.app.timeline.controls.snap;

import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.controls.TimelineButton;
import org.luke.gui.controls.input.radio.RadioGroup;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.RadioMenuItem;

public class SnapControl extends TimelineButton {
    public SnapControl(Home owner) {
        super(owner.getWindow(), "snap", "Snap to nearest");

        ContextMenu menu = new ContextMenu(owner.getWindow(), 130);

        RadioGroup radioGroup = new RadioGroup();
        for (SnapStrategy strategy : SnapStrategy.values()) {
            RadioMenuItem radio = new RadioMenuItem(menu, strategy.getName(), strategy.getIcon());
            radioGroup.add(radio.getRadio());
            menu.addMenuItem(radio);
        }

        setAction(() -> menu.showPop(this, Direction.RIGHT_DOWN, 8, 6));

    }
}
