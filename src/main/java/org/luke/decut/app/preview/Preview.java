package org.luke.decut.app.preview;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Preview extends VBox implements Styleable {
    private static final double SEG_SIZE = 3; // Duration of each segment in seconds
    private static final int PRELOAD_BUFFER = 2; // Number of segments to preload ahead/behind

    private final HashMap<Integer, FrameSequence> segments;
    private final HashMap<Integer, CompletableFuture<FrameSequence>> loadingSegments;
    private final ImageView view;
    private final Home owner;

    private int currentSegment = -1;
    private FrameSequence currentSequence;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final AnimationTimer frameTimer;

    private SourceDataLine audioLine;
    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();
    private Thread audioThread;
    private final AtomicBoolean isAudioRunning = new AtomicBoolean(false);

    // Audio synchronization
    private double lastAudioSeekTime = -1;
    private long playbackStartTime = -1;
    private double playbackStartPosition = 0;

    public Preview(Home owner) {
        this.owner = owner;
        setAlignment(Pos.CENTER);

        segments = new HashMap<>();
        loadingSegments = new HashMap<>();

        view = new ImageView();
        view.setPreserveRatio(true);
        view.setSmooth(true);

        getChildren().add(view);

        owner.atProperty().addListener((_, _, nv) -> {
            if (isPlaying.get()) return;
            int index = segIndex(nv.doubleValue());
            switchToSegment(index, nv.doubleValue());
        });

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        frameTimer = new AnimationTimer();
        initAudio();
        applyStyle(owner.getWindow().getStyl());
    }

    private void initAudio() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();

            isAudioRunning.set(true);
            audioThread = new Thread(() -> {
                try {
                    while (isAudioRunning.get()) {
                        byte[] buffer = audioQueue.take();
                        audioLine.write(buffer, 0, buffer.length);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            audioThread.setDaemon(true);
            audioThread.setName("Audio-Playback-Thread");
            audioThread.start();

        } catch (LineUnavailableException e) {
            ErrorHandler.handle(e, "Failed to initialize audio line");
        }
    }

    public void clearCache() {
        pause();
        segments.clear();
        loadingSegments.values().forEach(cf -> cf.cancel(true));
        loadingSegments.clear();
        if(currentSequence != null) {
            currentSequence = null;
            double at = owner.atProperty().get();
            int index = segIndex(at);
            switchToSegment(index, at);
        }
    }

    private void switchToSegment(int index, double time) {
        if(index * SEG_SIZE >= owner.durationProperty().get()) {
            pause();
            return;
        }
        if (index == currentSegment && currentSequence != null) {
            updateFrameOnly(time);
            return;
        }

        FrameSequence sequence = segments.get(index);
        if (sequence != null) {
            setCurrentSequence(sequence, index, time);
            preloadAdjacentSegments(index);
        } else {
            CompletableFuture<FrameSequence> loadingFuture = loadingSegments.get(index);
            if (loadingFuture != null) {
                loadingFuture.thenAccept(loadedSequence -> Platform.runLater(() -> {
                    if (segIndex(owner.atProperty().get()) == index) {
                        setCurrentSequence(loadedSequence, index, time);
                    }
                }));
            } else {
                loadSegmentAsync(index).thenAccept(loadedSequence -> Platform.runLater(() -> {
                    if (segIndex(owner.atProperty().get()) == index) {
                        setCurrentSequence(loadedSequence, index, time);
                    }
                }));
            }
            preloadAdjacentSegments(index);
        }
    }

    private void setCurrentSequence(FrameSequence sequence, int index, double time) {
        currentSequence = sequence;
        currentSegment = index;
        updateFrameOnly(time);

        if (isPlaying.get()) {
            currentSequence.play(time);
            resetPlaybackTiming(time);
            if (isPlaying.get()) {
                audioQueue.offer(currentSequence.audioData);
            }
        }
        updateFrameOnly(time);
    }

    private void updateFrameOnly(double time) {
        if (currentSequence != null) {
            double segmentTime = time % SEG_SIZE;
            int frameIndex = (int) (segmentTime * owner.framerateProperty().get());
            Image frame = currentSequence.getFrame(frameIndex);
            if (frame != null) {
                view.setImage(frame);
            }
        }
    }

    private void updateAudioPosition(double time) {
        if (currentSequence != null) {
            double segmentTime = time % SEG_SIZE;
            AudioFormat format = audioLine.getFormat();
            int frameSize = format.getFrameSize(); // Bytes per frame (e.g., 4 bytes for 16-bit stereo)
            float frameRate = format.getFrameRate(); // Frames per second (e.g., 44100)

            int byteOffset = (int) (segmentTime * frameRate * frameSize);

            if (byteOffset % frameSize != 0) {
                byteOffset -= byteOffset % frameSize;
            }

            byte[] fullAudioData = currentSequence.getAudioData();
            if (byteOffset >= fullAudioData.length) {
                return;
            }

            byte[] partialAudio = Arrays.copyOfRange(fullAudioData, byteOffset, fullAudioData.length);

            audioQueue.clear();
            audioQueue.offer(partialAudio);
            lastAudioSeekTime = time;
        }
    }

    private void resetPlaybackTiming(double time) {
        playbackStartTime = System.nanoTime();
        playbackStartPosition = time;
    }

    private void preloadAdjacentSegments(int currentIndex) {
        for (int i = currentIndex - PRELOAD_BUFFER; i <= currentIndex + PRELOAD_BUFFER; i++) {
            if (i >= 0 && i != currentIndex && !segments.containsKey(i) && !loadingSegments.containsKey(i)) {
                if(i * SEG_SIZE >= owner.durationProperty().get()) continue;
                loadSegmentAsync(i);
            }
        }
    }

    private CompletableFuture<FrameSequence> loadSegmentAsync(int index) {
        CompletableFuture<FrameSequence> future = CompletableFuture.supplyAsync(() -> {
            try {
                File tempDir = Files.createTempDirectory("decut_prev_" + index).toFile();
                double start = index * SEG_SIZE;

                FfmpegCommand imageCom = owner.previewFrames(tempDir, start, SEG_SIZE);
                System.out.println(imageCom.apply(imageCom));
                imageCom.execute();

                File audioFile = new File(tempDir, "audio_" + index + ".wav");
                FfmpegCommand audioCom = owner.previewAudio(audioFile, start, SEG_SIZE);
                audioCom.execute();

                imageCom.waitFor();
                audioCom.waitFor();

                // Load images
                File[] imageFiles = tempDir.listFiles((dir, name) -> name.matches("frame_\\d{6}\\.jpg"));
                if (imageFiles == null || imageFiles.length == 0) {
                    throw new IOException("No frames rendered for segment " + index);
                }

                List<Image> frames = new ArrayList<>();
                for (File imageFile : imageFiles) {
                    frames.add(new Image(imageFile.toURI().toString(), true));
                }

                // Load audio using Java Sound API
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                byte[] audioData = audioStream.readAllBytes();
                audioStream.close();

                FrameSequence sequence = new FrameSequence(frames, audioData, audioStream,
                        owner.framerateProperty().get(), tempDir);

                sequence.setOnEnd(() -> {
                    if (isPlaying.get()) {
                        double nextTime = (index + 1) * SEG_SIZE;
                        owner.atProperty().set(nextTime);
                        switchToSegment(index + 1, nextTime);
                    }
                });

                Platform.runLater(() -> {
                    segments.put(index, sequence);
                    loadingSegments.remove(index);
                });

                return sequence;
            } catch (Exception e) {
                ErrorHandler.handle(e, "render segment " + index);
                loadingSegments.remove(index);
                return null;
            }
        }, org.luke.gui.threading.Platform.back);

        loadingSegments.put(index, future);
        return future;
    }

    public void play() {
        if (currentSequence != null && !isPlaying.get()) {
            isPlaying.set(true);
            double time = owner.atProperty().get();
            currentSequence.play(time);
            resetPlaybackTiming(time);
            frameTimer.start();
            updateAudioPosition(time);
        }
    }

    public void pause() {
        if (currentSequence != null && isPlaying.get()) {
            isPlaying.set(false);
            frameTimer.stop();
            audioQueue.clear(); // Clear the queue to stop any playing sound
            audioLine.flush();
        }
    }

    public int segIndex(double time) {
        return (int) (time / SEG_SIZE);
    }

    public void dispose() {
        segments.values().forEach(FrameSequence::dispose);
        segments.clear();

        loadingSegments.values().forEach(future -> future.cancel(true));
        loadingSegments.clear();

        frameTimer.stop();

        isAudioRunning.set(false);
        if (audioThread != null) {
            audioThread.interrupt();
        }
        if (audioLine != null) {
            audioLine.drain();
            audioLine.close();
        }
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), new CornerRadii(2, 2, 2, 2, false)));
    }

    private class FrameSequence {
        private final List<Image> frames;
        private final byte[] audioData;
        private final AudioInputStream audioStream;
        private final double frameRate;
        private final File tempDir;
        private final AudioFormat audioFormat;
        private final int sampleRate;
        private Runnable onEnd;

        public FrameSequence(List<Image> frames, byte[] audioData, AudioInputStream audioStream,
                             double frameRate, File tempDir) {
            this.frames = frames;
            this.audioData = audioData;
            this.audioStream = audioStream;
            this.frameRate = frameRate;
            this.tempDir = tempDir;
            this.audioFormat = audioStream.getFormat();
            this.sampleRate = (int) audioFormat.getSampleRate();
        }

        public Image getFrame(int index) {
            if (index >= 0 && index < frames.size()) {
                return frames.get(index);
            }
            return null;
        }

        public void play(double time) {
            double segmentTime = time % SEG_SIZE;
            int frameIndex = (int) (segmentTime * frameRate);

            if (frameIndex >= frames.size()) {
                if (onEnd != null) {
                    onEnd.run();
                }
                return;
            }

            view.setImage(getFrame(frameIndex));
        }

        public byte[] getAudioData() {
            return audioData;
        }

        public void setOnEnd(Runnable onEnd) {
            this.onEnd = onEnd;
        }

        public void dispose() {
            frames.forEach(Image::cancel);

            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing audio stream: " + e.getMessage());
                }
            }

            if (tempDir != null && tempDir.exists()) {
                File[] files = tempDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                tempDir.delete();
            }
        }
    }

    private class AnimationTimer extends javafx.animation.AnimationTimer {
        private long lastUpdate = 0;

        @Override
        public void handle(long now) {
            if (lastUpdate == 0) {
                lastUpdate = now;
                return;
            }

            double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
            lastUpdate = now;

            // Use more accurate timing based on actual playback time
            double currentTime;
            if (playbackStartTime > 0) {
                double playbackElapsed = (now - playbackStartTime) / 1_000_000_000.0;
                currentTime = playbackStartPosition + playbackElapsed;
            } else {
                currentTime = owner.atProperty().get() + elapsedSeconds;
            }

            if(currentTime >= owner.durationProperty().get()) {
                stop();
                return;
            }
            int newSegment = segIndex(currentTime);

            if (newSegment != currentSegment) {
                switchToSegment(newSegment, currentTime);
            } else {
                updateFrameOnly(currentTime);
            }

            owner.atProperty().set(currentTime);
        }
    }
}