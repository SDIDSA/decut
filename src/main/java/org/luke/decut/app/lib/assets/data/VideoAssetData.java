package org.luke.decut.app.lib.assets.data;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.LineHandler;
import org.luke.decut.ffmpeg.bitrate.AudioBitrate;
import org.luke.decut.ffmpeg.codec.VideoCodec;
import org.luke.decut.ffmpeg.filter_complex.audio.APad;
import org.luke.decut.ffmpeg.handlers.ProgressHandler;
import org.luke.decut.ffmpeg.options.Seek;
import org.luke.decut.ffmpeg.options.Skip;
import org.luke.decut.ffmpeg.options.VFrames;
import org.luke.decut.ffprobe.FfprobeCommand;
import org.luke.gui.controls.image.ImageProxy;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class VideoAssetData extends AssetData {
    private static final HashMap<File, VideoAssetData> cache = new HashMap<>();
    private SubVideoAssetData video;
    private SubAudioAssetData audio;

    VideoAssetData(File file) {
        super(file, AssetType.VIDEO);
        fetch();
    }

    public static VideoAssetData getData(File file) {
        VideoAssetData found = cache.get(file);
        if (found == null) {
            found = new VideoAssetData(file);
            cache.put(file, found);
        }

        if (file.lastModified() > found.getUpdated()) {
            found.fetch();
        }

        return found;
    }

    public SubVideoAssetData getVideo() {
        return video;
    }

    public SubAudioAssetData getAudio() {
        return audio;
    }

    public void fetch() {
        fetch(true);
    }

    public void fetch(boolean parent) {
        super.fetch();
        if(parent) {
            FfprobeCommand durCom = new FfprobeCommand()
                    .onOutput(str -> {
                        duration = (long) (Double.parseDouble(str) * 1000);
                    })
                    .addArgument("-v")
                    .addArgument("error")
                    .addArgument("-show_entries")
                    .addArgument("format=duration")
                    .addArgument("-of")
                    .addArgument("default=noprint_wrappers=1:nokey=1")
                    .setInput(getFile())
                    .execute();
            FfmpegCommand makeThumbs = new FfmpegCommand()
                    .addInput(getFile())
                    .addOption(new Seek("00:00:01.000"))
                    .addOption(new VFrames(1))
                    .setOnOutput(file -> {
                                try (InputStream fis = new FileInputStream(file)) {
                                    Image im = new Image(fis);
                                    resolution = new Dimension2D(im.getWidth(), im.getHeight());
                                    thumb = ImageProxy.resize(ImageUtils.cropCenter(im), 128);
                                } catch (IOException x) {
                                    ErrorHandler.handle(x, "generate video thumbnail");
                                }
                            },
                            ".jpg")
                    .execute();

            makeThumbs.waitFor();
            durCom.waitFor();
            if(durCom.getExitCode() != 0) {
                fetch(true);
                return;
            }
            String name = getFile().getName();
            String ext = name.substring(name.lastIndexOf("."));
            FfmpegCommand makeAud = new FfmpegCommand()
                    .addInput(getFile())
                    .addOption(Skip.VIDEO)
                    .addComplexFilter(new APad().setWholeDur(getDurationSeconds()))
                    .setBitrate(new AudioBitrate("160k"))
                    .setOnOutput(file -> {
                        audio = SubAudioAssetData.getData(file, this);
                    }, ".mp3")
                    .execute();
            FfmpegCommand makeVid = new FfmpegCommand()
                    .addInput(getFile())
                    .addOption(Skip.AUDIO)
                    .setCodec(VideoCodec.COPY)
                    .setOnOutput(file -> {
                        video = SubVideoAssetData.getData(file, this);
                    }, ext)
                    .execute();
            makeVid.waitFor();
            makeAud.waitFor();
        }
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public AssetData getParent() {
        return null;
    }

    public String getName() {
        return getFile().getName();
    }
}
