package org.luke.decut.dragdrop;

import javafx.scene.input.Dragboard;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.tracks.Track;

public class TrackDrag extends DragContent<Track> {
    public static final String TYPE_TRACK = "track";
    public static final String INDEX = "index";

    private int index;

    public TrackDrag(Home owner, Dragboard db) throws DragContentException {
        super(owner, db);
    }

    public TrackDrag(Home owner, Track body) {
        super(owner, TYPE_TRACK, body);
    }

    @Override
    public Track decode(JSONObject body) throws DragContentException {
        index = body.getInt(INDEX);
        return owner.getTracks().getTracks().get(index);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public JSONObject encode(Track body) {
        return new JSONObject().put(INDEX, owner.getTracks().getTracks().indexOf(body));
    }

    @Override
    public boolean isValidType(String type) {
        return TYPE_TRACK.equals(type);
    }
}
