package org.luke.decut.render;

import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.clips.AudioClip;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.clips.VideoClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.app.timeline.viewport.content.TrackContent;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.codec.AudioCodec;
import org.luke.decut.ffmpeg.filter_complex.audio.*;
import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilterNode;
import org.luke.decut.ffmpeg.filter_complex.video.*;
import org.luke.decut.ffmpeg.options.Duration;
import org.luke.decut.ffmpeg.options.FfmpegOption;
import org.luke.decut.ffmpeg.options.Map;

import java.io.File;
import java.util.*;

public class SegmentRenderer {

    private final Home owner;

    public SegmentRenderer(Home owner) {
        this.owner = owner;
    }

    private List<Track> getTracks() {
        return owner.getTracks().getTracks();
    }

    private List<TimelineClip> getClips(Track track) {
        return track.getContent().getClips();
    }

    public int getWidth() {
        return owner.canvasWidthProperty().intValue();
    }

    public int getHeight() {
        return owner.canvasHeightProperty().intValue();
    }

    public double getFrameRate() {
        return owner.framerateProperty().get();
    }

    private int getTrackLayer(TimelineClip clip) {
        TrackContent cont = (TrackContent) clip.getParent();
        Track track = cont.getTrack();
        return owner.getTracks().getTracks().indexOf(track);
    }

    public FfmpegCommand renderSegmentFrames(File outputDir, double startTime, double duration, double qualityFactor) {
        FfmpegCommand command = new FfmpegCommand();

        File outputPattern = new File(outputDir, "frame_%06d.bmp");
        command.setOutput(outputPattern);

        command.addOption(new FfmpegOption("f").setValue("image2"));
        command.addOption(new FfmpegOption("q:v").setValue("3"));

        command.addOption(new FfmpegOption("r").setValue(String.valueOf(getFrameRate())));

        List<Track> tracks = getTracks();
        List<VideoClip> videoClips = new ArrayList<>();

        double endTime = startTime + duration;
        for (Track track : tracks) {
            for (TimelineClip clip : getClips(track)) {
                if(clip.getStartTime() < endTime && clip.getEndTime() > startTime) {
                    if (clip instanceof VideoClip) {
                        videoClips.add((VideoClip) clip);
                    }
                }
            }
        }

        Set<File> inputFiles = new HashSet<>();
        for (VideoClip clip : videoClips) {
            inputFiles.add(clip.getSourceAsset().getFile());
        }

        HashMap<File, Integer> fileToInputIndex = new HashMap<>();
        int inputIndex = 0;
        for (File file : inputFiles) {
            command.addInput(file);
            fileToInputIndex.put(file, inputIndex++);
        }

        command.setDuration((long) (duration * 1000));
        command.addOption(new Duration(duration));

        String finalVideoLabel = processVideoClips(command, videoClips, fileToInputIndex, duration, startTime, qualityFactor);

        if (finalVideoLabel != null) {
            command.addOption(new Map(finalVideoLabel));
        }

        return command;
    }

    public FfmpegCommand renderSegmentAudio(File outputFile, double startTime, double duration) {
        FfmpegCommand command = new FfmpegCommand();

        command.setOutput(outputFile);

        command.setCodec(new AudioCodec("pcm_s16le"));
        command.addOption(new FfmpegOption("ar").setValue("44100"));
        command.addOption(new FfmpegOption("ac").setValue("2"));

        List<Track> tracks = getTracks();
        List<AudioClip> audioClips = new ArrayList<>();

        double endTime = startTime + duration;
        for (Track track : tracks) {
            for (TimelineClip clip : getClips(track)) {
                if(clip.getStartTime() < endTime && clip.getEndTime() > startTime) {
                    if (clip instanceof AudioClip) {
                        audioClips.add((AudioClip) clip);
                    }
                }
            }
        }

        Set<File> inputFiles = new HashSet<>();
        for (AudioClip clip : audioClips) {
            inputFiles.add(clip.getSourceAsset().getFile());
        }

        HashMap<File, Integer> fileToInputIndex = new HashMap<>();
        int inputIndex = 0;
        for (File file : inputFiles) {
            command.addInput(file);
            fileToInputIndex.put(file, inputIndex++);
        }

        command.setDuration((long) (duration * 1000));
        command.addOption(new Duration(duration));

        String finalAudioLabel = processAudioClips(command, audioClips, fileToInputIndex, duration, startTime);

        if (finalAudioLabel != null) {
            command.addOption(new Map(finalAudioLabel));
        }

        return command;
    }

