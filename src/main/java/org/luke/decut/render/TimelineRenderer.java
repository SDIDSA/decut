package org.luke.decut.render;

import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.clips.AudioClip;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.clips.VideoClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.app.timeline.viewport.content.TrackContent;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.codec.AudioCodec;
import org.luke.decut.ffmpeg.codec.VideoCodec;
import org.luke.decut.ffmpeg.filter_complex.audio.*;
import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilterNode;
import org.luke.decut.ffmpeg.filter_complex.video.*;
import org.luke.decut.ffmpeg.options.Duration;
import org.luke.decut.ffmpeg.options.Map;

import java.io.File;
import java.util.*;

public class TimelineRenderer {

    private final Home owner;

    public TimelineRenderer(Home owner) {
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

    public FfmpegCommand generateRenderCommand(File outputFile) {
        FfmpegCommand command = new FfmpegCommand();

        // Set output file
        command.setOutput(outputFile);

        // Set codecs
        command.setCodec(VideoCodec.H264);
        command.setCodec(new AudioCodec("aac"));

        // Collect all clips from all tracks
        List<Track> tracks = getTracks();
        List<VideoClip> videoClips = new ArrayList<>();
        List<AudioClip> audioClips = new ArrayList<>();

        // Separate video and audio clips
        for (Track track : tracks) {
            for (TimelineClip clip : getClips(track)) {
                if (clip instanceof VideoClip) {
                    videoClips.add((VideoClip) clip);
                } else if (clip instanceof AudioClip) {
                    audioClips.add((AudioClip) clip);
                }
            }
        }

        // Add input files to command
        Set<File> inputFiles = new HashSet<>();
        for (VideoClip clip : videoClips) {
            inputFiles.add(clip.getSourceAsset().getFile());
        }
        for (AudioClip clip : audioClips) {
            inputFiles.add(clip.getSourceAsset().getFile());
        }

        // Create a mapping from file to input index
        HashMap<File, Integer> fileToInputIndex = new HashMap<>();
        int inputIndex = 0;
        for (File file : inputFiles) {
            command.addInput(file);
            fileToInputIndex.put(file, inputIndex++);
        }

        // Calculate timeline duration
        double timelineDuration = owner.durationProperty().get();
        command.setDuration((long) (timelineDuration * 1000));
        command.addOption(new Duration(timelineDuration));

        // Process video clips
        String finalVideoLabel = processVideoClips(command, videoClips, fileToInputIndex, timelineDuration);

        // Process audio clips
        String finalAudioLabel = processAudioClips(command, audioClips, fileToInputIndex, timelineDuration);

        // Map final outputs - use null filter for pass-through
        if (finalVideoLabel != null && finalAudioLabel != null) {
            command.addOption(new Map(finalVideoLabel));
            command.addOption(new Map(finalAudioLabel));
        } else if (finalVideoLabel != null) {
            command.addOption(new Map(finalVideoLabel));
        } else if (finalAudioLabel != null) {
            command.addOption(new Map(finalAudioLabel));
        }

        return command;
    }

    /**
     * Calculates the total duration of the timeline
     */
    private double calculateTimelineDuration() {
        double maxDuration = 0;
        for (Track track : getTracks()) {
            for (TimelineClip clip : getClips(track)) {
                double clipEnd = clip.getStartTime() + clip.getDuration();
                maxDuration = Math.max(maxDuration, clipEnd);
            }
        }
        return maxDuration;
    }

    private String processVideoClips(FfmpegCommand command, List<VideoClip> videoClips,
                                     HashMap<File, Integer> fileToInputIndex, double timelineDuration) {
        if (videoClips.isEmpty()) {
            return null;
        }

        // Sort video clips by timeline start time and track layer
        videoClips.sort((a, b) -> {
            int layerCompare = Integer.compare(getTrackLayer(a), getTrackLayer(b));
            if (layerCompare != 0) return layerCompare;
            return Double.compare(a.getStartTime(), b.getStartTime());
        });

        String currentVideoLabel = null;
        int labelCounter = 0;

        String baseVideoLabel = "[base_video]";
        command.addComplexFilterNode(new ComplexFilterNode()
                .addFilter(new ColorSrc()
                        .setColor("black")
                        .setSize(getWidth() + "x" + getHeight())
                        .setDuration(timelineDuration)
                        .setRate(String.valueOf(getFrameRate())))
                .setOutput(baseVideoLabel));

        currentVideoLabel = baseVideoLabel;

        for (VideoClip clip : videoClips) {
            int inputIdx = fileToInputIndex.get(clip.getSourceAsset().getFile());
            String clipLabel = "[clip_" + labelCounter + "]";
            String scaledLabel = "[scaled_" + labelCounter + "]";
            String overlayLabel = "[overlay_" + labelCounter + "]";

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput("[" + inputIdx + ":v]")
                    .addFilter(new TrimC(clip.getInPoint(), clip.getOutPoint()))
                    .addFilter(new SetPts().setExpr("PTS-STARTPTS+" + clip.getStartTime() + "/TB"))
                    .setOutput(clipLabel));

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput(clipLabel)
                    .addFilter(new Scale().cover(getWidth(), getHeight()))
                    .setOutput(scaledLabel));

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInputs(currentVideoLabel, scaledLabel)
                    .addFilter(new Overlay()
                            .center()
                            //.setX(String.valueOf(0 /*clip.getX()*/))
                            //.setY(String.valueOf(0 /*clip.getY()*/))
                            .setEofAction("pass"))
                    .setOutput(overlayLabel));

