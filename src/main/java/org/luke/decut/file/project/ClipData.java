package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.app.timeline.clips.TimelineClip;

import java.io.File;

public class ClipData {
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String START_TIME = "startTime";
    public static final String IN_POINT = "inPoint";
    public static final String OUT_POINT = "outPoint";

    private File source;
    private AssetType type;
    private double startTime;
    private double inPoint;
    private double outPoint;

    public ClipData(TimelineClip clip) {
        AssetData asset = clip.getSourceAsset();
        this.source = asset.hasParent() ? asset.getParent().getFile() : clip.getSourceAsset().getFile();
        this.type = asset.getType();
        this.startTime = clip.getStartTime();
        this.inPoint = clip.getInPoint();
        this.outPoint = clip.getOutPoint();
    }

    public ClipData(JSONObject obj) {
        deserialize(obj);
    }

    public File getSource() {
        return source;
    }

    public AssetType getType() {
        return type;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getInPoint() {
        return inPoint;
    }

    public double getOutPoint() {
        return outPoint;
    }

    public JSONObject serialize() {
        return new JSONObject()
                .put(SOURCE, source.getAbsolutePath())
                .put(TYPE, type.name())
                .put(START_TIME, startTime)
                .put(IN_POINT, inPoint)
                .put(OUT_POINT, outPoint);
    }

    public void deserialize(JSONObject data) {
        source = new File(data.getString(SOURCE));
        type = AssetType.valueOf(data.getString(TYPE));
        startTime = data.getDouble(START_TIME);
        inPoint = data.getDouble(IN_POINT);
        outPoint = data.getDouble(OUT_POINT);
    }
}
