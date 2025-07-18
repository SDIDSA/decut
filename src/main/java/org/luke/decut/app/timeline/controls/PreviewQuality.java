package org.luke.decut.app.timeline.controls;

import org.luke.decut.app.home.Home;
import org.luke.gui.controls.input.radio.RadioGroup;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.RadioMenuItem;

public class PreviewQuality extends TimelineButton {
    private final Home owner;
    private final RadioGroup radioGroup;
    private final ContextMenu menu;
    public PreviewQuality(Home owner) {
        super(owner.getWindow(), "preview-quality", "Preview quality");
        this.owner = owner;

        menu = new ContextMenu(owner.getWindow(), 130);

        radioGroup = new RadioGroup();

        addOption("Full", "hd", 1).setChecked(true);
        addOption("Half", "half", 0.5);
        addOption("Quarter", "quarter", 0.25);

        setAction(() -> menu.showPop(this, Direction.RIGHT_DOWN, 8, 6));
    }

    private RadioMenuItem addOption(String name, String icon, double qualityFactor) {
        RadioMenuItem radio = new RadioMenuItem(menu, name, icon);
        radio.checkedProperty().addListener((_,_,nv) -> {
            if(nv) {
                owner.setPreviewQuality(qualityFactor);
            }
        });
        radioGroup.add(radio.getRadio());
        menu.addMenuItem(radio);
        return radio;
    }
}
