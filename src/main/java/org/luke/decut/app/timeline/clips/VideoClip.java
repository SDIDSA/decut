package org.luke.decut.app.timeline.clips;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.VideoAssetData;
import org.luke.decut.app.timeline.tracks.Track;

public class VideoClip extends TimelineClip {

    private final ImageView thumb;

    public VideoClip(Home owner, Track track, VideoAssetData sourceAsset, double startTime) {
        super(owner, track, sourceAsset, startTime);

        thumb = new ImageView(sourceAsset.getThumb());
        thumb.setPreserveRatio(true);
        thumb.fitHeightProperty().bind(track.heightProperty().subtract(10));
        thumb.setLayoutY(2);
        thumb.setLayoutX(2);

        Rectangle imageClip = new Rectangle();
        imageClip.widthProperty().bind(widthProperty().subtract(4));
        imageClip.heightProperty().bind(thumb.fitHeightProperty());
        imageClip.setArcHeight(10);
        imageClip.setArcWidth(10);
        thumb.setClip(imageClip);

        getChildren().addAll(thumb);
    }

}