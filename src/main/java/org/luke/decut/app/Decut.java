package org.luke.decut.app;

import org.luke.gui.controls.popup.Direction;
import org.luke.gui.threading.Platform;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;
import org.luke.gui.window.content.app_bar.AppBarButton;
import org.luke.decut.app.home.Home;
import org.luke.decut.settings.SettingsPan;
import org.luke.decut.lang.LanguageMenu;
import org.luke.decut.local.LocalStore;

import javafx.application.Application;
import javafx.stage.Stage;
import org.luke.decut.about.Credits;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Decut extends Application {
	public static Decut instance;
	public static Window winstance;

	public Decut() {
		instance = this;
	}

	private SettingsPan sets;
	private Home home;

	@Override
	public void start(Stage ps) throws Exception {

		System.setProperty("prism.lcdtext", "false");

		Window window = new Window(this, LocalStore.getStyle(), LocalStore.getLanguage());
		winstance = window;
		
		window.getStyl().addListener((_, _, nv) -> {
			LocalStore.setStyle(nv);
		});

		home = Page.getInstance(window, Home.class);

		Credits about = new Credits(home);

		sets = new SettingsPan(home);

		AppBarButton openSettings = new AppBarButton(window, "settings");
		AppBarButton lang = new AppBarButton(window, "language");

		LanguageMenu langMenu = new LanguageMenu(window);

		window.addBarButton(1, openSettings);
		window.addBarButton(1, lang);
		window.setOnInfo(about::showAndWait);

		window.setWindowIcon("decut-icon");
		window.setTaskIcon("decut-task-icon");

		window.setTitle("Decut");

		openSettings.setAction(sets::show);

		lang.setAction(() -> {
			langMenu.showPop(lang, Direction.DOWN, 0, 15);
		});

		window.setOnShown(_ -> window.loadPage(Home.class));
		window.show();
	}

	public void openSettings(String match) {
		if (sets.fire(match)) {
			sets.showAndWait();
		}
	}

	public void openFfmpegConfig() {
		openSettings("ffmpeg_versions");
	}
	
	public Home getHome() {
		return home;
	}
}
