package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.tracks.Track;

public class DecutProject {
    public static final String ASSETS = "assets";
    public static final String TIMELINE = "timeline";

    private final ProjectAssets assets;
    private final TimelineData timeline;

    public DecutProject() {
        assets = new ProjectAssets();
        timeline = new TimelineData();
    }

    public ProjectAssets getAssets() {
        return assets;
    }

    public TimelineData getTimeline() {
        return timeline;
    }

    public void addAssets(AssetData...assets) {
        for (AssetData asset : assets) {
            this.assets.add(asset.getFile());
        }
    }

    public void addTracks(Track...tracks) {
        for (Track track : tracks) {
            timeline.add(new TrackData(track));
        }
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put(ASSETS, assets.serialize())
                .put(TIMELINE, timeline.serialize());
    }

    public void deserialize(JSONObject object) {
        assets.deserialize(object.getJSONArray(ASSETS));
        timeline.deserialize(object.getJSONObject(TIMELINE));
    }

    @Override
    public String toString() {
        return serialize().toString(4);
    }
}
