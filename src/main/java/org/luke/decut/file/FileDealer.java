package org.luke.decut.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.luke.gui.exception.ErrorHandler;

public class FileDealer {
	private FileDealer() {
	}

	public static String read(String path) {
		return read(FileDealer.class.getResourceAsStream(path), path);
	}

	public static String read(File file) {
		return read(file, StandardCharsets.UTF_8);
	}

	public static String read(File file, Charset charset) {
		try {
			return read(new FileInputStream(file), file.getAbsolutePath(), charset);
		} catch (FileNotFoundException e) {
			ErrorHandler.handle(e, "reading file " + file.getAbsolutePath());
		}
		return null;
	}

	public static String read(InputStream is, String path) {
		return read(is, path, StandardCharsets.UTF_8);
	}

	public static String read(InputStream is, String path, Charset charset) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, charset))){
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			ErrorHandler.handle(e, "reading file " + path);
		}
		return sb.toString();
	}

	public static void write(String content, File dest) {
		write(content, dest, StandardCharsets.UTF_8);
	}
	
	public static void write(String content, File dest, Charset charset) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), charset))){
			bw.append(content);
			bw.flush();
		} catch (IOException e) {
			ErrorHandler.handle(e, "write file content to " + dest);
		}
	}

	public static String formatSize(long sizeInBytes) {
		if (sizeInBytes < 1024) return sizeInBytes + " B";
		int exp = (int) (Math.log(sizeInBytes) / Math.log(1024));
		char unit = "KMGTPE".charAt(exp - 1);  // Kilo, Mega, Giga, etc.
		return String.format("%.1f %sB", sizeInBytes / Math.pow(1024, exp), unit);
	}
}
