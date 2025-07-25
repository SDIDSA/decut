package org.luke.decut.settings.abs;

import org.luke.decut.local.managers.FfmpegManager;
import org.luke.gui.controls.input.combo.ComboItem;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.context.items.MenuItem;

public class FfmpegVersionItem extends MenuItem implements ComboItem {

	private final String path;
	private final String version;

	public FfmpegVersionItem(ContextMenu menu, String text) {
		super(menu, FfmpegManager.versionOf(text).getVersion(), "empty");
		removeIcon();
		path = text;
		version = lab.getText();
	}

	@Override
	public String getDisplay() {
		return version;
	}

	@Override
	public String getValue() {
		return path;
	}

	@Override
	public MenuItem menuItem() {
		return this;
	}

	public boolean match(String toMatch) {
		return getValue().toLowerCase().contains(toMatch.toLowerCase())
				|| getDisplay().toLowerCase().contains(toMatch.toLowerCase());
	}

}
