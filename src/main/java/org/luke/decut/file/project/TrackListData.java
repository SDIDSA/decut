package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.lib.assets.data.VideoAssetData;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.threading.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class TrackListData extends ArrayList<TrackData> implements ProjectPart {

    public JSONObject serialize() {
        JSONObject arr = new JSONObject();
        for (int i = 0; i < this.size(); i++) {
            arr.put(String.valueOf(i), get(i).serialize());
        }
        return arr;
    }

    public void deserialize(JSONObject arr) {
        TrackData[] dat = new TrackData[arr.length()];
        for (String key : arr.keySet()) {
            int index = Integer.parseInt(key);
            dat[index] = new TrackData(arr.getJSONObject(key));
        }
        Collections.addAll(this, dat);
    }

    public void forEach(BiConsumer<Integer, TrackData> handler) {
        for (int i = 0; i < size(); i++) {
            handler.accept(i, get(i));
        }
    }

    @Override
    public void save(Home owner) {
        for (Track track : owner.getTracks().getTracks()) {
            add(new TrackData(track));
        }
    }

    @Override
    public void load(Home owner) {
        owner.getTracks().getTrackList().getTracks().clear();
        this.forEach((i, t) -> {
            Track track = owner.getTracks().getTrackList().addTrackAt(new Track(owner, t.getType()), i);
            for (ClipData clipData : t) {
                File source = getFile(owner, clipData);
                AssetData data = AssetData.getData(source);
                if(data instanceof VideoAssetData parent) {
                    if(clipData.getType() == AssetType.VIDEO) {
                        data = parent.getVideo();
                    } else if(clipData.getType() == AssetType.AUDIO) {
                        data = parent.getAudio();
                    }
                }
                TimelineClip clip = track.getContent().createClip(data, 0);
                clip.setStartTime(clipData.getStartTime());
                clip.setInPoint(clipData.getInPoint());
                clip.setOutPoint(clipData.getOutPoint());
                track.getContent().addClip(clip);
            }
        });
    }

    private static File getFile(Home owner, ClipData clipData) {
        File source = clipData.getSource();
        if(!source.exists()) {
            int lastIndexFor = source.getAbsolutePath().lastIndexOf("/");
            int lastIndexBack = source.getAbsolutePath().lastIndexOf("\\");
            String name = source.getAbsolutePath().substring(Math.max(lastIndexBack, lastIndexFor) + 1);
            File rep = new File(owner.getOpenProject().getParent(), name);
            if(rep.exists()) {
                source = rep;
            }
        }
        return source;
    }
}
