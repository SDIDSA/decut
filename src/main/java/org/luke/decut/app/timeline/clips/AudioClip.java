package org.luke.decut.app.timeline.clips;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AudioAssetData;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.controls.image.ColorImageView;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.factory.Borders;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;

public class AudioClip extends TimelineClip {

    private final ColorImageView thumb;
    private final Timeline waveformUpdateTimeline;

    private double visibleStartTime;
    private double visibleEndTime;

    private long lastCommand;

    public AudioClip(Home owner, Track track, AudioAssetData sourceAsset, double startTime) {
        super(owner, track, sourceAsset, startTime);

        thumb = new ColorImageView(sourceAsset.getThumb());
        thumb.prefHeightProperty().bind(track.heightProperty().subtract(10));
        thumb.setLayoutY(2);

        getChildren().add(thumb);

        waveformUpdateTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            long command = System.currentTimeMillis();
            lastCommand = command;
            Platform.runBack(() -> {
                double pps = owner.ppsProperty().get();

                double vpleft = owner.getViewPort().getScrollX() - owner.getViewPort().preProperty().get();
                vpleft = Math.max(vpleft , 0);
                double vpRight = vpleft + owner.getViewPort().getWidth();
                double clipLeft = getLayoutX();
                double clipRight = clipLeft + getWidth();

                double intersectionLeft = Math.max(clipLeft, vpleft);
                double intersectionRight = Math.min(clipRight, vpRight);

                if (intersectionLeft < intersectionRight) {
                    double visibleClipStart = intersectionLeft - clipLeft;
                    visibleClipStart = Math.max(0, visibleClipStart);
                    double visibleClipEnd = intersectionRight - clipLeft;
                    visibleClipEnd = Math.min(visibleClipEnd, getWidth());

                    double audioStartTime = visibleClipStart / pps + getInPoint();
                    double audioEndTime = visibleClipEnd / pps + getInPoint();
                    double audioDuration = audioEndTime - audioStartTime;

                    if(audioDuration <= 0) return;

                    double displayWidth = audioDuration * pps;
                    double displayX = Math.max(visibleClipStart, 0);

                    Image img = sourceAsset.generateWaveform((int) displayWidth, (int) (getHeight() - 4),
                            audioStartTime, audioDuration);

                    if(command == lastCommand) {
                        this.visibleStartTime = audioStartTime;
                        this.visibleEndTime = audioEndTime;
                        thumb.setPrefWidth(displayWidth);
                        thumb.setLayoutX(displayX);
                        thumb.setImage(img);
                    }
                }
            });
        }));
        waveformUpdateTimeline.setCycleCount(1);

        InvalidationListener listener = obs -> {
            // Update display based on current visible times
            if (visibleStartTime != 0 || visibleEndTime != 0) {
                double pps = owner.ppsProperty().get();
                double audioDuration = visibleEndTime - visibleStartTime;
                double displayWidth = audioDuration * pps;
                double timeOffsetStart = visibleStartTime - getInPoint();
                double displayX = timeOffsetStart * pps;

                thumb.setPrefWidth(displayWidth);
                thumb.setLayoutX(displayX);
            }

            waveformUpdateTimeline.stop();
            waveformUpdateTimeline.playFromStart();
        };

        owner.ppsProperty().addListener(listener);
        inPoint.addListener(listener);
        outPoint.addListener(listener);
        owner.getViewPort().scrollXProperty().addListener(listener);
        track.heightProperty().addListener(listener);

        Platform.runAfter(() -> {
            listener.invalidated(owner.ppsProperty());
        }, 100);

        applyStyle(owner.getWindow().getStyl().get());
    }

    @Override
    public void applyStyle(Style style) {
        if (thumb == null) return;
        thumb.setFill(style.getTextNormal());
        setBorder(Borders.make(style.getAccent(), 4, 1));
        setBackground(Backgrounds.make(style.getBackgroundFloatingOr(), 5));
    }

    // Getters for the visible time range (useful for debugging)
    public double getVisibleStartTime() { return visibleStartTime; }
    public double getVisibleEndTime() { return visibleEndTime; }
}