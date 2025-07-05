package org.luke.decut.app.lib.assets.display;

import javafx.stage.FileChooser;
import org.luke.decut.app.lib.assets.filter.AssetType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtensionFilters {

    private static List<FileChooser.ExtensionFilter> filters;

    private static FileChooser.ExtensionFilter video;
    private static FileChooser.ExtensionFilter audio;
    private static FileChooser.ExtensionFilter image;

    public static List<FileChooser.ExtensionFilter> getFilters() {
        if(filters == null) {
            filters = new ArrayList<>();

            filters.add(new FileChooser.ExtensionFilter("All Supported Media",
                    "*.mp4", "*.mov", "*.mkv", "*.avi", "*.webm", "*.flv", "*.wmv", "*.mpg", "*.mpeg",
                    "*.m4v", "*.3gp", "*.vob", "*.ogv", "*.ts", "*.m2ts", "*.asf", "*.rm", "*.rmvb", "*.swf",
                    "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", "*.wma", "*.m4a", "*.opus", "*.aiff",
                    "*.ape", "*.ac3", "*.dts", "*.amr", "*.au", "*.mp2",
                    "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.bmp", "*.tiff", "*.tif", "*.ico",
                    "*.tga", "*.heic", "*.heif", "*.jp2", "*.jpx", "*.ppm", "*.pgm", "*.pbm", "*.pnm"));

            video = new FileChooser.ExtensionFilter("Video Files",
                    "*.mp4", "*.mov", "*.mkv", "*.avi", "*.webm", "*.flv", "*.wmv", "*.mpg", "*.mpeg",
                    "*.m4v", "*.3gp", "*.vob", "*.ogv", "*.ts", "*.m2ts", "*.asf", "*.rm", "*.rmvb", "*.swf");
            audio = new FileChooser.ExtensionFilter("Audio Files",
                    "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", "*.wma", "*.m4a", "*.opus", "*.aiff",
                    "*.ape", "*.ac3", "*.dts", "*.amr", "*.au", "*.mp2");
            image = new FileChooser.ExtensionFilter("Image Files",
                    "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.bmp", "*.tiff", "*.tif", "*.ico",
                    "*.tga", "*.heic", "*.heif", "*.jp2", "*.jpx", "*.ppm", "*.pgm", "*.pbm", "*.pnm");

            filters.add(video);
            filters.add(audio);
            filters.add(image);
        }
        return filters;
    }

    public static List<File> validate(List<File> files) {
        ArrayList<File> res = new ArrayList<>();
        for (File file : files) {
            if(isValid(file)) {
                res.add(file);
            }
        }
        return res;
    }

    private static boolean isValid(File file) {
        for (String extension : getFilters().getFirst().getExtensions()) {
            if (file.getName().toLowerCase().endsWith(extension.substring(1))) {
                return true;
            }
        }
        return false;
    }

    public static AssetType typeOf(File file) {
        if(isOfType(file, video)) {
            return AssetType.VIDEO;
        } else if(isOfType(file, audio)) {
            return AssetType.AUDIO;
        } else {
            return AssetType.IMAGE;
        }
    }

    private static boolean isOfType(File file, FileChooser.ExtensionFilter filter) {
        for (String extension : filter.getExtensions()) {
            if (file.getName().toLowerCase().endsWith(extension.substring(1))) {
                return true;
            }
        }
        return false;
    }
}
