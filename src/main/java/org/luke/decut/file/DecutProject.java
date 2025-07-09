package org.luke.decut.file;

import org.json.JSONObject;
import org.luke.decut.app.lib.assets.data.AssetData;

public class DecutProject {
    public static final String ASSETS = "assets";

    private final ProjectAssets assets;

    public DecutProject() {
        assets = new ProjectAssets();
    }

    public ProjectAssets getAssets() {
        return assets;
    }

    public void addAssets(AssetData...assets) {
        for (AssetData asset : assets) {
            this.assets.add(asset.getFile());
        }
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put(ASSETS, assets.serialize());
    }

    public void deserialize(JSONObject object) {
        assets.deserialize(object.getJSONArray(ASSETS));
    }

    @Override
    public String toString() {
        return serialize().toString(4);
    }
}
