package org.luke.decut.app.timeline.tracks;

import org.luke.gui.controls.button.ColoredIconButton;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;

public class TrackButton extends ColoredIconButton {
    public TrackButton(Window owner, String iconFile, String tooltip) {
        super(owner,
                5, 24, 24,
                iconFile, 14,
                Style::getNothing, Style::getTextNormal);

        TextTooltip.install(this, Direction.UP, tooltip, 0, 15);
    }
}
