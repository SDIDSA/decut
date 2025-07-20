package org.luke.decut.settings.content.versionmanage;

import javafx.scene.Node;
import org.luke.decut.local.managers.FfmpegManager;
import org.luke.decut.settings.abs.FfmpegVersionItem;
import org.luke.decut.settings.abs.LocalManagerSettings;
import org.luke.decut.settings.abs.Settings;
import org.luke.gui.controls.input.combo.ComboItem;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.window.Window;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.decut.local.ui.DownloadJob;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class FfmpegSettings extends LocalManagerSettings {

	public FfmpegSettings(Settings settings) {
		super(settings, "Ffmpeg");
	}

	@Override
	public Comparator<String> comparator() {
		return FfmpegManager.COMPARATOR;
	}

	@Override
	public List<DownloadJob> downloadJobs() {
		return FfmpegManager.downloadJobs();
	}

	@Override
	public Node managedUi(Window win, String version, Runnable refresh) {
		return FfmpegManager.managedUi(win, version, refresh);
	}

	@Override
	public Node localUi(Window win, LocalInstall version, Runnable refresh) {
		return FfmpegManager.localUi(win, version, refresh);
	}

	@Override
	public List<File> managedInstalls() {
		return FfmpegManager.managedInstalls();
	}

	@Override
	public List<LocalInstall> localInstalls() {
		return FfmpegManager.localInstalls();
	}

	@Override
	public List<String> installableVersions() {
		return FfmpegManager.installableVersions();
	}

	@Override
	public DownloadJob install(Window win, String version) {
		return FfmpegManager.install(win, version);
	}

	@Override
	public void setDefaultVersion(String version) {
		LocalStore.setDefaultFfmpeg(version);
	}

	@Override
	public String getDefaultVersion() {
		return LocalStore.getDefaultFfmpeg();
	}

	@Override
	public boolean isValid(String version) {
		return FfmpegManager.isValid(version);
	}

	@Override
	public void addInst(File dir) {
		FfmpegManager.addLocal(dir.getAbsolutePath());
	}

	@Override
	public void clearDefault() {
		LocalStore.setDefaultFfmpeg(null);
		defCombo.setValue("");
	}

	@Override
	public ComboItem createComboItem(ContextMenu men, String key) {
		return new FfmpegVersionItem(men, key);
	}
}
