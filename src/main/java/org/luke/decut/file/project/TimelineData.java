package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.home.Home;

public class TimelineData implements ProjectPart {
    public static final String TRACKS = "tracks";
    public static final String LINKED = "linked";

    private final TrackListData tracks;
    private final LinkedGroupsData linked;

    public TimelineData() {
        this.tracks = new TrackListData();
        this.linked = new LinkedGroupsData();
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put(TRACKS, tracks.serialize())
                .put(LINKED, linked.serialize());
    }

    public void deserialize(JSONObject data) {
        tracks.deserialize(data.getJSONObject(TRACKS));
        linked.deserialize(data.getJSONObject(LINKED));
    }

    @Override
    public void save(Home owner) {
        tracks.save(owner);
        linked.save(owner);
    }

    @Override
    public void load(Home owner) {
        tracks.load(owner);
        linked.load(owner);
    }
}
