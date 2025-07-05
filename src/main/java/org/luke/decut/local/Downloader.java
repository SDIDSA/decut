package org.luke.decut.local;

import org.luke.gui.exception.ErrorHandler;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;
import java.util.function.Function;

public class Downloader {

	public static void downloadZipInto(String urlString, Consumer<Double> onDownload, Consumer<Double> onCopy,
			File targetDir, Function<File, File> rootSupplier) {
		try {
			URL url = URI.create(urlString).toURL();
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();

			int ind = urlString.indexOf("?");
			String name = urlString.substring(urlString.lastIndexOf("/") + 1, ind == -1 ? urlString.length() : ind);
			name = name.substring(0, name.lastIndexOf("."));

			File output = File.createTempFile(name + "_", ".zip");
			OutputStream os = new FileOutputStream(output);

			int fileLength = con.getContentLength();
			int count;
			byte[] buffer = new byte[2048];
			int totalRead = 0;

			long lastUpdate = System.currentTimeMillis();

			while ((count = is.read(buffer)) != -1) {
				os.write(buffer, 0, count);
				totalRead += count;

				long now = System.currentTimeMillis();
				if (now - lastUpdate > 200) {
					onDownload.accept((double) totalRead / fileLength);
					lastUpdate = now;
				}
			}
			os.flush();
			os.close();
			
			onDownload.accept(1.0);

			File temp = File.createTempFile(name, "");
			temp.delete();
//			new Command("cmd", "/c",
//					"7z x \"" + output.getAbsolutePath() + "\" -aou -o\"" + temp.getAbsolutePath() + "\"")
//					.execute(Jwin.get7z()).waitFor();

			if (!targetDir.exists() || !targetDir.isDirectory()) {
				targetDir.mkdir();
			}

//			int fileCount = JwinActions.countDir(temp);
//			int[] copyCount = new int[] { 0 };
//			long[] la = new long[] { 0 };
//			JwinActions.copyDirCont(rootSupplier.apply(temp), targetDir, () -> {
//				copyCount[0]++;
//
//				long now = System.currentTimeMillis();
//				if (now - la[0] > 200) {
//					onCopy.accept((copyCount[0] / (double) fileCount));
//					la[0] = now;
//				}
//			});
		} catch (IOException e) {
			ErrorHandler.handle(e, "download file " + urlString);
		}
	}
}
