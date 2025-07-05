package org.luke.decut.dragdrop;

import javafx.scene.input.Dragboard;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;

import java.io.File;

public class AssetDrag extends DragContent<AssetData> {
    public static final String TYPE_ASSET = "asset";
    public static final String SOURCE = "source";

    public AssetDrag(Home owner, Dragboard db) throws DragContentException {
        super(owner, db);
    }

    public AssetDrag(Home owner, AssetData body) {
        super(owner, TYPE_ASSET, body);
    }

    @Override
    public AssetData decode(JSONObject body) {
        return AssetData.getData(new File(body.getString(SOURCE)));
    }

    @Override
    public JSONObject encode(AssetData body) {
        return new JSONObject().put(SOURCE, body.getFile().getAbsolutePath());
    }

    @Override
    public boolean isValidType(String type) {
        return TYPE_ASSET.equals(type);
    }
}
