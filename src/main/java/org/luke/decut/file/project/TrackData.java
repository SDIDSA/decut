package org.luke.decut.file.project;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.app.timeline.tracks.TrackType;

import java.util.ArrayList;

public class TrackData extends ArrayList<ClipData> {
    public static final String TYPE = "type";
    public static final String CLIPS = "clips";

    private TrackType type;

    public TrackData(Track track) {
        type = track.getType();

        for (TimelineClip clip : track.getContent().getClips()) {
            add(new ClipData(clip));
        }
    }

    public TrackData(JSONObject data) {
        deserialize(data);
    }

    public TrackType getType() {
        return type;
    }

    public JSONArray serializeClips() {
        JSONArray arr = new JSONArray();
        for (ClipData clip : this) {
            arr.put(clip.serialize());
        }
        return arr;
    }

    public void deserializeClips(JSONArray arr) {
        for(int i = 0; i < arr.length(); i++) {
            add(new ClipData(arr.getJSONObject(i)));
        }
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put(TYPE, type.name())
                .put(CLIPS, serializeClips());
    }

    public void deserialize(JSONObject data) {
        type = TrackType.valueOf(data.getString(TYPE));
        deserializeClips(data.getJSONArray(CLIPS));
    }
}