            currentVideoLabel = overlayLabel;
            labelCounter++;
        }

        return currentVideoLabel;
    }

    public String processAudioClips(FfmpegCommand command, List<AudioClip> audioClips,
                                    HashMap<File, Integer> fileToInputIndex, double timelineDuration) {

        String silenceBaseLabel = "[silence_base]";
        command.addComplexFilterNode(new ComplexFilterNode()
                .addFilter(new AEvalSrc()
                        .setExprs("0")
                        .setDuration(String.valueOf(timelineDuration)))
                .setOutput(silenceBaseLabel));

        List<String> audioMixInputs = new ArrayList<>();
        audioMixInputs.add(silenceBaseLabel);

        int labelCounter = 0;

        for (AudioClip clip : audioClips) {
            int inputIdx = fileToInputIndex.get(clip.getSourceAsset().getFile());
            String trimmedLabel = "[atrimmed_" + labelCounter + "]";
            String processedClipLabel = trimmedLabel;

            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput("[" + inputIdx + ":a]")
                    .addFilter(new ATrimC(clip.getInPoint(), clip.getOutPoint()))
                    .addFilter(new ASetPts().setExpr("PTS-STARTPTS"))
                    .setOutput(trimmedLabel));

            if (clip.getStartTime() > 0) {
                String delayedLabel = "[adelayed_" + labelCounter + "]";
                long delayMs = (long) (clip.getStartTime() * 1000);
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
                    .setInputs(audioMixInputs.toArray(new String[0])) // Convert list of labels to an array
                    .addFilter(new AMix(audioMixInputs.size()).setDuration("longest").setNormalize("0"))
                    .setOutput(finalMixedAudioLabel));


            String masterVolumeLabel = "[master_output_volume]";
            command.addComplexFilterNode(new ComplexFilterNode()
                    .setInput(finalMixedAudioLabel)
                    .addFilter(new Volume(1.0)) // Default to 1.0, adjust as needed.
                    .setOutput(masterVolumeLabel));
            finalMixedAudioLabel = masterVolumeLabel;
        } else {
            finalMixedAudioLabel = silenceBaseLabel;
        }

        return finalMixedAudioLabel;
    }
    /**
     * Processes all audio clips and creates the mixing chain
     */
//    private String processAudioClips(FfmpegCommand command, List<AudioClip> audioClips,
//                                     HashMap<File, Integer> fileToInputIndex, double timelineDuration) {
//        if (audioClips.isEmpty()) {
//            return null;
//        }
//
//        // Create a silent audio base for the full timeline duration
//        String silenceLabel = "[silence]";
//        command.addComplexFilterNode(new ComplexFilterNode()
//                .addFilter(new AEvalSrc()
//                        .setExprs("0")
//                        .setDuration(String.valueOf(timelineDuration)))
//                .setOutput(silenceLabel));
//
//        String currentAudioLabel = silenceLabel;
//        int labelCounter = 0;
//
//        // Process each audio clip
//        for (AudioClip clip : audioClips) {
//            int inputIdx = fileToInputIndex.get(clip.getSourceAsset().getFile());
//            String clipLabel = "[aclip_" + labelCounter + "]";
//            String mixedLabel = "[amixed_" + labelCounter + "]";
//
//            // Trim the audio clip
//            command.addComplexFilterNode(new ComplexFilterNode()
//                    .setInput("[" + inputIdx + ":a]")
//                    .addFilter(new ATrimC(clip.getInPoint(), clip.getOutPoint()))
//                    .addFilter(new ASetPts().setExpr("PTS-STARTPTS"))
//                    .setOutput(clipLabel));
//
//            // Add delay to position on timeline
//            if (clip.getStartTime() > 0) {
//                String delayedLabel = "[adelayed_" + labelCounter + "]";
//                long delayMs = (long) (clip.getStartTime() * 1000);
//                command.addComplexFilterNode(new ComplexFilterNode()
//                        .setInput(clipLabel)
//                        .addFilter(new ADelay(delayMs))
//                        .setOutput(delayedLabel));
//                clipLabel = delayedLabel;
//            }
//
//            // Mix with current audio
//            command.addComplexFilterNode(new ComplexFilterNode()
//                    .setInputs(currentAudioLabel, clipLabel)
//                    .addFilter(new AMix(2).setDuration("longest"))
//                    .setOutput(mixedLabel));
//
//            currentAudioLabel = mixedLabel;
//            labelCounter++;
//        }
//
//        return currentAudioLabel;
//    }
}