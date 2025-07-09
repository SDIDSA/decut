package org.luke.decut.file;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

public class ProjectAssets extends ArrayList<File> {
    public JSONArray serialize() {
        JSONArray arr = new JSONArray();
        for (File asset : this) {
            arr.put(asset.getAbsolutePath());
        }
        return arr;
    }

    public void deserialize(JSONArray arr) {
        for(int i = 0; i < arr.length(); i++) {
            add(new File(arr.getString(i)));
        }
    }
}
