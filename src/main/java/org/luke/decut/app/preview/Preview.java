package org.luke.decut.app.preview;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.decut.crossplatform.Os;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.gui.exception.ErrorHandler;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Preview extends VBox implements Styleable {
    private static final double SEG_SIZE = 3;
    private static final int PRELOAD_BUFFER = 2;
    private static final int AUDIO_QUEUE_CAPACITY = 5;
    private static final double AUDIO_POS_THRESHOLD = 0.03;

    private final HashMap<Integer, FrameSequence> segments;
    private final HashMap<Integer, CompletableFuture<FrameSequence>> loadingSegments;
    private final ImageView view;
    private final Home owner;

    private double qualityFactor = 0.5;

    private int currentSegment = -1;
    private FrameSequence currentSequence;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final AnimationTimer frameTimer;

    private SourceDataLine audioLine;
    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>(AUDIO_QUEUE_CAPACITY);
    private Thread audioThread;
    private final AtomicBoolean isAudioRunning = new AtomicBoolean(false);

    private long playbackStartTime = -1;
    private double playbackStartPosition = 0;

    private final ExecutorService executor;

    public Preview(Home owner) {
        this.owner = owner;
        setAlignment(Pos.CENTER);

        segments = new HashMap<>();
        loadingSegments = new HashMap<>();

        view = new ImageView();

        Runnable scaler = () -> {
            double pw = getWidth();
            double ph = getHeight();

            double sw = owner.canvasWidthProperty().get();
            double sh = owner.canvasHeightProperty().get();

            double scaleX = pw / sw;
            double scaleY = ph / sh;

            double scale = Math.min(scaleX, scaleY);

            double finalWidth = sw * scale;
            double finalHeight = sh * scale;

            view.setFitWidth(finalWidth);
            view.setFitHeight(finalHeight);
        };

        widthProperty().addListener((_, _, _) -> scaler.run());
        heightProperty().addListener((_, _, _) -> scaler.run());
        owner.canvasWidthProperty().addListener((_, _, _) -> scaler.run());
        owner.canvasHeightProperty().addListener((_, _, _) -> scaler.run());

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

        executor = Executors.newFixedThreadPool(1);

        frameTimer = new AnimationTimer();
        initAudio();
        applyStyle(owner.getWindow().getStyl());
    }

    public void setQualityFactor(double qualityFactor) {
        this.qualityFactor = qualityFactor;
        clearCache();
    }

    private void initAudio() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format, 44100 * 4);
            audioLine.start();

            isAudioRunning.set(true);
            audioThread = new Thread(() -> {
                try {
                    while (isAudioRunning.get()) {
                        byte[] buffer = audioQueue.take();
                        if (buffer.length > 0) {
                            audioLine.write(buffer, 0, buffer.length);
                        }
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
        owner.pausePlayback();
        ArrayList<Integer> toRemove = new ArrayList<>();
        File out = Os.fromSystem().getDecutRoot();
        segments.keySet().forEach(index -> {
            FfmpegCommand imgCom = owner.previewFrames(out, index * SEG_SIZE, SEG_SIZE, qualityFactor);
            FfmpegCommand audioCom = owner.previewAudio(out, index * SEG_SIZE, SEG_SIZE);
            String com = imgCom.setOutput(out).apply(imgCom, "ffmpeg").toString() + audioCom.setOutput(out).apply(audioCom, "ffmpeg").toString();
            if(segments.get(index) != null && !segments.get(index).getCommand().equals(com)) {
                toRemove.add(index);
            }
        });
        toRemove.forEach(segments::remove);
        toRemove.forEach(index -> {
            if(loadingSegments.get(index) != null) {
                loadingSegments.get(index).cancel(true);
            }
        });
        toRemove.forEach(loadingSegments::remove);
        if (currentSequence != null) {
            view.setImage(null);
            FfmpegCommand imgCom = owner.previewFrames(out, currentSegment * SEG_SIZE, SEG_SIZE, qualityFactor);
            FfmpegCommand audioCom = owner.previewAudio(out, currentSegment * SEG_SIZE, SEG_SIZE);
            String com = imgCom.setOutput(out).apply(imgCom, "ffmpeg").toString() + audioCom.setOutput(out).apply(audioCom, "ffmpeg").toString();
            if(!currentSequence.getCommand().equals(com)) {
                currentSequence = null;
                double at = owner.atProperty().get();
                int index = segIndex(at);
                switchToSegment(index, at);
            }
        }
    }

    private void switchToSegment(int index, double time) {
        if (index * SEG_SIZE >= owner.durationProperty().get()) {
            owner.pausePlayback();
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
            view.setImage(null);
            boolean wasPlaying = frameTimer.isRunning();
            frameTimer.stop();
            CompletableFuture<FrameSequence> loadingFuture = loadingSegments.get(index);
            if (loadingFuture != null) {
                loadingFuture.thenAccept(loadedSequence -> Platform.runLater(() -> {
                    if (segIndex(owner.atProperty().get()) == index) {
                        setCurrentSequence(loadedSequence, index, time);
                        if(wasPlaying) {
                            frameTimer.start();
                        }
                    }
                }));
            } else {
                loadSegmentAsync(index).thenAccept(loadedSequence -> Platform.runLater(() -> {
                    if (segIndex(owner.atProperty().get()) == index) {
                        setCurrentSequence(loadedSequence, index, time);
                        if(wasPlaying) {
                            frameTimer.start();
                        }
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
            audioQueue.clear();
            currentSequence.play(time);
            resetPlaybackTiming(time);
            updateAudioPosition(time);
        }
    }

    private void updateFrameOnly(double time) {
        if (currentSequence != null) {
            double segmentTime = time - (currentSegment * SEG_SIZE);
            int frameIndex = (int) (segmentTime * owner.framerateProperty().get());
            Image frame = currentSequence.getFrame(frameIndex);
            if (frame != null) {
                view.setImage(frame);
            }
        }
    }

    private void updateAudioPosition(double time) {
        if (currentSequence != null) {
            double segmentTime = time - (currentSegment * SEG_SIZE);
            if(segmentTime < AUDIO_POS_THRESHOLD) {
                audioQueue.offer(currentSequence.audioData);
            } else {
                AudioFormat format = audioLine.getFormat();
                int frameSize = format.getFrameSize();
                float frameRate = format.getFrameRate();
                int byteOffset = (int) (segmentTime * frameRate * frameSize);

                byteOffset -= byteOffset % frameSize;

                byte[] fullAudioData = currentSequence.getAudioData();
                if (byteOffset < fullAudioData.length) {
                    byte[] partialAudio = Arrays.copyOfRange(fullAudioData, byteOffset, fullAudioData.length);
                    try {
                        audioQueue.put(partialAudio);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        }
    }

    private void resetPlaybackTiming(double time) {
        playbackStartTime = System.nanoTime();
        playbackStartPosition = time;
    }

    private void preloadAdjacentSegments(int currentIndex) {
        for (int i = currentIndex - PRELOAD_BUFFER; i <= currentIndex + PRELOAD_BUFFER; i++) {
            if (i >= 0 && i != currentIndex && !segments.containsKey(i) && !loadingSegments.containsKey(i)) {
                if (i * SEG_SIZE >= owner.durationProperty().get()) continue;
                loadSegmentAsync(i);
            }
        }
    }

    private CompletableFuture<FrameSequence> loadSegmentAsync(int index) {
        CompletableFuture<FrameSequence> future = CompletableFuture.supplyAsync(() -> {
            try {
                double start = index * SEG_SIZE;
                long diag = System.currentTimeMillis();
                File tempDir = Files.createTempDirectory("decut_prev_" + index).toFile();
                FfmpegCommand imageCom = owner.previewFrames(tempDir, start, SEG_SIZE, qualityFactor);
                imageCom.execute();

                File audioFile = new File(tempDir, "audio_" + index + ".wav");
                FfmpegCommand audioCom = owner.previewAudio(audioFile, start, SEG_SIZE);
                audioCom.execute();

                imageCom.waitFor();

                File[] imageFiles = tempDir.listFiles((dir, name) -> name.matches("frame_\\d{6}\\.bmp"));
                if (imageFiles == null || imageFiles.length == 0) {
                    throw new IOException("No frames rendered for segment " + index);
                }

                List<Image> frames = new ArrayList<>();
                Arrays.stream(imageFiles).sorted(Comparator.comparing(File::getName)).forEach(imageFile -> {
                    frames.add(new Image(imageFile.toURI().toString(), true));
                });

                audioCom.waitFor();
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                byte[] audioData = audioStream.readAllBytes();
                audioStream.close();

                System.out.println((System.currentTimeMillis() - diag) + " ms");

                String com = imageCom.setOutput(Os.fromSystem().getDecutRoot()).apply(imageCom, "ffmpeg").toString() + audioCom.setOutput(Os.fromSystem().getDecutRoot()).apply(audioCom, "ffmpeg").toString();
                FrameSequence sequence = new FrameSequence(com, frames, audioData, audioStream,
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
        }, executor);

        loadingSegments.put(index, future);
        return future;
    }

    public void play() {
        if (currentSequence != null && !isPlaying.get()) {
            isPlaying.set(true);
            double time = owner.atProperty().get();
            resetPlaybackTiming(time);
            frameTimer.start();
            updateAudioPosition(time);
        }
    }

    public void pause() {
        if (currentSequence != null && isPlaying.get()) {
            isPlaying.set(false);
            frameTimer.stop();
            audioQueue.clear();
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
        private final String command;
        private final List<Image> frames;
        private final byte[] audioData;
        private final AudioInputStream audioStream;
        private final double frameRate;
        private final File tempDir;
        private Runnable onEnd;

        public FrameSequence(String command, List<Image> frames, byte[] audioData, AudioInputStream audioStream,
                             double frameRate, File tempDir) {
            this.command = command;
            this.frames = frames;
            this.audioData = audioData;
            this.audioStream = audioStream;
            this.frameRate = frameRate;
            this.tempDir = tempDir;
        }

        public String getCommand() {
            return command;
        }

        public Image getFrame(int index) {
            if (index >= 0 && index < frames.size()) {
                return frames.get(index);
            }
            return null;
        }

        public void play(double time) {
            double segmentTime = time - (currentSegment * SEG_SIZE);
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

        private boolean running =false;

        @Override
        public void handle(long now) {
            if (lastUpdate == 0) {
                lastUpdate = now;
                return;
            }

            double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
            lastUpdate = now;

            double currentTime = playbackStartTime > 0 ?
                    playbackStartPosition + ((now - playbackStartTime) / 1_000_000_000.0) :
                    owner.atProperty().get() + elapsedSeconds;

            if (currentTime >= owner.durationProperty().get()) {
                stop();
                owner.pausePlayback();
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

        @Override
        public void start() {
            super.start();
            running = true;
        }

        @Override
        public void stop() {
            super.stop();
            running = false;
        }

        public boolean isRunning() {
            return running;
        }
    }
}