    private String processVideoClips(FfmpegCommand command, List<VideoClip> videoClips,
                                     HashMap<File, Integer> fileToInputIndex, double duration, double startTime,
                                     double qualityFactor) {

        videoClips.sort((a, b) -> {
            int layerCompare = -Integer.compare(getTrackLayer(a), getTrackLayer(b));
            if (layerCompare != 0) return layerCompare;
            return Double.compare(a.getStartTime(), b.getStartTime());
        });

        int previewWidth = (int) (getWidth() * qualityFactor);
        int previewHeight = (int) (getHeight() * qualityFactor);

        String currentVideoLabel;
        int labelCounter = 0;

        String baseVideoLabel = "[base_video]";
        command.addComplexFilterNode(new ComplexFilterNode()
                .addFilter(new ColorSrc()
                        .setColor("black")
                        .setSize(previewWidth + "x" + previewHeight)
                        .setDuration(duration)
                        .setRate(String.valueOf(getFrameRate())))
                .setOutput(baseVideoLabel));

        currentVideoLabel = baseVideoLabel;

        for (VideoClip clip : videoClips) {
            int inputIdx = fileToInputIndex.get(clip.getSourceAsset().getFile());
            String clipLabel = "[clip_" + labelCounter + "]";
            String scaledLabel = "[scaled_" + labelCounter + "]";
            String overlayLabel = "[overlay_" + labelCounter + "]";

            double clipStart = clip.getStartTime() - startTime;
            double clipInpoint = clip.getInPoint();
            double clipDuration = clip.getDuration();
            if(clipStart < 0) {
                double ds = -clipStart;
                clipStart = 0;
                clipInpoint += ds;
                clipDuration -= ds;
            }

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput("[" + inputIdx + ":v]")
                    .addFilter(new TrimC(clipInpoint, clipDuration))
                    .addFilter(new SetPts().setExpr("PTS-STARTPTS+" + clipStart + "/TB"))
                    .setOutput(clipLabel));

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput(clipLabel)
                    .addFilter(new Scale().cover(previewWidth, previewHeight))
                    .setOutput(scaledLabel));

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInputs(currentVideoLabel, scaledLabel)
                    .addFilter(new Overlay()
                            .center()
                            .setEofAction("pass"))
                    .setOutput(overlayLabel));

            currentVideoLabel = overlayLabel;
            labelCounter++;
        }

        return currentVideoLabel;
    }

    public String processAudioClips(FfmpegCommand command, List<AudioClip> audioClips,
                                    HashMap<File, Integer> fileToInputIndex, double duration, double startTime) {

        String silenceBaseLabel = "[silence_base]";
        command.addComplexFilterNode(new ComplexFilterNode()
                .addFilter(new AEvalSrc()
                        .setExprs("0")
                        .setDuration(String.valueOf(duration)))
                .setOutput(silenceBaseLabel));

        List<String> audioMixInputs = new ArrayList<>();
        audioMixInputs.add(silenceBaseLabel);

        int labelCounter = 0;

        for (AudioClip clip : audioClips) {
            int inputIdx = fileToInputIndex.get(clip.getSourceAsset().getFile());
            String trimmedLabel = "[atrimmed_" + labelCounter + "]";
            String processedClipLabel = trimmedLabel;

            double clipStart = clip.getStartTime() - startTime;
            double clipInpoint = clip.getInPoint();
            double clipDuration = clip.getDuration();
            if(clipStart < 0) {
                double ds = -clipStart;
                clipStart = 0;
                clipInpoint += ds;
                clipDuration -= ds;
            }
            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput("[" + inputIdx + ":a]")
                    .addFilter(new ATrimC(clipInpoint, clipDuration))
                    .addFilter(new ASetPts().setExpr("PTS-STARTPTS"))
                    .setOutput(trimmedLabel));

            if (clip.getStartTime() > 0) {
                String delayedLabel = "[adelayed_" + labelCounter + "]";
                long delayMs = (long) (clipStart * 1000);
                command.addComplexFilterNode(new ComplexFilterNode()
                        .setInput(processedClipLabel)
                        .addFilter(new ADelay(delayMs))
                        .setOutput(delayedLabel));
                processedClipLabel = delayedLabel;
            }

            audioMixInputs.add(processedClipLabel);
            labelCounter++;
        }

        String finalMixedAudioLabel = "[final_mixed_audio]";

        if (audioMixInputs.size() > 1) {
            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInputs(audioMixInputs.toArray(new String[0]))
                    .addFilter(new AMix(audioMixInputs.size()).setDuration("longest").setNormalize("0"))
                    .setOutput(finalMixedAudioLabel));


            String masterVolumeLabel = "[master_output_volume]";
            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput(finalMixedAudioLabel)
                    .addFilter(new Volume(1.0))
                    .setOutput(masterVolumeLabel));
            finalMixedAudioLabel = masterVolumeLabel;
        } else {
            finalMixedAudioLabel = silenceBaseLabel;
        }

        return finalMixedAudioLabel;
    }
}