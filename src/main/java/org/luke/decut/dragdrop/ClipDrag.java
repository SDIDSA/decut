package org.luke.decut.dragdrop;

import javafx.scene.input.Dragboard;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.tracks.Track;

import java.io.File;

public class ClipDrag extends DragContent<TimelineClip> {
    public static final String TYPE_CLIP = "clip";
    public static final String TRACK_INDEX = "track";
    public static final String CLIP_INDEX = "clip";

    public ClipDrag(Home owner, Dragboard db) throws DragContentException {
        super(owner, db);
    }

    public ClipDrag(Home owner, TimelineClip body) {
        super(owner, TYPE_CLIP, body);
    }

    @Override
    public TimelineClip decode(JSONObject body) {
        int trackIndex = body.getInt(TRACK_INDEX);
        int clipIndex = body.getInt(CLIP_INDEX);

        Track track = owner.getTracks().getTracks().get(trackIndex);
        return track.getContent().getSortedClips().get(clipIndex);
    }

    @Override
    public JSONObject encode(TimelineClip body) {
        Track track = body.getTrack();
        return new JSONObject()
                .put(TRACK_INDEX, owner.getTracks().getTracks().indexOf(track))
                .put(CLIP_INDEX, track.getContent().getSortedClips().indexOf(body));
    }

    @Override
    public boolean isValidType(String type) {
        return TYPE_CLIP.equals(type);
    }
}
