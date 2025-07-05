package org.luke.decut.settings;

import org.luke.decut.settings.content.ffmpeg.FfmpegSettings;
import org.luke.gui.window.Window;
import org.luke.decut.settings.abs.Settings;
import org.luke.decut.settings.abs.left.Section;
import org.luke.decut.settings.abs.left.SectionItem;
import org.luke.decut.settings.content.display.DisplaySettings;


public class AppSettings extends Settings {

	public AppSettings(Window win) {
		super(win);
		
		Section appSettings = new Section(this, "decut_settings", true);
		appSettings.addItem(new SectionItem(this, "display", DisplaySettings.class));
		appSettings.addItem(new SectionItem(this, "ffmpeg_versions", FfmpegSettings.class));

		sideBar.addSection(appSettings);
		sideBar.separate(win);

		fire("display");
	}
	
	public boolean fire(String match) {
		return sideBar.fire(match);
	}

}
