package org.luke.decut.local.managers;

import org.json.JSONObject;
import org.luke.decut.cmd.Command;
import org.luke.decut.crossplatform.Os;
import org.luke.decut.file.FileDealer;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.file.DirUtils;
import org.luke.gui.window.Window;
import org.luke.decut.local.LocalStore;
import org.luke.decut.local.ui.DownloadJob;
import org.luke.decut.local.ui.DownloadState;
import org.luke.decut.local.ui.Installed;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FfmpegManager {

	private static final HashMap<String, LocalInstall> versionCache = new HashMap<>();

	private static final HashMap<String, String> installable;

	private static final HashMap<String, Installed> managedCache = new HashMap<>();

	private static final HashMap<File, Installed> localCache = new HashMap<>();

	private static final File root;

	private static final HashMap<String, DownloadJob> downloadJobs = new HashMap<>();

	static {
		root = Os.fromSystem().getDecutPath("ffmpeg");

		installable = new HashMap<>();
		JSONObject obj = new JSONObject(FileDealer.read("/ffbinaries.json")).getJSONObject(Os.fromSystem().getName());
		obj.keySet().forEach(key -> installable.put(key, obj.getString(key)));
		if (!root.exists() || !root.isDirectory())
			root.mkdirs();
	}

	public static Installed managedUi(Window win, String version, Runnable onChange) {
		Installed found = managedCache.get(version);

		if (found == null) {
			found = new Installed(win, version, dirForVer(version), onChange);
			final Installed ffound = found;
			found.setOnRemove(() -> DirUtils.deleteDir(ffound.getTargetDir()));
			managedCache.put(version, found);
		}

		return found;
	}

	public static Installed localUi(Window win, LocalInstall ver, Runnable onChange) {
		Installed found = localCache.get(ver.getRoot());

		if (found == null) {
			found = new Installed(win, ver.getVersion(), ver.getRoot(), onChange);
			found.setOnRemove(() -> LocalStore.removeFfmpegInst(ver.getRoot().getAbsolutePath()));
			localCache.put(ver.getRoot(), found);
		}

		return found;
	}

	public static List<String> installableVersions() {
		ArrayList<String> res = new ArrayList<>();
		installable.keySet().forEach(version -> {
			if (!dirForVer(version).exists()) {
				res.add(version);
			}
		});
		res.sort(COMPARATOR);
		return res;
	}

	public static DownloadJob install(Window win, String version) {
		if (downloadJobs.containsKey(version))
			return null;

		DownloadJob job = new DownloadJob(win, version, downloadUrlForVer(version), dirForVer(version));

		job.addOnStateChanged(s -> {
			if (s == DownloadState.DONE || s == DownloadState.CANCELED || s == DownloadState.FAILED) {
				downloadJobs.remove(version);
			}
		});

		job.start();

		downloadJobs.put(version, job);

		return job;
	}

	public static boolean isValid(String version) {
		List<String> managedPaths = managedInstalls().stream().map(File::getAbsolutePath).toList();
		return (LocalStore.ffmpegAdded().contains(version) || managedPaths.contains(version)) && versionOf(version) != null && new File(version).exists();
	}

	public static List<DownloadJob> downloadJobs() {
		return new ArrayList<>(downloadJobs.values());
	}

	public static String downloadUrlForVer(String version) {
		return installable.get(version);
	}

	public static File dirForVer(String version) {
		return new File(root, version);
	}

	public static List<File> managedInstalls() {
		ArrayList<File> res = new ArrayList<>();

		for (File sub : root.listFiles()) {
			res.add(sub);

			if (!versionCache.containsKey(sub.getAbsolutePath()))
			{
				LocalInstall version = versionFromDir(sub);
				if(version != null) {
					versionCache.put(sub.getAbsolutePath(), version);
				}
			}
		}

		res.sort(FCOMPARATOR);

		return res;
	}

	public static List<LocalInstall> localInstalls() {
		List<String> paths = LocalStore.ffmpegAdded();

		ArrayList<LocalInstall> res = new ArrayList<>();

		paths.forEach(p -> {
			File r = new File(p);
			if (r.exists() && r.isDirectory()) {
				LocalInstall version = versionFromDir(r);

				if (!versionCache.containsKey(version.getRoot().getAbsolutePath()))
					versionCache.put(version.getRoot().getAbsolutePath(), version);

                res.add(version);
            }
		});

		return res;
	}

	public static void addLocal(String absolutePath) {
		LocalInstall inst = versionFromDir(new File(absolutePath));
		if (inst != null) {
			LocalStore.addFfmpegInst(inst.getRoot().getAbsolutePath());
		}
	}

	public static LocalInstall versionOf(String path) {
		return versionCache.get(path);
	}

	public static LocalInstall versionFromDir(File file) {
		if (file.listFiles() == null) {
			return null;
		}
		for (File sf : Objects.requireNonNull(file.listFiles())) {
			if (sf.isDirectory()) {
				LocalInstall version = versionFromDir(sf);
				if (version != null) {
					return version;
				}
			} else if (isFFmpegBinary(sf)) {
				if(!Os.fromSystem().isWindows() && !sf.canExecute()) {
                    try {
                        new Command("chmod a+x \"" + sf.getAbsolutePath() + "\"")
                                .execute(file).waitFor();
                    } catch (InterruptedException e) {
                        ErrorHandler.handle(e, "enabling the execution of the ffmpeg binary");
                    }
                }
				String version = getFFmpegVersion("\""+sf.getAbsolutePath()+"\"");
				if (version != null) {
					return new LocalInstall(sf.getParentFile(), sf, version);
				}
			}
		}
		return null;
	}

	private static boolean isFFmpegBinary(File file) {
		String name = file.getName().toLowerCase();
		Os currentOs = Os.fromSystem();

		if (currentOs.isWindows()) {
			return name.equals("ffmpeg.exe");
		} else {
			return name.equals("ffmpeg") && file.isFile();
		}
	}

	public static String getFFmpegVersion(String ffmpegBinary) {
		AtomicReference<String> versionRef = new AtomicReference<>();
		Consumer<String> parser = line -> {
			if (line.startsWith("ffmpeg version")) {
				String version = extractVersionFromLine(line);
				if (version != null) {
					versionRef.set(version);
				}
			}
		};
        try {
            new Command(ffmpegBinary, "-version")
                    .addErrorHandler(parser)
                    .addInputHandler(parser)
                    .execute(new File("/"))
                    .waitFor();
            if(versionRef.get() != null) {
				return versionRef.get();
			}
        } catch (InterruptedException e) {
			ErrorHandler.handle(e, "parse ffmpeg version from binary");
        }
		return null;
    }

	private static String extractVersionFromLine(String line) {
		try {
			String[] parts = line.split(" ");
			if (parts.length >= 3) {
				String versionPart = parts[2];

				if (versionPart.matches("\\d{4}-\\d{2}-\\d{2}-git-.*")) {
					String[] dateParts = versionPart.split("-");
					if (dateParts.length >= 3) {
						return dateParts[0] + "." + dateParts[1] + "." + dateParts[2];
					}
				}

				if (versionPart.contains("-")) {
					versionPart = versionPart.split("-")[0];
				}
				Matcher m = Pattern.compile("(\\d+\\.\\d+(\\.\\d+)?)").matcher(versionPart);
				if (m.find()) {
					return m.group(1);
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing version line: " + line);
		}
		return null;
	}

	public static final Comparator<String> COMPARATOR = (v1, v2) -> {
		if (v1.equals(v2)) {
			return 0;
		}

		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");

		int maxLength = Math.max(parts1.length, parts2.length);

		for (int i = 0; i < maxLength; i++) {
			int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
			int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;

			if (num1 != num2) {
				return -Integer.compare(num1, num2);
			}
		}

		return 0;
	};

	private static int parseVersionPart(String part) {
		try {
			return Integer.parseInt(part);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static final Comparator<File> FCOMPARATOR = (v1, v2) -> COMPARATOR.compare(v1.getName(), v2.getName());
}
