package org.luke.decut.file.project;

import org.json.JSONArray;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.LibraryContent;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.decut.app.lib.assets.data.AssetData;

import java.io.File;
import java.util.ArrayList;

public class ProjectAssets extends ArrayList<File> implements ProjectPart {
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

    @Override
    public void save(Home owner) {
        for (AssetData asset : LibraryContent.getInstance(owner, Assets.class).getGrid().getData()) {
            add(asset.getFile());
        }
    }

    @Override
    public void load(Home owner) {
        LibraryContent.getInstance(owner, Assets.class).getGrid().importFiles(this);
    }
}
