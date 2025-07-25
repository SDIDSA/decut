package org.luke.decut.local;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luke.decut.crossplatform.Os;
import org.luke.gui.locale.Locale;
import org.luke.gui.style.Style;
import org.luke.decut.file.FileDealer;

import javafx.scene.paint.Color;

public class LocalStore {
	private static final String STYLE = "style";
	private static final String LANGUAGE = "language";
	private static final String STYLE_MODE = "style_mode";
	private static final String STYLE_BRIGHTNESS = "style_brightness";
	private static final String STYLE_ACCENT = "style_accent";


	private static final String DEFAULT_FFMPEG = "default_ffmpeg";
	private static final String DEFAULT_FFPROBE = "default_ffprobe";

	private static final String FFMPEG_INSTS = "ffmpeg_added";
	private static final String FFPROBE_INSTS = "ffprobe_added";

	private static final File json;

	private static final HashMap<String, String> loaded = new HashMap<>();

	static {
		json = Os.fromSystem().getDecutPath("config.json");
		if (json.exists()) {
			JSONObject t = new JSONObject(Objects.requireNonNull(FileDealer.read(json)));
			t.keySet().forEach(key -> loaded.put(key, t.getString(key)));
		} else {
			if(!json.getParentFile().exists()) {
				json.getParentFile().mkdirs();
			}
			FileDealer.write("{}", json);
		}
	}

	private static void save() {
		FileDealer.write(new JSONObject(loaded).toString(4), json);
	}

	private static void set(String key, String value) {
		if(value == null) {
			loaded.remove(key);
		}else {
			loaded.put(key, value);
		}
		save();
	}

	private static String get(String key) {
		return loaded.get(key);
	}

	// Getters and setters

	public static Style getStyle() {
		String s = get(STYLE);

		if (s == null) {
			s = serializeStyle(Style.GRAY_1);
			set(STYLE, s);
		}

		return deserializeStyle(s);
	}

	public static void setStyle(Style style) {
		set(STYLE, serializeStyle(style));
	}

	public static Locale getLanguage() {
		String langId = get(LANGUAGE);
		return Locale.byName(langId == null ? "en_US" : get(LANGUAGE));
	}

	public static void setLanguage(Locale locale) {
		set(LANGUAGE, locale.getName());
	}

	public static String getDefaultFfmpeg() {
		return get(DEFAULT_FFMPEG);
	}

	public static void setDefaultFfmpeg(String defaultFfmpeg) {
		set(DEFAULT_FFMPEG, defaultFfmpeg);
	}

	public static String getDefaultFfprobe() {
		return get(DEFAULT_FFPROBE);
	}

	public static void setDefaultFfprobe(String defaultFfprobe) {
		set(DEFAULT_FFPROBE, defaultFfprobe);
	}

	public static ArrayList<String> ffmpegAdded() {
		String s = get(FFMPEG_INSTS);
		if (s == null) {
			s = "[]";
			set(FFMPEG_INSTS, s);
		}
		return deserializeList(s);
	}

	public static ArrayList<String> ffprobeAdded() {
		String s = get(FFPROBE_INSTS);
		if (s == null) {
			s = "[]";
			set(FFPROBE_INSTS, s);
		}
		return deserializeList(s);
	}

	public static void addFfmpegInst(String path) {
		ArrayList<String> insts = ffmpegAdded();
		if(insts.contains(path)) return;
		insts.add(path);
		set(FFMPEG_INSTS, serializeList(insts));
	}

	public static void addFfprobeInst(String path) {
		ArrayList<String> insts = ffprobeAdded();
		if(insts.contains(path)) return;
		insts.add(path);
		set(FFPROBE_INSTS, serializeList(insts));
	}

	public static void removeFfmpegInst(String path) {
		ArrayList<String> insts = ffmpegAdded();

		String p1 = new File(path).getAbsolutePath();

		insts.removeIf(op -> {
			String p2 = new File(op).getAbsolutePath();
			return p2.contains(p1) || p1.contains(p2);
		});

		set(FFMPEG_INSTS, serializeList(insts));
	}

	public static void removeFfprobeInst(String path) {
		ArrayList<String> insts = ffprobeAdded();

		String p1 = new File(path).getAbsolutePath();

		insts.removeIf(op -> {
			String p2 = new File(op).getAbsolutePath();
			return p2.contains(p1) || p1.contains(p2);
		});

		set(FFPROBE_INSTS, serializeList(insts));
	}
	
	// Utility methods
	private static String serializeList(List<String> list) {
		JSONArray arr = new JSONArray();
		list.forEach(arr::put);
		return arr.toString();
	}

	private static ArrayList<String> deserializeList(String arr) {
		ArrayList<String> res = new ArrayList<>();
		JSONArray jarr = new JSONArray(arr);
		jarr.forEach(o -> res.add((String) o));
		return res;
	}

	private static String serializeStyle(Style style) {
		JSONObject obj = new JSONObject();
		obj.put(STYLE_MODE, style.getThemeName());
		obj.put(STYLE_BRIGHTNESS, String.valueOf(style.getBrightnessModifier()));
		obj.put(STYLE_ACCENT, serializeColor(style.getAccent()));
		return obj.toString();
	}

	private static Style deserializeStyle(String style) {
		JSONObject obj = new JSONObject(style);
		String mode = obj.getString(STYLE_MODE);
		double brightness = Double.parseDouble(obj.getString(STYLE_BRIGHTNESS));
		Color accent = deserializeColor(obj.getString(STYLE_ACCENT));
		return new Style(mode, accent, brightness);
	}

	private static String serializeColor(Color color) {
		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);
		return String.format("#%02X%02X%02X", red, green, blue);
	}

	private static Color deserializeColor(String hexColorCode) {
		int red = Integer.parseInt(hexColorCode.substring(1, 3), 16);
		int green = Integer.parseInt(hexColorCode.substring(3, 5), 16);
		int blue = Integer.parseInt(hexColorCode.substring(5, 7), 16);
		return Color.rgb(red, green, blue);
	}
}