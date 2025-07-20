package org.luke.decut.settings.content.versionmanage;

import javafx.scene.Node;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.managers.FfprobeManager;
import org.luke.decut.local.managers.LocalInstall;
import org.luke.decut.local.ui.DownloadJob;
import org.luke.decut.settings.abs.FfprobeVersionItem;
import org.luke.decut.settings.abs.LocalManagerSettings;
import org.luke.decut.settings.abs.Settings;
import org.luke.gui.controls.input.combo.ComboItem;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.window.Window;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class FfprobeSettings extends LocalManagerSettings {

	public FfprobeSettings(Settings settings) {
		super(settings, "Ffprobe");
	}

	@Override
	public Comparator<String> comparator() {
		return FfprobeManager.COMPARATOR;
	}

	@Override
	public List<DownloadJob> downloadJobs() {
		return FfprobeManager.downloadJobs();
	}

	@Override
	public Node managedUi(Window win, String version, Runnable refresh) {
		return FfprobeManager.managedUi(win, version, refresh);
	}

	@Override
	public Node localUi(Window win, LocalInstall version, Runnable refresh) {
		return FfprobeManager.localUi(win, version, refresh);
	}

	@Override
	public List<File> managedInstalls() {
		return FfprobeManager.managedInstalls();
	}

	@Override
	public List<LocalInstall> localInstalls() {
		return FfprobeManager.localInstalls();
	}

	@Override
	public List<String> installableVersions() {
		return FfprobeManager.installableVersions();
	}

	@Override
	public DownloadJob install(Window win, String version) {
		return FfprobeManager.install(win, version);
	}

	@Override
	public void setDefaultVersion(String version) {
		LocalStore.setDefaultFfprobe(version);
	}

	@Override
	public String getDefaultVersion() {
		return LocalStore.getDefaultFfprobe();
	}

	@Override
	public boolean isValid(String version) {
		return FfprobeManager.isValid(version);
	}

	@Override
	public void addInst(File dir) {
		FfprobeManager.addLocal(dir.getAbsolutePath());
	}

	@Override
	public void clearDefault() {
		LocalStore.setDefaultFfprobe(null);
		defCombo.setValue("");
	}

	@Override
	public ComboItem createComboItem(ContextMenu men, String key) {
		return new FfprobeVersionItem(men, key);
	}
}
