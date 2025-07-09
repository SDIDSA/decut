package org.luke.decut.file.project;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;

public class TimelineData extends ArrayList<TrackData> {

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

}
