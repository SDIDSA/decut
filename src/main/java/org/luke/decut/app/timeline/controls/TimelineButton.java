package org.luke.decut.app.timeline.controls;

import org.luke.gui.controls.button.ColoredIconButton;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

public class TimelineButton extends ColoredIconButton {
    public TimelineButton(Window owner, String iconFile, String tooltip) {
        super(owner,
                5, 30, 30,
                iconFile, 18,
                Style::getNothing, Style::getTextNormal);

        TextTooltip.install(this, Direction.UP, tooltip, 0, 15);
    }
}
